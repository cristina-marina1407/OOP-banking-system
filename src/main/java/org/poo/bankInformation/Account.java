package org.poo.bankInformation;

import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    private String iban;
    private double balance;
    private double minBalance;
    private String currency;
    private String type;
    private double interestRate;
    private List<Card> cards;
    private List<Transaction> transactions = new ArrayList<>();

    private Map<String, Integer> transactionCountByCategory;
    private Map<String, Double> cashbackReceivedByCategory;

    private double totalSpentRON;


    public Account(final AccountBuilder builder) {
       this.iban = builder.iban;
       this.balance = builder.balance;
       this.minBalance = builder.minBalance;
       this.currency = builder.currency;
       this.type = builder.type;
       this.interestRate = builder.interestRate;
       this.cards = builder.cards;

       this.transactionCountByCategory = builder.transactionCountByCategory;
       this.cashbackReceivedByCategory = builder.cashbackReceivedByCategory;

        this.totalSpentRON = builder.totalSpentRON;
    }

    /**
     * Builder class for constructing Account objects.
     */
    public static class AccountBuilder {
        private String iban;
        private double balance;
        private double minBalance;
        private String currency;
        private String type;
        private double interestRate;
        private List<Card> cards = new ArrayList<>();

        private Map<String, Integer> transactionCountByCategory = new HashMap<>();
        private Map<String, Double> cashbackReceivedByCategory = new HashMap<>();

        private double totalSpentRON;

        /**
         * Constructs an AccountBuilder with the specified currency and type.
         * @param currency the currency of the account
         * @param type the type of the account
         */
        public AccountBuilder(final String currency, final String type) {
            this.iban = Utils.generateIBAN();
            this.currency = currency;
            this.balance = 0;
            this.minBalance = 0;
            this.type = type;
            this.interestRate = 0;
            this.totalSpentRON = 0;
        }

        /**
         * Sets the interest rate for savings accounts.
         * @param interest the interest rate to set
         * @return the updated AccountBuilder
         * @throws IllegalArgumentException if the account is not a savings account
         */
        public AccountBuilder savings(final double interest) {
            if (type.equals("savings")) {
                this.interestRate = interest;
            } else {
                throw new IllegalArgumentException("Interest rate is only "
                                                    + "applicable for savings accounts");
            }
            return this;
        }

        /**
         * Builds and returns an Account object.
         * @return the constructed Account object
         */
        public Account build() {
            return new Account(this);
        }
    }


    /**
     * Updates the cashback amount received for a specific category
     * @param category is Food, Clothes or Tech
     * @param cashback is the cashback received
     */
    public void addCashbackReceived(final String category,
                                    final double cashback) {
        if (cashbackReceivedByCategory.containsKey(category)) {
            double currentCashback =
                    cashbackReceivedByCategory.get(category);
            cashbackReceivedByCategory.put(category,
                    currentCashback + cashback);
        } else {
            cashbackReceivedByCategory.put(category, cashback);
        }
    }

    /**
     * Increments the transaction count for a specific category
     * @param category is Food, Clothes or Tech
     */
    public void incrementTransactionCount(final String category) {
        if (transactionCountByCategory.containsKey(category)) {
            transactionCountByCategory.put(category,
                    transactionCountByCategory.get(category) + 1);
        } else {
            transactionCountByCategory.put(category, 1);
        }
    }

    /**
     * Returns the number of transactions for a specific category
     * @param category is Food, Clothes or Tech
     * @return the number of transactions for the category
     */
    public int getTransactionCountForCategory(final String category) {
        if (transactionCountByCategory.containsKey(category)) {
            return transactionCountByCategory.get(category);
        } else {
            return 0;
        }
    }

    /**
     * Adds the spent amount in RON to the total spent in RON
     * @param amountInRON the amount to add to the total spent in RON
     */
    public void addToTotalSpentRON(final double amountInRON) {
        this.totalSpentRON += amountInRON;
    }

    /**
     * @return the iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * @param iban the iban to set
     */
    public void setIban(final String iban) {
        this.iban = iban;
    }

    /**
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(final double balance) {
        this.balance = balance;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @return the cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * @param cards the cards to set
     */
    public void setCards(final List<Card> cards) {
        this.cards = cards;
    }

    /**
     * @return the interest rate
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * @param interestRate the interest rate to set
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * @return the transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @param transactions the transactions to set
     */
    public void setTransactions(final List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * @return the minimum balance
     */
    public double getMinBalance() {
        return minBalance;
    }

    /**
     * @param minBalance the minimum balance to set
     */
    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    /**
     * @return the hashmap with the transactions count by category
     */
    public Map<String, Integer> getTransactionCountByCategory() {
        return transactionCountByCategory;
    }

    /**
     * @param transactionCountByCategory the hashmap with the transactions
     count by category to set
     */
    public void setTransactionCountByCategory(final Map<String,
            Integer> transactionCountByCategory) {
        this.transactionCountByCategory = transactionCountByCategory;
    }

    /**
     * @return the hashmap with the cashback received by category
     */
    public Map<String, Double> getCashbackReceivedByCategory() {
        return cashbackReceivedByCategory;
    }

    /**
     * @param cashbackReceivedByCategory the hashmap with the cashback
     received by category to set
     */
    public void setCashbackReceivedByCategory(final Map<String,
            Double> cashbackReceivedByCategory) {
        this.cashbackReceivedByCategory = cashbackReceivedByCategory;
    }

    /**
     * @return the total spent in RON
     */
    public double getTotalSpentRON() {
        return totalSpentRON;
    }

    /**
     * @param totalSpentRON the total spent in RON to set
     */
    public void setTotalSpentRON(final double totalSpentRON) {
        this.totalSpentRON = totalSpentRON;
    }
}
