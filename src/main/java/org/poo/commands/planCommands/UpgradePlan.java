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
import org.poo.transactions.Transaction;

import java.util.List;

public class UpgradePlan implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;
    private Graph graph;
    private static final int UPGRADE_FEE_SILVER = 100;
    private static final int UPGRADE_FEE_SILVER_TO_GOLD = 250;
    private static final int UPGRADE_FEE_GOLD = 350;

    public UpgradePlan(final List<User> users, final Command command,
                        final ArrayNode output, final Graph graph) {
        this.command = command;
        this.users = users;
        this.output = output;
        this.graph = graph;
    }

    /**
     * Upgrade plan command
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "upgradePlan");
        ObjectNode outputNode = objectMapper.createObjectNode();

        int accountFound = 0;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = 1;

                /* check if the user already has this plan */
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
                checkTypes = CompareTypesHelper.compareTypes(user.getServicePlan(),
                        command.getNewPlanType());

                /* checks if the new plan is a downgrade */
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

                /* upgrades plan from student/standard to silver */
                if ((user.getServicePlan().equals("student")
                    || user.getServicePlan().equals("standard"))
                    && command.getNewPlanType().equals("silver")) {
                    UpgradePlanHelper.upgradePlan(user, account, command.getNewPlanType(),
                            UPGRADE_FEE_SILVER, account.getCurrency(),
                            command.getTimestamp(), graph);
                    break;
                }

                /* upgrades plan from student/standard to gold */
                if ((user.getServicePlan().equals("student")
                        || user.getServicePlan().equals("standard"))
                        && command.getNewPlanType().equals("gold")) {
                    UpgradePlanHelper.upgradePlan(user, account, command.getNewPlanType(),
                            UPGRADE_FEE_GOLD, account.getCurrency(),
                            command.getTimestamp(), graph);
                    break;
                }

                /* upgrades plan from silver to gold */
                if (user.getServicePlan().equals("silver")
                        && command.getNewPlanType().equals("gold")) {
                    UpgradePlanHelper.upgradePlan(user, account, command.getNewPlanType(),
                            UPGRADE_FEE_SILVER_TO_GOLD, account.getCurrency(),
                            command.getTimestamp(), graph);
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
