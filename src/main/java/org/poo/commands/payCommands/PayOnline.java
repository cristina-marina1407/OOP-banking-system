package org.poo.commands.payCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Card;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;

import org.poo.cashback.CashbackHelper;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.commands.planCommands.AutoUpgrade;
import org.poo.transactions.Transaction;

import java.util.List;

import static org.poo.commands.helperMethods.TakeCommissionHelper.takeCommission;

public class PayOnline implements CommandInterface {
    private Command command;
    private List<User> users;
    private List<Commerciant> commerciants;
    private Graph graph;
    private ArrayNode output;

    public PayOnline(final List<User> users, final Command command, final Graph graph,
                     final ArrayNode output, final List<Commerciant> commerciants) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.output = output;
        this.commerciants = commerciants;
    }

    /**
     * Pay online command
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "payOnline");
        ObjectNode outputNode = objectMapper.createObjectNode();

        User user = FindHelper.findUser(users, command.getEmail());
        Card card = FindHelper.findCardByCardNumber(users, command.getCardNumber());
        /* checks if the card was not found and prints an error for that case */
        if (card == null) {
            PrintOutputErrorHelper.printOutputError("Card not found", outputNode, resultNode,
                                              command, output);
            return;
        }
        Account account = FindHelper.findAccountByCardNumber(users, command.getCardNumber());
        /* convert the amount to the currency of the account */
        double newAmount = graph.convert(command.getCurrency(), account.getCurrency(),
                command.getAmount());
        /* convert the amount to RON */
        double ronAmount = graph.convert(account.getCurrency(), "RON", newAmount);

        if (account.getType().equals("business")) {
            /* checks if the associate has exceeded the spending limit */
            if (account.isEmployee(command.getEmail())
                    && newAmount > account.getSpendingLimit()) {
                return;
            }
            /* checks if the user is an associate to the business account */
            if (!user.getEmail().equals(account.getOwner())
                    && !account.isAssociate(command.getEmail())) {
                PrintOutputErrorHelper.printOutputError("Card not found", outputNode, resultNode,
                                                  command, output);
                return;
            }
            User owner = FindHelper.findUser(users, account.getOwner());
            /* calculate the commission */
            double commission = owner.calculateCommission(newAmount, graph, account);
            /* checks if the card is active and if it has funds for the payment */
            if (card.getStatus().equals("active")) {
                if (newAmount == 0) {
                    return;
                }
                if (account.getBalance() >= newAmount + commission) {
                    String commerciantName = command.getCommerciant();
                    String category = null;
                    Commerciant commerciantToPay = null;
                    /* finds the commerciant and the commerciant type */
                    for (Commerciant commerciant : commerciants) {
                        if (commerciant.getCommerciant().equals(commerciantName)) {
                            category = commerciant.getType();
                            commerciantToPay = commerciant;
                            break;
                        }
                    }
                    /* creates the transaction for the payment */
                    Transaction transaction;
                    transaction =
                            new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Card payment", "payOnline")
                                    .payOnline(newAmount, command.getCommerciant(), category,
                                               user.getEmail())
                                    .build();
                    account.getTransactions().add(transaction);
                    card.pay(account, newAmount, command.getEmail(),
                            command.getTimestamp());
                    /* applies the cashback */
                    if (commerciantToPay != null) {
                        account.updateNrOfTransactions(commerciantToPay.getCommerciant());
                        CashbackHelper.applyCashback(commerciantToPay, account, category,
                                transaction, user, graph, command, users);
                    }
                    AutoUpgrade.autoUpgrade(user, account, command, graph);
                    /* updates the total spent by the associate and the total spent
                     to a commerciant */
                    account.addSpending(commerciantName, command.getEmail(), newAmount);
                    account.updateTotalSpentByAssociate(command.getEmail(), newAmount);
                    /* takes the commission from the account for the owner service plan */
                    takeCommission(owner, account, ronAmount, commission);
                    return;
                } else {
                    /* creates a transaction for the case when the account
                     has insufficient funds */
                    payOnlineErrorTransaction(command, account, "Insufficient funds");
                }
                return;
            } else {
                /* creates a transaction for the case when the card is frozen */
                payOnlineErrorTransaction(command, account, "The card is frozen");
                return;
            }
        }

        /* checks if the user is the card owner if the account is not of type business */
        if ((account.getType().equals("classic") || account.getType().equals("savings"))
                && !card.getOwner().equals(command.getEmail())) {
            PrintOutputErrorHelper.printOutputError("Card not found", outputNode, resultNode,
                                              command, output);
            return;
        }

        double commission = user.calculateCommission(newAmount, graph, account);
        /* checks if the card is active and if it has funds for the payment */
        if (card.getStatus().equals("active")) {
            if (newAmount == 0) {
                return;
            }
            if (account.getBalance() >= newAmount + commission) {
                String commerciantName = command.getCommerciant();
                String category = null;
                Commerciant commerciantToPay = null;
                /* finds the commerciant and the commerciant type */
                for (Commerciant commerciant : commerciants) {
                    if (commerciant.getCommerciant().equals(commerciantName)) {
                        category = commerciant.getType();
                        commerciantToPay = commerciant;
                        break;
                    }
                }
                Transaction transaction;
                transaction =
                        new Transaction.TransactionBuilder(command.getTimestamp(),
                                "Card payment", "payOnline")
                                .payOnline(newAmount, command.getCommerciant(), category,
                                           user.getEmail())
                                .build();
                account.getTransactions().add(transaction);

                card.pay(account, newAmount, command.getEmail(),
                        command.getTimestamp());
                /* applies the cashback */
                if (commerciantToPay != null) {
                    account.updateNrOfTransactions(commerciantToPay.getCommerciant());
                    CashbackHelper.applyCashback(commerciantToPay, account, category,
                            transaction, user, graph, command, users);
                }
                AutoUpgrade.autoUpgrade(user, account, command, graph);
                /* takes the commission from the account for the user service plan */
                takeCommission(user, account, ronAmount, commission);
            } else {
                /* creates a transaction for the case when the account
                has insufficient funds */
                payOnlineErrorTransaction(command, account, "Insufficient funds");
            }
        } else {
            /* creates a transaction for the case when the card is frozen */
            payOnlineErrorTransaction(command, account, "The card is frozen");
        }
    }

    /**
     * Create a transaction for the error of the online payment
     * @param command the command
     * @param account the account
     * @param error the error message
     */
    public static void payOnlineErrorTransaction(final Command command,
                                                 final Account account,
                                                 final String error) {
        Transaction transaction = new Transaction.TransactionBuilder(
                command.getTimestamp(), error,
                "payOnlineError")
                .payOnlineError()
                .build();
        account.getTransactions().add(transaction);
    }
}
