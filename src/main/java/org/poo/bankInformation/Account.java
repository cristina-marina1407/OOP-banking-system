package org.poo.bankInformation;

import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.poo.commands.helperMethods.Constants.DEFAULT_LIMIT;

public class Account {
    private String iban;
    private double balance;
    private double minBalance;
    private String currency;
    private String type;
    private double interestRate;
    private List<Card> cards;
    private List<Transaction> transactions = new ArrayList<>();

    /* associates is a map that contains the role of the associate and the
    list of users with that role */
    private Map<String, List<User>> associates;

    private double spendingLimit;
    private double depositLimit;

    private String owner;

    /* receivedCategoryCashback is a boolean that tells if the user has
     received cashback for a category */
    private boolean receivedFoodCashback;
    private boolean receivedClothesCashback;
    private boolean receivedTechCashback;

    /* transactionCountByCommerciant is a hashmap that contains the number
     of transactions for each commerciant */
    private Map<String, Integer> transactionCountByCommerciant;

    /* activeDiscountByCategory is a hashmap that contains the active
     cashback that will be aplied at the next transaction for each category */
    private Map<String, Double> activeDiscountByCategory;

    private double totalSpentRON;

    /* totalSpentCommerciants is a hashmap that contains the total amount
     spent by each associate at each commerciant for the business report */
    private Map<String, Map<String, Double>> totalSpentCommerciants;

    /* totalSpentByAssociate is a hashmap that contains the total amount
     spent by each associate */
    private Map<String, Double> totalSpentByAssociate;

    /* totalDepositedByAssociate is a hashmap that contains the total amount
     deposited by each associate */
    private Map<String, Double> totalDepositedByAssociate;


