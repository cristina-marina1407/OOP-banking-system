package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

import java.util.List;

import static org.poo.commands.helperMethods.Constants.NUMBER_OF_PAYMENTS_FOR_UPGRADE;
import static org.poo.commands.helperMethods.Constants.RON_AMOUNT_FOR_COUNT;



public final class CountTransactionsHelper {
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

                /* convert the amount to RON */
                double ronAmount = graph.convert(account.getCurrency(), "RON", amount);

                /* checks if the transaction is of type "payOnline" or "sendMoney"
                 to a commerciant */
                if ((transaction.getType().equals("payOnline")
                    || (transaction.getType().equals("sendMoney")
                    && transaction.getReceiverIban() != null))
                    && ronAmount >= RON_AMOUNT_FOR_COUNT) {
                    count++;
                }

                /* if the number of transactions is greater than or equal to 5, return true */
                if (count >= NUMBER_OF_PAYMENTS_FOR_UPGRADE) {
                    return true;
                }
            }
        }
        return false;
    }

}
