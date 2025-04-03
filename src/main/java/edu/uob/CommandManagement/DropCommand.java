package edu.uob.CommandManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.PlayerEntity;

public class DropCommand extends GameCommand {
    @Override
    public String executeCommand() {
        if (!this.trimmedCommand.hasCommandType()) {
            return "drop command is not valid.";
        }

        PlayerEntity player = this.getPlayer();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "drop only works with exactly one item.";
        }

        String itemName = this.command.toLowerCase().replace("drop", "").trim();
        if (itemName.isEmpty()) {
            return "You need to provide an item name.";
        }

        for (String entityName : this.trimmedCommand.getEntities()) {
            return this.tryToDropEntity(entityName, player);
        }

        return "No item was found.";
    }

    private String tryToDropEntity(String entityName, PlayerEntity player) {
        GameEntity itemToDrop = this.gameTracker.findEntityInInventory(entityName, player);

        if (itemToDrop != null) {
            player.removeFromInventory(itemToDrop);
            player.getCurrentLocation().addEntity(itemToDrop);

            StringBuilder response = new StringBuilder();
            response.append("You dropped the ").append(itemToDrop.getEntityName());
            return response.toString();
        }

        return "You can't drop an item you don't have.";
    }
}

