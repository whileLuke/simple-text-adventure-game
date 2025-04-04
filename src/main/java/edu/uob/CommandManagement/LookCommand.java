package edu.uob.CommandManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.GamePath;
import edu.uob.EntityManagement.PlayerEntity;

import java.util.Map;

public class LookCommand extends GameCommand {
    private StringBuilder responseBuilder;
    private LocationEntity locationEntity;

    @Override
    public String executeCommand() {
        CommandComponents commandComponents = this.trimmedCommand;
        if (!commandComponents.getEntities().isEmpty()) {
            return "You can't look at entities. Just use 'look'.";
        }

        PlayerEntity currentPlayer = this.getPlayer();
        if (currentPlayer == null) return "No player found";

        this.locationEntity = currentPlayer.getPlayerLocation();
        this.responseBuilder = new StringBuilder();

        this.appendLocationInfo();
        this.appendOtherPlayersInfo(currentPlayer);
        this.appendEntitiesInfo();
        this.appendPathsInfo();

        return this.responseBuilder.toString();
    }

    private void appendLocationInfo() {
        this.responseBuilder.append("You are in ").append(this.locationEntity.getEntityName());
        this.responseBuilder.append(": ").append(this.locationEntity.getEntityDescription()).append("\n");
    }

    private void appendOtherPlayersInfo(PlayerEntity currentPlayer) {
        boolean otherPlayersPresent = false;

        for (PlayerEntity player : this.gameTracker.getPlayerMap().values()) {
            if (player != currentPlayer && player.getPlayerLocation() == this.locationEntity) {
                if (!otherPlayersPresent) {
                    this.responseBuilder.append("You can see other players:\n");
                    otherPlayersPresent = true;
                }
                this.responseBuilder.append(player.getEntityName()).append("\n");
            }
        }
    }

    private void appendEntitiesInfo() {
        if (!this.locationEntity.getEntityList().isEmpty()) {
            this.responseBuilder.append("You can see:\n");
            for (GameEntity gameEntity : this.locationEntity.getEntityList()) {
                this.responseBuilder.append(gameEntity.getEntityName()).append(": ");
                this.responseBuilder.append(gameEntity.getEntityDescription()).append("\n");
            }
        }
    }

    private void appendPathsInfo() {
        if (!this.locationEntity.getPathMap().isEmpty()) {
            this.responseBuilder.append("You can see paths to:\n");
            for (Map.Entry<String, GamePath> pathEntry : this.locationEntity.getPathMap().entrySet()) {
                this.responseBuilder.append(pathEntry.getKey()).append("\n");
            }
        }
    }
}