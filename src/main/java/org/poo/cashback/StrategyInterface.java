package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;
import org.poo.transactions.Transaction;

public interface StrategyInterface {
    double calculateCashback(Transaction transaction, Account account, User user);
}
