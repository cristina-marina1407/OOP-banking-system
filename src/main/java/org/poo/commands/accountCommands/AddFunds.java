package org.poo.commands.accountCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;

import java.util.List;

public class AddFunds implements CommandInterface {
    private Command command;
    private List<User> users;

    public AddFunds(final List<User> users, final Command command) {
        this.command = command;
        this.users = users;
    }

    /**
     * Add funds to the account
     */
    public void execute() {
        User user = FindHelper.findUser(users, command.getEmail());
        Account account = FindHelper.findAccountByIban(users, command.getAccount());

        if (user != null) {
            if (account != null) {
                if (account.getType().equals("business")) {
                    /* checks if the user is an associate to the business account */
                    if (!account.isAssociate(user.getEmail())
                        && !account.getOwner().equals(user.getEmail())) {
                        return;
                    }

                    /* checks if the associate has exceeded the deposit limit */
                    if (account.isEmployee(command.getEmail())
                        && command.getAmount() > account.getDepositLimit()) {
                        return;
                    }

                    /* updates the total deposited by the associate */
                    account.updateTotalDepositedByAssociate(command.getEmail(),
                            command.getAmount());
                }
                account.setBalance(account.getBalance() + command.getAmount());
            }
        }
    }
}
