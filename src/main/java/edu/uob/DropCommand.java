package edu.uob;

public class DropCommand extends GameCommand {
    @Override
    public String execute() {
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid drop command";
        }

        Player player = getPlayer();
        // Extract the item name from the original command to preserve exact text
        String itemName = this.command.toLowerCase().replace("drop", "").trim();

        if (itemName.isEmpty()) {
            return "No item specified";
        }

        // First try exact matches
        for (GameEntity item : player.getInventory()) {
            if (item.getEntityName().equalsIgnoreCase(itemName)) {
                player.removeFromInventory(item);
                player.getCurrentLocation().addEntity(item);
                return "You dropped the " + item.getEntityName();
            }
        }

        // If no exact match, check if any inventory item name contains the specified text
        for (GameEntity item : player.getInventory()) {
            if (item.getEntityName().toLowerCase().contains(itemName)) {
                player.removeFromInventory(item);
                player.getCurrentLocation().addEntity(item);
                return "You dropped the " + item.getEntityName();
            }
        }

        return "You don't have that item in your inventory.";
    }
}
