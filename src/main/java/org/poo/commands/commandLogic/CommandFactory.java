package org.poo.commands.commandLogic;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Commerciant;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.User;
import org.poo.commands.accountCommands.AddAccount;
import org.poo.commands.accountCommands.AddFunds;
import org.poo.commands.accountCommands.DeleteAccount;
import org.poo.commands.accountCommands.SetMinimumBalance;
import org.poo.commands.accountCommands.WithdrawSavings;
import org.poo.commands.accountCommands.SetAlias;
import org.poo.commands.accountCommands.AddInterest;
import org.poo.commands.accountCommands.ChangeInterestRate;
import org.poo.commands.cardCommands.CheckCardStatus;
import org.poo.commands.cardCommands.CreateCard;
import org.poo.commands.cardCommands.CreateOneTimeCard;
import org.poo.commands.cardCommands.DeleteCard;
import org.poo.commands.payCommands.CashWithdrawal;
import org.poo.commands.payCommands.PayOnline;
import org.poo.commands.payCommands.SendMoney;
import org.poo.commands.payCommands.SplitPayment;
import org.poo.commands.planCommands.UpgradePlan;
import org.poo.commands.printCommands.PrintTransactions;
import org.poo.commands.printCommands.PrintUsers;
import org.poo.commands.reportCommands.Report;
import org.poo.commands.reportCommands.SpendingReport;

import java.util.List;
import java.util.Map;

/**
 * Factory for creating commands
 */
public final  class CommandFactory {
    private CommandFactory() {

    }

    /**
     * Creates a command based on the command type
     * @param command the command to create
     * @param users the list of users
     * @param output the output array
     * @param graph the graph of exchange rates
     * @param aliases the map of aliases
     * @return the command
     */
    public static CommandInterface createCommand(final Command command, final List<User> users,
                                                 final List<Commerciant> commerciants,
                                                 final ArrayNode output, final Graph graph,
                                                 final Map<String, String> aliases) {
        switch (command.getCommand()) {
            case "printUsers":
                return new PrintUsers(users, output, command);
            case "addAccount":
                return new AddAccount(users, command);
            case "addFunds":
                return new AddFunds(users, command);
            case "createCard":
                return new CreateCard(users, command);
            case "createOneTimeCard":
                return new CreateOneTimeCard(users, command);
            case "deleteCard":
                return new DeleteCard(users, command);
            case "deleteAccount":
                return new DeleteAccount(users, command, output);
            case "payOnline":
                return new PayOnline(users, command, graph, output, commerciants);
            case "sendMoney":
                return new SendMoney(users, command, graph, aliases, output, commerciants);
            case "setAlias":
                return new SetAlias(command, aliases);
            case "printTransactions":
                return new PrintTransactions(users, command, output);
            case "setMinimumBalance":
                return new SetMinimumBalance(users, command);
            case "checkCardStatus":
                return new CheckCardStatus(users, command, output);
            case "splitPayment":
                return new SplitPayment(users, command, graph);
            case "addInterest":
                return new AddInterest(users, command, output);
            case "changeInterestRate":
                return new ChangeInterestRate(users, command, output);
            case "report":
                return new Report(users, command, output);
            case "spendingsReport":
                return new SpendingReport(users, command, output);
            case "withdrawSavings":
                return new WithdrawSavings(users, command, output);
            case "upgradePlan":
                return new UpgradePlan(users, command, output, graph);
            case "cashWithdrawal":
                return new CashWithdrawal(users, command, output, graph);
            default:
                return null;
        }
    }
}
