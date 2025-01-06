package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private final SplitPaymentManager splitPaymentManager;

    public SplitPayment(final List<User> users, final Command command, final Graph graph, final SplitPaymentManager splitPaymentManager) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.splitPaymentManager = splitPaymentManager;
    }

    /**
     * Splits the amount of money to multiple accounts
     */
    public void execute() {
        String splitPaymentType = command.getSplitPaymentType();
        long timestamp = command.getTimestamp();
        splitPaymentManager.addSplitPayment(splitPaymentType, timestamp, command);
    }
}