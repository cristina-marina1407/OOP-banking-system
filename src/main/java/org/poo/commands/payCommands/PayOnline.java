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
import org.poo.commands.helperMethods.CountTransactionsHelper;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class PayOnline implements CommandInterface {
    private Command command;
    private List<User> users;
    private List<Commerciant> commerciants;
    private Graph graph;
    private ArrayNode output;
    private static final int COMISSION_SUM = 500;

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
        User user = FindHelper.findUser(users, command.getEmail());

        Card card = FindHelper.findCardByCardNumber(users, command.getCardNumber());

        /* checks if the card was not found and prints an error for that case */
        if (card == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("command", "payOnline");
            resultNode.put("timestamp", command.getTimestamp());
            ObjectNode outputDetails = resultNode.putObject("output");
            outputDetails.put("description", "Card not found");
            outputDetails.put("timestamp", command.getTimestamp());
            output.add(resultNode);
            return;
        }

        Account account = FindHelper.findAccountByCardNumber(users, command.getCardNumber());

        double newAmount = graph.convert(command.getCurrency(), account.getCurrency(),
                command.getAmount());

//       System.out.println("PRINT NOU PAYONLINE" + " card " + card.getCardNumber() + " acount " + account.getIban() + " user "
//               + user.getEmail() + " command " + command.getTimestamp());


        if (account.getType().equals("business")) {
            if (account.isEmployee(command.getEmail())
                    && newAmount > account.getSpendingLimit()) {
                System.out.println("user " + user.getEmail() + " exceeded spending limit " + account.getSpendingLimit() +
                        " timestamp " + command.getTimestamp() + " amount " + newAmount);
                return;
            }

            if (!user.getEmail().equals(account.getOwner())
                    && !account.isAssociate(command.getEmail())) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode resultNode = objectMapper.createObjectNode();
                resultNode.put("command", "payOnline");
                resultNode.put("timestamp", command.getTimestamp());
                ObjectNode outputDetails = resultNode.putObject("output");
                outputDetails.put("description", "Card not found");
                outputDetails.put("timestamp", command.getTimestamp());
                output.add(resultNode);
                return;
            }

            double ronAmount = graph.convert(account.getCurrency(), "RON", newAmount);

            System.out.println("ronAmount " + ronAmount + " timestamp " + command.getTimestamp() + " user " + user.getEmail() + " account owner " + account.getOwner() +
                    " card owner " + card.getOwner());

            User owner = FindHelper.findUser(users, account.getOwner());

            double commission = owner.calculateCommission(newAmount, graph, account);


            /* checks if the card is active and if it has funds for the payment */
            if (card.getStatus().equals("active")) {

                if (newAmount == 0) {
                    return;
                }

                if (account.getBalance() >= newAmount + commission) {
                    /* creates the transaction for the payment */
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
                                    .payOnline(newAmount, command.getCommerciant(), category, user.getEmail())
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

                    boolean upgradeCheck = CountTransactionsHelper.countTransactions(user, graph);

                    if (upgradeCheck) {
                        if (user.getServicePlan().equals("silver")) {
                            user.setServicePlan("gold");
                            Transaction transactionUpgrade = new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Upgrade plan", "upgradePlan")
                                    .upgradePlan("gold", account.getIban())
                                    .build();
                            account.getTransactions().add(transactionUpgrade);
                            System.out.println("payOnline" + "user" + user.getEmail() + " updated from silver to gold timestamp " + command.getTimestamp());
                        }
                    }

                    //System.out.println(account.getAssociates());

                    account.addSpending(commerciantName, command.getEmail(), newAmount);
                    account.updateTotalSpentByAssociate(command.getEmail(), newAmount);

                    if (owner.getServicePlan().equals("standard")) {
                        account.setBalance(account.getBalance() - commission);
                    }

                    if (owner.getServicePlan().equals("silver")
                            && ronAmount >= COMISSION_SUM) {
                        account.setBalance(account.getBalance() - commission);
                    }

                    System.out.println("business " + "timestamp " + command.getTimestamp() + " pay online account: " + account.getIban() + " email " +
                            command.getEmail() + " amount " + newAmount + " commerciant " + commerciantToPay.getCommerciant() + " strategy " +
                            commerciantToPay.getCashbackStrategy() + " comission " + commission + " plan " + owner.getServicePlan() + " balance " + account.getBalance());

                    /* check if the user can be upgraded to gold */
                    return;
                } else {
                            /* creates a transaction for the case when the account
                             has insufficient funds */
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                            "Insufficient funds", "payOnlineError")
                            .payOnlineError()
                            .build();
                    account.getTransactions().add(transaction);
                }
                return;
            } else {
                /* creates a transaction for the case when the card is frozen */
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                        "The card is frozen", "payOnlineError")
                        .payOnlineError()
                        .build();
                account.getTransactions().add(transaction);
                return;
            }
        }


                    /* daca contul nu e de business, daca cel ce incearca
                     sa foloseasca cardul nu e proprietarul cardului */

                    /* daca contul e de business verifica daca utilizatorul care
                    vrea sa faca plata apartine contului de business */

        if ((account.getType().equals("classic") || account.getType().equals("savings"))
                && !card.getOwner().equals(command.getEmail())) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("command", "payOnline");
            resultNode.put("timestamp", command.getTimestamp());
            ObjectNode outputDetails = resultNode.putObject("output");
            outputDetails.put("description", "Card not found");
            outputDetails.put("timestamp", command.getTimestamp());
            output.add(resultNode);
            return;
        }

