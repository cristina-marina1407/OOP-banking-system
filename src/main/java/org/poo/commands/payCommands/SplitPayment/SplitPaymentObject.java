package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Command;

import java.util.ArrayList;
import java.util.List;

public class SplitPaymentObject {
    private final String splitPaymentType;
    private final long timestamp;
    private final Command command;
    private List<String> acceptedUsers = new ArrayList<>();

    public SplitPaymentObject(String splitPaymentType, long timestamp, Command command) {
        this.splitPaymentType = splitPaymentType;
        this.timestamp = timestamp;
        this.command = command;
    }

    public String getSplitPaymentType() {
        return splitPaymentType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Command getCommand() {
        return command;
    }

    public List<String> getAcceptedUsers() {
        return acceptedUsers;
    }

    public void addAcceptedUser(String username) {
        acceptedUsers.add(username);
    }

    /*trebuie sa modific*/
    public boolean isFullyAccepted(List<String> users) {
        return acceptedUsers.containsAll(users);
    }
}
