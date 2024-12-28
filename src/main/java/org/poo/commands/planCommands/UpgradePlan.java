package org.poo.commands.planCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.CompareTypesHelper;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.UpgradePlanHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class UpgradePlan implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;
    private Graph graph;

    public UpgradePlan (final List<User> users, final Command command, final ArrayNode output, Graph graph) {
        this.command = command;
        this.users = users;
        this.output = output;
        this.graph = graph;
    }

    /**
     * Upgrade plan command
     */
    public void execute () {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "upgradePlan");
        ObjectNode outputNode = objectMapper.createObjectNode();

        int accountFound = 0;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = 1;
                if (command.getNewPlanType().equals(user.getServicePlan())) {
                    Transaction transaction;
                    transaction =
                            new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "The user already has the " + command.getNewPlanType() + " plan",
                                    "upgradePlanError")
                                    .upgradePlanError()
                                    .build();
                    account.getTransactions().add(transaction);
                    break;
                }
                boolean checkTypes = false;
                checkTypes = CompareTypesHelper.compareTypes(user.getServicePlan(), command.getNewPlanType());

                if (!checkTypes) {
                    Transaction transaction;
                    transaction =
                            new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "You cannot downgrade your plan.",
                                    "upgradePlanError")
                                    .upgradePlanError()
                                    .build();
                    account.getTransactions().add(transaction);
                    break;
                }

                if ((user.getServicePlan().equals("student")
                    || user.getServicePlan().equals("standard")) && command.getNewPlanType().equals("silver")) {
                    UpgradePlanHelper.upgradePlan(user, account, command.getNewPlanType(), 100,
                            account.getCurrency(), command.getTimestamp(), graph);
                    break;
                }

                if ((user.getServicePlan().equals("student")
                        || user.getServicePlan().equals("standard")) && command.getNewPlanType().equals("gold")) {
                    UpgradePlanHelper.upgradePlan(user, account, command.getNewPlanType(), 350,
                            account.getCurrency(), command.getTimestamp(), graph);
                    break;
                }

                if (user.getServicePlan().equals("silver") && command.getNewPlanType().equals("gold")) {
                    UpgradePlanHelper.upgradePlan(user, account, command.getNewPlanType(), 250,
                            account.getCurrency(), command.getTimestamp(), graph);
                    break;
                }

            }
        }

        if (accountFound == 0) {
            outputNode.put("error",
                    "Account not found");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
        }
    }
}
