package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PrintTransactionsJson {
    private Transaction transaction;

    public PrintTransactionsJson(final Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Prints the JSON object for the add account transaction.
     * @return the JSON object
     */
    public ObjectNode printAddAccount() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the delete account error transaction.
     * @return the JSON object
     */
    public ObjectNode printDeleteAccountError() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the send money transaction.
     * @return the JSON object
     */
    public ObjectNode printSendMoney() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("receiverIBAN", transaction.getReceiverIban());
        transactionNode.put("senderIBAN", transaction.getSenderIban());
        transactionNode.put("amount", transaction.getAmount() + " " + transaction.getCurrency());
        transactionNode.put("transferType", transaction.getTransferType());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the send money error transaction.
     * @return the JSON object
     */
    public ObjectNode printSendMoneyError() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the delete card transaction.
     * @return the JSON object
     */
    public ObjectNode printDeleteCard() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("account", transaction.getAccount());
        transactionNode.put("card", transaction.getCard());
        transactionNode.put("cardHolder", transaction.getCardHolder());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the create card transaction.
     * @return the JSON object
     */
    public ObjectNode printCreateCard() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("account", transaction.getAccount());
        transactionNode.put("card", transaction.getCard());
        transactionNode.put("cardHolder", transaction.getCardHolder());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the pay online error transaction.
     * @return the JSON object
     */
    public ObjectNode printPayOnlineError() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the pay online transaction.
     * @return the JSON object
     */
    public ObjectNode printPayOnline() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("amount", transaction.getAmount());
        transactionNode.put("commerciant", transaction.getCommerciant());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the check card status transaction.
     * @return the JSON object
     */
    public ObjectNode printCheckCardStatus() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the split payment transaction.
     * @return the JSON object
     */
    public ObjectNode printSplitPayment() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("amount", transaction.getAmount());
        transactionNode.put("currency", transaction.getCurrency());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());

        ArrayNode involvedAccountsArray = objectMapper.createArrayNode();
        for (String account : transaction.getInvolvedAccounts()) {
            involvedAccountsArray.add(account);
        }
        transactionNode.set("involvedAccounts", involvedAccountsArray);

        return transactionNode;
    }

    /**
     * Prints the JSON object for the split payment error transaction.
     * @return the JSON object
     */
    public ObjectNode printSplitPaymentError() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("amount", transaction.getAmount());
        transactionNode.put("currency", transaction.getCurrency());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());

        ArrayNode involvedAccountsArray = objectMapper.createArrayNode();
        for (String account : transaction.getInvolvedAccounts()) {
            involvedAccountsArray.add(account);
        }

        transactionNode.put("error", "Account " + transaction.getInsufficientAccount()
                            + " has insufficient funds for a split payment.");

        transactionNode.set("involvedAccounts", involvedAccountsArray);

        return transactionNode;
    }

    /**
     * Prints the JSON object for the change interest rate transaction.
     * @return the JSON object
     */
    public ObjectNode printChangeInterestRate() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * Prints the JSON object for withdraw savings the  transaction.
     * @return the JSON object
     */

    public ObjectNode printWithdrawSavings() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("savingsAccountIBAN", transaction.getAccount());
        transactionNode.put("classicAccountIBAN", transaction.getClassicAccount());
        transactionNode.put("amount", transaction.getAmount());
        return transactionNode;
    }

    /**
     * Prints the JSON object for the withdrawal savings error transaction.
     * @return the JSON object
     */
    public ObjectNode printWithdrawSavingsError() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    public ObjectNode printUpgradePlan() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("accountIBAN", transaction.getAccount());
        transactionNode.put("newPlanType", transaction.getNewPlanType());
        return transactionNode;
    }

    public ObjectNode printUpgradePlanError() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    public ObjectNode printCashWithdrawal() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("amount", transaction.getAmount());
        return transactionNode;
    }

    public ObjectNode printCashWithdrawalError() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    public ObjectNode printAddInterest() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("amount", transaction.getInterest());
        transactionNode.put("currency", transaction.getCurrency());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        return transactionNode;
    }

    /**
     * @return the transaction
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }
}
