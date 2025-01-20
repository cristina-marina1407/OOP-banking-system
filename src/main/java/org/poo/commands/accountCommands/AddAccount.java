package org.poo.commands.accountCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class AddAccount implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;

    public AddAccount(final List<User> users, final Command command, final Graph graph) {
        this.command = command;
        this.users = users;
        this.graph = graph;
    }

    /**
     * Adds a new account to a user
     */
    public void execute() {
        User user = FindHelper.findUser(users, command.getEmail());
        Account newAccount;
        if (command.getAccountType().equals("savings")) {
            newAccount = new Account.AccountBuilder(command.getCurrency(),
                    command.getAccountType())
                    .savings(command.getInterestRate())
                    .build();
            /* creates a transaction for the new savings account */
            newAccountTransaction(newAccount);
        } else if (command.getAccountType().equals("business")) {
            newAccount = new Account.AccountBuilder(command.getCurrency(),
                    command.getAccountType())
                    .business(command.getEmail(), graph)
                    .build();
            /* creates a transaction for the new business account */
            newAccountTransaction(newAccount);
        } else {
            newAccount = new Account.AccountBuilder(command.getCurrency(),
                    command.getAccountType())
                    .build();
            /* creates a transaction for the new account */
            newAccountTransaction(newAccount);
        }
        /* adds the new account to the user */
        user.getAccounts().add(newAccount);
    }

    /**
     * Creates a transaction for the new account
     * @param newAccount the new account
     */
    public void newAccountTransaction(final Account newAccount) {
        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                "New account created", "addAccount")
                .addAccount()
                .build();
        newAccount.getTransactions().add(transaction);
    }
}
