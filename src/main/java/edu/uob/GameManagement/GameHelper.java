package edu.uob.GameManagement;

import java.util.List;

public class GameHelper {

    public boolean containsWord(String gameCommand, String gameAction) {
        StringBuilder gameActionString = new StringBuilder();
        gameActionString.append(".*").append("\\b").append(gameAction).append("\\b").append(".*");
        return gameCommand.matches(gameActionString.toString());
    }


    public boolean isEntityInList(String entityName, List<String> entityList) {
        for (String entityString : entityList) {
            if (entityString.equalsIgnoreCase(entityName)) {
                return true;
            }
        }
        return false;
    }

    public String processText(String textToSanitise) {
        if (textToSanitise == null || textToSanitise.isEmpty()) return textToSanitise;

        String processedText = this.replaceSpecialCharacters(textToSanitise);
        processedText = this.collapseSpaces(processedText);
        return processedText.trim();
    }

    private String replaceSpecialCharacters(String textToReplace) {
        StringBuilder processedText = new StringBuilder();

        for (int textIndex = 0; textIndex < textToReplace.length(); textIndex++) {
            char character = textToReplace.charAt(textIndex);
            if (Character.isLetterOrDigit(character) || Character.isWhitespace(character)) {
                processedText.append(character);
            } else {
                processedText.append(' ');
            }
        }

        return processedText.toString();
    }

    private String collapseSpaces(String textToCollapseSpaces) {
        StringBuilder resultBuilder = new StringBuilder();
        boolean wasSpace = false;

        for (int textIndex = 0; textIndex < textToCollapseSpaces.length(); textIndex++) {
            char character = textToCollapseSpaces.charAt(textIndex);
            if (Character.isWhitespace(character)) {
                if (!wasSpace) {
                    resultBuilder.append(' ');
                    wasSpace = true;
                }
            } else {
                resultBuilder.append(character);
                wasSpace = false;
            }
        }

        return resultBuilder.toString();
    }
}
