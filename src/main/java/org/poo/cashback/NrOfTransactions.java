package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public class NrOfTransactions implements StrategyInterface {
    private int threshold;
    private double cashback;
    private String category;

    public NrOfTransactions(final int threshold, final double cashback, final String category) {
        this.threshold = threshold;
        this.cashback = cashback;
        this.category = category;
    }

    /**
     * Calculate the cashback for a transaction based on the number of transactions
     * of the same category that the account has.
     * @param transaction the transaction for which the cashback is calculated
     * @param account the account of the user
     * @param user the user that made the transaction
     * @return the cashback for the transaction
     */
    public double calculateCashback(final Transaction transaction, final Account account,
                                    final User user) {
        String transactioncategory = transaction.getCategory();
        if (this.category.equals(transactioncategory)) {
            int transactionCount = account.getTransactionCountForCategory(transactioncategory);

            /* checks if the number of transactions of the same category is receiving cashback */
            if (transactionCount >= this.threshold) {
                return transaction.getAmount() * this.cashback;
            }
        }
        return 0;
    }
}
