package org.poo.commands.payCommands;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.transactions.Transaction;

import java.util.List;
import java.util.Map;

public class SendMoney implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private Map<String, String> aliases;

    public SendMoney(final List<User> users, final Command command, final Graph graph,
                     final Map<String, String> aliases) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.aliases = aliases;
    }

    /**
     * Sends money from one account to another
     */
    public void execute() {
        Account sender = null;
        Account receiver = null;
        User senderUser = null;
        String senderAlias = command.getAccount();
        String receiverAlias = command.getReceiver();
        for (User user : users) {
            /* checks if the accounts are referred to using an alias */
            for (Account account : user.getAccounts()) {
                if (aliases.containsKey(senderAlias) && !aliases.containsKey(receiverAlias)) {
                    if (account.getIban().equals(senderAlias)) {
                        senderUser = user;
                        sender = account;
                    } else if (account.getIban().equals(command.getReceiver())) {
                        receiver = account;
                    }
                } else if (aliases.containsKey(receiverAlias)
                           && !aliases.containsKey(senderAlias)) {
                    if (account.getIban().equals(receiverAlias)) {
                        senderUser = user;
                        sender = account;
                    } else if (account.getIban().equals(command.getReceiver())) {
                        receiver = account;
                    }
                } else if (aliases.containsKey(senderAlias) && aliases.containsKey(receiverAlias)) {
                    if (account.getIban().equals(senderAlias)) {
                        senderUser = user;
                        sender = account;
                    } else if (account.getIban().equals(receiverAlias)) {
                        receiver = account;
                    }
                } else {
                    if (account.getIban().equals(command.getAccount())) {
                        senderUser = user;
                        sender = account;
                    } else if (account.getIban().equals(command.getReceiver())) {
                        receiver = account;
                    }
                }
            }
        }

        if (sender != null && receiver != null) {
            /* converts the amount to the currency of the receiver account */
            double newAmount = graph.convert(sender.getCurrency(), receiver.getCurrency(),
                               command.getAmount());
            /* checks if the sender has enough money to make the transaction */
            if (sender.getBalance() >= command.getAmount()) {
                if (senderUser.getServicePlan().equals("standard")) {
                    sender.setBalance(sender.getBalance() - newAmount * 0.2);
                }

                if (senderUser.getServicePlan().equals("silver") && command.getAmount() > 500) {
                    sender.setBalance(sender.getBalance() - newAmount * 0.1);
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
                                "sent", sender.getCurrency())
                        .build();
                receiverTransaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                        command.getDescription(), "sendMoney")
                        .sendMoney(command.getAccount(), command.getReceiver(), newAmount,
                                "received", receiver.getCurrency())
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
        }
    }
}
