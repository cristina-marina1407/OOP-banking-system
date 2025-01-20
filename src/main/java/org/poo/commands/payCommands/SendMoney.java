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
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.commands.planCommands.AutoUpgrade;
import org.poo.transactions.Transaction;

import java.util.List;
import java.util.Map;

import static org.poo.commands.helperMethods.TakeCommissionHelper.takeCommission;

public class SendMoney implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private Map<String, String> aliases;
    private ArrayNode output;
    private List<Commerciant> commerciants;

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

        Account senderAccount = null;
        Account receiverAccount = null;
        User senderUser = null;
        String senderAlias = command.getAccount();
        String receiverAlias = command.getReceiver();
        for (User user : users) {
            /* checks if the accounts are referred to using an alias */
            for (Account account : user.getAccounts()) {
                if (aliases.containsKey(senderAlias) && !aliases.containsKey(receiverAlias)) {
                    if (account.getIban().equals(aliases.get(senderAlias))) {
                        senderUser = user;
                        senderAccount = account;
                    } else if (account.getIban().equals(command.getReceiver())) {
                        receiverAccount = account;
                    }
                } else if (aliases.containsKey(receiverAlias)
                        && !aliases.containsKey(senderAlias)) {
                    if (account.getIban().equals(aliases.get(receiverAlias))) {
                        receiverAccount = account;
                    } else if (account.getIban().equals(command.getReceiver())) {
                        senderUser = user;
                        senderAccount = account;
                    }
                } else if (aliases.containsKey(senderAlias)
                        && aliases.containsKey(receiverAlias)) {
                    if (account.getIban().equals(aliases.get(senderAlias))) {
                        senderUser = user;
                        senderAccount = account;
                    } else if (account.getIban().equals(aliases.get(receiverAlias))) {
                        receiverAccount = account;
                    }
                } else {
                    if (account.getIban().equals(command.getAccount())) {
                        senderUser = user;
                        senderAccount = account;
                    }
                    if (account.getIban().equals(command.getReceiver())) {
                        receiverAccount = account;
                    }
                }
            }
        }

        /* checks if the account was not found and prints an error for that case */
        if (senderAccount == null) {
            PrintOutputErrorHelper.printOutputError("Account not found", outputNode, resultNode,
                    command, output);
            return;
        }

        boolean commerciantFound = false;

        if (receiverAccount != null) {
            /* converts the amount to the currency of the receiverAccount account */
            double newAmount = graph.convert(senderAccount.getCurrency(),
                    receiverAccount.getCurrency(), command.getAmount());
            /* checks if the associate has exceeded the spending limit */
            if (senderAccount.getType().equals("business")) {
                if (senderAccount.isEmployee(command.getEmail())
                        && command.getAmount() > senderAccount.getSpendingLimit()) {
                    return;
                }
            }
            double ronAmount = graph.convert(senderAccount.getCurrency(), "RON", newAmount);
            double commission = senderUser.calculateCommission(command.getAmount(),
                                graph, senderAccount);
            /* checks if the senderAccount has enough money to make the transaction */
            if (senderAccount.getBalance() >= command.getAmount() + commission) {
                /* takes the commission from the account for the senderAccount service plan */
                takeCommission(senderUser, senderAccount, ronAmount, commission);
                senderAccount.setBalance(senderAccount.getBalance() - command.getAmount());
                receiverAccount.setBalance(receiverAccount.getBalance() + newAmount);
                /* creates the transactions for the both accounts and adds them
                 to the transactions list */
                sendMoneyTransaction(command, senderAccount, senderUser,
                                "sent", command.getAmount());
                sendMoneyTransaction(command, receiverAccount, senderUser,
                                "received", newAmount);
            } else {
                /* creates a transaction for the senderAccount account
                 in case of insufficient funds */
                sendMoneyErrorTransaction(command, senderAccount, "Insufficient funds");
            }
        } else {
            for (Commerciant commerciant : commerciants) {
                if (commerciant.getAccount().equals(command.getReceiver())) {
                    commerciantFound = true;
                    /* checks if the associate has exceeded the spending limit */
                    if (senderAccount.getType().equals("business")) {
                        if (senderAccount.isEmployee(command.getEmail())
                                && command.getAmount() > senderAccount.getSpendingLimit()) {
                            break;
                        }
                    }
                    double ronAmount = graph.convert(senderAccount.getCurrency(), "RON",
                                                     command.getAmount());
                    double commission = senderUser.calculateCommission(command.getAmount(),
                                                                       graph, senderAccount);
                    /* checks if the senderAccount has enough money to make the transaction */
                    if (senderAccount.getBalance() >= command.getAmount() + commission) {
                        /* takes the commission from the account for the senderAccount
                         service plan */
                        takeCommission(senderUser, senderAccount, ronAmount, commission);
                        senderAccount.setBalance(senderAccount.getBalance() - command.getAmount());
                        senderAccount.updateNrOfTransactions(commerciant.getCommerciant());
                        senderAccount.addToTotalSpentRON(ronAmount);
                        /* creates the transactions for the both accounts and adds them
                        to the transactions list */
                        Transaction senderTransaction;
                        senderTransaction =
                                new Transaction.TransactionBuilder(command.getTimestamp(),
                                command.getDescription(), "sendMoney")
                                .sendMoney(command.getAccount(), command.getReceiver(),
                                        command.getAmount(), "sent",
                                        senderAccount.getCurrency(), senderUser.getEmail())
                                .build();
                        senderAccount.getTransactions().add(senderTransaction);
                        if (senderAccount.getType().equals("business")
                                && !senderAccount.isEmployee(commerciant.getCommerciant())) {
                            senderAccount.addSpending(commerciant.getCommerciant(),
                                    command.getEmail(), command.getAmount());
                            senderAccount.updateTotalSpentByAssociate(command.getEmail(),
                                    command.getAmount());
                        }
                        CashbackHelper.applyCashback(commerciant, senderAccount,
                                commerciant.getType(), senderTransaction, senderUser,
                                graph, command, users);

                        AutoUpgrade.autoUpgrade(senderUser, senderAccount, command, graph);
                    } else {
                        /* creates a transaction for the senderAccount account
                         in case of insufficient funds */
                        sendMoneyErrorTransaction(command, senderAccount, "Insufficient funds");
                    }
                }
            }
            /* checks if the account was not found and prints an error for that case */
            if (!commerciantFound) {
                PrintOutputErrorHelper.printOutputError("User not found", outputNode, resultNode,
                        command, output);
            }
        }
    }

    /**
     * Creates a transaction for the senderAccount account in case of insufficient funds
     * @param command the command
     * @param account the account
     * @param error the error message
     */
    public static void sendMoneyErrorTransaction(final Command command,
                                                 final Account account,
                                                 final String error) {
        Transaction transaction = new Transaction.TransactionBuilder(
                command.getTimestamp(), error,
                "sendMoneyError")
                .sendMoneyError()
                .build();
        account.getTransactions().add(transaction);
    }

    /**
     * Creates a transaction for the senderAccount account in case of successful transaction
     * @param command the command
     * @param account the account
     * @param user the user
     */
    public static void sendMoneyTransaction(final Command command, final Account account,
                                            final User user, final String type,
                                            final double amount) {
        Transaction transaction = new Transaction.TransactionBuilder(
                command.getTimestamp(), command.getDescription(),
                "sendMoney")
                .sendMoney(command.getAccount(), command.getReceiver(), amount,
                        type, account.getCurrency(), user.getEmail())
                .build();
        account.getTransactions().add(transaction);
    }
}
