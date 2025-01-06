package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;

import java.util.ArrayList;
import java.util.List;

public class RejectSplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private final SplitPaymentManager splitPaymentManager;

    public RejectSplitPayment(final List<User> users, final Command command, final Graph graph,
                              final SplitPaymentManager splitPaymentManager) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.splitPaymentManager = splitPaymentManager;
    }

    /**
     * Rejects a split payment
     */
    public void execute() {
        List<SplitPaymentObject> activeSplitPayments
                = splitPaymentManager.getActiveSplitPayments();

        /* uses an auxiliary list to avoid ConcurrentModificationException */
        List<SplitPaymentObject> paymentsToRemove = new ArrayList<>();

        for (SplitPaymentObject splitPayment : activeSplitPayments) {
            if (splitPayment != null) {
                List<String> accounts = splitPayment.getCommand().getAccounts();
                for (String account : accounts) {
                    User user = FindHelper.findUser(users, command.getEmail());
                    if (user != null) {
                        for (Account userAccount : user.getAccounts()) {

                            /* add the split payment to the list after a reject */
                            if (userAccount.getIban().equals(account)) {
                                paymentsToRemove.add(splitPayment);
                                break;
                            }

                        }
                    }
                }
            }
        }
        /* remove the split payments */
        activeSplitPayments.removeAll(paymentsToRemove);
    }
}
