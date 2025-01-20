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

public class DeleteAccount implements CommandInterface {
    private final Command command;
    private final List<User> users;
    private final ArrayNode output;

    public DeleteAccount(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Delete the account
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "deleteAccount");
        ObjectNode outputNode = objectMapper.createObjectNode();

        boolean accountFound = false;
        User user = FindHelper.findUser(users, command.getEmail());
        if (user != null) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = true;
                /* if the account has funds, it can't be deleted */
                if (account.getBalance() != 0) {
                    outputNode.put("error",
                            "Account couldn't be deleted - see org.poo.transactions for details");
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                            "Account couldn't be deleted - there are funds remaining",
                            "deleteAccountError")
                            .deleteAccountError()
                            .build();
                    account.getTransactions().add(transaction);
                } else {
                    user.getAccounts().remove(account);
                    outputNode.put("success", "Account deleted");
                }
            }
        }
        /* checks if the account was not found and prints an error for that cas */
        if (!accountFound) {
            outputNode.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
        }
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());
        output.add(resultNode);
    }
}
