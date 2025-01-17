package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public class SpendingThreshold implements StrategyInterface {
    private static final double GOLD_CASHBACK_ABOVE_500 = 0.007;
    private static final double SILVER_CASHBACK_ABOVE_500 = 0.005;
    private static final double DEFAULT_CASHBACK_ABOVE_500 = 0.0025;

    private static final double GOLD_CASHBACK_ABOVE_300 = 0.0055;
    private static final double SILVER_CASHBACK_ABOVE_300 = 0.004;
    private static final double DEFAULT_CASHBACK_ABOVE_300 = 0.002;

    private static final double GOLD_CASHBACK_ABOVE_100 = 0.005;
    private static final double SILVER_CASHBACK_ABOVE_100 = 0.003;
    private static final double DEFAULT_CASHBACK_ABOVE_100 = 0.001;

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
    public double calculateCashback(Commerciant commerciant, final Transaction transaction, final Account account,
                                    final User user) {
        double totalSpentRON = account.getTotalSpentRON();
        double cashbackPercentage = 0.0;

        /* calculates the cashback percentage based on the service plan and
         the total amount spent by the user */
        System.out.println("user plan " + user.getServicePlan());
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

        return cashbackPercentage;
    }
}
