package org.poo.commands.reportCommands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.*;

public class BusinessReport implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public BusinessReport(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /* gresit */

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

                if (!account.getType().equals("business")) {
                    outputNode.put("error", "Account is not of type business");
                    outputNode.put("timestamp", command.getTimestamp());
                    resultNode.set("output", outputNode);
                    resultNode.put("timestamp", command.getTimestamp());
                    output.add(resultNode);
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
            outputNode.put("error", "Account not found");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
        }
    }

    private void businessReportCommerciants(Account account, ObjectNode outputNode, int start, int end) {
        System.out.println("print report " + account.getTotalSpentCommerciants());

        ObjectMapper objectMapper = new ObjectMapper();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("spending limit", account.getSpendingLimit());
        outputNode.put("deposit limit", account.getDepositLimit());
        outputNode.put("statistics type", "commerciant");

        ArrayNode commerciantsArray = objectMapper.createArrayNode();

        List<String> commerciantsList = getSortedCommerciants(account, start, end);

        System.out.println(commerciantsList);

        for (String commerciantName : commerciantsList) {
            Map<String, Double> userSpending = account.getTotalSpentCommerciants().get(commerciantName);

            System.out.println("user spending " + userSpending);

            if (userSpending != null) {
                ObjectNode commerciantNode = objectMapper.createObjectNode();
                commerciantNode.put("commerciant", commerciantName);

                ArrayNode managersArray = objectMapper.createArrayNode();
                ArrayNode employeesArray = objectMapper.createArrayNode();
                double totalReceived = 0.0;

                for (Map.Entry<String, Double> entry : userSpending.entrySet()) {
                    String email = entry.getKey();

                    User user = findUserByEmail(email);

                    if (account.isAssociate(email)) {
                        Double amount = entry.getValue();
                        totalReceived += amount;
                        String fullName = user.getLastName() + " " + user.getFirstName();

                        List<User> managers = account.getAssociates().get("manager");

                        int nr = nrOfTransactions(commerciantName ,account, email, start, end);

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

    private void businessReportTransactions(Account account, ObjectNode outputNode) {
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

        for (Map.Entry<String, List<User>> associateEntry : account.getAssociates().entrySet()) {
            String role = associateEntry.getKey();
            List<User> associateUsers = associateEntry.getValue();
            for (User user : associateUsers) {
                String email = user.getEmail();

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

    private List<String> sortAssociates(List<User> associatesToSort) {
        List<String> sortedAssociates = new ArrayList<>();
        for (User user : associatesToSort) {
            sortedAssociates.add(user.getLastName() + " " + user.getFirstName());
        }
        Collections.sort(sortedAssociates);
        return sortedAssociates;
    }

    private List<String> getSortedCommerciants(Account account, int start, int end) {
        List<String> commerciantsList = new ArrayList<>();
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= start && transaction.getTimestamp() <= end &&
                    (transaction.getType().equals("payOnline") || (transaction.getType().equals("sendMoney") &&
                            transaction.getReceiverIban() == null))) {
                if (!commerciantsList.contains(transaction.getCommerciant()))
                    commerciantsList.add(transaction.getCommerciant());
            }
        }
        Collections.sort(commerciantsList);
        return commerciantsList;
    }

    private int nrOfTransactions(String commerciant, Account account, String email, int start, int end) {
        int nr = 0;
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= start && transaction.getTimestamp() <= end &&
                    (transaction.getType().equals("payOnline") || (transaction.getType().equals("sendMoney") &&
                            transaction.getReceiverIban() == null))) {
                if (transaction.getEmail().equals(email) && transaction.getCommerciant().equals(commerciant)) {
                    nr++;
                }
            }
        }
        return nr;
    }

    /* trebuie sa ma uit dupa */
    private User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
}
