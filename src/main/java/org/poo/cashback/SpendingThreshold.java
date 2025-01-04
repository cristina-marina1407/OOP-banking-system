package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public class SpendingThreshold implements StrategyInterface {
    public double calculateCashback(Transaction transaction, Account account, User user) {
        double totalSpentRON = account.getTotalSpentRON();
        double cashbackPercentage = 0.0;

        if (totalSpentRON >= 500) {
            if (user.getServicePlan().equals("gold")) {
                cashbackPercentage = 0.7;
            } else if (user.getServicePlan().equals("silver")) {
                cashbackPercentage = 0.5;
            } else {
                cashbackPercentage = 0.25;
            }
        } else if (totalSpentRON >= 300) {
            if (user.getServicePlan().equals("gold")) {
                cashbackPercentage = 0.55;
            } else if (user.getServicePlan().equals("silver")) {
                cashbackPercentage = 0.4;
            } else {
                cashbackPercentage = 0.2;
            }
        } else if (totalSpentRON >= 100) {
            if (user.getServicePlan().equals("gold")) {
                cashbackPercentage = 0.5;
            } else if (user.getServicePlan().equals("silver")) {
                cashbackPercentage = 0.3;
            } else {
                cashbackPercentage = 0.1;
            }
        }

        return transaction.getAmount() * cashbackPercentage / 100;
    }
}
