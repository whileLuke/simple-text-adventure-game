package edu.uob.CommandManagement;

import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.GamePath;
import edu.uob.EntityManagement.PlayerEntity;

public class GotoCommand extends GameCommand {
    @Override
    public String executeCommand() {
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid goto command.";
        }

        if (trimmedCommand.getEntities().size() != 1) {
            return "goto only works with exactly one location.";
        }

        PlayerEntity playerEntity = getPlayer();
        LocationEntity playerLocation = playerEntity.getPlayerLocation();

        for (String locationName : trimmedCommand.getEntities()) {
            GamePath gamePath = playerLocation.getPath(locationName);

            if (gamePath == null) {
                StringBuilder responseBuilder = new StringBuilder();
                responseBuilder.append("You can't go to ").append(locationName);
                return responseBuilder.toString();
            }
            LocationEntity destination = gamePath.pathTo();
            playerEntity.setPlayerLocation(destination);

            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("You have gone to ").append(destination.getEntityName());
            responseBuilder.append(": ").append(destination.getEntityDescription());
            return responseBuilder.toString();
        }

        return "You can't go there.";
    }
}