    public Account(final AccountBuilder builder) {
       this.iban = builder.iban;
       this.balance = builder.balance;
       this.minBalance = builder.minBalance;
       this.currency = builder.currency;
       this.type = builder.type;
       this.interestRate = builder.interestRate;
       this.cards = builder.cards;
       this.transactions = builder.transactions;

       this.transactionCountByCommerciant = builder.transactionCountByCommerciant;
       this.activeDiscountByCategory = builder.activeDiscountByCategory;
       this.totalSpentRON = builder.totalSpentRON;

       this.owner = builder.owner;
       this.associates = builder.associates;
       this.totalSpentCommerciants = builder.totalSpentCommerciants;
       this.totalSpentByAssociate = builder.totalSpentByAssociate;
       this.totalDepositedByAssociate = builder.totalDepositedByAssociate;
       this.spendingLimit = builder.spendingLimit;
       this.depositLimit = builder.depositLimit;
       this.receivedFoodCashback = builder.receivedFoodCashback;
       this.receivedClothesCashback = builder.receivedClothesCashback;
       this.receivedTechCashback = builder.receivedTechCashback;
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
        private List<Transaction> transactions = new ArrayList<>();

        private double totalSpentRON;
        private Map<String, Double> activeDiscountByCategory = new HashMap<>();
        private Map<String, Integer> transactionCountByCommerciant = new HashMap<>();

        private String owner;
        private double spendingLimit;
        private double depositLimit;
        private boolean receivedFoodCashback = false;
        private boolean receivedClothesCashback = false;
        private boolean receivedTechCashback = false;
        private Map<String, List<User>> associates = new HashMap<>();
        private Map<String, Map<String, Double>> totalSpentCommerciants = new HashMap<>();
        private Map<String, Double> totalSpentByAssociate = new HashMap<>();
        private Map<String, Double> totalDepositedByAssociate = new HashMap<>();

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
         * Sets the owner and the deposit and spending limits for business accounts.
         * @param givenOwner the owner of the account
         * @param graph the graph used for currency conversion
         */
        public AccountBuilder business(final String givenOwner, final Graph graph) {
            if (type.equals("business")) {
                this.owner = givenOwner;
                double newAmount = graph.convert("RON", currency, DEFAULT_LIMIT);
                this.depositLimit = newAmount;
                this.spendingLimit = newAmount;
            } else {
                throw new IllegalArgumentException("Owner is only "
                                                    + "applicable for business accounts");
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
     * checks if the user is an employee of the account
     * @param email the email of the user
     * @return true if the user is an employee, false otherwise
     */
    public boolean isEmployee(final String email) {
        if (this.owner.equals(email)) {
            return false;
        }

        if (associates.containsKey("employee")) {
            for (User user : associates.get("employee")) {
                if (user.getEmail().equals(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if the user is an associate of the account
     * @param email the email of the user
     * @return true if the user is an associate, false otherwise
     */
    public boolean isAssociate(final String email) {
        if (this.owner.equals(email)) {
            return false;
        }

        for (List<User> users : associates.values()) {
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if the account has a cashback for a category
     * @param category the category of the commerciant (Food, Clothes, Tech)
     * @return
     */
    public boolean hasCashbackForCategory(final String category) {
        return activeDiscountByCategory.containsKey(category);
    }

    /**
     * checks if the account has used the cashback for a category
     * @param category the category of the commerciant (Food, Clothes, Tech)
     * @return
     */
    public boolean hasUsedCashbackForCategory(final String category) {
        switch (category) {
            case "Food":
                return receivedFoodCashback;
            case "Clothes":
                return receivedClothesCashback;
            case "Tech":
                return receivedTechCashback;
            default:
                return false;
        }
    }

    /**
     * adds the cashback for a category
     @param category the category of the commerciant (Food, Clothes, Tech)
     */
    public void addReceivedCashbackCategory(final String category) {
        switch (category) {
            case "Food":
                receivedFoodCashback = true;
                break;
            case "Clothes":
                receivedClothesCashback = true;
                break;
            case "Tech":
                receivedTechCashback = true;
                break;
            default:
                break;
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
     * Adds the cashback for a category
     @param category the category of the commerciant (Food, Clothes, Tech)
     * @param cashback the cashback to add
     */
    public void addCashbackForCategory(final String category, final double cashback) {
        activeDiscountByCategory.put(category, cashback);
    }

    /**
     * Adds an associate to the account
     * @param role the role of the associate
     * @param user the user to add
     */
    public void addAssociate(final String role, final User user) {
        /* the owner of the account cannot be an associate */
        if (this.owner.equals(user.getEmail())) {
            return;
        }

        /* checks if the user is already an associate */
        for (Map.Entry<String, List<User>> entry : associates.entrySet()) {
            if (entry.getValue().contains(user)) {
                return;
            }
        }

        /* adds the associate to the account */
        if (associates.containsKey(role)) {
            List<User> users = associates.get(role);
            users.add(user);
        } else {
            List<User> users = new ArrayList<>();
            users.add(user);
            associates.put(role, users);
        }
    }

    /**
     * Adds the spending of an associate at a commerciant
     * @param commerciant the commerciant where the associate spent money
     * @param userEmail the email of the associate
     * @param amount the amount spent
     */
    public void addSpending(final String commerciant, final String userEmail,
                            final double amount) {
        Map<String, Double> userSpending = null;

        /* checks if the commerciant is already in the hashmap */
        for (Map.Entry<String, Map<String, Double>> entry
             : totalSpentCommerciants.entrySet()) {
            if (entry.getKey() == null && commerciant == null) {
                userSpending = entry.getValue();
                break;
            } else if (entry.getKey() != null
                       && entry.getKey().equals(commerciant)) {
                userSpending = entry.getValue();
                break;
            }
        }

        /* creates the hashmap of the user if it doesn't exist */
        if (userSpending == null) {
            userSpending = new HashMap<>();
            totalSpentCommerciants.put(commerciant, userSpending);
        }

        Double currentAmount = null;
        for (Map.Entry<String, Double> entry : userSpending.entrySet()) {
            if (entry.getKey() == null && userEmail == null) {
                currentAmount = entry.getValue();
                break;
            } else if (entry.getKey() != null
                       && entry.getKey().equals(userEmail)) {
                currentAmount = entry.getValue();
                break;
            }
        }

        /* adds the amount spent by the associate */
        if (currentAmount == null) {
            userSpending.put(userEmail, amount);
        } else {
            userSpending.put(userEmail, currentAmount + amount);
        }
    }

    /**
     * Removes the cashback for a category
     * @param category the category of the commerciant (Food, Clothes, Tech)
     */
    public void removeCashbackForCategory(final String category) {
        activeDiscountByCategory.remove(category);
    }

    /**
     * Updates the number of transactions for a commerciant
     * @param commerciant the commerciant where the transaction was made
     */
    public void updateNrOfTransactions(final String commerciant) {
        if (this.transactionCountByCommerciant.containsKey(commerciant)) {
            int newNumber = this.transactionCountByCommerciant.get(commerciant) + 1;
            this.transactionCountByCommerciant.put(commerciant, newNumber);
        } else {
            this.transactionCountByCommerciant.put(commerciant, 1);
        }
    }

    /**
     * Updates the total spent by an associate
     * @param userEmail the email of the associate
     * @param amount the amount spent
     */
    public void updateTotalSpentByAssociate(final String userEmail,
                                            final double amount) {
        if (totalSpentByAssociate.containsKey(userEmail)) {
            double currentAmount = totalSpentByAssociate.get(userEmail);
            totalSpentByAssociate.put(userEmail, currentAmount + amount);
        } else {
            totalSpentByAssociate.put(userEmail, amount);
        }
    }

    /**
     * Updates the total deposited by an associate
     * @param userEmail the email of the associate
     * @param amount the amount deposited
     */
    public void updateTotalDepositedByAssociate(final String userEmail,
                                                final double amount) {
        if (totalDepositedByAssociate.containsKey(userEmail)) {
            double currentAmount = totalDepositedByAssociate.get(userEmail);
            totalDepositedByAssociate.put(userEmail, currentAmount + amount);
        } else {
            totalDepositedByAssociate.put(userEmail, amount);
        }
    }

    /**
     * @return the number of transactions for a commerciant
     */
    public int getNrOfTransactions(final String commerciant) {
        if (transactionCountByCommerciant.containsKey(commerciant)) {
            return transactionCountByCommerciant.get(commerciant);
        }
        return 0;
    }

    /**
     * @return the cashback for a category
     */
    public double getCashbackForCategory(final String category) {
        if (activeDiscountByCategory.containsKey(category)) {
            return activeDiscountByCategory.get(category);
        }
        return 0;
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
    public Map<String, Integer> getTransactionCountByCommerciant() {
        return transactionCountByCommerciant;
    }

    /**
     * @param transactionCountByCommerciant the hashmap with the transactions
     count by category to set
     */
    public void setTransactionCountByCommerciant(final Map<String,
            Integer> transactionCountByCommerciant) {
        this.transactionCountByCommerciant = transactionCountByCommerciant;
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

    /**
     * @return the associates
     */
    public Map<String, List<User>> getAssociates() {
        return associates;
    }

    /**
     * @param associates the associates to set
     */
    public void setAssociates(final Map<String, List<User>> associates) {
        this.associates = associates;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * @return the spending limit
     */
    public double getSpendingLimit() {
        return spendingLimit;
    }

    /**
     * @param spendingLimit the spending limit to set
     */
    public void setSpendingLimit(final double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    /**
     * @return the deposit limit
     */
    public double getDepositLimit() {
        return depositLimit;
    }

    /**
     * @param depositLimit the deposit limit to set
     */
    public void setDepositLimit(final double depositLimit) {
        this.depositLimit = depositLimit;
    }

    /**
     * @return the total spent by commerciants
     */
    public Map<String, Map<String, Double>> getTotalSpentCommerciants() {
        return totalSpentCommerciants;
    }

    /**
     * @param totalSpentCommerciants the total spent by commerciants to set
     */
    public void setTotalSpentCommerciants(final Map<String, Map<String, Double>>
                                          totalSpentCommerciants) {
        this.totalSpentCommerciants = totalSpentCommerciants;
    }

    /**
     * @return the total spent by associate
     */
    public Map<String, Double> getTotalSpentByAssociate() {
        return totalSpentByAssociate;
    }

    /**
     * @param totalSpentByAssociate the total spent by associate to set
     */
    public void setTotalSpentByAssociate(final Map<String, Double> totalSpentByAssociate) {
        this.totalSpentByAssociate = totalSpentByAssociate;
    }

    /**
     * @return the total deposited by associate
     */
    public Map<String, Double> getTotalDepositedByAssociate() {
        return totalDepositedByAssociate;
    }

    /**
     * @param totalDepositedByAssociate the total deposited by associate to set
     */
    public void setTotalDepositedByAssociate(final Map<String, Double> totalDepositedByAssociate) {
        this.totalDepositedByAssociate = totalDepositedByAssociate;
    }

    /**
     * @return the active discount by category
     */
    public Map<String, Double> getActiveDiscountByCategory() {
        return activeDiscountByCategory;
    }

    /**
     * @param activeDiscountByCategory the active discount by category to set
     */
    public void setActiveDiscountByCategory(final Map<String, Double> activeDiscountByCategory) {
        this.activeDiscountByCategory = activeDiscountByCategory;
    }

    /**
     * @return the received cashback for food
     */
    public boolean getReceivedFoodCashback() {
        return receivedFoodCashback;
    }

    /**
     * @param receivedFoodCashback the received cashback for food to set
     */
    public void setReceivedFoodCashback(final boolean receivedFoodCashback) {
        this.receivedFoodCashback = receivedFoodCashback;
    }

    /**
     * @return the received cashback for clothes
     */
    public boolean getReceivedClothesCashback() {
        return receivedClothesCashback;
    }

    /**
     * @param receivedClothesCashback the received cashback for clothes to set
     */
    public void setReceivedClothesCashback(final boolean receivedClothesCashback) {
        this.receivedClothesCashback = receivedClothesCashback;
    }

    /**
     * @return the received cashback for tech
     */
    public boolean getReceivedTechCashback() {
        return receivedTechCashback;
    }

    /**
     * @param receivedTechCashback the received cashback for tech to set
     */
    public void setReceivedTechCashback(final boolean receivedTechCashback) {
        this.receivedTechCashback = receivedTechCashback;
    }

    /**
     * @return if the cashback for food was received
     */
    public boolean isReceivedFoodCashback() {
        return receivedFoodCashback;
    }

    /**
     * @return if the cashback for clothes was received
     */
    public boolean isReceivedClothesCashback() {
        return receivedClothesCashback;
    }

    /**
     * @return if the cashback for tech was received
     */
    public boolean isReceivedTechCashback() {
        return receivedTechCashback;
    }
}
