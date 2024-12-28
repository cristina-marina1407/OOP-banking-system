package org.poo.commands.payCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.bankInformation.Graph;

import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.CountTransactionsHelper;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class PayOnline implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private ArrayNode output;

    public PayOnline(final List<User> users, final Command command, final Graph graph,
                     final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.output = output;
    }

    /**
     * Pay online command
     */
    public void execute() {
        boolean cardFound = false;
        User user = FindHelper.findUser(users, command.getEmail());
        if (user != null) {
            for (Account account : user.getAccounts()) {
                Card card = FindHelper.findCard(account.getCards(), command.getCardNumber());
                if (card != null) {
                    /* converts the amount to the currency of the account */
                    double newAmount = graph.convert(command.getCurrency(), account.getCurrency(),
                                       command.getAmount());
                    /* checks if the card is active and if it has funds for the payment */
                    if (card.getStatus().equals("active")) {
                        if (account.getBalance() >= newAmount) {
                            /* creates the transaction for the payment */
                            Transaction transaction;
                            transaction =
                                    new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Card payment", "payOnline")
                                    .payOnline(newAmount, command.getCommerciant())
                                    .build();
                            account.getTransactions().add(transaction);
                            /* pays the amount */
                            card.pay(account, newAmount, command.getEmail(),
                                    command.getTimestamp());

                            if (user.getServicePlan().equals("standard")) {
                                account.setBalance(account.getBalance() - newAmount * 0.2);
                            }

                            if (user.getServicePlan().equals("silver") && command.getAmount() > 500) {
                                account.setBalance(account.getBalance() - newAmount * 0.1);
                            }

                            boolean upgradeCheck = CountTransactionsHelper.countTransactions(users);

                            if (upgradeCheck) {
                                if (user.getServicePlan().equals("silver")) {
                                    user.setServicePlan("gold");
                                }
                            }

                        } else {
                            /* creates a transaction for the case when the account
                             has insufficient funds */
                            Transaction transaction;
                            transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Insufficient funds", "payOnlineError")
                                    .payOnlineError()
                                    .build();
                            account.getTransactions().add(transaction);
                        }
                        cardFound = true;
                        break;
                    } else {
                        /* creates a transaction for the case when the card is frozen */
                        Transaction transaction;
                        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                "The card is frozen", "payOnlineError")
                                .payOnlineError()
                                .build();
                        account.getTransactions().add(transaction);
                        cardFound = true;
                        break;
                    }
                }
            }
        }
        /* checks if the card was not found and prints an error for that case */
        if (!cardFound) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("command", "payOnline");
            resultNode.put("timestamp", command.getTimestamp());
            ObjectNode outputDetails = resultNode.putObject("output");
            outputDetails.put("description", "Card not found");
            outputDetails.put("timestamp", command.getTimestamp());
            output.add(resultNode);
        }
    }

}
