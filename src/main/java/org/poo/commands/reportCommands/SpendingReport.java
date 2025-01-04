package org.poo.commands.reportCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.PrintTransactionsJson;
import org.poo.transactions.Transaction;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SpendingReport implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public SpendingReport(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Creates the report of the online payments of an account
     */
    public void execute() {
        /* creates a TreeMap for the total amount spent at each commerciant */
        Map<String, Double> sortedCommerciants = new TreeMap<>();
        boolean accountFound = false;
        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            /* checks if the account is a saving account */
            if (account != null) {
                if (account.getType().equals("savings")) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode resultNode = mapper.createObjectNode();
                    resultNode.put("command", "spendingsReport");

                    ObjectNode outputNode = mapper.createObjectNode();
                    outputNode.put("error",
                                "This kind of report is not supported for a saving account");

                    resultNode.set("output", outputNode);
                    resultNode.put("timestamp", command.getTimestamp());
                    output.add(resultNode);
                    return;
                }


                int start = command.getStartTimestamp();
                int end = command.getEndTimestamp();
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode resultNode = mapper.createObjectNode();
                resultNode.put("command", "spendingsReport");

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("IBAN", account.getIban());
                String formatted = String.format("%.2f", account.getBalance());
                double formattedBalance = Double.parseDouble(formatted);
                outputNode.put("balance", formattedBalance);
                outputNode.put("currency", account.getCurrency());

                ArrayNode transactionsArray = mapper.createArrayNode();

                /* prints the transactions of the account within the specified interval */
                for (Transaction transaction : account.getTransactions()) {
                    if (transaction.getTimestamp() >= start && transaction.getTimestamp() <= end) {
                        PrintTransactionsJson printTransactionsJSON =
                                new PrintTransactionsJson(transaction);
                        ObjectNode transactionNode = null;
                        if (transaction.getType().equals("payOnline")) {
                            transactionNode = printTransactionsJSON.printPayOnline();
                            if (transactionNode != null) {
                                transactionsArray.add(transactionNode);
                            }

                            /* adds the sorted amounts for each commerciant */
                            if (sortedCommerciants.containsKey(transaction.getCommerciant())) {
                                double newAmount =
                                        sortedCommerciants.get(transaction.getCommerciant())
                                        + transaction.getAmount();
                                sortedCommerciants.put(transaction.getCommerciant(), newAmount);
                            } else {
                                sortedCommerciants.put(transaction.getCommerciant(),
                                                       transaction.getAmount());
                            }
                        }
                    }
                }

                ArrayNode commerciantsArray = mapper.createArrayNode();
                for (Map.Entry<String, Double> entry : sortedCommerciants.entrySet()) {
                    ObjectNode commerciantNode = mapper.createObjectNode();
                    commerciantNode.put("commerciant", entry.getKey());
                    commerciantNode.put("total", entry.getValue());
                    commerciantsArray.add(commerciantNode);
                }

                outputNode.set("transactions", transactionsArray);
                outputNode.set("commerciants", commerciantsArray);
                resultNode.set("output", outputNode);
                resultNode.put("timestamp", command.getTimestamp());
                output.add(resultNode);
                accountFound = true;
                break;
            }
        }

        /* checks if the account was not found and prints an error for that case */
        if (!accountFound) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode resultNode = mapper.createObjectNode();
            resultNode.put("command", "spendingsReport");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", "Account not found");
            outputNode.put("timestamp", command.getTimestamp());

            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
        }
    }
}
