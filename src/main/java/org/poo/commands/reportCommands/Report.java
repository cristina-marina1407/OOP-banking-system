package org.poo.commands.reportCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.transactions.PrintTransactionsJson;
import org.poo.transactions.Transaction;

import java.util.List;

public class Report implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public Report(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Creates a report of the transactions of an account
     */
    public void execute() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        resultNode.put("command", "report");
        ObjectNode outputNode = mapper.createObjectNode();

        boolean accountFound = false;
        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                int start = command.getStartTimestamp();
                int end = command.getEndTimestamp();
                accountFound = true;

                outputNode.put("IBAN", account.getIban());
                outputNode.put("balance", account.getBalance());
                outputNode.put("currency", account.getCurrency());

                ArrayNode transactionsArray = mapper.createArrayNode();

                /* prints the transactions of the account within the specified interval */
                for (Transaction transaction : account.getTransactions()) {
                    if (transaction.getTimestamp() >= start && transaction.getTimestamp() <= end) {
                        PrintTransactionsJson printTransactionsJSON =
                                new PrintTransactionsJson(transaction);
                        ObjectNode transactionNode = PrintHelper.printParsing(transaction,
                                                     printTransactionsJSON);
                        if (transactionNode != null) {
                            transactionsArray.add(transactionNode);
                        }
                    }
                }
                outputNode.set("transactions", transactionsArray);
                resultNode.set("output", outputNode);
                resultNode.put("timestamp", command.getTimestamp());
                output.add(resultNode);
                break;
            }
        }
        /* checks if the account was not found and prints an error for that case */
        if (!accountFound) {
            PrintOutputErrorHelper.printOutputError("Account not found", outputNode,
                    resultNode, command, output);
        }
    }
}
