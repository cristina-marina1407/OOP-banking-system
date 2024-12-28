package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

import java.util.List;

public class CountTransactionsHelper {
    private CountTransactionsHelper() {

    }

    public static boolean countTransactions(final List<User> users) {
        int count = 0;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                List<Transaction> transactions = account.getTransactions();
                for (Transaction transaction : transactions) {
                    if (count >= 5) {
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
