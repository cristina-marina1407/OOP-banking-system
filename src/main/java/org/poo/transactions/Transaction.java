package org.poo.transactions;

import java.util.List;

public class Transaction {
    private int timestamp;
    private String description;
    private String type;
    private String senderIban;
    private String receiverIban;
    private String classicAccount;
    private double amount;
    private String transferType;
    private String currency;
    private String account;
    private String card;
    private String cardHolder;
    private String commerciant;
    private List<String> involvedAccounts;
    private String insufficientAccount;

    public Transaction(final TransactionBuilder builder) {
        this.timestamp = builder.timestamp;
        this.description = builder.description;
        this.type = builder.type;
        this.senderIban = builder.senderIban;
        this.receiverIban = builder.receiverIban;
        this.classicAccount = builder.classicAccount;
        this.amount = builder.amount;
        this.transferType = builder.transferType;
        this.currency = builder.currency;
        this.account = builder.account;
        this.card = builder.card;
        this.cardHolder = builder.cardHolder;
        this.commerciant = builder.commerciant;
        this.involvedAccounts = builder.involvedAccounts;
        this.insufficientAccount = builder.insufficientAccount;
    }

    /**
     * Builder class for constructing Transaction objects.
     */
    public static class TransactionBuilder {
        private int timestamp;
        private String description;
        private String type;
        private String senderIban;
        private String receiverIban;
        private String classicAccount;
        private double amount;
        private String transferType;
        private String currency;
        private String account;
        private String card;
        private String cardHolder;
        private String commerciant;
        private List<String> involvedAccounts;
        private String insufficientAccount;

        /**
         * Constructs a TransactionBuilder with the specified timestamp, description and type.
         * @param timestamp the timestamp of the transaction
         * @param description the description of the transaction
         * @param type the type of the transaction
         */
        public TransactionBuilder(final int timestamp, final String description,
                                  final String type) {
            this.timestamp = timestamp;
            this.description = description;
            this.type = type;
        }

        /**
         * Sets the type to addAccount.
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder addAccount() {
            this.type = "addAccount";
            return this;
        }

        /**
         * Sets the type to deleteAccountError.
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder deleteAccountError() {
            this.type = "deleteAccountError";
            return this;
        }

        /**
         * Sets the type to sendMoneyError.
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder sendMoneyError() {
            this.type = "sendMoneyError";
            return this;
        }

        /**
         * Sets the type to sendMoney.
         * @param givenSenderIban the sender iban
         * @param givenReceiverIban the receiver iban
         * @param givenAmount the amount to send
         * @param givenTransferType the type of transfer
         * @param givenCurrency the currency of the transfer
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder sendMoney(final String givenSenderIban,
                                            final String givenReceiverIban,
                                            final double givenAmount,
                                            final String givenTransferType,
                                            final String givenCurrency) {
            this.type = "sendMoney";
            this.senderIban = givenSenderIban;
            this.receiverIban = givenReceiverIban;
            this.amount = givenAmount;
            this.transferType = givenTransferType;
            this.currency = givenCurrency;
            return this;
        }

        /**
         * Sets the type to deleteCard.
         * @param givenAccount the account to delete the card from
         * @param givenCard the card to delete
         * @param givenCardHolder the card holder
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder deleteCard(final String givenAccount, final String givenCard,
                                             final String givenCardHolder) {
            this.type = "deleteCard";
            this.card = givenCard;
            this.account = givenAccount;
            this.cardHolder = givenCardHolder;
            return this;
        }

        /**
         * Sets the type to createCard.
         * @param givenAccount the account to create the card for
         * @param givenCard the card to create
         * @param givenCardHolder the card holder
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder createCard(final String givenAccount, final String givenCard,
                                             final String givenCardHolder) {
            this.type = "createCard";
            this.card = givenCard;
            this.account = givenAccount;
            this.cardHolder = givenCardHolder;
            return this;
        }

        /**
         * Sets the type to payOnlineError.
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder payOnlineError() {
            this.type = "payOnlineError";
            return this;
        }

        /**
         * Sets the type to payOnline.
         * @param givenAmount the amount to pay
         * @param givenCommerciant the commerciant to pay
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder payOnline(final double givenAmount,
                                            final String givenCommerciant) {
            this.type = "payOnline";
            this.amount = givenAmount;
            this.commerciant = givenCommerciant;
            return this;
        }

        /**
         * Sets the type to checkCardStatus.
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder checkStatusCard() {
            this.type = "checkCardStatus";
            return this;
        }

        /**
         * Sets the type to splitPayment.
         * @param givenCurrency the currency of the payment
         * @param givenAmount the amount to pay
         * @param givenInvolvedAccounts the accounts involved in the payment
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder splitPayment(final String givenCurrency,
                                               final double givenAmount,
                                               final List<String> givenInvolvedAccounts) {
            this.type = "splitPayment";
            this.currency = givenCurrency;
            this.amount = givenAmount;
            this.involvedAccounts = givenInvolvedAccounts;
            return this;
        }

        /**
         * Sets the type to splitPaymentError.
         * @param givenCurrency the currency of the payment
         * @param givenAmount the amount to pay
         * @param givenInvolvedAccounts the accounts involved in the payment
         * @param givenInsufficientAccount the account with insufficient funds
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder splitPaymentError(final String givenCurrency,
                                                    final double givenAmount,
                                                    final List<String> givenInvolvedAccounts,
                                                    final String givenInsufficientAccount) {
            this.type = "splitPaymentError";
            this.currency = givenCurrency;
            this.amount = givenAmount;
            this.involvedAccounts = givenInvolvedAccounts;
            this.insufficientAccount = givenInsufficientAccount;
            return this;
        }

        /**
         * Sets the type to changeInterestRate.
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder changeInterestRate() {
            this.type = "changeInterestRate";
            return this;
        }

        /**
         * Sets the type to withdrawSavings.
         * @param givenAmount the amount to withdraw
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder withdrawSavings(final double givenAmount,
                                                  final String givenAccount,
                                                  final String givenClassicAccount) {
            this.type = "withdrawSavings";
            this.amount = givenAmount;
            this.account = givenAccount;
            this.classicAccount = givenClassicAccount;
            return this;
        }

        /**
         * Sets the type to withdrawSavingsError.
         * @return the updated TransactionBuilder
         */
        public TransactionBuilder withdrawSavingsError() {
            this.type = "withdrawSavingsError";
            return this;
        }

