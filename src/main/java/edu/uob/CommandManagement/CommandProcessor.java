package edu.uob.CommandManagement;

import edu.uob.GameManagement.GameHelper;

import java.util.StringTokenizer;

public class CommandProcessor {
    public String processCommand(String commandToProcess) {
        if (commandToProcess == null || commandToProcess.isEmpty()) {
            return commandToProcess;
        }

        StringTokenizer stringTokeniser = new StringTokenizer(commandToProcess, ":", true);
        if (stringTokeniser.countTokens() < 3) return null;

        String playerName = stringTokeniser.nextToken();
        stringTokeniser.nextToken();
        String commandPart = this.extractCommandPart(stringTokeniser);

        commandPart = GameHelper.sanitiseText(commandPart);

        return this.formatProcessedCommand(playerName, commandPart);
    }

    private String extractCommandPart(StringTokenizer stringTokeniser) {
        StringBuilder commandBuilder = new StringBuilder(stringTokeniser.nextToken());
        while (stringTokeniser.hasMoreTokens()) commandBuilder.append(stringTokeniser.nextToken());

        return commandBuilder.toString();
    }

    private String formatProcessedCommand(String playerName, String commandPart) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(playerName).append(":").append(commandPart);
        return responseBuilder.toString();
    }
}