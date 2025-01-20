package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;


public final class SplitPaymentHelper {
    private SplitPaymentHelper() {

    }

    /**
     * Processes the split payment
     *
     * @param splitPaymentObject the split payment to be processed
     * @param splitAccounts      the accounts that are involved in the split payment
     * @param amount             the amount of the split payment
     * @param amounts            the amounts that each account has to pay
     * @param splitPaymentType   the type of the split payment
     * @param users              the list of users
     * @param graph              the graph that contains the exchange rates
     */
    public static void processPaymentHelper(final SplitPaymentObject splitPaymentObject,
                                      final List<String> splitAccounts,
                                      final double amount, final List<Double> amounts,
                                      final String splitPaymentType, final List<User> users,
                                      final Graph graph) {

        int checkAccounts = 0;
        List<String> insufficientAccounts = new ArrayList<>();

        /* check if the accounts have enough funds to make the payment */
        for (int i = 0; i < splitAccounts.size(); i++) {
            String iban = splitAccounts.get(i);
            double customAmount = amounts.get(i);
            for (User user : users) {
                Account account = FindHelper.findAccount(user.getAccounts(), iban);
                if (account != null) {
                    double newAmount = graph.convert(splitPaymentObject.getCommand().getCurrency(),
                            account.getCurrency(), customAmount);
                    if (account.getBalance() < newAmount) {
                        insufficientAccounts.add(iban);
                        checkAccounts++;
                    }
                }
            }
        }

        for (int i = 0; i < splitAccounts.size(); i++) {
            String iban = splitAccounts.get(i);
            double customAmount = amounts.get(i);
            for (User user : users) {
                Account account = FindHelper.findAccount(user.getAccounts(), iban);
                if (account != null) {
                    /* convert the amount to the currency of the account */
                    double newAmount = graph.convert(splitPaymentObject.getCommand().getCurrency(),
                            account.getCurrency(), customAmount);
                    if (checkAccounts > 0) {
                        Transaction transaction;
                        String formattedAmount = String.format("%.2f %s", amount,
                                splitPaymentObject.getCommand().getCurrency());

                        /* if an account doesn't have enough funds, the payment is not done */
                        transaction = new Transaction.TransactionBuilder(splitPaymentObject
                                .getCommand().getTimestamp(),
                                "Split payment of " + formattedAmount, "splitPayment")
                                .splitPaymentError(splitPaymentObject.getCommand().getCurrency(),
                                        newAmount, splitAccounts,
                                        insufficientAccounts.get(0),
                                        splitPaymentType, amounts,
                                        "Account " + insufficientAccounts.get(0)
                                                + " has insufficient funds for a split payment.")
                                .build();
                        account.getTransactions().add(transaction);
                        /* if the accounts have enough funds, the payment is done */
                    } else {
                        account.setBalance(account.getBalance() - newAmount);
                        /* creates the transaction for the successful payment */
                        Transaction transaction;
                        String formattedAmount = String.format("%.2f %s", amount,
                                splitPaymentObject.getCommand().getCurrency());
                        transaction = new Transaction.TransactionBuilder(splitPaymentObject
                                .getCommand().getTimestamp(),
                                "Split payment of " + formattedAmount, "splitPayment")
                                .splitPayment(splitPaymentObject.getCommand().getCurrency(),
                                        customAmount, splitAccounts, splitPaymentType, amounts)
                                .build();
                        account.getTransactions().add(transaction);
                    }
                }
            }
        }
    }
}
