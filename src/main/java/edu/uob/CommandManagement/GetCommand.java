package edu.uob.CommandManagement;

import edu.uob.EntityManagement.ArtefactEntity;
import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

public class GetCommand extends GameCommand {
    @Override
    public String executeCommand() {
        if (!this.trimmedCommand.hasCommandType()) {
            return "get command is not valid.";
        }

        PlayerEntity player = this.getPlayer();
        LocationEntity currentLocation = player.getCurrentLocation();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "get only works with exactly one item.";
        }

        for (String entityName : this.trimmedCommand.getEntities()) {
            return this.tryToGetEntity(entityName, player, currentLocation);
        }

        return "The specified item is not here.";
    }

    private String tryToGetEntity(String entityName, PlayerEntity player, LocationEntity currentLocation) {
        GameEntity itemToGet = this.gameTracker.findEntityInLocation(entityName, currentLocation);

        if (itemToGet != null) {
            if (!(itemToGet instanceof ArtefactEntity)) {
                StringBuilder response = new StringBuilder();
                response.append("The item ");
                response.append(entityName);
                response.append(" cannot be taken.");
                return response.toString();
            }

            currentLocation.removeEntity(itemToGet);
            player.addToInventory(itemToGet);

            StringBuilder response = new StringBuilder();
            response.append("You picked up the ");
            response.append(entityName);
            response.append(".");
            return response.toString();
        }

        return "The item is not here.";
    }
}
