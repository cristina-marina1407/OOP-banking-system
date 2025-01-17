package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

import java.util.List;

public final class CountTransactionsHelper {
    private static final double NUMBER_OF_PAYMENTS_FOR_UPGRADE = 5;

    private CountTransactionsHelper() {

    }

    /**
     * Count the number of transactions of type "payOnline" in the list of users
     * @param user the user for which the transactions are counted
     * @return true if the number of transactions is greater than or equal to 5, false otherwise
     */
    public static boolean countTransactions(final User user, final Graph graph) {
        int count = 0;
        if (!user.getServicePlan().equals("silver")) {
            return false;
        }
        for (Account account : user.getAccounts()) {
            List<Transaction> transactions = account.getTransactions();
            for (Transaction transaction : transactions) {
                double amount = transaction.getAmount();
                double ronAmount = graph.convert(account.getCurrency(), "RON", amount);

                if ((transaction.getType().equals("payOnline")
                    || (transaction.getType().equals("sendMoney") && transaction.getReceiverIban() != null)) &&
                        ronAmount >= 300) {
                    count++;
                    System.out.println("amountTransaction " + transaction.getAmount() + " currency " + transaction.getCurrency() + " iban " + transaction.getCommerciant());
                }

                if (count >= NUMBER_OF_PAYMENTS_FOR_UPGRADE) {
                    return true;
                }
            }
        }
        return false;
    }

}
