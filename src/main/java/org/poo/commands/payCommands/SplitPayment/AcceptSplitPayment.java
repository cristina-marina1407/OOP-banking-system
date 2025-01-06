package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;

import java.util.ArrayList;
import java.util.List;

public class AcceptSplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private final SplitPaymentManager splitPaymentManager;

    public AcceptSplitPayment(final List<User> users, final Command command, final Graph graph,
                              final SplitPaymentManager splitPaymentManager) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.splitPaymentManager = splitPaymentManager;
    }

    /**
     * Accepts the split payment and processes it if all the accounts have accepted it
     */
    public void execute() {
        List<SplitPaymentObject> activeSplitPayments
                = splitPaymentManager.getActiveSplitPayments();

        for (SplitPaymentObject splitPayment : activeSplitPayments) {
            if (splitPayment != null) {
                List<String> accounts = splitPayment.getCommand().getAccounts();
                for (String account : accounts) {
                    User user = FindHelper.findUser(users, command.getEmail());
                    if (user != null) {
                        for (Account userAccount : user.getAccounts()) {
                            /* accepts the splitPayment of the account that has this email
                            and adds it to the list of the users that accepted the payment*/
                            if (userAccount.getIban().equals(account)) {
                                splitPayment.addAcceptedUser(account);
                            }
                        }
                    }
                }
                /* if all the accounts have accepted the splitPayment, the payment is processed */
                if (splitPayment.isFullyAccepted(accounts)) {
                    processPayment(splitPayment);
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

        if (splitPaymentType.equals("equal")) {
            double moneySplit = splitPaymentCommand.getAmount() / splitAccounts.size();

            List<Double> equalAmounts = new ArrayList<>();
            for (int i = 0; i < splitAccounts.size(); i++) {
                equalAmounts.add(moneySplit);
            }

            SplitPaymentHelper.processPayment(splitPaymentObject, splitAccounts,
                                              splitPaymentCommand.getAmount(),
                                              equalAmounts, splitPaymentType, users, graph);
        } else if (splitPaymentType.equals("custom")) {
            SplitPaymentHelper.processPayment(splitPaymentObject, splitAccounts,
                                              splitPaymentCommand.getAmount(),
                                              amounts, splitPaymentType, users, graph);
        }
    }
}
