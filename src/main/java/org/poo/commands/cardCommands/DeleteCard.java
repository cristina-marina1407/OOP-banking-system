package org.poo.commands.cardCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class DeleteCard implements CommandInterface {
    private Command command;
    private List<User> users;
    public DeleteCard(final List<User> users, final Command command) {
        this.command = command;
        this.users = users;
    }

    /**
     * Deletes the card from the account
     */
    public void execute() {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                /* finds and deletes the card from the list of cards of the account specified
                   in the command */
                Card card = FindHelper.findCard(account.getCards(), command.getCardNumber());
                if (card != null) {
                    if (account.getBalance() <= 0 && !account.getType().equals("classic")) {
                        account.getCards().remove(card);
                    }

                    /* creates a transaction for the deletion of the card */
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                            "The card has been destroyed", "deleteCard")
                            .deleteCard(account.getIban(), command.getCardNumber(),
                                        command.getEmail())
                            .build();
                    /* adds the transaction to the account's transaction list */
                    account.getTransactions().add(transaction);
                    break;
                }
            }
        }
    }
}
