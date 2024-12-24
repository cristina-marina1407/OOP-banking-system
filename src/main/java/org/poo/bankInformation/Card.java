package org.poo.bankInformation;
import org.poo.utils.Utils;

public class Card {
    private String cardNumber;
    private String status;

    public Card() {
        this.cardNumber = Utils.generateCardNumber();
        this.status = "active";
    }

    /**
     * Pay the amount from the account
     * @param account the account to pay from
     * @param amount the amount to pay
     * @param cardHolder the user that has the card
     * @param timestamp the timestamp of the transaction
     */
    public void pay(final Account account, final double amount,
                    final String cardHolder, final int timestamp) {
        account.setBalance(account.getBalance() - amount);
    }

    /**
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * @param cardNumber the card number to set
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(final String status) {
        this.status = status;
    }
}
