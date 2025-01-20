package org.poo.commands.planCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.helperMethods.CountTransactionsHelper;
import org.poo.transactions.Transaction;

public final class AutoUpgrade {
    private AutoUpgrade() {

    }

    /**
     * Upgrades the plan of a user automatically
     * @param user the user that wants to upgrade the plan
     * @param account the account of the user
     * @param command the command that contains the timestamp
     * @param graph the graph that contains the exchange rates
     */
    public static void autoUpgrade(final User user, final Account account,
                                    final Command command, final Graph graph) {
        boolean upgradeCheck = CountTransactionsHelper.countTransactions(user, graph);

        if (upgradeCheck) {
            if (user.getServicePlan().equals("silver")) {
                user.setServicePlan("gold");
                Transaction transactionUpgrade =
                        new Transaction.TransactionBuilder(command.getTimestamp(),
                                "Upgrade plan", "upgradePlan")
                                .upgradePlan("gold", account.getIban())
                                .build();
                account.getTransactions().add(transactionUpgrade);
            }
        }
    }
}
