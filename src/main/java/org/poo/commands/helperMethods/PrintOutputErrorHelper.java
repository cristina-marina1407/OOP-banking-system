package org.poo.commands.helperMethods;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankInformation.Command;

public final class PrintOutputErrorHelper {
    private PrintOutputErrorHelper() {

    }

    /**
     * Print the transaction error
     * @param error the error message
     */
    public static void printOutputError(final String error, final ObjectNode outputNode,
                                        final ObjectNode resultNode, final Command command,
                                        final ArrayNode output) {
        outputNode.put("description", error);
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());
        output.add(resultNode);
    }
}
