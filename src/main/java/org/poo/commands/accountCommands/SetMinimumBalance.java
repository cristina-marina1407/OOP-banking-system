package org.poo.commands.accountCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;

import java.util.List;

public class SetMinimumBalance implements CommandInterface {
    private Command command;
    private List<User> users;

    public SetMinimumBalance(final List<User> users, final Command command) {
        this.command = command;
        this.users = users;
    }

    /**
     * Set the minimum balance for the account
     */
    public void execute() {
        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                account.setMinBalance(command.getMinBalance());

                /* formatted the balance after setting the minimum balance */
                String formatted = String.format("%.2f", account.getBalance());
                double formattedBalance = Double.parseDouble(formatted);
                account.setBalance(formattedBalance);

                break;
            }
        }
    }
}
