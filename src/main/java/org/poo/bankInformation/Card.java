package org.poo.bankInformation;
import org.poo.utils.Utils;

public class Card {
    private String cardNumber;
    private String status;
    private String owner;

    public Card(final String owner) {
        this.cardNumber = Utils.generateCardNumber();
        this.status = "active";
        this.owner = owner;
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

        /* formatted the balance after paying with the card */
//        String formatted = String.format("%.2f", account.getBalance());
//        double formattedBalance = Double.parseDouble(formatted);
//        account.setBalance(account.getBalance());
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
