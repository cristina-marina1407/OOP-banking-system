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
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.transactions.Transaction;

import static org.poo.commands.helperMethods.Constants.UPGRADE_FEE_GOLD;
import static org.poo.commands.helperMethods.Constants.UPGRADE_FEE_SILVER;
import static org.poo.commands.helperMethods.Constants.UPGRADE_FEE_SILVER_TO_GOLD;

import java.util.List;

public class UpgradePlan implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;
    private Graph graph;

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

        boolean accountFound = false;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = true;

                /* check if the user already has this plan */
                if (command.getNewPlanType().equals(user.getServicePlan())) {
                    Transaction transaction;
                    transaction =
                            new Transaction.TransactionBuilder(command.getTimestamp(),
                            "The user already has the " + command.getNewPlanType() + " plan.",
                            "upgradePlanError")
                            .upgradePlanError()
                            .build();
                    account.getTransactions().add(transaction);
                    return;
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
                    return;
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
        /* checks if the account was found and prints an error for that case */
        if (!accountFound) {
            PrintOutputErrorHelper.printOutputError("Account not found", outputNode,
                    resultNode, command, output);
        }
    }
}
