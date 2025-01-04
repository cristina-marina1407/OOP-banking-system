package org.poo.commands.accountCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class WithdrawSavings implements CommandInterface {
    private static final int MINIMUM_AGE = 21;
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public WithdrawSavings(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Withdraw savings command
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "withdrawSavings");
        ObjectNode outputNode = objectMapper.createObjectNode();

        int accountFound = 0;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = 1;
                if (account.getType().equals("savings")) {
                    int age = user.calculateAge();
                    if (age >= MINIMUM_AGE) {
                        int classicAccountFound = 0;
                        if (account.getBalance() >= command.getAmount()) {
                            for (Account classicAccount : user.getAccounts()) {
                                if (classicAccount.getType().equals("classic")
                                        && classicAccount.getCurrency().
                                        equals(command.getCurrency())) {
                                    classicAccountFound = 1;
                                    Transaction transaction;
                                    transaction =
                                            new Transaction.TransactionBuilder(
                                                    command.getTimestamp(),
                                                    "Savings withdrawal", "withdrawSavings")
                                                    .withdrawSavings(command.getAmount(),
                                                            command.getAccount(),
                                                            classicAccount.getIban())
                                                    .build();
                                    account.getTransactions().add(transaction);
                                    account.setBalance(account.getBalance() - command.getAmount());
                                    classicAccount.setBalance(classicAccount.getBalance()
                                            + command.getAmount());
                                    break;
                                }
                            }
                            if (classicAccountFound == 0) {
                                Transaction transaction;
                                transaction =
                                        new Transaction.TransactionBuilder(command.getTimestamp(),
                                                "You do not have a classic account.",
                                                "withdrawSavingsError")
                                                .withdrawSavingsError()
                                                .build();
                                account.getTransactions().add(transaction);
                                break;
                            }
                        }
                        Transaction transaction;
                        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                "Insufficient funds",
                                "withdrawSavingsError")
                                .withdrawSavingsError()
                                .build();
                        account.getTransactions().add(transaction);
                    }
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                            "You don't have the minimum age required.",
                            "withdrawSavingsError")
                            .withdrawSavingsError()
                            .build();
                    account.getTransactions().add(transaction);
                } else {
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                            "Account is not of type savings.",
                            "withdrawSavingsError")
                            .withdrawSavingsError()
                            .build();
                    account.getTransactions().add(transaction);
                }
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
    }
}
