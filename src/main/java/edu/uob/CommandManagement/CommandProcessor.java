package edu.uob.CommandManagement;

import java.util.StringTokenizer;
import java.lang.Character;

public class CommandProcessor {
    public String processCommand(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }

        StringTokenizer tokenizer = new StringTokenizer(command, ":", true);
        if (tokenizer.countTokens() < 3) {
            return null;
        }

        String playerName = tokenizer.nextToken();
        tokenizer.nextToken(); // Skip delimiter
        String commandPart = this.extractFullCommandPart(tokenizer);

        commandPart = this.sanitizeCommandText(commandPart);

        return this.formatProcessedCommand(playerName, commandPart);
    }

    private String extractFullCommandPart(StringTokenizer tokenizer) {
        String commandPart = tokenizer.nextToken();

        while (tokenizer.hasMoreTokens()) {
            StringBuilder commandPartBuilder = new StringBuilder();
            commandPartBuilder.append(commandPart);
            commandPartBuilder.append(tokenizer.nextToken());
            commandPart = commandPartBuilder.toString();
        }

        return commandPart;
    }

    private String sanitizeCommandText(String text) {
        String processedText = this.replaceSpecialCharacters(text);
        processedText = this.collapseSpaces(processedText);
        return processedText.trim();
    }

    private String replaceSpecialCharacters(String text) {
        StringBuilder processedText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                processedText.append(c);
            } else {
                processedText.append(' ');
            }
        }

        return processedText.toString();
    }

    private String collapseSpaces(String text) {
        StringBuilder result = new StringBuilder();
        boolean wasSpace = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!wasSpace) {
                    result.append(' ');
                    wasSpace = true;
                }
            } else {
                result.append(c);
                wasSpace = false;
            }
        }

        return result.toString();
    }

    private String formatProcessedCommand(String playerName, String commandPart) {
        StringBuilder response = new StringBuilder();
        response.append(playerName);
        response.append(":");
        response.append(commandPart);
        return response.toString();
    }
}