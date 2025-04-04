package edu.uob.CommandManagement;

import edu.uob.GameManagement.GameTracker;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

public abstract class GameCommand {
    protected String gameCommand;
    protected GameTracker gameTracker;
    protected String playerName;
    protected CommandComponents trimmedCommand;
    protected CommandTrimmer commandTrimmer;

    public boolean setCommand(String playerCommand) {
        if (!playerCommand.contains(":")) return false;

        int colonIndex = playerCommand.indexOf(":");
        this.playerName = playerCommand.substring(0, colonIndex).trim();
        if (!this.isValidPlayerName()) return false;

        this.gameCommand = playerCommand.substring(colonIndex + 1).trim();

        if (this.gameTracker != null) {
            if (this.commandTrimmer == null) {
                this.commandTrimmer = new CommandTrimmer(this.gameTracker);
            }
            this.trimmedCommand = this.commandTrimmer.parseCommand(this.gameCommand);
        }
        return true;
    }

    private boolean isValidPlayerName() {
        for (char character : this.playerName.toCharArray()) {
            boolean isValidCharacter = (character >= 'a' && character <= 'z') ||
                    (character >= 'A' && character <= 'Z') ||
                    character == ' ' || character == '-' || character == '\'';
            if (!isValidCharacter) return false;
        }
        return true;
    }

    public void setGameTracker(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
        this.commandTrimmer = new CommandTrimmer(this.gameTracker);

        if (this.gameCommand != null) {
            this.trimmedCommand = this.commandTrimmer.parseCommand(this.gameCommand);
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
            newPlayer.setPlayerLocation(startLocation);
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
