package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public class NrOfTransactions implements StrategyInterface {
    private int threshold;
    private double cashback;
    private String category;

    public NrOfTransactions(int threshold, double cashback, String category) {
        this.threshold = threshold;
        this.cashback = cashback;
        this.category = category;
    }

    public double calculateCashback(Transaction transaction, Account account, User user) {
        String category = transaction.getCategory();
        if (this.category.equals(category)) {
            int transactionCount = account.getTransactionCountForCategory(category);
            if (transactionCount >= this.threshold) {
                return transaction.getAmount() * this.cashback;
            }
        }
        return 0;
    }
}
