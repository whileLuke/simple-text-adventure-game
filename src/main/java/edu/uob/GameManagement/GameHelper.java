package edu.uob.GameManagement;

import java.util.List;

public class GameHelper {

    public static boolean containsWord(String commandText, String gameAction) {
        StringBuilder gameActionString = new StringBuilder();
        gameActionString.append(".*").append("\\b").append(gameAction).append("\\b").append(".*");
        return commandText.matches(gameActionString.toString());
    }


    public static boolean isEntityInList(String entityName, List<String> entityList) {
        for (String entity : entityList) {
            if (entity.equalsIgnoreCase(entityName)) {
                return true;
            }
        }
        return false;
    }

    public static void appendWithLineBreak(StringBuilder stringBuilder, String textToAppend) {
        stringBuilder.append(textToAppend).append("\n");
    }

    public static String sanitiseText(String textToSanitise) {
        if (textToSanitise == null || textToSanitise.isEmpty()) {
            return textToSanitise;
        }

        String processedText = replaceSpecialCharacters(textToSanitise);
        processedText = collapseSpaces(processedText);
        return processedText.trim();
    }

    private static String replaceSpecialCharacters(String textToReplace) {
        StringBuilder processedText = new StringBuilder();

        for (int i = 0; i < textToReplace.length(); i++) {
            char c = textToReplace.charAt(i);
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                processedText.append(c);
            } else {
                processedText.append(' ');
            }
        }

        return processedText.toString();
    }

    private static String collapseSpaces(String textToRemoveSpaces) {
        StringBuilder result = new StringBuilder();
        boolean wasSpace = false;

        for (int i = 0; i < textToRemoveSpaces.length(); i++) {
            char character = textToRemoveSpaces.charAt(i);
            if (Character.isWhitespace(character)) {
                if (!wasSpace) {
                    result.append(' ');
                    wasSpace = true;
                }
            } else {
                result.append(character);
                wasSpace = false;
            }
        }

        return result.toString();
    }
}
