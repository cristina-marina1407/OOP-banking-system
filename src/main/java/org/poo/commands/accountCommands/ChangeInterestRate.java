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

public class ChangeInterestRate implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public ChangeInterestRate(final List<User> users, final Command command,
                              final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Change the interest rate of the account
     */
    public void execute() {
        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                /* if the account is a savings account, change the interest rate */
                if (account.getType().equals("savings")) {
                    account.setInterestRate(command.getInterestRate());
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                            "Interest rate of the account changed to " + command.getInterestRate(),
                            "changeInterestRate")
                            .changeInterestRate()
                            .build();
                    account.getTransactions().add(transaction);
                    break;
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode resultNode = objectMapper.createObjectNode();
                    resultNode.put("command", "changeInterestRate");
                    resultNode.put("timestamp", command.getTimestamp());
                    ObjectNode outputDetails = resultNode.putObject("output");
                    outputDetails.put("description", "This is not a savings account");
                    outputDetails.put("timestamp", command.getTimestamp());
                    output.add(resultNode);
                }
            }
        }
    }
}