//        System.out.println("timestamp " + command.getTimestamp() + " user " + user.getEmail() + " account owner " + account.getOwner() +
//                " card owner " + card.getOwner() + " associates " + account.getAssociates());




        double ronAmount = graph.convert(account.getCurrency(), "RON", newAmount);
        double commission = user.calculateCommission(newAmount, graph, account);
        /* checks if the card is active and if it has funds for the payment */
        if (card.getStatus().equals("active")) {

            if (newAmount == 0) {
                return;
            }

            if (account.getBalance() >= newAmount + commission) {
                /* creates the transaction for the payment */
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
                                .payOnline(newAmount, command.getCommerciant(), category, user.getEmail())
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

                boolean upgradeCheck = CountTransactionsHelper.countTransactions(user, graph);

                if (upgradeCheck) {
                    if (user.getServicePlan().equals("silver")) {
                        user.setServicePlan("gold");
                        Transaction transactionUpgrade = new Transaction.TransactionBuilder(command.getTimestamp(),
                                "Upgrade plan", "upgradePlan")
                                .upgradePlan("gold", account.getIban())
                                .build();
                        account.getTransactions().add(transactionUpgrade);
                        System.out.println("payOnline" + "user" + user.getEmail() + " updated from silver to gold timestamp " + command.getTimestamp());

                    }
                }

                System.out.println("classic " + "timestamp " + command.getTimestamp() + " pay online account: " + account.getIban() + " email " + command.getEmail() +
                        " amount " + newAmount + " commerciant " + commerciantToPay.getCommerciant() + " strategy " + commerciantToPay.getCashbackStrategy() + " comission " + commission + " plan " + user.getServicePlan());

                if (user.getServicePlan().equals("standard")) {
                    account.setBalance(account.getBalance() - commission);
                }

                if (user.getServicePlan().equals("silver")
                        && ronAmount >= COMISSION_SUM) {
                    account.setBalance(account.getBalance() - commission);
                }

                /* check if the user can be upgraded to gold */

            } else {
                            /* creates a transaction for the case when the account
                             has insufficient funds */
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                        "Insufficient funds", "payOnlineError")
                        .payOnlineError()
                        .build();
                account.getTransactions().add(transaction);
            }
        } else {
            /* creates a transaction for the case when the card is frozen */
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                    "The card is frozen", "payOnlineError")
                    .payOnlineError()
                    .build();
            account.getTransactions().add(transaction);
        }
    }
}
