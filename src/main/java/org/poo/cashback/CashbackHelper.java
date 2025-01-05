package org.poo.cashback;

import org.poo.bankInformation.*;
import org.poo.transactions.Transaction;

public class CashbackHelper {
    public static void applyCashback(Commerciant commerciantToPay, Account sender, String category,
                                     Transaction senderTransaction, User senderUser, Graph graph,
                                     Command command) {
        if (commerciantToPay.getCashbackStrategy().equals("nrOfTransactions")) {
            sender.incrementTransactionCount(category);

            NrOfTransactions nrOfTransactions = null;

            if (category.equals("Food")) {
                nrOfTransactions = new NrOfTransactions(2, 0.02, "Food");
            } else if (category.equals("Clothes")) {
                nrOfTransactions = new NrOfTransactions(5, 0.05, "Clothes");
            } else if (category.equals("Tech")) {
                nrOfTransactions = new NrOfTransactions(10, 0.10, "Tech");
            }

            if (nrOfTransactions != null) {
                double cashback = nrOfTransactions.calculateCashback(senderTransaction, sender, senderUser);

                if (cashback > 0) {
                    sender.addCashbackReceived(category, cashback);
                    sender.setBalance(sender.getBalance() + cashback);

                    /*formatare*/
                    String formatted = String.format("%.2f", sender.getBalance());
                    double formattedBalance = Double.parseDouble(formatted);
                    sender.setBalance(formattedBalance);

                }
            }
        } else if (commerciantToPay.getCashbackStrategy().equals("spendingThreshold")) {
            double amountInRON = graph.convert(command.getCurrency(), "RON", command.getAmount());
            sender.addToTotalSpentRON(amountInRON);
            SpendingThreshold spendingThreshold = new SpendingThreshold();
            double cashback = spendingThreshold.calculateCashback(senderTransaction, sender, senderUser);
            sender.setBalance(sender.getBalance() + cashback);

            /*formatare*/
            String formatted = String.format("%.2f", sender.getBalance());
            double formattedBalance = Double.parseDouble(formatted);
            sender.setBalance(formattedBalance);

        }
    }
}
