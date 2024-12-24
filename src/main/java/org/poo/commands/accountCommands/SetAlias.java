package org.poo.commands.accountCommands;

import org.poo.bankInformation.Command;
import org.poo.commands.commandLogic.CommandInterface;

import java.util.Map;

public class SetAlias implements CommandInterface {
    private Command command;
    private Map<String, String> aliases;

    public SetAlias(final Command command, final Map<String, String> aliases) {
        this.command = command;
        this.aliases = aliases;
    }

    /**
     * Set the alias for the account
     */
    public void execute() {
        aliases.put(command.getAlias(), command.getAccount());
    }
}
