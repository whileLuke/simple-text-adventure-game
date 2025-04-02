package edu.uob;

import java.util.StringTokenizer;
import java.lang.Character;

public class CommandProcessor {
    public static String processCommand(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }

        StringTokenizer tokenizer = new StringTokenizer(command, ":", true);
        if (tokenizer.countTokens() >= 3) {
            String playerName = tokenizer.nextToken();
            tokenizer.nextToken();
            String commandPart = tokenizer.nextToken();

            while (tokenizer.hasMoreTokens()) {
                StringBuilder tempBuilder = new StringBuilder();
                tempBuilder.append(commandPart);
                tempBuilder.append(tokenizer.nextToken());
                commandPart = tempBuilder.toString();
            }

            commandPart = replaceSpecialCharacters(commandPart);

            StringBuilder result = new StringBuilder();
            result.append(playerName);
            result.append(":");
            result.append(commandPart);
            return result.toString();
        } else {
            return null;
        }
    }

    private static String replaceSpecialCharacters(String text) {
        StringBuilder processedText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                processedText.append(c);
            } else {
                processedText.append(' ');
            }
        }

        String result = processedText.toString();
        result = collapseSpaces(result);

        return result.trim();
    }

    private static String collapseSpaces(String text) {
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
}