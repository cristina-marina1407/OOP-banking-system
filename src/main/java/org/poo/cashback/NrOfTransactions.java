package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public class NrOfTransactions implements StrategyInterface {

    /**
     * Calculate the cashback for a transaction based on the number of transactions
     * of the same category that the account has.
     * @param transaction the transaction for which the cashback is calculated
     * @param account the account of the user
     * @param user the user that made the transaction
     * @return the cashback for the transaction
     */
    public double calculateCashback(Commerciant commerciant, final Transaction transaction, final Account account,
                                    final User user) {
        int threshold = 0;
        double cashback = 0.0;
        if (commerciant.getType().equals("Food")) {
            threshold = 2;
            cashback = 0.02;
        } else if (commerciant.getType().equals("Clothes")) {
            threshold = 5;
            cashback = 0.05;
        } else if (commerciant.getType().equals("Tech")) {
            threshold = 10;
            cashback = 0.10;
        }

        int transactionCount = account.getNrOfTransactions(commerciant.getCommerciant());
        System.out.println("TransactionCount: " + transactionCount + " " + commerciant.getCommerciant() + " " + threshold);
        /* checks if the number of transactions of the same category is receiving cashback */
        if (transactionCount == threshold) {
            double amount = transaction.getAmount();
            System.out.println("amount " + amount + " in calculating nr of transactions cashback " + cashback + " commerciant " + commerciant.getCommerciant());
            return cashback;
        }
        return 0;
    }
}
