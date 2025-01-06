package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public class SpendingThreshold implements StrategyInterface {
    private static final double GOLD_CASHBACK_ABOVE_500 = 0.7;
    private static final double SILVER_CASHBACK_ABOVE_500 = 0.5;
    private static final double DEFAULT_CASHBACK_ABOVE_500 = 0.25;

    private static final double GOLD_CASHBACK_ABOVE_300 = 0.55;
    private static final double SILVER_CASHBACK_ABOVE_300 = 0.4;
    private static final double DEFAULT_CASHBACK_ABOVE_300 = 0.2;

    private static final double GOLD_CASHBACK_ABOVE_100 = 0.5;
    private static final double SILVER_CASHBACK_ABOVE_100 = 0.3;
    private static final double DEFAULT_CASHBACK_ABOVE_100 = 0.1;

    private static final double THRESHOLD_500 = 500;
    private static final double THRESHOLD_300 = 300;
    private static final double THRESHOLD_100 = 100;

    private static final int PERCENTAGE = 100;


    /**
     * Calculate the cashback for a transaction based on the total amount spent by the user.
     * @param transaction the transaction for which the cashback is calculated
     * @param account the account of the user
     * @param user the user for which the cashback is calculated
     * @return the cashback amount
     */
    public double calculateCashback(final Transaction transaction, final Account account,
                                    final User user) {
        double totalSpentRON = account.getTotalSpentRON();
        double cashbackPercentage = 0.0;

        /* calculates the cashback percentage based on the service plan and
         the total amount spent by the user */
        if (totalSpentRON >= THRESHOLD_500) {
            if (user.getServicePlan().equals("gold")) {
                cashbackPercentage = GOLD_CASHBACK_ABOVE_500;
            } else if (user.getServicePlan().equals("silver")) {
                cashbackPercentage = SILVER_CASHBACK_ABOVE_500;
            } else {
                cashbackPercentage = DEFAULT_CASHBACK_ABOVE_500;
            }
        } else if (totalSpentRON >= THRESHOLD_300) {
            if (user.getServicePlan().equals("gold")) {
                cashbackPercentage = GOLD_CASHBACK_ABOVE_300;
            } else if (user.getServicePlan().equals("silver")) {
                cashbackPercentage = SILVER_CASHBACK_ABOVE_300;
            } else {
                cashbackPercentage = DEFAULT_CASHBACK_ABOVE_300;
            }
        } else if (totalSpentRON >= THRESHOLD_100) {
            if (user.getServicePlan().equals("gold")) {
                cashbackPercentage = GOLD_CASHBACK_ABOVE_100;
            } else if (user.getServicePlan().equals("silver")) {
                cashbackPercentage = SILVER_CASHBACK_ABOVE_100;
            } else {
                cashbackPercentage = DEFAULT_CASHBACK_ABOVE_100;
            }
        }

        return transaction.getAmount() * cashbackPercentage / PERCENTAGE;
    }
}
