package org.poo.commands.payCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class CashWithdrawal implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;
    private Graph graph;
    private static final int COMISSION_SUM = 500;


    public CashWithdrawal(final List<User> users, final Command command,
                          final ArrayNode output, final Graph graph) {
        this.command = command;
        this.users = users;
        this.output = output;
        this.graph = graph;
    }

    /**
     * Withdraws cash from the account
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "cashWithdrawal");
        ObjectNode outputNode = objectMapper.createObjectNode();

        User user = FindHelper.findUser(users, command.getEmail());

        /* checks if the user was not found and prints an error for that case */
        if (user == null) {
            PrintOutputErrorHelper.printOutputError("User not found", outputNode,
                    resultNode, command, output);
            return;
        }

        Account account = FindHelper.findAccountByCardNumber(users, command.getCardNumber());

        Card card = FindHelper.findCardByCardNumber(users, command.getCardNumber());

        /* check if the card exists and if it belongs to the owner */
        if (card == null || !card.getOwner().equals(command.getEmail())) {
            outputNode.put("description", "Card not found");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
            return;
        }

        /* a user can withdraw money only in RON */
        double newAmount = graph.convert("RON", account.getCurrency(),
                command.getAmount());

        /* calculate the commission */
        double commission = user.calculateCommission(newAmount, graph, account);
        if (account.getBalance() >= newAmount + commission) {
            if (user.getServicePlan().equals("standard")) {
                account.setBalance(account.getBalance() - commission);
            }

            if (user.getServicePlan().equals("silver") && command.getAmount() >= COMISSION_SUM) {
                account.setBalance(account.getBalance() - commission);
            }

            Transaction transaction;
            transaction =
                    new Transaction.TransactionBuilder(
                            command.getTimestamp(),
                            "Cash withdrawal of " + command.getAmount(),
                            "cashWithdrawal")
                            .cashWithdrawl(command.getAmount())
                            .build();
            account.getTransactions().add(transaction);
            account.setBalance(account.getBalance() - newAmount);
            return;
        }
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                "Insufficient funds",
                "cashWithdrawalError")
                .cashWithdrawalError()
                .build();
        account.getTransactions().add(transaction);
    }
}
