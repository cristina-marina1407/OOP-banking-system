package org.poo.commands.reportCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BusinessReport implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public BusinessReport(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * This method executes the business report command.
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "businessReport");
        ObjectNode outputNode = objectMapper.createObjectNode();

        boolean accountFound = false;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = true;

                /* checks if the account is of type business */
                if (!account.getType().equals("business")) {
                    PrintOutputErrorHelper.printOutputError("Account is not of type business",
                                                        outputNode, resultNode, command, output);
                    return;
                }

                int start = command.getStartTimestamp();
                int end = command.getEndTimestamp();

                if (command.getType().equals("commerciant")) {
                    businessReportCommerciants(account, outputNode, start, end);
                    resultNode.set("output", outputNode);
                    resultNode.put("timestamp", command.getTimestamp());
                    output.add(resultNode);
                } else {
                    businessReportTransactions(account, outputNode);
                    resultNode.set("output", outputNode);
                    resultNode.put("timestamp", command.getTimestamp());
                    output.add(resultNode);
                }
            }
        }
        if (!accountFound) {
            PrintOutputErrorHelper.printOutputError("Account not found",
                    outputNode, resultNode, command, output);
        }
    }

    /**
     * This method creates the commerciant type business report
     * @param account the account for which the report is created
     * @param outputNode the output node
     * @param start the start timestamp
     * @param end the end timestamp
     */
    private void businessReportCommerciants(final Account account, final ObjectNode outputNode,
                                            final int start, final int end) {
        ObjectMapper objectMapper = new ObjectMapper();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("spending limit", account.getSpendingLimit());
        outputNode.put("deposit limit", account.getDepositLimit());
        outputNode.put("statistics type", "commerciant");

        ArrayNode commerciantsArray = objectMapper.createArrayNode();

        /* gets the sorted commerciant list */
        List<String> commerciantsList = sortCommerciants(account, start, end);

        for (String commerciantName : commerciantsList) {
            /* gets the amount that every user spent at this commerciant */
            Map<String, Double> userSpending =
                    account.getTotalSpentCommerciants().get(commerciantName);

            if (userSpending != null) {
                ObjectNode commerciantNode = objectMapper.createObjectNode();
                commerciantNode.put("commerciant", commerciantName);

                ArrayNode managersArray = objectMapper.createArrayNode();
                ArrayNode employeesArray = objectMapper.createArrayNode();

                double totalReceived = 0.0;

                for (Map.Entry<String, Double> entry : userSpending.entrySet()) {
                    String email = entry.getKey();

                    User user = FindHelper.findUser(users, email);

                    /* checks if the user is an associate */
                    if (account.isAssociate(email)) {
                        Double amount = entry.getValue();
                        totalReceived += amount;
                        String fullName = user.getLastName() + " " + user.getFirstName();

                        List<User> managers = account.getAssociates().get("manager");

                        /* gets the number of transactions for the associate at the current
                         commerciant */
                        int nr = nrOfTransactions(commerciantName, account, email, start, end);

                        /* sort the associates */
                        List<String> sortedManagers = new ArrayList<>();
                        if (managers != null) {
                            sortedManagers = sortAssociates(managers);
                        }

                        List<User> employees = account.getAssociates().get("employee");
                        List<String> sortedEmployees = new ArrayList<>();
                        if (employees != null) {
                            sortedEmployees = sortAssociates(employees);
                        }

                        if (sortedManagers.contains(fullName)) {
                            for (int i = 0; i < nr; i++) {
                                managersArray.add(fullName);
                            }
                        } else if (sortedEmployees.contains(fullName)) {
                            for (int i = 0; i < nr; i++) {
                                employeesArray.add(fullName);
                            }
                        }
                    }
                }

                commerciantNode.set("managers", managersArray);
                commerciantNode.set("employees", employeesArray);
                commerciantNode.put("total received", totalReceived);

                commerciantsArray.add(commerciantNode);
            }
        }
        outputNode.set("commerciants", commerciantsArray);
    }

    /**
     * This method creates the transaction type business report
     * @param account the account for which the report is created
     * @param outputNode the output node
     */
    private void businessReportTransactions(final Account account, final ObjectNode outputNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("spending limit", account.getSpendingLimit());
        outputNode.put("deposit limit", account.getDepositLimit());
        outputNode.put("statistics type", "transaction");

        ArrayNode managersArray = objectMapper.createArrayNode();
        ArrayNode employeesArray = objectMapper.createArrayNode();

        double totalSpent = 0.0;
        double totalDeposited = 0.0;

        /* iterate through the associates for this account */
        for (Map.Entry<String, List<User>> associateEntry : account.getAssociates().entrySet()) {
            String role = associateEntry.getKey();
            List<User> associateUsers = associateEntry.getValue();
            for (User user : associateUsers) {
                String email = user.getEmail();

                /* gets the total spent and deposited for the associate */
                Double spent = account.getTotalSpentByAssociate().get(email);
                if (spent != null) {
                    totalSpent += spent;
                } else {
                    spent = 0.0;
                }

                Double deposited = account.getTotalDepositedByAssociate().get(email);
                if (deposited != null) {
                    totalDeposited += deposited;
                } else {
                    deposited = 0.0;
                }

                /* print the associate's full name */
                String fullName = user.getLastName() + " " + user.getFirstName();

                ObjectNode associateNode = objectMapper.createObjectNode();
                associateNode.put("username", fullName);
                associateNode.put("spent", spent);
                associateNode.put("deposited", deposited);

                if (role.equals("manager")) {
                    managersArray.add(associateNode);
                } else if (role.equals("employee")) {
                    employeesArray.add(associateNode);
                }
            }
        }

        outputNode.set("managers", managersArray);
        outputNode.set("employees", employeesArray);
        outputNode.put("total spent", totalSpent);
        outputNode.put("total deposited", totalDeposited);
    }

    /**
     * This method sorts the associates by their full name
     * @param associatesToSort the list of associates to sort
     * @return the sorted list of associates
     */
    private List<String> sortAssociates(final List<User> associatesToSort) {
        List<String> sortedAssociates = new ArrayList<>();
        for (User user : associatesToSort) {
            sortedAssociates.add(user.getLastName() + " " + user.getFirstName());
        }
        Collections.sort(sortedAssociates);
        return sortedAssociates;
    }

    /**
     * This method gets the sorted commerciants
     * @param account the account for which the commerciants are sorted
     * @param start the start timestamp
     * @param end the end timestamp
     * @return the sorted list of commerciants
     */
    private List<String> sortCommerciants(final Account account, final int start, final int end) {
        List<String> commerciantsList = new ArrayList<>();
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= start && transaction.getTimestamp() <= end
                    && (transaction.getType().equals("payOnline")
                    || (transaction.getType().equals("sendMoney")
                    && transaction.getReceiverIban() == null))) {
                if (!commerciantsList.contains(transaction.getCommerciant())) {
                    commerciantsList.add(transaction.getCommerciant());
                }
            }
        }
        Collections.sort(commerciantsList);
        return commerciantsList;
    }

    /**
     * This method gets the number of transactions for a specific commerciant
     * @param commerciant the commerciant for which the transactions are counted
     * @param account the account for which the transactions are counted
     * @param email the email of the associate
     * @param start the start timestamp
     * @param end the end timestamp
     * @return the number of transactions
     */
    private int nrOfTransactions(final String commerciant, final Account account,
                                 final String email, final int start, final int end) {
        int nr = 0;
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= start && transaction.getTimestamp() <= end
                && (transaction.getType().equals("payOnline")
                || (transaction.getType().equals("sendMoney")
                && transaction.getReceiverIban() == null))) {
                if (transaction.getEmail().equals(email)
                    && transaction.getCommerciant().equals(commerciant)) {
                    nr++;
                }
            }
        }
        return nr;
    }
}
