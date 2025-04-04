package edu.uob.CommandManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.PlayerEntity;

public class DropCommand extends GameCommand {
    @Override
    public String executeCommand() {
        if (this.trimmedCommand == null) return "This command isn't valid.";

        if (!this.trimmedCommand.hasCommandType()) {
            return "drop command is not valid.";
        }

        PlayerEntity player = this.getPlayer();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "drop only works with exactly one item.";
        }

        String itemName = this.gameCommand.toLowerCase().replace("drop", "").trim();
        if (itemName.isEmpty()) {
            return "You need to provide an item name.";
        }

        for (String entityName : this.trimmedCommand.getEntities()) {
            return this.tryToDropEntity(entityName, player);
        }

        return "No item was found.";
    }

    private String tryToDropEntity(String entityName, PlayerEntity player) {
        GameEntity itemToDrop = this.gameTracker.findEntity(entityName, player.getPlayerInventory());

        if (itemToDrop != null) {
            player.removeFromInventory(itemToDrop);
            player.getPlayerLocation().addEntity(itemToDrop);

            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("You dropped the ").append(itemToDrop.getEntityName());
            return responseBuilder.toString();
        }

        return "You can't drop an item you don't have.";
    }
}

