package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;

import java.util.List;

public class SplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private final SplitPaymentManager splitPaymentManager;

    public SplitPayment(final List<User> users, final Command command, final Graph graph,
                        final SplitPaymentManager splitPaymentManager) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.splitPaymentManager = splitPaymentManager;
    }

    /**
     * Adds the split payment to the split payment manager
     */
    public void execute() {
        String splitPaymentType = command.getSplitPaymentType();
        long timestamp = command.getTimestamp();
        /* adds the splitPayment required to the splitPayment manager */
        splitPaymentManager.addSplitPayment(splitPaymentType, timestamp, command);
    }
}
