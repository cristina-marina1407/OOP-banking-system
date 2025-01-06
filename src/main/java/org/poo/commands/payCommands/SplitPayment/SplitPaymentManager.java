package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Command;

import java.util.ArrayList;
import java.util.List;

public class SplitPaymentManager {
    private List<SplitPaymentObject> activeSplitPaymentsList = new ArrayList<>();

    public void addSplitPayment(String splitPaymentType, long timestamp, Command command) {
        SplitPaymentObject data = new SplitPaymentObject(splitPaymentType, timestamp, command);
        activeSplitPaymentsList.add(data);
    }

    public List<SplitPaymentObject> getActiveSplitPayments() {
        return activeSplitPaymentsList;
    }

    public void setActiveSplitPayments(List<SplitPaymentObject> activeSplitPayments) {
        this.activeSplitPaymentsList = activeSplitPayments;
    }
}
