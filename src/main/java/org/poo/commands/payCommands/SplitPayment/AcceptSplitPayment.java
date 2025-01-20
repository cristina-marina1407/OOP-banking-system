package org.poo.commands.payCommands.SplitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;

import java.util.ArrayList;
import java.util.List;

public class AcceptSplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private final SplitPaymentManager splitPaymentManager;
    private ArrayNode output;

    public AcceptSplitPayment(final List<User> users, final Command command,
                              final Graph graph, final SplitPaymentManager splitPaymentManager,
                              final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.splitPaymentManager = splitPaymentManager;
        this.output = output;
    }

    /**
     * Accepts the split payment and processes it if all the accounts have accepted it
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "acceptSplitPayment");
        ObjectNode outputNode = objectMapper.createObjectNode();

        List<SplitPaymentObject> activeSplitPayments
                = splitPaymentManager.getActiveSplitPayments();

        User user = FindHelper.findUser(users, command.getEmail());

        if (user == null) {
            PrintOutputErrorHelper.printOutputError("User not found", outputNode,
                    resultNode, command, output);
            return;
        }

        for (SplitPaymentObject splitPayment : activeSplitPayments) {
            if (splitPayment != null) {
                List<String> accounts = splitPayment.getCommand().getAccounts();
                for (String account : accounts) {
                    for (Account userAccount : user.getAccounts()) {
                        /* accepts the splitPayment of the account that has this email
                        and adds it to the list of the users that accepted the payment*/
                        if (userAccount.getIban().equals(account)
                                && splitPayment.getSplitPaymentType()
                                .equals(command.getSplitPaymentType())) {
                            splitPayment.addAcceptedUser(account);
                            break;
                        }
                    }

                }
                /* if all the accounts have accepted the splitPayment, the payment is processed */
                if (splitPayment.isFullyAccepted(accounts)) {
                    processPayment(splitPayment);
                    break;
                }
            }
        }
    }

    /**
     * Processes the split payment
     * @param splitPaymentObject the split payment to be processed
     */
    private void processPayment(final SplitPaymentObject splitPaymentObject) {
        String splitPaymentType = splitPaymentObject.getSplitPaymentType();
        Command splitPaymentCommand = splitPaymentObject.getCommand();
        List<String> splitAccounts = splitPaymentObject.getCommand().getAccounts();
        List<Double> amounts = splitPaymentCommand.getAmountForUsers();

        /* realizes the payment based on the split payment type */
        if (splitPaymentType.equals("equal")) {
            double moneySplit = splitPaymentCommand.getAmount() / splitAccounts.size();

            /* creates a list with the equal amounts that each account has to pay */
            List<Double> equalAmounts = new ArrayList<>();
            for (int i = 0; i < splitAccounts.size(); i++) {
                equalAmounts.add(moneySplit);
            }

            SplitPaymentHelper.processPaymentHelper(splitPaymentObject, splitAccounts,
                                              splitPaymentCommand.getAmount(),
                                              equalAmounts, splitPaymentType, users, graph);
            splitPaymentManager.removeSplitPayment(splitPaymentObject);
        } else if (splitPaymentType.equals("custom")) {
            SplitPaymentHelper.processPaymentHelper(splitPaymentObject, splitAccounts,
                                              splitPaymentCommand.getAmount(),
                                              amounts, splitPaymentType, users, graph);
            splitPaymentManager.removeSplitPayment(splitPaymentObject);
        }
    }
}
