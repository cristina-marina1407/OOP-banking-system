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
                                  final Account classicAccount, double newAmount) {
        Transaction transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                "Savings withdrawal", "withdrawSavings").withdrawSavings(command.getAmount(),
                command.getAccount(), classicAccount.getIban()).build();
        savingsAccount.getTransactions().add(transaction);
        classicAccount.getTransactions().add(transaction);
        //System.out.println("here " + "new amount: " + newAmount + " balance: " + classicAccount.getBalance() + " amount: " + command.getAmount() + " balance savings: " + savingsAccount.getBalance());
        savingsAccount.setBalance(savingsAccount.getBalance() - command.getAmount());
        classicAccount.setBalance(classicAccount.getBalance() + newAmount);
        //System.out.println("here " + "new amount: " + newAmount + " balance: " + classicAccount.getBalance() + " amount: " + command.getAmount() + " balance savings: " + savingsAccount.getBalance());

    }
}
