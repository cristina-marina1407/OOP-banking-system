package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public class UpgradePlanHelper {
    private UpgradePlanHelper() {

    }

    public static void upgradePlan(User user, Account account, String newPlanType,
                                double amount, String currency, int timestamp,
                                Graph graph) {
        double newAmount = graph.convert("RON", currency, amount);
        if (account.getBalance() >= newAmount) {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp, "Upgrade plan", "upgradePlan")
                    .upgradePlan(newPlanType, account.getIban())
                    .build();
            account.getTransactions().add(transaction);
            account.setBalance(account.getBalance() - newAmount);
            user.setServicePlan(newPlanType);
        } else {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp, "Insufficient funds", "upgradePlanError")
                    .upgradePlanError()
                    .build();
            account.getTransactions().add(transaction);
        }
    }
}
