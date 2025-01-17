package org.poo.bankInformation;

import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

public class OneTimeCard extends Card {
    public OneTimeCard(String owner) {
        super(owner);
    }

    /**
     * Pay the amount from the account
     * @param account the account to pay from
     * @param amount the amount to pay
     * @param cardHolder the user that holds the card
     * @param timestamp the timestamp of the transaction
     */
    @Override
    public void pay(final Account account, final double amount,
                    final String cardHolder, final int timestamp) {
        account.setBalance(account.getBalance() - amount);

        /*formatare*/
//        String formatted = String.format("%.2f", account.getBalance());
//        double formattedBalance = Double.parseDouble(formatted);
//        account.setBalance(account.getBalance());

        /* create a transaction for the deleted card */
        Transaction transactionDelete;
        String deletedCard = this.getCardNumber();
        transactionDelete = new Transaction.TransactionBuilder(timestamp,
                "The card has been destroyed", "deleteCard")
                .deleteCard(account.getIban(), deletedCard, cardHolder)
                .build();
        account.getTransactions().add(transactionDelete);

        /* generate a new card number */
        this.setCardNumber(Utils.generateCardNumber());
        /* create a transaction for the new generated card */
        Transaction transactionCreate;
        String createdCard = this.getCardNumber();
        transactionCreate = new Transaction.TransactionBuilder(timestamp,
                "New card created", "createCard")
                .createCard(account.getIban(), createdCard, cardHolder)
                .build();
        account.getTransactions().add(transactionCreate);
    }
}
