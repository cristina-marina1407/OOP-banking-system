package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

import java.util.List;

public final class CountTransactionsHelper {
    private static final double NUMBER_OF_PAYMENTS_FOR_UPGRADE = 5;

    private CountTransactionsHelper() {

    }

    /**
     * Count the number of transactions of type "payOnline" in the list of users
     * @param users list of users
     * @return true if the number of transactions is greater than or equal to 5, false otherwise
     */
    public static boolean countTransactions(final List<User> users) {
        int count = 0;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                List<Transaction> transactions = account.getTransactions();
                for (Transaction transaction : transactions) {
                    if (count >= NUMBER_OF_PAYMENTS_FOR_UPGRADE) {
                        return true;
                    }

                    if (transaction.getType().equals("payOnline")) {
                        count++;
                    }

                }
            }
        }
        return false;
    }

}
