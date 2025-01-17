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

    /* adaugat chestii cashback */

    /* nrOfTransctions */
    private Map<String, Integer> transactionCountByCommerciant;

    private Map<String, Double> activeDiscountByCategory;
    /* spending thresold */
    private double totalSpentRON;

    /* chestii printare */
    /* cat a cheltuit fiecare user la un comerciant */
    private Map<String, Map<String, Double>> totalSpentCommerciants;

    /* cat a depozitat fiecare user in cont */
    /* cat a cheltuit fiecare user in cont */
    private Map<String, Double> totalSpentByAssociate;
    private Map<String, Double> totalDepositedByAssociate;

    //private Map<String, String> associates;
    private Map<String, List<User>> associates;

    private double spendingLimit;
    private double depositLimit;
    private String owner;

    private boolean receivedFoodCashback;
    private boolean receivedClothesCashback;
    private boolean receivedTechCashback;

    private static final int DEFAULT_LIMIT = 500;


    public Account(final AccountBuilder builder) {
       this.iban = builder.iban;
       this.balance = builder.balance;
       this.minBalance = builder.minBalance;
       this.currency = builder.currency;
       this.type = builder.type;
       this.interestRate = builder.interestRate;
       this.cards = builder.cards;

       this.transactionCountByCommerciant = builder.transactionCountByCategory;
       this.activeDiscountByCategory = builder.activeDiscountByCategory;
       this.totalSpentRON = builder.totalSpentRON;

       this.associates = builder.associates;
       this.totalSpentCommerciants = builder.totalSpentCommerciants;
       this.totalSpentByAssociate = builder.totalSpentByAssociate;
       this.totalDepositedByAssociate = builder.totalDepositedByAssociate;
       this.spendingLimit = builder.spendingLimit;
       this.depositLimit = builder.depositLimit;
       this.receivedFoodCashback = builder.receivedFoodCashback;
       this.receivedClothesCashback = builder.receivedClothesCashback;
       this.receivedTechCashback = builder.receivedTechCashback;
       this.owner = builder.owner;
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

        private double totalSpentRON;

        private Map<String, List<User>> associates = new HashMap<>();
        private Map<String, Map<String, Double>> totalSpentCommerciants = new HashMap<>();
        private Map<String, Double> totalSpentByAssociate = new HashMap<>();
        private Map<String, Double> totalDepositedByAssociate = new HashMap<>();
        private double spendingLimit;
        private double depositLimit;
        private String owner;

        private Map<String, Double> activeDiscountByCategory = new HashMap<>();

        private boolean receivedFoodCashback = false;
        private boolean receivedClothesCashback = false;
        private boolean receivedTechCashback = false;

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

        public AccountBuilder business(final String owner, final Graph graph) {
            if (type.equals("business")) {
                this.owner = owner;
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

    public void updateNrOfTransactions(final String commerciant) {
        if (this.transactionCountByCommerciant.containsKey(commerciant)) {
            int newNumber = this.transactionCountByCommerciant.get(commerciant) + 1;
            this.transactionCountByCommerciant.put(commerciant, newNumber);
        } else {
            this.transactionCountByCommerciant.put(commerciant, 1);
        }
    }

    /**
     * Adds the spent amount in RON to the total spent in RON
     * @param amountInRON the amount to add to the total spent in RON
     */
    public void addToTotalSpentRON(final double amountInRON) {
        this.totalSpentRON += amountInRON;
    }

//    public boolean isEmployee(String email) {
//        if (!associates.containsKey(email)) {
//            return false;
//        }
//
//        if (associates.get(email).equals("employee")) {
//            return true;
//        }
//        return false;
//    }

    public boolean isEmployee(String email) {
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

    public boolean isAssociate(String email) {
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

//    public void addAssociate(String role, User user) {
//        for (List<User> users : associates.values()) {
//            if (users.contains(user)) {
//                return;
//            }
//        }
//
//        if (associates.containsKey(role)) {
//            List<User> users = associates.get(role);
//            users.add(user);
//        } else {
//            List<User> users = new ArrayList<>();
//            users.add(user);
//            associates.put(role, users);
//        }
//    }

    public void addAssociate(String role, User user) {
        if (this.owner.equals(user.getEmail())) {
            return;
        }

        for (Map.Entry<String, List<User>> entry : associates.entrySet()) {
            if (entry.getValue().contains(user)) {
                return;
            }
        }

        if (associates.containsKey(role)) {
            List<User> users = associates.get(role);
            users.add(user);
        } else {
            List<User> users = new ArrayList<>();
            users.add(user);
            associates.put(role, users);
        }
    }

    /* this has to be modified, but I have to continue now */
    public void addSpending(String merchant, String userEmail, double amount) {
        Map<String, Double> userSpending = null;
        for (Map.Entry<String, Map<String, Double>> entry : totalSpentCommerciants.entrySet()) {
            if (entry.getKey() == null && merchant == null) {
                userSpending = entry.getValue();
                break;
            } else if (entry.getKey() != null && entry.getKey().equals(merchant)) {
                userSpending = entry.getValue();
                break;
            }
        }

        if (userSpending == null) {
            userSpending = new HashMap<>();
            totalSpentCommerciants.put(merchant, userSpending);
        }

        Double currentAmount = null;
        for (Map.Entry<String, Double> entry : userSpending.entrySet()) {
            if (entry.getKey() == null && userEmail == null) {
                currentAmount = entry.getValue();
                break;
            } else if (entry.getKey() != null && entry.getKey().equals(userEmail)) {
                currentAmount = entry.getValue();
                break;
            }
        }

        if (currentAmount == null) {
            userSpending.put(userEmail, amount);
        } else {
            userSpending.put(userEmail, currentAmount + amount);
        }
    }

    public void updateTotalSpentByAssociate(String userEmail, double amount) {
        if (totalSpentByAssociate.containsKey(userEmail)) {
            double currentAmount = totalSpentByAssociate.get(userEmail);
            totalSpentByAssociate.put(userEmail, currentAmount + amount);
        } else {
            totalSpentByAssociate.put(userEmail, amount);
        }
    }

    public void updateTotalDepositedByAssociate(String userEmail, double amount) {
        if (totalDepositedByAssociate.containsKey(userEmail)) {
            double currentAmount = totalDepositedByAssociate.get(userEmail);
            totalDepositedByAssociate.put(userEmail, currentAmount + amount);
        } else {
            totalDepositedByAssociate.put(userEmail, amount);
        }
    }

    public int getNrOfTransactions(String merchant) {
        if (transactionCountByCommerciant.containsKey(merchant)) {
            return transactionCountByCommerciant.get(merchant);
        }
        return 0;
    }


    public void addCashbackForCategory(final String category, final double cashback) {
        activeDiscountByCategory.put(category, cashback);
        System.out.println(activeDiscountByCategory);
    }

    public void removeCashbackForCategory(final String category) {
        activeDiscountByCategory.remove(category);
    }

    public boolean hasCashbackForCategory(String category) {
        return activeDiscountByCategory.containsKey(category);
    }
    public double getCashbackForCategory(String category) {
        if (activeDiscountByCategory.containsKey(category)) {
            return activeDiscountByCategory.get(category);
        }
        return 0;
    }


    public boolean hasUsedCashbackForCategory(String category) {
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

    public void addReceivedCashbackCategory(String category) {
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

    public Map<String, List<User>> getAssociates() {
        return associates;
    }

    public void setAssociates(Map<String, List<User>> associates) {
        this.associates = associates;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    public void setDepositLimit(double depositLimit) {
        this.depositLimit = depositLimit;
    }

    public Map<String, Map<String, Double>> getTotalSpentCommerciants() {
        return totalSpentCommerciants;
    }

    public void setTotalSpentCommerciants(Map<String, Map<String, Double>> totalSpentCommerciants) {
        this.totalSpentCommerciants = totalSpentCommerciants;
    }

    public Map<String, Double> getTotalSpentByAssociate() {
        return totalSpentByAssociate;
    }

    public void setTotalSpentByAssociate(Map<String, Double> totalSpentByAssociate) {
        this.totalSpentByAssociate = totalSpentByAssociate;
    }

    public Map<String, Double> getTotalDepositedByAssociate() {
        return totalDepositedByAssociate;
    }

    public void setTotalDepositedByAssociate(Map<String, Double> totalDepositedByAssociate) {
        this.totalDepositedByAssociate = totalDepositedByAssociate;
    }

    public Map<String, Double> getActiveDiscountByCategory() {
        return activeDiscountByCategory;
    }

    public void setActiveDiscountByCategory(Map<String, Double> activeDiscountByCategory) {
        this.activeDiscountByCategory = activeDiscountByCategory;
    }

    public boolean getReceivedFoodCashback() {
        return receivedFoodCashback;
    }

    public void setReceivedFoodCashback(boolean receivedFoodCashback) {
        this.receivedFoodCashback = receivedFoodCashback;
    }

    public boolean getReceivedClothesCashback() {
        return receivedClothesCashback;
    }

    public void setReceivedClothesCashback(boolean receivedClothesCashback) {
        this.receivedClothesCashback = receivedClothesCashback;
    }

    public boolean getReceivedTechCashback() {
        return receivedTechCashback;
    }

    public void setReceivedTechCashback(boolean receivedTechCashback) {
        this.receivedTechCashback = receivedTechCashback;
    }

    public boolean isReceivedFoodCashback() {
        return receivedFoodCashback;
    }

    public boolean isReceivedClothesCashback() {
        return receivedClothesCashback;
    }

    public boolean isReceivedTechCashback() {
        return receivedTechCashback;
    }
}
