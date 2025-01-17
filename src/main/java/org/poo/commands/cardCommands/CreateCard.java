package org.poo.commands.cardCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class CreateCard implements CommandInterface {
    private Command command;
    private List<User> users;

    public CreateCard(final List<User> users, final Command command) {
        this.command = command;
        this.users = users;
    }

    /**
     * Create a new card for the account
     */
    public void execute() {
        User user = FindHelper.findUser(users, command.getEmail());
        if (user != null) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                /* creates a new card and adds it to the account's list of cards */
                Card newCard = new Card(command.getEmail());
                account.getCards().add(newCard);
                /* creates a transaction for the new card */
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                        "New card created", "createCard")
                        .createCard(account.getIban(), newCard.getCardNumber(), command.getEmail())
                        .build();
                /* adds the transaction to the account's transaction list */
                account.getTransactions().add(transaction);
            }
        }
    }
}
