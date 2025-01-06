package org.poo.commands.payCommands.SplitPayment;

import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class AcceptSplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private final SplitPaymentManager splitPaymentManager;

    public AcceptSplitPayment(final List<User> users, final Command command, final Graph graph, final SplitPaymentManager splitPaymentManager) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.splitPaymentManager = splitPaymentManager;
    }

    public void execute() {
        List<SplitPaymentObject> activeSplitPayments = splitPaymentManager.getActiveSplitPayments();

        for (SplitPaymentObject splitPayment : activeSplitPayments) {
            if (splitPayment != null) {
                List<String> accounts = splitPayment.getCommand().getAccounts();
//                for (String account : accounts) {
//                    System.out.println(account);
//                }
                for (String account : accounts) {
                    User user = FindHelper.findUser(users, command.getEmail());
                    if (user != null) {
                        for (Account userAccount : user.getAccounts()) {
                            if (userAccount.getIban().equals(account)) {
                                splitPayment.addAcceptedUser(account);
                            }
                        }
                    }
                }

                if (splitPayment.isFullyAccepted(accounts)) {
                    processPayment(splitPayment);
                }
            }
        }
    }

    private void processPayment(SplitPaymentObject splitPaymentObject) {
        String splitPaymentType = splitPaymentObject.getSplitPaymentType();
        Command command = splitPaymentObject.getCommand();
        List<String> splitAccounts = splitPaymentObject.getCommand().getAccounts();
        List<Double> amounts = command.getAmountForUsers();
        Account accountWithInsufficientFunds = null;

        if (splitPaymentType.equals("equal")) {
            double amount = command.getAmount();
            double moneySplit = amount / splitAccounts.size();
            int checkAccounts = 0;
            for (String iban : splitAccounts) {
                for (User user : users) {
                    Account account = FindHelper.findAccount(user.getAccounts(), iban);
                    if (account != null) {
                        double newAmount = graph.convert(splitPaymentObject.getCommand().getCurrency(),
                                account.getCurrency(), moneySplit);
                        if (account.getBalance() < newAmount) {
                            accountWithInsufficientFunds = account;
                            checkAccounts++;
                        }
                    }
                }
            }

            for (String iban : splitAccounts) {
                for (User user : users) {
                    Account account = FindHelper.findAccount(user.getAccounts(), iban);
                    if (account != null) {
                        double newAmount = graph.convert(splitPaymentObject.getCommand().getCurrency(),
                                account.getCurrency(), moneySplit);
                        if (checkAccounts > 0) {
                            Transaction transaction;
                            String formattedAmount = String.format("%.2f %s", command.getAmount(),
                                    command.getCurrency());
                            transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Split payment of " + formattedAmount, "splitPayment")
                                    .splitPaymentError(splitPaymentObject.getCommand().getCurrency(),
                                            moneySplit, splitAccounts, accountWithInsufficientFunds.getIban())
                                    .build();
                            account.getTransactions().add(transaction);
                        } else {
                            account.setBalance(account.getBalance() - newAmount);

                            /*formatare*/
                            String formatted = String.format("%.2f", account.getBalance());
                            double formattedBalance = Double.parseDouble(formatted);
                            account.setBalance(formattedBalance);

                            Transaction transaction;
                            String formattedAmount = String.format("%.2f %s", newAmount,
                                    command.getCurrency());
                            transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Split payment of " + formattedAmount, "splitPayment")
                                    .splitPayment(splitPaymentObject.getCommand().getCurrency(),
                                            moneySplit, splitAccounts, splitPaymentType, amounts)
                                    .build();
                            account.getTransactions().add(transaction);
                        }
                    }
                }
            }

        } else if (splitPaymentType.equals("custom")) {
            for (int i = 0; i < splitAccounts.size(); i++) {
                String iban = splitAccounts.get(i);
                double customAmount = amounts.get(i);
                for (User user : users) {
                    Account account = FindHelper.findAccount(user.getAccounts(), iban);
                    if (account != null) {
                        double newAmount = graph.convert(splitPaymentObject.getCommand().getCurrency(),
                                account.getCurrency(), customAmount);
                        if (account.getBalance() < newAmount) {
                            accountWithInsufficientFunds = account;
                        }
                    }
                }
            }

            for (int i = 0; i < splitAccounts.size(); i++) {
                String iban = splitAccounts.get(i);
                double customAmount = amounts.get(i);
                for (User user : users) {
                    Account account = FindHelper.findAccount(user.getAccounts(), iban);
                    if (account != null) {
                        double newAmount = graph.convert(splitPaymentObject.getCommand().getCurrency(),
                                account.getCurrency(), customAmount);
                        if (accountWithInsufficientFunds != null) {
                            Transaction transaction;
                            String formattedAmount = String.format("%.2f %s", command.getAmount(),
                                    command.getCurrency());
                            transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Split payment of " + formattedAmount, "splitPayment")
                                    .splitPaymentError(splitPaymentObject.getCommand().getCurrency(),
                                            customAmount, splitAccounts, accountWithInsufficientFunds.getIban())
                                    .build();
                            account.getTransactions().add(transaction);
                        } else {
                            account.setBalance(account.getBalance() - customAmount);

                            String formatted = String.format("%.2f", account.getBalance());
                            double formattedBalance = Double.parseDouble(formatted);
                            account.setBalance(formattedBalance);

                            Transaction transaction;
                            String formattedAmount = String.format("%.2f %s", command.getAmount(),
                                    command.getCurrency());
                            transaction = new Transaction.TransactionBuilder(command.getTimestamp(),
                                    "Split payment of " + formattedAmount, "splitPayment")
                                    .splitPayment(splitPaymentObject.getCommand().getCurrency(), customAmount,
                                            splitAccounts, splitPaymentType, amounts)
                                    .build();
                            account.getTransactions().add(transaction);
                        }
                    }
                }
            }
        }
    }
}
