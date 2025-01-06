package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

/**
 * Interface for the strategy pattern.
 */
public interface StrategyInterface {
    /**
     * Calculate the cashback for a transaction.
     * @param transaction the transaction for which the cashback is calculated
     * @param account the account of the user
     * @param user the user that made the transaction
     * @return the cashback for the transaction
     */
    double calculateCashback(Transaction transaction, Account account, User user);
}
