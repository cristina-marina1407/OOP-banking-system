package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public final class CashbackHelper {
    private static final int FOOD_THRESOLD = 2;
    private static final double FOOD_CASHBACK = 0.02;
    private static final int CLOTHES_THRESOLD = 5;
    private static final double CLOTHES_CASHBACK = 0.05;
    private static final int TECH_THRESOLD = 10;
    private static final double TECH_CASHBACK = 0.10;

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
                                     final User user, final Graph graph, final Command command) {
        /* apply the number of transaction cashback strategy */
        if (commerciantToPay.getCashbackStrategy().equals("nrOfTransactions")) {
            /* increment the number of transactions for the category */
            account.incrementTransactionCount(category);
            NrOfTransactions nrOfTransactions = null;

            /* initialize the number of transactions object based on the category */
            /* this can be wrong */
            if (category.equals("Food")) {
                nrOfTransactions = new NrOfTransactions(FOOD_THRESOLD, FOOD_CASHBACK, "Food");
            } else if (category.equals("Clothes")) {
                nrOfTransactions = new NrOfTransactions(CLOTHES_THRESOLD, CLOTHES_CASHBACK,
                                                "Clothes");
            } else if (category.equals("Tech")) {
                nrOfTransactions = new NrOfTransactions(TECH_THRESOLD, TECH_CASHBACK, "Tech");
            }

            /* calculate the cashback and add it to the account */
            if (nrOfTransactions != null) {
                double cashback = nrOfTransactions.calculateCashback(transaction, account, user);

                if (cashback > 0) {
                    account.addCashbackReceived(category, cashback);
                    account.setBalance(account.getBalance() + cashback);

                    /*formatted the balance after adding the cashback*/
                    String formatted = String.format("%.2f", account.getBalance());
                    double formattedBalance = Double.parseDouble(formatted);
                    account.setBalance(formattedBalance);

                }
            }
        /* apply the spending threshold cashback strategy */
        } else if (commerciantToPay.getCashbackStrategy().equals("spendingThreshold")) {
            /* convert the amount to RON and add it to the total spent in RON*/
            double amountInRON = graph.convert(command.getCurrency(), "RON", command.getAmount());
            account.addToTotalSpentRON(amountInRON);


            SpendingThreshold spendingThreshold = new SpendingThreshold();
            double cashback = spendingThreshold.calculateCashback(transaction, account, user);
            account.setBalance(account.getBalance() + cashback);

            /*formatted the balance after adding the cashback*/
            String formatted = String.format("%.2f", account.getBalance());
            double formattedBalance = Double.parseDouble(formatted);
            account.setBalance(formattedBalance);

        }
    }
}
