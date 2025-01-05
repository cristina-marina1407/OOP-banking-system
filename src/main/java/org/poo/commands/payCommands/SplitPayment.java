package org.poo.commands.payCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;

    public SplitPayment(final List<User> users, final Command command, final Graph graph) {
        this.command = command;
        this.users = users;
        this.graph = graph;
    }

    /**
     * Splits the amount of money to multiple accounts
     */
    public void execute() {
        /* creates a list of the accounts that the money will be split into */
        List<String> splitAccounts;
        splitAccounts = command.getAccounts();
        /* calculates the amount of money that each account will receive */
        double moneySplit = command.getAmount() / splitAccounts.size();
        int checkAccounts = 0;
        /* creates a list of the accounts that have insufficient funds */
        List<String> insufficientAccounts = new ArrayList<>();
        for (String iban : splitAccounts) {
            for (User user : users) {
                Account account = FindHelper.findAccount(user.getAccounts(), iban);
                if (account != null) {
                    double newAmount = graph.convert(command.getCurrency(), account.getCurrency(),
                                       moneySplit);
                    if (account.getBalance() < newAmount) {
                        checkAccounts++;
                        insufficientAccounts.add(iban);
                    }
                }
            }
        }


        for (String iban : splitAccounts) {
            for (User user : users) {
                Account account = FindHelper.findAccount(user.getAccounts(), iban);
                if (account != null) {
                    /* converts the amount of money to the currency of the account */
                    double newAmount = graph.convert(command.getCurrency(),
                                       account.getCurrency(), moneySplit);
                    if (checkAccounts > 0) {
                        /* if there are accounts with insufficient funds creates a transaction
                         for the last account that had insufficient funds*/
                        Transaction transaction;
                        String formattedAmount = String.format("%.2f %s", command.getAmount(),
                                                 command.getCurrency());
                        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                "Split payment of " + formattedAmount, "splitPayment")
                                .splitPaymentError(command.getCurrency(), moneySplit, splitAccounts,
                                        insufficientAccounts.get(insufficientAccounts.size() - 1))
                                .build();
                        account.getTransactions().add(transaction);
                    } else {
                        /* substracts the amount of money from the accounts */
                        account.setBalance(account.getBalance() - newAmount);

                        /*formatare*/
                        String formatted = String.format("%.2f", account.getBalance());
                        double formattedBalance = Double.parseDouble(formatted);
                        account.setBalance(formattedBalance);

                        /* creates the transactions for the accounts and adds them
                         to the transactions list */
                        Transaction transaction;
                        String formattedAmount = String.format("%.2f %s", command.getAmount(),
                                                 command.getCurrency());
                        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                "Split payment of " + formattedAmount, "splitPayment")
                                .splitPayment(command.getCurrency(), moneySplit, splitAccounts)
                                .build();
                        account.getTransactions().add(transaction);
                    }
                }
            }
        }
    }
}
