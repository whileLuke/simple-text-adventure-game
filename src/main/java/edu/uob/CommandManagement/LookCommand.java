package edu.uob.CommandManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.GamePath;
import edu.uob.EntityManagement.PlayerEntity;

import java.util.Map;

public class LookCommand extends GameCommand {
    @Override
    public String executeCommand() {

        CommandTrimmer commandTrimmer = new CommandTrimmer(this.gameTracker);
        CommandComponents commandComponents = commandTrimmer.parseCommand(this.gameCommand);
        if (!commandComponents.getEntities().isEmpty()) {
            return "You can't look at entities. Just use 'look'.";
        }

        PlayerEntity currentPlayer = this.getPlayer();
        if (currentPlayer == null) return "No player found";

        LocationEntity location = currentPlayer.getCurrentLocation();
        StringBuilder response = new StringBuilder();

        this.appendLocationInfo(response, location);
        this.appendOtherPlayersInfo(response, location, currentPlayer);
        this.appendEntitiesInfo(response, location);
        this.appendPathsInfo(response, location);

        return response.toString();
    }

    private void appendLocationInfo(StringBuilder response, LocationEntity location) {
        response.append("You are in ").append(location.getEntityName());
        response.append(": ").append(location.getEntityDescription()).append("\n");
    }

    private void appendOtherPlayersInfo(StringBuilder response, LocationEntity location, PlayerEntity currentPlayer) {
        boolean otherPlayersPresent = false;

        for (PlayerEntity player : this.gameTracker.getPlayerMap().values()) {
            if (player != currentPlayer && player.getCurrentLocation() == location) {
                if (!otherPlayersPresent) {
                    response.append("You can see other players:\n");
                    otherPlayersPresent = true;
                }
                response.append(player.getEntityName()).append("\n");
            }
        }
    }

    private void appendEntitiesInfo(StringBuilder response, LocationEntity location) {
        if (!location.getEntityList().isEmpty()) {
            response.append("You can see:\n");
            for (GameEntity gameEntity : location.getEntityList()) {
                response.append(gameEntity.getEntityName()).append(": ");
                response.append(gameEntity.getEntityDescription()).append("\n");
            }
        }
    }

    private void appendPathsInfo(StringBuilder response, LocationEntity location) {
        if (!location.getPathMap().isEmpty()) {
            response.append("You can see paths to:\n");
            for (Map.Entry<String, GamePath> pathEntry : location.getPathMap().entrySet()) {
                response.append(pathEntry.getKey()).append("\n");
            }
        }
    }
}