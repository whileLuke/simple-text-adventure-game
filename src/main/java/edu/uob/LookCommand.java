package edu.uob;

import java.util.Map;

public class LookCommand extends GameCommand {
    @Override
    public String execute() {
        Player currentPlayer = this.getPlayer();
        if (currentPlayer == null) return "No player found";

        Location location = currentPlayer.getCurrentLocation();
        StringBuilder response = new StringBuilder();

        this.appendLocationInfo(response, location);
        this.appendOtherPlayersInfo(response, location, currentPlayer);
        this.appendEntitiesInfo(response, location);
        this.appendPathsInfo(response, location);

        return response.toString();
    }

    private void appendLocationInfo(StringBuilder response, Location location) {
        response.append("You are in ");
        response.append(location.getEntityName());
        response.append(": ");
        response.append(location.getEntityDescription());
        response.append("\n");
    }

    private void appendOtherPlayersInfo(StringBuilder response, Location location, Player currentPlayer) {
        boolean otherPlayersPresent = false;

        for (Player player : this.gameTracker.playerMap.values()) {
            if (player != currentPlayer && player.getCurrentLocation() == location) {
                if (!otherPlayersPresent) {
                    response.append("You can see other players:\n");
                    otherPlayersPresent = true;
                }
                response.append(player.getEntityName());
                response.append("\n");
            }
        }
    }

    private void appendEntitiesInfo(StringBuilder response, Location location) {
        if (!location.getEntityList().isEmpty()) {
            response.append("You can see:\n");
            for (GameEntity gameEntity : location.getEntityList()) {
                response.append(gameEntity.getEntityName());
                response.append(": ");
                response.append(gameEntity.getEntityDescription());
                response.append("\n");
            }
        }
    }

    private void appendPathsInfo(StringBuilder response, Location location) {
        if (!location.getPathMap().isEmpty()) {
            response.append("You can see paths to:\n");
            for (Map.Entry<String, Path> pathEntry : location.getPathMap().entrySet()) {
                response.append(pathEntry.getKey());
                response.append("\n");
            }
        }
    }
}