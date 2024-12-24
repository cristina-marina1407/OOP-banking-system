package org.poo.commands.printCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintHelper;
import org.poo.transactions.PrintTransactionsJson;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PrintTransactions implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public PrintTransactions(final List<User> users, final Command command,
                             final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Print all the transactions of a user
     */
    public void execute() {
        List<Transaction> allTransactions = new ArrayList<>();
        User user = FindHelper.findUser(users, command.getEmail());

        /* Add all transactions of the user to a list */
        if (user != null) {
            for (Account account : user.getAccounts()) {
                for (Transaction transaction : account.getTransactions()) {
                    allTransactions.add(transaction);
                }
            }
        }

        /* Sort the transactions by timestamp */
        Collections.sort(allTransactions, new Comparator<Transaction>() {
            @Override
            public int compare(final Transaction o1, final Transaction o2) {
                return Integer.compare(o1.getTimestamp(), o2.getTimestamp());
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        resultNode.put("command", "printTransactions");

        ArrayNode transactionsArray = mapper.createArrayNode();
        /* Print all transactions */
        for (Transaction transaction : allTransactions) {
            if (transaction != null) {
                PrintTransactionsJson printTransactionsJSON =
                        new PrintTransactionsJson(transaction);
                ObjectNode transactionNode = PrintHelper.printParsing(transaction,
                        printTransactionsJSON);
                if (transactionNode != null) {
                    transactionsArray.add(transactionNode);
                }
            }
        }

        resultNode.set("output", transactionsArray);
        resultNode.put("timestamp", command.getTimestamp());
        output.add(resultNode);
    }
}
