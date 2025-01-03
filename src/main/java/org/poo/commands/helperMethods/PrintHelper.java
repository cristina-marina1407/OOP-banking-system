package org.poo.commands.helperMethods;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.transactions.PrintTransactionsJson;
import org.poo.transactions.Transaction;

public final class PrintHelper {
    private PrintHelper() {

    }

    /**
     * Print the transaction by type
     * @param transaction the transaction to print
     * @param printTransactionsJSON the class that prints the transaction
     * @return the transaction node for the output
     */
    public static ObjectNode printParsing(final Transaction transaction,
                                          final PrintTransactionsJson printTransactionsJSON) {
        ObjectNode transactionNode = null;
        switch (transaction.getType()) {
            case "addAccount":
                transactionNode = printTransactionsJSON.printAddAccount();
                break;
            case "sendMoney":
                transactionNode = printTransactionsJSON.printSendMoney();
                break;
            case "sendMoneyError":
                transactionNode = printTransactionsJSON.printSendMoneyError();
                break;
            case "deleteCard":
                transactionNode = printTransactionsJSON.printDeleteCard();
                break;
            case "createCard":
                transactionNode = printTransactionsJSON.printCreateCard();
                break;
            case "payOnlineError":
                transactionNode = printTransactionsJSON.printPayOnlineError();
                break;
            case "payOnline":
                transactionNode = printTransactionsJSON.printPayOnline();
                break;
            case "checkCardStatus":
                transactionNode = printTransactionsJSON.printCheckCardStatus();
                break;
            case "splitPayment":
                transactionNode = printTransactionsJSON.printSplitPayment();
                break;
            case "splitPaymentError":
                transactionNode = printTransactionsJSON.printSplitPaymentError();
                break;
            case "changeInterestRate":
                transactionNode = printTransactionsJSON.printChangeInterestRate();
                break;
            case "deleteAccountError":
                transactionNode = printTransactionsJSON.printDeleteAccountError();
                break;
            case "withdrawSavingsError":
                transactionNode = printTransactionsJSON.printWithdrawSavingsError();
                break;
            case "withdrawSavings":
                transactionNode = printTransactionsJSON.printWithdrawSavings();
                break;
            case "upgradePlanError":
                transactionNode = printTransactionsJSON.printUpgradePlanError();
                break;
            case "upgradePlan":
                transactionNode = printTransactionsJSON.printUpgradePlan();
                break;
            case "cashWithdrawal":
                transactionNode = printTransactionsJSON.printCashWithdrawal();
                break;
            case "cashWithdrawalError":
                transactionNode = printTransactionsJSON.printCashWithdrawalError();
            default:
                break;
        }
        return transactionNode;
    }

}
