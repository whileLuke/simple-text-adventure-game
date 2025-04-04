package edu.uob.CommandManagement;

import edu.uob.GameManagement.GameTracker;

public class CommandCreator {
    private final GameTracker gameTracker;

    public CommandCreator(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public GameCommand createCommand(String playerCommand) {
        String lowercaseCommand = playerCommand.toLowerCase();
        GameCommand gameCommand;

        if (containsWord(lowercaseCommand, "look")) {
            gameCommand = new LookCommand();
        } else if (containsWord(lowercaseCommand, "inv") || containsWord(lowercaseCommand, "inventory")) {
            gameCommand = new InvCommand();
        } else if (containsWord(lowercaseCommand, "get")) {
            gameCommand = new GetCommand();
        } else if (containsWord(lowercaseCommand, "drop")) {
            gameCommand = new DropCommand();
        } else if (containsWord(lowercaseCommand, "goto")) {
            gameCommand = new GotoCommand();
        } else if (containsWord(lowercaseCommand, "health")) {
            gameCommand = new HealthCommand();
        } else {
            gameCommand = new OtherCommand();
        }

        if (gameCommand.setCommand(playerCommand)) {
            gameCommand.setGameTracker(this.gameTracker);
        }

        return gameCommand;
    }

    private boolean containsWord(String text, String word) {
        StringBuilder wordChecker = new StringBuilder();
        wordChecker.append(".*\\b").append(word).append("\\b.*");
        return text.matches(wordChecker.toString());
    }
}
