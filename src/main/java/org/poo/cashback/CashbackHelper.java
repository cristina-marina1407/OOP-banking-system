package org.poo.cashback;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public final class CashbackHelper {
    private static final int FOOD_THRESOLD = 2;
    private static final double FOOD_CASHBACK = 0.02;
    private static final int CLOTHES_THRESOLD = 5;
    private static final double CLOTHES_CASHBACK = 0.05;
    private static final int TECH_THRESOLD = 10;
    private static final double TECH_CASHBACK = 0.10;

    private CashbackHelper() {

    }

    /**
     * Method that applies the cashback strategy for a transaction
     * @param commerciantToPay the commerciant that the user is paying
     * @param account the account of the user
     * @param category the category of the transaction
     * @param transaction the transaction that the user is making
     * @param user the user that is making the transaction
     * @param graph the graph that contains the exchange rates
     * @param command the command that the user is making
     */
    public static void applyCashback(final Commerciant commerciantToPay, final Account account,
                                     final String category, final Transaction transaction,
                                     final User user, final Graph graph, final Command command, final List<User> users) {
        /* apply the number of transaction cashback strategy */
//        boolean food, clothes, tech;
//        if (category.equals("Food") && account.getReceivedFoodCashback()) {
//            System.out.println("am primit cashback la food account " + account.getIban());
//            food = true;
//        }
//
//        if (category.equals("Tech") && account.getReceivedTechCashback()) {
//            System.out.println("am primit cashback la tech account " + account.getIban());
//            tech = true;
//        }
//
//        if (category.equals("Clothes") && account.getReceivedClothesCashback()) {
//            System.out.println("am primit cashback la clothes account " + account.getIban());
//            clothes = true;
//        }

        System.out.println("categoria curenta " + category + account.getActiveDiscountByCategory());
        if (account.hasCashbackForCategory(category) && !account.hasUsedCashbackForCategory(category)) {
            double cashback = account.getCashbackForCategory(category);
            System.out.println("aplic cashback nr of transactions " + cashback + " cont " + account.getIban());

            account.setBalance(account.getBalance() + cashback * transaction.getAmount());
            account.removeCashbackForCategory(category);
            account.addReceivedCashbackCategory(category);
        } else if (commerciantToPay.getCashbackStrategy().equals("nrOfTransactions")) {
            NrOfTransactions nrOfTransactions = new NrOfTransactions();
            if (nrOfTransactions != null) {
                double cashback = nrOfTransactions.calculateCashback(commerciantToPay, transaction, account, user);
                if (cashback > 0) {
                    account.addCashbackForCategory(category, cashback);
                    System.out.println("creez cashback nr of transactions " + cashback + " cont " + account.getIban());
                }
            }
        }

        /* apply the spending threshold cashback strategy */
        if (commerciantToPay.getCashbackStrategy().equals("spendingThreshold")) {
            /* convert the amount to RON and add it to the total spent in RON*/
            if (command.getCurrency() == null) {
                account.addToTotalSpentRON(command.getAmount());
                SpendingThreshold spendingThreshold = new SpendingThreshold();
                if (account.getType().equals("business")) {
                    User owner = FindHelper.findUser(users, account.getOwner());
                    double cashback = spendingThreshold.calculateCashback(commerciantToPay, transaction, account, owner);
                    System.out.println("total spent "  + account.getTotalSpentRON() +  "procent cashback: " + cashback + " cashback " + cashback * transaction.getAmount() + " cont " + account.getIban());
                    account.setBalance(account.getBalance() + cashback * transaction.getAmount());
                    return;
                }
                double cashback = spendingThreshold.calculateCashback(commerciantToPay, transaction, account, user);
                System.out.println("total spent "  +account.getTotalSpentRON() +  "procent cashback: " + cashback + " cashback " + cashback * transaction.getAmount() + " cont " + account.getIban());
                account.setBalance(account.getBalance() + cashback * transaction.getAmount());
                return;
            }

            double amountInRON = graph.convert(command.getCurrency(), "RON", command.getAmount());
            account.addToTotalSpentRON(amountInRON);


            SpendingThreshold spendingThreshold = new SpendingThreshold();
            if (account.getType().equals("business")) {
                User owner = FindHelper.findUser(users, account.getOwner());
                double cashback = spendingThreshold.calculateCashback(commerciantToPay, transaction, account, owner);
                System.out.println("total spent "  +account.getTotalSpentRON() +  "procent cashback: " + cashback + " cashback " + cashback * transaction.getAmount() + " cont " + account.getIban());
                account.setBalance(account.getBalance() + cashback * transaction.getAmount());
                return;
            }
            double cashback = spendingThreshold.calculateCashback(commerciantToPay, transaction, account, user);
            System.out.println("total spent "  +account.getTotalSpentRON() +  "procent cashback: " + cashback + " cashback " + cashback * transaction.getAmount() + " cont " + account.getIban());
            account.setBalance(account.getBalance() + cashback * transaction.getAmount());
        }
    }
}
