package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.User;

import static org.poo.commands.helperMethods.Constants.COMISSION_SUM;

public final class TakeCommissionHelper {
    private TakeCommissionHelper() {

    }
    /**
     * Take the commission from the account
     * @param user the user
     * @param account the account
     * @param ronAmount the amount in RON
     */
    public static void takeCommission(final User user, final Account account,
                                      final double ronAmount, final double commission) {
        if (user.getServicePlan().equals("standard")) {
            account.setBalance(account.getBalance() - commission);
        }

        if (user.getServicePlan().equals("silver")
                && ronAmount >= COMISSION_SUM) {
            account.setBalance(account.getBalance() - commission);
        }
    }
}
