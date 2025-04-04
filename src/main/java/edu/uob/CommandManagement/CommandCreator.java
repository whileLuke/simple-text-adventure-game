package edu.uob.CommandManagement;

import edu.uob.GameManagement.GameHelper;
import edu.uob.GameManagement.GameTracker;

public class CommandCreator {
    private final GameTracker gameTracker;
    private final GameHelper gameHelper;

    public CommandCreator(GameTracker gameTracker, GameHelper gameHelper) {
        this.gameTracker = gameTracker;
        this.gameHelper = gameHelper;
    }

    public GameCommand createCommand(String playerCommand) {
        String lowercaseCommand = playerCommand.toLowerCase();
        GameCommand gameCommand;

        if (this.gameHelper.containsWord(lowercaseCommand, "look")) {
            gameCommand = new LookCommand();
        } else if (this.gameHelper.containsWord(lowercaseCommand, "inv") ||
                this.gameHelper.containsWord(lowercaseCommand, "inventory")) {
            gameCommand = new InvCommand();
        } else if (this.gameHelper.containsWord(lowercaseCommand, "get")) {
            gameCommand = new GetCommand();
        } else if (this.gameHelper.containsWord(lowercaseCommand, "drop")) {
            gameCommand = new DropCommand();
        } else if (this.gameHelper.containsWord(lowercaseCommand, "goto")) {
            gameCommand = new GotoCommand();
        } else if (this.gameHelper.containsWord(lowercaseCommand, "health")) {
            gameCommand = new HealthCommand();
        } else {
            gameCommand = new CustomCommand();
        }

        if (!gameCommand.setCommand(playerCommand)) return null;

        gameCommand.setGameTracker(this.gameTracker);
        return gameCommand;
    }
}
