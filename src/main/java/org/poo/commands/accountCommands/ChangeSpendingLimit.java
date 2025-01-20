package org.poo.commands.accountCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;

import java.util.List;

/**
 * This method changes the spending limit of a business account.
 */
public class ChangeSpendingLimit implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public ChangeSpendingLimit(final List<User> users, final Command command,
                               final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * This method changes the spending limit of a business account.
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "changeSpendingLimit");
        ObjectNode outputNode = objectMapper.createObjectNode();

        boolean checkEmail = false;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                if (account.getType().equals("business")) {
                    if (user.getEmail().equals(command.getEmail())) {
                        account.setSpendingLimit(command.getAmount());
                        checkEmail = true;
                    }
                } else {
                    outputNode.put("description", "This is not a business account");
                    outputNode.put("timestamp", command.getTimestamp());
                    resultNode.set("output", outputNode);
                    resultNode.put("timestamp", command.getTimestamp());
                    output.add(resultNode);
                    return;
                }
            }
        }
        /* if the email is not the owner's email, the spending limit cannot be changed. */
        if (!checkEmail) {
            PrintOutputErrorHelper.printOutputError("You must be owner in order"
                            + " to change spending limit.",
                    outputNode, resultNode, command, output);
        }
    }
}
