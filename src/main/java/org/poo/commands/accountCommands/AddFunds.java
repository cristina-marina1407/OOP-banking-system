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

        Account account = null;

        for (User accountUser : users) {
            account = FindHelper.findAccount(accountUser.getAccounts(), command.getAccount());
            if (account != null) {
                break;
            }
        }

//        System.out.println("Account is " + account.getIban());
//        System.out.println("User is " + user.getEmail());

        //for (User user : users) {
        if (user != null) {
            if (account != null) {
                if (account.getType().equals("business")) {
                    System.out.println("business " + "timestamp " + command.getTimestamp() + " addFunds account: " + account.getIban() + " email " +
                            command.getEmail() + " amount " + command.getAmount());

                    if (!account.isAssociate(user.getEmail()) && !account.getOwner().equals(user.getEmail())) {
                        System.out.println("Account is not associate or owner " + user.getEmail());
                        return;
                    }
                    if (account.isEmployee(command.getEmail())
                            && command.getAmount() > account.getDepositLimit()) {
                        System.out.println("Exceeds the deposit limit " + user.getEmail());
                       return;
                    }
                    account.updateTotalDepositedByAssociate(command.getEmail(),
                            command.getAmount());
                }
//                System.out.println("Adding funds to account " + account.getIban() + " "  + command.getAmount() + " by " + user.getEmail() + " timestamp " + command.getTimestamp());
                if (account.getType().equals("classic") || account.getType().equals("savings")) {
                    System.out.println("classic " + "timestamp " + command.getTimestamp() + " addFunds account: " + account.getIban() + " email " +
                            command.getEmail() + " amount " + command.getAmount());
                }
                account.setBalance(account.getBalance() + command.getAmount());

                /* formatted the balance after adding funds */
//                String formatted = String.format("%.2f", account.getBalance());
                //double formattedBalance = Double.parseDouble(formatted);
                //account.setBalance(account.getBalance());
            }
        }
    }
}
