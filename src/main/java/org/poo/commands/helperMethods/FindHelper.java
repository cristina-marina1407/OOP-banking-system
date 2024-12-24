package org.poo.commands.helperMethods;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.User;

import java.util.List;

public final class FindHelper {

    private FindHelper() {

    }

    /**
     * Finds a user by email
     * @param users list of users
     * @param email email of the user
     * @return  the user with the given email
     */
    public static User findUser(final List<User> users, final String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Finds an account by IBAN
     * @param accounts list of accounts
     * @param iban IBAN of the account
     * @return the account with the given IBAN
     */
    public static Account findAccount(final List<Account> accounts, final String iban) {
        for (Account account : accounts) {
            if (account.getIban().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Finds a card by card number
     * @param cards list of cards
     * @param cardNumber card number
     * @return the card with the given card number
     */
    public static Card findCard(final List<Card> cards, final String cardNumber) {
        for (Card card : cards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null;
    }
}
