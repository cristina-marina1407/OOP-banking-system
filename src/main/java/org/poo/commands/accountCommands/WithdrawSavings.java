package org.poo.commands.accountCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Account;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.commands.helperMethods.FindHelper;
import org.poo.commands.helperMethods.PrintOutputErrorHelper;
import org.poo.commands.helperMethods.WithdrawSavingsHelper;
import static org.poo.commands.helperMethods.Constants.MINIMUM_AGE;

import java.util.List;

public class WithdrawSavings implements CommandInterface {
    private Command command;
    private List<User> users;
    private ArrayNode output;
    private Graph graph;

    public WithdrawSavings(final List<User> users, final Command command,
                           final ArrayNode output, final Graph graph) {
        this.command = command;
        this.users = users;
        this.output = output;
        this.graph = graph;
    }

    /**
     * Withdraw savings command
     */
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "withdrawSavings");
        ObjectNode outputNode = objectMapper.createObjectNode();

        boolean accountFound = false;

        for (User user : users) {
            Account account = FindHelper.findAccount(user.getAccounts(), command.getAccount());
            if (account != null) {
                accountFound = true;
                if (account.getType().equals("savings")) {
                    int age = user.calculateAge();

                    /* check if the user has the minimum age required */
                    boolean check = false;
                    if (age >= MINIMUM_AGE) {
                        boolean classicAccountFound = false;

                        for (Account classicAccount : user.getAccounts()) {
                            /* check if the user has a classic account with the same currency */
                            if (classicAccount.getType().equals("classic")
                                    && classicAccount.getCurrency().equals(command.getCurrency())) {
                                classicAccountFound = true;
                                double newAmount = graph.convert(command.getCurrency(),
                                        classicAccount.getCurrency(),
                                        command.getAmount());
                                if (account.getBalance() >= newAmount && classicAccountFound) {
                                    check = true;
                                    WithdrawSavingsHelper.withdrawal(command, account,
                                            classicAccount, newAmount);
                                    break;
                                }
                            }
                        }

                        if (!classicAccountFound) {
                            WithdrawSavingsHelper.withdrawSavingsErrorTransaction(command, account,
                                    "You do not have a classic account.");
                            return;
                        }
                        if (!check) {
                            WithdrawSavingsHelper.withdrawSavingsErrorTransaction(command, account,
                                    "Insufficient funds.");
                            return;
                        }
                    } else {
                        WithdrawSavingsHelper.withdrawSavingsErrorTransaction(command, account,
                                "You don't have the minimum age required.");
                        return;
                    }
                } else {
                    WithdrawSavingsHelper.withdrawSavingsErrorTransaction(command, account,
                            "Account is not of type savings.");
                    return;
                }
            }
        }
        /* checks if the account was not found and prints an error for that case */
        if (!accountFound) {
            PrintOutputErrorHelper.printOutputError("Account not found", outputNode, resultNode,
                    command, output);
        }
    }
}
