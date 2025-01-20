package org.poo.commands.cardCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class CheckCardStatus implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;


    public CheckCardStatus(final List<User> users, final Command command,
                           final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Checks the status of the card
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "checkCardStatus");
        ObjectNode outputNode = objectMapper.createObjectNode();

        boolean cardFound = false;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                Card card = FindHelper.findCard(account.getCards(), command.getCardNumber());
                if (card != null) {
                    /* checks if the balance of the account is below the minimum balance,
                     if that happens the card will be frozen */
                    if (account.getBalance() <= account.getMinBalance()) {
                        card.setStatus("frozen");
                        /* creates a transaction for the frozen card */
                        Transaction transaction;
                        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                "You have reached the minimum amount of funds,"
                                        + " the card will be frozen",
                                "checkCardStatus")
                                .checkStatusCard()
                                .build();
                        /* adds the transaction to the account's transaction list */
                        account.getTransactions().add(transaction);
                    }
                    cardFound = true;
                }
            }
        }
        /* checks if the card was not found and prints an error for that case */
        if (!cardFound) {
            PrintOutputErrorHelper.printOutputError("Card not found", outputNode,
                    resultNode, command, output);
        }
    }
}
