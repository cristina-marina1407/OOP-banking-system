package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Command;

import java.util.ArrayList;
import java.util.List;

public class SplitPaymentObject {
    private final String splitPaymentType;
    private final long timestamp;
    private final Command command;
    private List<String> acceptedUsers = new ArrayList<>();

    public SplitPaymentObject(final String splitPaymentType, final long timestamp,
                              final Command command) {
        this.splitPaymentType = splitPaymentType;
        this.timestamp = timestamp;
        this.command = command;
    }

    /**
     * @return the split payment type
     */
    public String getSplitPaymentType() {
        return splitPaymentType;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the command
     */
    public Command getCommand() {
        return command;
    }

    /**
     * @return the accepted users
     */
    public List<String> getAcceptedUsers() {
        return acceptedUsers;
    }

    /**
     * @param iban the iban to be added
     */
    public void addAcceptedUser(final String iban) {
        acceptedUsers.add(iban);
    }

    /*trebuie sa modific*/
    /**
     * checks if all the users accepted the split payment
     * @param users the users to be checked
     */
    public boolean isFullyAccepted(final List<String> users) {
        return acceptedUsers.containsAll(users);
    }
}
