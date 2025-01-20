package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

import static org.poo.commands.helperMethods.Constants.GOLD_CASHBACK_ABOVE_500;
import static org.poo.commands.helperMethods.Constants.SILVER_CASHBACK_ABOVE_500;
import static org.poo.commands.helperMethods.Constants.DEFAULT_CASHBACK_ABOVE_500;

import static org.poo.commands.helperMethods.Constants.GOLD_CASHBACK_ABOVE_300;
import static org.poo.commands.helperMethods.Constants.SILVER_CASHBACK_ABOVE_300;
import static org.poo.commands.helperMethods.Constants.DEFAULT_CASHBACK_ABOVE_300;

import static org.poo.commands.helperMethods.Constants.GOLD_CASHBACK_ABOVE_100;
import static org.poo.commands.helperMethods.Constants.SILVER_CASHBACK_ABOVE_100;
import static org.poo.commands.helperMethods.Constants.DEFAULT_CASHBACK_ABOVE_100;

import static org.poo.commands.helperMethods.Constants.THRESHOLD_500;
import static org.poo.commands.helperMethods.Constants.THRESHOLD_300;
import static org.poo.commands.helperMethods.Constants.THRESHOLD_100;

public class SpendingThreshold implements StrategyInterface {
    /**
     * Calculate the cashback for a transaction based on the total amount spent by the user.
     * @param transaction the transaction for which the cashback is calculated
     * @param account the account of the user
     * @param user the user for which the cashback is calculated
     * @return the cashback amount
     */
    public double calculateCashback(final Commerciant commerciant, final Transaction transaction,
                                    final Account account, final User user) {
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
        return cashbackPercentage;
    }
}
