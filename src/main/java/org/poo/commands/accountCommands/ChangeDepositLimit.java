package org.poo.commands.accountCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;

import java.util.List;

public class ChangeDepositLimit implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public ChangeDepositLimit(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "changeDepositLimit");
        ObjectNode outputNode = objectMapper.createObjectNode();

        boolean checkEmail = false;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                if (account.getType().equals("business")) {
                    if (user.getEmail().equals(command.getEmail())) {
                        account.setDepositLimit(command.getAmount());
                        checkEmail = true;
                    }
                }
            }
        }
        if (!checkEmail) {
            outputNode.put("description", "You must be owner in order to change deposit limit.");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
        }
    }
}
