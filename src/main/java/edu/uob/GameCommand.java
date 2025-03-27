package edu.uob;

public abstract class GameCommand {
    protected String command;
    protected GameTracker gameTracker;
    protected String playerName;
    protected CommandTrimmer.CommandComponents trimmedCommand;



    public void setCommand(String command) {
        if (command.contains(":")) {
            String[] commandParts = command.split(":", 2);
            this.playerName = commandParts[0].trim();
            this.command = commandParts[1].trim();
        } else {
            this.playerName = "player"; // Default player name
            this.command = command.trim();
        }

        // Use the command parser to extract components
        this.trimmedCommand = new CommandTrimmer(gameTracker).parseCommand(this.command);
    }

    public String getCommand() {
        return this.command;
    }

    public void setGameTracker(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public abstract String execute();

    public Player getPlayer() {
        if (gameTracker == null) return null;
        if (!gameTracker.playerExists(playerName)) {
            Player newPlayer = new Player(playerName);
            Location startLocation = null;
            for (Location location : gameTracker.getLocationMap().values()) {
                startLocation = location;
                break;
            }
            newPlayer.setCurrentLocation(startLocation);
            this.gameTracker.addPlayer(newPlayer);
        }
        return gameTracker.getPlayer(playerName);
    }
}
