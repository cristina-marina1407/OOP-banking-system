package org.poo.commands.planCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public final class UpgradePlanHelper {
    private UpgradePlanHelper() {

    }

    /**
     * Upgrades the plan of a user
     * @param user the user that wants to upgrade the plan
     * @param account the account of the user
     * @param newPlanType the new plan type
     * @param amount the amount of money needed to upgrade the plan
     * @param currency the currency of the amount
     * @param timestamp the timestamp of the transaction
     * @param graph the graph that contains the exchange rates
     */
    public static void upgradePlan(final User user, final Account account,
                                   final String newPlanType, final double amount,
                                   final String currency, final int timestamp,
                                   final Graph graph) {
        double newAmount = graph.convert("RON", currency, amount);

        /* checks if the user has enough money to pay the fee */
        if (account.getBalance() >= newAmount) {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp,
                            "Upgrade plan", "upgradePlan")
                    .upgradePlan(newPlanType, account.getIban())
                    .build();
            account.getTransactions().add(transaction);
            account.setBalance(account.getBalance() - newAmount);
            user.setServicePlan(newPlanType);

            System.out.println("user " + user.getEmail() + " upgraded plan to " + user.getServicePlan() + " timestamp " + timestamp);
        } else {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp,
                            "Insufficient funds", "upgradePlanError")
                    .upgradePlanError()
                    .build();
            account.getTransactions().add(transaction);
        }
    }
}
