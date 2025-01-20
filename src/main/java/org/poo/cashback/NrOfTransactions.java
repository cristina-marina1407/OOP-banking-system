package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

import static org.poo.commands.helperMethods.Constants.FOOD_THRESOLD;
import static org.poo.commands.helperMethods.Constants.FOOD_CASHBACK;
import static org.poo.commands.helperMethods.Constants.CLOTHES_THRESOLD;
import static org.poo.commands.helperMethods.Constants.CLOTHES_CASHBACK;
import static org.poo.commands.helperMethods.Constants.TECH_THRESOLD;
import static org.poo.commands.helperMethods.Constants.TECH_CASHBACK;

public class NrOfTransactions implements StrategyInterface {

    /**
     * Calculate the cashback for a transaction based on the number of transactions
     * of the same category that the account has.
     * @param commerciant the commerciant of the transaction
     * @param transaction the transaction for which the cashback is calculated
     * @param account the account of the user
     * @param user the user that made the transaction
     * @return the cashback for the transaction
     */
    public double calculateCashback(final Commerciant commerciant, final Transaction transaction,
                                    final Account account, final User user) {
        int threshold = 0;
        double cashback = 0.0;

        /* set the threshold and cashback for the commerciant's type */
        if (commerciant.getType().equals("Food")) {
            threshold = FOOD_THRESOLD;
            cashback = FOOD_CASHBACK;
        } else if (commerciant.getType().equals("Clothes")) {
            threshold = CLOTHES_THRESOLD;
            cashback = CLOTHES_CASHBACK;
        } else if (commerciant.getType().equals("Tech")) {
            threshold = TECH_THRESOLD;
            cashback = TECH_CASHBACK;
        }

        /* get the number of transactions at this commerciant*/
        int transactionCount = account.getNrOfTransactions(commerciant.getCommerciant());

        /* if the number of transactions is equal to the threshold, return the cashback */
        if (transactionCount == threshold) {
            double amount = transaction.getAmount();
            return cashback;
        }
        return 0;
    }
}
