package org.poo.commands.accountCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;

import java.util.List;

public class AddNewBusinessAssociate implements CommandInterface {
    private Command command;
    private List<User> users;

    public AddNewBusinessAssociate(final List<User> users, final Command command) {
        this.command = command;
        this.users = users;
    }

    /**
     * Adds a new business associate to the account.
     */
    public void execute() {
        User user = FindHelper.findUser(users, command.getEmail());
        Account account = FindHelper.findAccountByIban(users, command.getAccount());

        if (user != null) {
            if (account != null) {
                if (account.getType().equals("business")) {
                    account.addAssociate(command.getRole(), user);
                }
            }
        }
    }
}
