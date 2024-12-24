package org.poo.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bankInformation.User;
import org.poo.bankInformation.Command;
import org.poo.bankInformation.Graph;
import org.poo.bankInformation.Exchange;
import org.poo.bankInformation.Commerciant;
import org.poo.commands.commandLogic.CommandFactory;
import org.poo.commands.commandLogic.CommandInterface;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {
    private ObjectInput objectInput;
    private ObjectMapper objectMapper;
    private ArrayNode output;
    private Map<String, String> aliases = new HashMap<>();

    public Application(final ObjectInput objectInput, final ArrayNode output,
                       final ObjectMapper objectMapper) {
        this.objectInput = objectInput;
        this.output = output;
        this.objectMapper = objectMapper;
        applicationManager();
    }

    /**
     * Takes the elements from the input and uses them to create every command
     * using the CommandFactory
     */
    public void applicationManager() {
        List<User> users = new ArrayList<>();
        List<Command> commands = new ArrayList<>();
        List<Exchange> exchanges = new ArrayList<>();
        List<Commerciant> commerciants = new ArrayList<>();

        for (UserInput user : objectInput.getUsers()) {
            users.add(new User(user));
        }

        for (CommandInput command : objectInput.getCommands()) {
            commands.add(new Command(command));
        }

        for (ExchangeInput exchange : objectInput.getExchangeRates()) {
            exchanges.add(new Exchange(exchange));
        }

        for (CommerciantInput commerciant : objectInput.getCommerciants()) {
            commerciants.add(new Commerciant(commerciant));
        }

        /* Initialize the graph necessary for the exchange rate */
        Graph graph = new Graph(exchanges);

        /* Reset the ibans and the card numbers */
        Utils.resetRandom();

        for (Command command : commands) {
            CommandInterface commandInterface =
                    CommandFactory.createCommand(command, users, commerciants,
                                                 output, graph, aliases);
            if (commandInterface != null) {
                commandInterface.execute();
            }
        }
    }

    /**
     * @return the object input
     */
    public ObjectInput getObjectInput() {
        return objectInput;
    }


    /**
     * @param objectInput the object input to set
     */
    public void setObjectInput(final ObjectInput objectInput) {
        this.objectInput = objectInput;
    }

    /**
     * @return the object mapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * @param objectMapper the object mapper to set
     */
    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @return the output
     */
    public ArrayNode getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(final ArrayNode output) {
        this.output = output;
    }

    /**
     * @return the aliases
     */
    public Map<String, String> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the aliases to set
     */
    public void setAliases(final Map<String, String> aliases) {
        this.aliases = aliases;
    }
}




