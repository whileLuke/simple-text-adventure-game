package edu.uob;

import java.util.Map;

public class LookCommand extends GameCommand {
    @Override
    public String execute() {
        Player currentPlayer = this.getPlayer();
        if (currentPlayer == null) return "No player found";
        Location location = currentPlayer.getCurrentLocation();
        StringBuilder response = new StringBuilder();
        response.append("You are in ").append(location.getEntityName()).append(": ");
        response.append(location.getEntityDescription()).append("\n");

        // List other players in this location
        boolean otherPlayersPresent = false;
        for (Player player : gameTracker.playerMap.values()) {
            if (player != currentPlayer && player.getCurrentLocation() == location) {
                if (!otherPlayersPresent) {
                    response.append("You can see other players:\n");
                    otherPlayersPresent = true;
                }
                response.append(player.getEntityName()).append("\n");
            }
        }

        if (!location.getEntityList().isEmpty()) {
            response.append("You can see:\n");
            for (GameEntity gameEntity : location.getEntityList()) {
                response.append(gameEntity.getEntityName()).append(": ");
                response.append(gameEntity.getEntityDescription()).append("\n");
            }
        }

        if (!location.getPathMap().isEmpty()) {
            response.append("You can see paths to:\n");
            for (Map.Entry<String, Path> pathEntry : location.getPathMap().entrySet()) {
                response.append(pathEntry.getKey()).append("\n");
            }
        }
        return response.toString();
    }
}
