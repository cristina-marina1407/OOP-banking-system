package org.poo.commands.accountCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.transactions.Transaction;

import java.util.List;

public class AddInterest implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;

    public AddInterest(final List<User> users, final Command command, final ArrayNode output) {
        this.command = command;
        this.users = users;
        this.output = output;
    }

    /**
     * Add interest to the savings account
     */
    public void execute() {
        System.out.println("Add interest");
        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                /* if the account is a savings account, add the interest */
                if (account.getType().equals("savings")) {
                    double interest = account.getBalance() * account.getInterestRate();
                    System.out.println("balance before: " + account.getBalance());

                    account.setBalance(account.getBalance() + interest);

                    System.out.println("Interest: " + interest +   " interestRate " + account.getInterestRate() + " Balance: " + account.getBalance() + " Account: " + account.getIban() + " timestamp: " + command.getTimestamp());

                    Transaction transaction;
                    transaction =
                            new Transaction.TransactionBuilder(
                                    command.getTimestamp(),
                                    "Interest rate income", "addInterest")
                                    .addInterest(interest, account.getCurrency())
                                    .build();
                    account.getTransactions().add(transaction);
                    break;
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode resultNode = objectMapper.createObjectNode();
                    resultNode.put("command", "addInterest");
                    resultNode.put("timestamp", command.getTimestamp());
                    ObjectNode outputDetails = resultNode.putObject("output");
                    outputDetails.put("description", "This is not a savings account");
                    outputDetails.put("timestamp", command.getTimestamp());
                    output.add(resultNode);
                }
            }
        }
    }
}