        /**
         * Builds the transaction.
         * @return the transaction
         */
        public Transaction build() {
            return new Transaction(this);
        }
    }

    /**
     * @return the timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
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
     * @return the sender iban
     */
    public String getSenderIban() {
        return senderIban;
    }

    /**
     * @param senderIban the sender iban to set
     */
    public void setSenderIban(final String senderIban) {
        this.senderIban = senderIban;
    }

    /**
     * @return the receiver iban
     */
    public String getReceiverIban() {
        return receiverIban;
    }

    /**
     * @param receiverIban the receiver iban to set
     */
    public void setReceiverIban(final String receiverIban) {
        this.receiverIban = receiverIban;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(final double amount) {
        this.amount = amount;
    }

    /**
     * @return the transfer type
     */
    public String getTransferType() {
        return transferType;
    }

    /**
     * @param transferType the transfer type to set
     */
    public void setTransferType(final String transferType) {
        this.transferType = transferType;
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
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(final String account) {
        this.account = account;
    }

    /**
     * @return the card
     */
    public String getCard() {
        return card;
    }

    /**
     * @param card the card to set
     */
    public void setCard(final String card) {
        this.card = card;
    }

    /**
     * @return the card holder
     */
    public String getCardHolder() {
        return cardHolder;
    }

    /**
     * @param cardHolder the card holder to set
     */
    public void setCardHolder(final String cardHolder) {
        this.cardHolder = cardHolder;
    }

    /**
     * @return the commerciant
     */
    public String getCommerciant() {
        return commerciant;
    }

    /**
     * @param commerciant the commerciant to set
     */
    public void setCommerciant(final String commerciant) {
        this.commerciant = commerciant;
    }

    /**
     * @return the involved accounts
     */
    public List<String> getInvolvedAccounts() {
        return involvedAccounts;
    }

    /**
     * @param involvedAccounts the involved accounts to set
     */
    public void setInvolvedAccounts(final List<String> involvedAccounts) {
        this.involvedAccounts = involvedAccounts;
    }

    /**
     * @return the insufficient account
     */
    public String getInsufficientAccount() {
        return insufficientAccount;
    }

    /**
     * @param insufficientAccount the insufficient account to set
     */
    public void setInsufficientAccount(final String insufficientAccount) {
        this.insufficientAccount = insufficientAccount;
    }

    /**
     * @return the classic account
     */
    public String getClassicAccount() {
        return classicAccount;
    }

    /**
     * @param classicAccount the classic account to set
     */
    public void setClassicAccount(final String classicAccount) {
        this.classicAccount = classicAccount;
    }
}
