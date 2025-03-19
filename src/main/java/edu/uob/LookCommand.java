package edu.uob;

import java.util.Map;

public class LookCommand extends STAGCommand {
    @Override
    public String execute() {
        Player player = this.getPlayer();
        Location location = player.getCurrentLocation();
        StringBuilder response = new StringBuilder();
        response.append("You are in").append(location.getName()).append("\n");
        response.append(location.getDescription()).append("\n");

        if (!location.getEntityList().isEmpty()) {
            response.append("You can see:\n");
            for (GameEntity gameEntity : location.getEntityList()) {
                response.append(gameEntity.getName()).append("\n");
            }
        }

        if (!location.getPathMap().isEmpty()) {
            response.append("You can see paths to:\n");
            for (Map.Entry<String, Path> pathMap : location.getPathMap().entrySet()) {
                response.append(pathMap.getKey()).append("\n");
            }
        }
        return response.toString();
    }
}
