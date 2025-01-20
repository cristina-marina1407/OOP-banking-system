package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.transactions.Transaction;

public final class WithdrawSavingsHelper {
    private WithdrawSavingsHelper() {

    }

    /**
     * Withdraw the savings from the savings account and add them to the classic account.
     * @param command the command
     * @param savingsAccount the savings account
     * @param classicAccount the classic account
     */
    public static void withdrawal(final Command command, final Account savingsAccount,
                                  final Account classicAccount, final double newAmount) {
        Transaction transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                "Savings withdrawal", "withdrawSavings").withdrawSavings(command.getAmount(),
                command.getAccount(), classicAccount.getIban()).build();
        savingsAccount.getTransactions().add(transaction);
        classicAccount.getTransactions().add(transaction);
        savingsAccount.setBalance(savingsAccount.getBalance() - command.getAmount());
        classicAccount.setBalance(classicAccount.getBalance() + newAmount);
    }

    /**
     * Create a transaction for the error of the withdrawal of the savings.
     * @param command the command
     * @param account the account
     * @param error the error message
     */
    public static void withdrawSavingsErrorTransaction(final Command command,
                                                       final Account account,
                                                       final String error) {
        Transaction transaction = new Transaction.TransactionBuilder(
                command.getTimestamp(),
                error,
                "withdrawSavingsError")
                .withdrawSavingsError()
                .build();
        account.getTransactions().add(transaction);
    }
}
