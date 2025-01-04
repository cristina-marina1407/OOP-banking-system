package org.poo.commands.payCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.*;

import org.poo.cashback.NrOfTransactions;
import org.poo.cashback.SpendingThreshold;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.CountTransactionsHelper;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class PayOnline implements CommandInterface {
    private Command command;
    private List<User> users;
    private List<Commerciant> commerciants;
    private Graph graph;
    private ArrayNode output;

    public PayOnline(final List<User> users, final Command command, final Graph graph,
                     final ArrayNode output, final List<Commerciant> commerciants) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.output = output;
        this.commerciants = commerciants;
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
                    double commission = user.calculateCommission(newAmount);
                    /* checks if the card is active and if it has funds for the payment */
                    if (card.getStatus().equals("active")) {
                        if (account.getBalance() >= newAmount + commission) {
                            /* creates the transaction for the payment */
                            String commerciantName = command.getCommerciant();
                            String category = null;
                            Commerciant commerciantToPay = null;
                            for (Commerciant commerciant : commerciants) {
                                if (commerciant.getCommerciant().equals(commerciantName)) {
                                    category = commerciant.getType();
                                    commerciantToPay = commerciant;
                                    break;
                                }
                            }

                            Transaction transaction;
                            transaction =
                                    new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Card payment", "payOnline")
                                    .payOnline(newAmount, command.getCommerciant(), category)
                                    .build();
                            account.getTransactions().add(transaction);

                            if (commerciantToPay != null) {
                                CashbackHelper.applyCashback(commerciantToPay, account, category,
                                        transaction, user, graph, command);
                            }

                            /* pays the amount */
                            card.pay(account, newAmount, command.getEmail(),
                                    command.getTimestamp());

                            if (user.getServicePlan().equals("standard")) {
                                account.setBalance(account.getBalance() - commission);
                            }

                            if (user.getServicePlan().equals("silver") && command.getAmount() > 500) {
                                account.setBalance(account.getBalance() - commission);
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
