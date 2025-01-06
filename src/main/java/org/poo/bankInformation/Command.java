package org.poo.bankInformation;

import org.poo.fileio.CommandInput;

import java.util.List;

public class Command {
    private String command;
    private String email;
    private String account;
    private String newPlanType;
    private String role;
    private String currency;
    private String target;
    private String description;
    private String cardNumber;
    private String commerciant;
    private String receiver;
    private String alias;
    private String accountType;
    private String splitPaymentType;
    private String type;
    private String location;
    private int timestamp;
    private int startTimestamp;
    private int endTimestamp;
    private double interestRate;
    private double spendingLimit;
    private double depositLimit;
    private double amount;
    private double minBalance;
    private List<String> accounts;
    private List<Double> amountForUsers;

    public Command(final CommandInput commandInput) {
        this.command = commandInput.getCommand();
        this.email = commandInput.getEmail();
        this.account = commandInput.getAccount();
        this.currency = commandInput.getCurrency();
        this.amount = commandInput.getAmount();
        this.minBalance = commandInput.getMinBalance();
        this.target = commandInput.getTarget();
        this.description = commandInput.getDescription();
        this.cardNumber = commandInput.getCardNumber();
        this.commerciant = commandInput.getCommerciant();
        this.timestamp = commandInput.getTimestamp();
        this.startTimestamp = commandInput.getStartTimestamp();
        this.endTimestamp = commandInput.getEndTimestamp();
        this.receiver = commandInput.getReceiver();
        this.alias = commandInput.getAlias();
        this.accountType = commandInput.getAccountType();
        this.interestRate = commandInput.getInterestRate();
        this.accounts = commandInput.getAccounts();
        this.newPlanType = commandInput.getNewPlanType();
        this.role = commandInput.getRole();
        this.splitPaymentType = commandInput.getSplitPaymentType();
        this.type = commandInput.getType();
        this.location = commandInput.getLocation();
        this.spendingLimit = commandInput.getSpendingLimit();
        this.depositLimit = commandInput.getDepositLimit();
        this.amountForUsers = commandInput.getAmountForUsers();
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
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
     * @return the minBalance
     */
    public double getMinBalance() {
        return minBalance;
    }

    /**
     * @param minBalance the minBalance to set
     */
    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
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
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(final String target) {
        this.target = target;
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
     * @return the cardNumber
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * @param cardNumber the cardNumber to set
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
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
     * @return the startTimestamp
     */
    public int getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * @param startTimestamp the startTimestamp to set
     */
    public void setStartTimestamp(final int startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * @return the endTimestamp
     */
    public int getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * @param endTimestamp the endTimestamp to set
     */
    public void setEndTimestamp(final int endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(final String receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * @return the accountType
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * @param accountType the accountType to set
     */
    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    /**
     * @return the interestRate
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * @param interestRate the interestRate to set
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * @return the accounts
     */
    public List<String> getAccounts() {
        return accounts;
    }

    /**
     * @param accounts the accounts to set
     */
    public void setAccounts(final List<String> accounts) {
        this.accounts = accounts;
    }

    /**
     * @return the newPlanType
     */
    public String getNewPlanType() {
        return newPlanType;
    }

    /**
     * @param newPlanType the newPlanType to set
     */
    public void setNewPlanType(final String newPlanType) {
        this.newPlanType = newPlanType;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(final String role) {
        this.role = role;
    }

    /**
     * @return the splitPaymentType
     */
    public String getSplitPaymentType() {
        return splitPaymentType;
    }

    /**
     * @param splitPaymentType the splitPaymentType to set
     */
    public void setSplitPaymentType(final String splitPaymentType) {
        this.splitPaymentType = splitPaymentType;
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
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * @return the spendingLimit
     */
    public double getSpendingLimit() {
        return spendingLimit;
    }

    /**
     * @param spendingLimit the spendingLimit to set
     */
    public void setSpendingLimit(final double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    /**
     * @return the depositLimit
     */
    public double getDepositLimit() {
        return depositLimit;
    }

    /**
     * @param depositLimit the depositLimit to set
     */
    public void setDepositLimit(final double depositLimit) {
        this.depositLimit = depositLimit;
    }

    /**
     * @return the amountForUsers
     */
    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    /**
     * @param amountForUsers the amountForUsers to set
     */
    public void setAmountForUsers(final List<Double> amountForUsers) {
        this.amountForUsers = amountForUsers;
    }
}
