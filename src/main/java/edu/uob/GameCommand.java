package edu.uob;

public abstract class GameCommand {
    protected String command;
    protected GameTracker gameTracker;
    protected String playerName;
    protected CommandComponents trimmedCommand;

    public boolean setCommand(String command) {
        if (command.contains(":")) {
            int colonIndex = command.indexOf(":");
            String possiblePlayerName = command.substring(0, colonIndex).trim();

            if (this.isValidPlayerName(possiblePlayerName)) {
                this.playerName = possiblePlayerName;
            } else return false;
            this.command = command.substring(colonIndex + 1).trim();
        } else return false;

        if (this.gameTracker != null) {
            this.trimmedCommand = new CommandTrimmer(this.gameTracker).parseCommand(this.command);
        }
        return true;
    }

    private boolean isValidPlayerName(String name) {
        for (char c : name.toCharArray()) {
            if (!((c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= '0' && c <= '9') ||
                    c == ' ' || c == '-' || c == '\'')) {
                return false;
            }
        }
        return true;
    }

    public String getCommand() {
        return this.command;
    }

    public void setGameTracker(GameTracker gameTracker) {
        this.gameTracker = gameTracker;

        if (this.command != null) {
            this.trimmedCommand = new CommandTrimmer(gameTracker).parseCommand(this.command);
        }
    }

    public abstract String execute();

    public Player getPlayer() {
        if (this.gameTracker == null) return null;

        if (!this.gameTracker.playerExists(this.playerName)) {
            return this.createNewPlayer();
        }

        return this.gameTracker.getPlayer(this.playerName);
    }

    private Player createNewPlayer() {
        Player newPlayer = new Player(this.playerName);
        Location startLocation = this.findStartLocation();

        if (startLocation != null) {
            newPlayer.setCurrentLocation(startLocation);
            this.gameTracker.addPlayer(newPlayer);
            return newPlayer;
        }

        return null;
    }

    private Location findStartLocation() {
        if (!this.gameTracker.getLocationMap().isEmpty()) {
            return this.gameTracker.getLocationMap().values().iterator().next();
        }
        return null;
    }
}
