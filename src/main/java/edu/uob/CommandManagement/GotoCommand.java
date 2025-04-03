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

        PlayerEntity player = getPlayer();
        LocationEntity currentLocation = player.getCurrentLocation();

        for (String locationName : trimmedCommand.getEntities()) {
            GamePath gamePath = currentLocation.getPath(locationName);

            if (gamePath == null) {
                StringBuilder message = new StringBuilder();
                message.append("You can't go to ").append(locationName);
                return message.toString();
            }
            LocationEntity destination = gamePath.pathTo();
            player.setCurrentLocation(destination);

            StringBuilder response = new StringBuilder();
            response.append("You have gone to ").append(destination.getEntityName());
            response.append(": ").append(destination.getEntityDescription());
            return response.toString();
        }

        return "You can't go there.";
    }
}

