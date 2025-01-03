package org.poo.commands.payCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.*;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class CashWithdrawal implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;
    private Graph graph;

    public CashWithdrawal(final List<User> users, final Command command, final Graph graph, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
        this.graph = graph;
    }

    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "cashWithdrawal");
        ObjectNode outputNode = objectMapper.createObjectNode();

        int accountFound = 0;

        User user = FindHelper.findUser(users, command.getEmail());
        if (user != null) {
            for (Account account : user.getAccounts()) {
                Card card = FindHelper.findCard(account.getCards(), command.getCardNumber());
                if (card != null) {
                    accountFound = 1;
                    double newAmount = graph.convert(account.getCurrency(), "RON", command.getAmount());
                    if (account.getBalance() >= newAmount) {
                        Transaction transaction;
                        transaction =
                                new Transaction.TransactionBuilder(
                                        command.getTimestamp(),
                                        "Savings withdrawal", "cashWithdrawal")
                                        .cashWithdrawl()
                                        .build();
                        account.getTransactions().add(transaction);
                        account.setBalance(account.getBalance() - newAmount);
                        break;
                    }
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                            "Insufficient funds",
                            "cashWithdrawalError")
                            .cashWithdrawlError()
                            .build();
                    account.getTransactions().add(transaction);
                    break;
                }
            }

            if (accountFound == 0) {
                outputNode.put("error",
                        "Account not found");
                outputNode.put("timestamp", command.getTimestamp());
                resultNode.set("output", outputNode);
                resultNode.put("timestamp", command.getTimestamp());
                output.add(resultNode);
                outputNode.put("timestamp", command.getTimestamp());
                resultNode.set("output", outputNode);
                resultNode.put("timestamp", command.getTimestamp());
                output.add(resultNode);
            }

        } else {
            outputNode.put("error",
                    "User not found");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
        }
    }
}
