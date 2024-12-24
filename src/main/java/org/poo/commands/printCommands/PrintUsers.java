package org.poo.commands.printCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;

import java.util.List;

public class PrintUsers implements CommandInterface {
    private final List<User> users;
    private final ArrayNode output;
    private final Command command;

    public PrintUsers(final List<User> users, final ArrayNode output, final Command command) {
        this.users = users;
        this.output = output;
        this.command = command;
    }

    /**
     * Print the users and their accounts and cards
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "printUsers");

        ArrayNode usersArray = objectMapper.createArrayNode();
        for (User user : users) {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());

            ArrayNode accountsNode = objectMapper.createArrayNode();
            for (Account account : user.getAccounts()) {
                ObjectNode accountNode = objectMapper.createObjectNode();
                accountNode.put("IBAN", account.getIban());
                accountNode.put("balance", account.getBalance());
                accountNode.put("currency", account.getCurrency());
                accountNode.put("type", account.getType());

                ArrayNode cardsNode = objectMapper.createArrayNode();
                for (Card card : account.getCards()) {
                    ObjectNode cardNode = objectMapper.createObjectNode();
                    cardNode.put("cardNumber", card.getCardNumber());
                    cardNode.put("status", card.getStatus());
                    cardsNode.add(cardNode);
                }
                accountNode.set("cards", cardsNode);
                accountsNode.add(accountNode);
            }
            userNode.set("accounts", accountsNode);
            usersArray.add(userNode);
        }
        resultNode.set("output", usersArray);
        resultNode.put("timestamp", command.getTimestamp());
        output.add(resultNode);
    }
}

