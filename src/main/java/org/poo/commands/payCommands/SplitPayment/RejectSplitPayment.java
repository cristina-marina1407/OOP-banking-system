package org.poo.commands.payCommands.SplitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class RejectSplitPayment implements CommandInterface {
    private Command command;
    private List<User> users;
    private Graph graph;
    private final SplitPaymentManager splitPaymentManager;
    private ArrayNode output;

    public RejectSplitPayment(final List<User> users, final Command command,
                              final Graph graph, final SplitPaymentManager splitPaymentManager,
                              final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.graph = graph;
        this.splitPaymentManager = splitPaymentManager;
        this.output = output;
    }

    /**
     * Rejects a split payment
     */
    public void execute() {
        //System.out.println("here");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "rejectSplitPayment");
        ObjectNode outputNode = objectMapper.createObjectNode();

        int userFound = 0;

        List<SplitPaymentObject> activeSplitPayments
                = splitPaymentManager.getActiveSplitPayments();

        /* uses an auxiliary list to avoid ConcurrentModificationException */
        SplitPaymentObject paymentToRemove = null;

        for (SplitPaymentObject splitPayment : activeSplitPayments) {
            if (splitPayment != null) {
                List<String> accounts = splitPayment.getCommand().getAccounts();
                Account printAccount = null;
                for (String account : accounts) {
                    User user = FindHelper.findUser(users, command.getEmail());
                    if (user != null) {
                        userFound = 1;
                        for (Account userAccount : user.getAccounts()) {
                            /* add the split payment to the list after a reject */
                            if (userAccount.getIban().equals(account)) {
                                paymentToRemove = splitPayment;
                                printAccount = userAccount;
                                break;
                            }
                        }
                    }
                }
            }
        }

        List<Account> involvedAccounts = new ArrayList<>();

        if (paymentToRemove != null) {
            for (User user : users) {
                for (String iban : paymentToRemove.getCommand().getAccounts()) {
                    for (Account account : user.getAccounts()) {
                        if (account.getIban().equals(iban)) {
                            involvedAccounts.add(account);
                        }
                    }
                }
            }
        }

        if (paymentToRemove != null) {
            for (Account account : involvedAccounts) {
                Transaction transaction;
                double newAmount = graph.convert(paymentToRemove.getCommand().getCurrency(),
                        account.getCurrency(), paymentToRemove.getCommand().getAmount());
                String formattedAmount = String.format("%.2f %s", paymentToRemove.getCommand().getAmount(),
                        paymentToRemove.getCommand().getCurrency());
                transaction = new Transaction.TransactionBuilder(paymentToRemove
                        .getCommand().getTimestamp(),
                        "Split payment of " + formattedAmount, "splitPayment")
                        .splitPaymentError(paymentToRemove.getCommand().getCurrency(),
                                newAmount, paymentToRemove.getCommand().getAccounts(),
                                null,
                                paymentToRemove.getSplitPaymentType(), paymentToRemove.getCommand().getAmountForUsers(),
                                "One user rejected the payment.")
                        .build();
                account.getTransactions().add(transaction);
            }
        }

        if (userFound == 0) {
            outputNode.put("description", "User not found");
            outputNode.put("timestamp", command.getTimestamp());
            resultNode.set("output", outputNode);
            resultNode.put("timestamp", command.getTimestamp());
            output.add(resultNode);
            return;
        }
        activeSplitPayments.remove(paymentToRemove);
    }

    public List<Account> findAccountsByIban(List<String> ibans, List<Account> allAccounts) {
        List<Account> involvedAccounts = new ArrayList<>();
        for (String iban : ibans) {
            for (Account account : allAccounts) {
                if (account.getIban().equals(iban)) {
                    involvedAccounts.add(account);
                    break;
                }
            }
        }
        return involvedAccounts;
    }

}
