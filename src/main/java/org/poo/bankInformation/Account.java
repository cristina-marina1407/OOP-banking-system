package org.poo.bankInformation;

import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String iban;
    private double balance;
    private double minBalance;
    private String currency;
    private String type;
    private double interestRate;
    private List<Card> cards;
    private List<Transaction> transactions = new ArrayList<>();

    public Account(final AccountBuilder builder) {
       this.iban = builder.iban;
       this.balance = builder.balance;
       this.minBalance = builder.minBalance;
       this.currency = builder.currency;
       this.type = builder.type;
       this.interestRate = builder.interestRate;
       this.cards = builder.cards;
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
}
