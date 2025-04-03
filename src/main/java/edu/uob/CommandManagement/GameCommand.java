package edu.uob.CommandManagement;

import edu.uob.GameManagement.GameTracker;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

public abstract class GameCommand {
    protected String gameCommand;
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
            this.gameCommand = command.substring(colonIndex + 1).trim();
        } else return false;

        if (this.gameTracker != null) {
            this.trimmedCommand = new CommandTrimmer(this.gameTracker).parseCommand(this.gameCommand);
        }
        return true;
    }

    private boolean isValidPlayerName(String name) {
        for (char c : name.toCharArray()) {
            if (!((c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    c == ' ' || c == '-' || c == '\'')) {
                return false;
            }
        }
        return true;
    }

    public String getGameCommand() {
        return this.gameCommand;
    }

    public void setGameTracker(GameTracker gameTracker) {
        this.gameTracker = gameTracker;

        if (this.gameCommand != null) {
            this.trimmedCommand = new CommandTrimmer(gameTracker).parseCommand(this.gameCommand);
        }
    }

    public abstract String executeCommand();

    public PlayerEntity getPlayer() {
        if (this.gameTracker == null) return null;

        if (!this.gameTracker.playerExists(this.playerName)) {
            return this.createNewPlayer();
        }

        return this.gameTracker.getPlayer(this.playerName);
    }

    private PlayerEntity createNewPlayer() {
        PlayerEntity newPlayer = new PlayerEntity(this.playerName);
        LocationEntity startLocation = this.findStartLocation();

        if (startLocation != null) {
            newPlayer.setCurrentLocation(startLocation);
            this.gameTracker.addPlayer(newPlayer);
            return newPlayer;
        }

        return null;
    }

    private LocationEntity findStartLocation() {
        if (!this.gameTracker.getLocationMap().isEmpty()) {
            return this.gameTracker.getLocationMap().values().iterator().next();
        }
        return null;
    }
}
