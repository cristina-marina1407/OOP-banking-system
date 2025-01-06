package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Command;

import java.util.ArrayList;
import java.util.List;

public class SplitPaymentManager {
    /* list of active split payments */
    private List<SplitPaymentObject> activeSplitPaymentsList = new ArrayList<>();
    /**
     * Add a split payment to the list of active split payments.
     * @param splitPaymentType the type of the split payment
     * @param timestamp the timestamp of the split payment
     * @param command the command of the split payment
     */
    public void addSplitPayment(final String splitPaymentType, final long timestamp,
                                final Command command) {
        SplitPaymentObject data = new SplitPaymentObject(splitPaymentType, timestamp, command);
        activeSplitPaymentsList.add(data);
    }

    /**
     * @return the list of active split payments
     */
    public List<SplitPaymentObject> getActiveSplitPayments() {
        return activeSplitPaymentsList;
    }

    /**
     * @param activeSplitPayments the list of active split payments to set
     */
    public void setActiveSplitPayments(final List<SplitPaymentObject> activeSplitPayments) {
        this.activeSplitPaymentsList = activeSplitPayments;
    }
}
