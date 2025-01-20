package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public final class CashbackHelper {
    private CashbackHelper() {

    }

    /**
     * Method that applies the cashback strategy for a transaction
     * @param commerciantToPay the commerciant that the user is paying
     * @param account the account of the user
     * @param category the category of the transaction
     * @param transaction the transaction that the user is making
     * @param user the user that is making the transaction
     * @param graph the graph that contains the exchange rates
     * @param command the command that the user is making
     */
    public static void applyCashback(final Commerciant commerciantToPay, final Account account,
                                     final String category, final Transaction transaction,
                                     final User user, final Graph graph, final Command command,
                                     final List<User> users) {
        /* apply the cashback for a category */
        if (account.hasCashbackForCategory(category)
            && !account.hasUsedCashbackForCategory(category)) {
            double cashback = account.getCashbackForCategory(category);
            account.setBalance(account.getBalance() + cashback * transaction.getAmount());

            /* removes the cashback from the active cashback list */
            account.removeCashbackForCategory(category);
            /* set the cashback as used */
            account.addReceivedCashbackCategory(category);
        } else if (commerciantToPay.getCashbackStrategy().equals("nrOfTransactions")) {
            NrOfTransactions nrOfTransactions = new NrOfTransactions();

            /* creates the cashback for the category when its threshold was hit */
            double cashback = nrOfTransactions.calculateCashback(commerciantToPay,
                              transaction, account, user);
            if (cashback > 0) {
                account.addCashbackForCategory(category, cashback);
            }
        }

        /* apply the spending threshold cashback strategy */
        if (commerciantToPay.getCashbackStrategy().equals("spendingThreshold")) {
            /* checks if the currency is null, that happens when a user sends money to a
             commerciant */
            if (command.getCurrency() == null) {
                spendingThresholdHelper(account, transaction, commerciantToPay,
                        command.getAmount(), users, user);
                return;
            }

            /* if the currency is not null, the amount has to be converted in RON */
            double amountInRON = graph.convert(command.getCurrency(), "RON", command.getAmount());
            spendingThresholdHelper(account, transaction, commerciantToPay,
                                    amountInRON, users, user);
        }
    }

    /**
     * Helper method for the spending threshold cashback strategy
     * @param account the account of the user
     * @param transaction the transaction that the user is making
     * @param commerciantToPay the commerciant that the user is paying
     * @param amount the amount of the transaction
     * @param users the list of users
     * @param user the user that is making the transaction
     */
    public static void spendingThresholdHelper(final Account account, final Transaction transaction,
                                               final Commerciant commerciantToPay,
                                               final double amount,
                                               final List<User> users, final User user) {
        account.addToTotalSpentRON(amount);

        SpendingThreshold spendingThreshold = new SpendingThreshold();

        /* if the account is a business account, the cashback is calculated based on the owner */
        if (account.getType().equals("business")) {
            User owner = FindHelper.findUser(users, account.getOwner());
            double cashback = spendingThreshold.calculateCashback(commerciantToPay,
                    transaction, account, owner);
            account.setBalance(account.getBalance() + cashback * transaction.getAmount());
            return;
        }

        double cashback = spendingThreshold.calculateCashback(commerciantToPay,
                transaction, account, user);
        account.setBalance(account.getBalance() + cashback * transaction.getAmount());
    }
}
