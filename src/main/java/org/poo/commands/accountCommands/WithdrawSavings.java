package org.poo.commands.accountCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.WithdrawSavingsHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class WithdrawSavings implements CommandInterface {
    private static final int MINIMUM_AGE = 21;
    private Command command;
    private List<User> users;
    private ArrayNode output;
    private Graph graph;

    public WithdrawSavings(final List<User> users, final Command command, final ArrayNode output, final Graph graph) {
        this.command = command;
        this.users = users;
        this.output = output;
        this.graph = graph;
    }

    /**
     * Withdraw savings command
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "withdrawSavings");
        ObjectNode outputNode = objectMapper.createObjectNode();

        boolean accountFound = false;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = true;
                if (account.getType().equals("savings")) {
                    int age = user.calculateAge();



                    /* check if the user has the minimum age required */
                    boolean check = false;
                    if (age >= MINIMUM_AGE) {
                        boolean classicAccountFound = false;
                        for (Account classicAccount : user.getAccounts()) {
                            /* check if the user has a classic account with the same currency */
                            if (classicAccount.getType().equals("classic")
                                    && classicAccount.getCurrency().equals(command.getCurrency())) {
                                classicAccountFound = true;
                                double newAmount = graph.convert(command.getCurrency(), classicAccount.getCurrency(),
                                        command.getAmount());
                                if (account.getBalance() >= newAmount && classicAccountFound) {
                                    check = true;
                                    WithdrawSavingsHelper.withdrawal(command, account,
                                            classicAccount, newAmount);
                                    break;
                                }
                            }
                        }

                        if (!classicAccountFound) {
                            Transaction transaction = new Transaction.TransactionBuilder(
                                    command.getTimestamp(),
                                    "You do not have a classic account.",
                                    "withdrawSavingsError")
                                    .withdrawSavingsError()
                                    .build();
                            account.getTransactions().add(transaction);
                            return;
                        }

                         if(!check) {
                            Transaction transaction = new Transaction.TransactionBuilder(
                                    command.getTimestamp(),
                                    "Insufficient funds",
                                    "withdrawSavingsError")
                                    .withdrawSavingsError()
                                    .build();
                            account.getTransactions().add(transaction);
                            return;
                        }

                    } else {
                        Transaction transaction = new Transaction.TransactionBuilder(
                                command.getTimestamp(),
                                "You don't have the minimum age required.",
                                "withdrawSavingsError")
                                .withdrawSavingsError()
                                .build();
                        account.getTransactions().add(transaction);
                    }
                }  else {
                    Transaction transaction = new Transaction.TransactionBuilder(
                            command.getTimestamp(),
                            "Account is not of type savings.",
                            "withdrawSavingsError")
                            .withdrawSavingsError()
                            .build();
                    account.getTransactions().add(transaction);
                }
            }
        }

        if (!accountFound) {
            outputNode.put("error", "Account not found");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
        }
    }
}
