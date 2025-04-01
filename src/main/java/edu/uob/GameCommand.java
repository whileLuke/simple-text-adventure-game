package edu.uob;

public abstract class GameCommand {
    protected String command;
    protected GameTracker gameTracker;
    protected String playerName;
    protected CommandTrimmer.CommandComponents trimmedCommand;

    public void setCommand(String command) {
        if (command.contains(":")) {
            String[] commandParts = command.split(":", 2);
            String possiblePlayerName = commandParts[0].trim();
            boolean isValidName = true;
            for (char c : possiblePlayerName.toCharArray()) {
                if (!((c >= 'a' && c <= 'z') ||
                        (c >= 'A' && c <= 'Z') ||
                        (c >= '0' && c <= '9') ||
                        c == ' ' || c == '-' || c == '\'')) {
                    isValidName = false;
                    break;
                }
            }
            if (isValidName) {
                this.playerName = possiblePlayerName;
            } else {
                this.playerName = "player"; //Maybe just return here
            }
            this.command = commandParts[1].trim();
        } else {
            this.playerName = "player"; // Maybe just return here
            this.command = command.trim();
        }
        if (gameTracker != null) {
            this.trimmedCommand = new CommandTrimmer(gameTracker).parseCommand(this.command);
        }
    }

    public String getCommand() {
        return this.command;
    }

    public void setGameTracker(GameTracker gameTracker) {
        this.gameTracker = gameTracker;

        // If command was previously set, re-parse with new gameTracker
        if (this.command != null) {
            this.trimmedCommand = new CommandTrimmer(gameTracker).parseCommand(this.command);
        }
    }

    public abstract String execute();

    public Player getPlayer() {
        if (gameTracker == null) return null;

        if (!gameTracker.playerExists(playerName)) {
            Player newPlayer = new Player(playerName);
            Location startLocation = null;

            // Find first available location as start location
            if (!gameTracker.getLocationMap().isEmpty()) {
                startLocation = gameTracker.getLocationMap().values().iterator().next();
            }

            if (startLocation != null) {
                newPlayer.setCurrentLocation(startLocation);
                this.gameTracker.addPlayer(newPlayer);
            } else {
                return null; // Cannot create player without a location
            }
        }

        return gameTracker.getPlayer(playerName);
    }
}

