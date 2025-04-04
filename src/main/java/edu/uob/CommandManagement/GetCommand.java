package edu.uob.CommandManagement;

import edu.uob.EntityManagement.ArtefactEntity;
import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

public class GetCommand extends GameCommand {
    @Override
    public String executeCommand() {
        if (this.trimmedCommand == null) return "Not a valid command.";

        if (!this.trimmedCommand.hasCommandType()) {
            return "get command is not valid.";
        }

        PlayerEntity player = this.getPlayer();
        LocationEntity currentLocation = player.getPlayerLocation();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "get only works with exactly one item.";
        }

        for (String entityName : this.trimmedCommand.getEntities()) {
            return this.tryToGetEntity(entityName, player, currentLocation);
        }

        return "That item is not here.";
    }

    private String tryToGetEntity(String entityName, PlayerEntity player, LocationEntity currentLocation) {
        GameEntity itemToGet = this.gameTracker.findEntity(entityName, currentLocation.getEntityList());

        if (itemToGet != null) {
            if (!(itemToGet instanceof ArtefactEntity)) {
                StringBuilder responseBuilder = new StringBuilder();
                responseBuilder.append("The item ").append(entityName).append(" cannot be taken.");
                return responseBuilder.toString();
            }

            currentLocation.removeEntity(itemToGet);
            player.addToInventory(itemToGet);

            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("You picked up the ").append(entityName).append(".");
            return responseBuilder.toString();
        }

        return "That item is not here.";
    }
}
