package org.poo.commands.payCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.User;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.Graph;
import org.poo.cashback.CashbackHelper;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.CountTransactionsHelper;
import org.poo.transactions.Transaction;

import java.util.List;
import java.util.Map;

public class SendMoney implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private Map<String, String> aliases;
    private ArrayNode output;
    private List<Commerciant> commerciants;
    private static final int COMISSION_SUM = 500;

    public SendMoney(final List<User> users, final Command command, final Graph graph,
                     final Map<String, String> aliases, final ArrayNode output,
                     final List<Commerciant> commerciants) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.aliases = aliases;
        this.output = output;
        this.commerciants = commerciants;
    }

    /**
     * Sends money from one account to another
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "sendMoney");
        ObjectNode outputNode = objectMapper.createObjectNode();

        //System.out.println("here " + command.getTimestamp());

        Account sender = null;
        Account receiver = null;
        User senderUser = null;
        String senderAlias = command.getAccount();
        String receiverAlias = command.getReceiver();
        for (User user : users) {
            /* checks if the accounts are referred to using an alias */
            for (Account account : user.getAccounts()) {
                if (aliases.containsKey(senderAlias) && !aliases.containsKey(receiverAlias)) {
                    if (account.getIban().equals(aliases.get(senderAlias))) {
                        senderUser = user;
                        sender = account;
                    } else if (account.getIban().equals(command.getReceiver())) {
                        receiver = account;
                    }
                } else if (aliases.containsKey(receiverAlias)
                        && !aliases.containsKey(senderAlias)) {
                    if (account.getIban().equals(aliases.get(receiverAlias))) {
                        receiver = account;
                    } else if (account.getIban().equals(command.getReceiver())) {
                        senderUser = user;
                        sender = account;
                    }
                } else if (aliases.containsKey(senderAlias) && aliases.containsKey(receiverAlias)) {
                    if (account.getIban().equals(aliases.get(senderAlias))) {
                        senderUser = user;
                        sender = account;
                    } else if (account.getIban().equals(aliases.get(receiverAlias))) {
                        receiver = account;
                    }
                } else {
                    if (account.getIban().equals(command.getAccount())) {
                        senderUser = user;
                        sender = account;
                    }
                    if (account.getIban().equals(command.getReceiver())) {
                        receiver = account;
                    }
                }
            }
        }

        if (sender == null) {
            outputNode.put("description", "User not found");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
            return;
        }

        boolean commerciantFound = false;

        if (receiver != null) {
            /* converts the amount to the currency of the receiver account */
            double newAmount = graph.convert(sender.getCurrency(), receiver.getCurrency(),
                    command.getAmount());

            if (sender.getType().equals("business")) {
                if (sender.isEmployee(command.getEmail())
                        && command.getAmount() > sender.getSpendingLimit()) {
                    return;
                }
            }

            double ronAmount = graph.convert(sender.getCurrency(), "RON", newAmount);
            double commission = senderUser.calculateCommission(command.getAmount(), graph, sender);
            /* checks if the sender has enough money to make the transaction */
            if (sender.getBalance() >= command.getAmount() + commission) {
                if (senderUser.getServicePlan().equals("standard")) {
                    sender.setBalance(sender.getBalance() - commission);
                }

                if (senderUser.getServicePlan().equals("silver")
                        && ronAmount >= COMISSION_SUM) {
                    sender.setBalance(sender.getBalance() - commission);
                }

                sender.setBalance(sender.getBalance() - command.getAmount());

                receiver.setBalance(receiver.getBalance() + newAmount);

                /* creates the transactions for the both accounts and adds them
                 to the transactions list */
                Transaction senderTransaction;
                Transaction receiverTransaction;
                senderTransaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                        command.getDescription(), "sendMoney")
                        .sendMoney(command.getAccount(), command.getReceiver(), command.getAmount(),
                                "sent", sender.getCurrency(), senderUser.getEmail())
                        .build();
                receiverTransaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                        command.getDescription(), "sendMoney")
                        .sendMoney(command.getAccount(), command.getReceiver(), newAmount,
                                "received", receiver.getCurrency(), senderUser.getEmail())
                        .build();
                sender.getTransactions().add(senderTransaction);
                receiver.getTransactions().add(receiverTransaction);

            } else {
                /* creates a transaction for the sender account in case of insufficient funds */
                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                        "Insufficient funds", "sendMoney")
                        .sendMoneyError()
                        .build();
                sender.getTransactions().add(transaction);
            }
        } else {
            for (Commerciant commerciant : commerciants) {
                if (commerciant.getAccount().equals(command.getReceiver())) {
                    commerciantFound = true;
                    if (sender.getType().equals("business")) {
                        if (sender.isEmployee(command.getEmail())
                                && command.getAmount() > sender.getSpendingLimit()) {
                            break;
                        }
                    }

                    double ronAmount = graph.convert(sender.getCurrency(), "RON", command.getAmount());
                    double commission = senderUser.calculateCommission(command.getAmount(), graph, sender);
                    /* checks if the sender has enough money to make the transaction */
                    if (sender.getBalance() >= command.getAmount() + commission) {
                        if (senderUser.getServicePlan().equals("standard")) {
                            sender.setBalance(sender.getBalance() - commission);
                        }

                        if (senderUser.getServicePlan().equals("silver")
                                && ronAmount >= COMISSION_SUM) {
                            sender.setBalance(sender.getBalance() - commission);
                        }

                        sender.setBalance(sender.getBalance() - command.getAmount());

                        sender.updateNrOfTransactions(commerciant.getCommerciant());

                        sender.addToTotalSpentRON(ronAmount);

                /* creates the transactions for the both accounts and adds them
                 to the transactions list */

                        Transaction senderTransaction;
                        senderTransaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                command.getDescription(), "sendMoney")
                                .sendMoney(command.getAccount(), command.getReceiver(), command.getAmount(),
                                        "sent", sender.getCurrency(), senderUser.getEmail())
                                .build();
                        sender.getTransactions().add(senderTransaction);

                        if (sender.getType().equals("business") && !sender.isEmployee(commerciant.getCommerciant())) {
                            sender.addSpending(commerciant.getCommerciant(), command.getEmail(), command.getAmount());
                            sender.updateTotalSpentByAssociate(command.getEmail(), command.getAmount());
                        }

                        System.out.println("business " + "timestamp " + command.getTimestamp() + " sendMoney account: " + sender.getIban() + " email " +
                                command.getEmail() + " amount " + command.getAmount() + " commerciant " + commerciant.getCommerciant() + " strategy " +
                                commerciant.getCashbackStrategy() + " comission " + commission + " plan " + senderUser.getServicePlan());

                        CashbackHelper.applyCashback(commerciant, sender, commerciant.getType(),
                                senderTransaction, senderUser, graph, command, users);

                        boolean upgradeCheck = CountTransactionsHelper.countTransactions(senderUser, graph);

                        if (upgradeCheck) {
                            if (senderUser.getServicePlan().equals("silver")) {
                                senderUser.setServicePlan("gold");
                                Transaction transactionUpgrade = new Transaction.TransactionBuilder(command.getTimestamp(),
                                        "Upgrade plan", "upgradePlan")
                                        .upgradePlan("gold", sender.getIban())
                                        .build();
                                sender.getTransactions().add(transactionUpgrade);
                                System.out.println("user" + senderUser.getEmail() + " updated from silver to gold timestamp " + command.getTimestamp());
                            }
                        }
                    } else {
                        /* creates a transaction for the sender account in case of insufficient funds */
                        Transaction transaction;
                        transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                "Insufficient funds", "sendMoney")
                                .sendMoneyError()
                                .build();
                        sender.getTransactions().add(transaction);
                    }
                }
            }
            if (!commerciantFound) {
                outputNode.put("description", "User not found");
                outputNode.put("timestamp", command.getTimestamp());
                resultNode.set("output", outputNode);
                resultNode.put("timestamp", command.getTimestamp());
                output.add(resultNode);
            }
        }
    }
}
