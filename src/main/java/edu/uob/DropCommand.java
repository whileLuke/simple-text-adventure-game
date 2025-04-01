package edu.uob;

public class DropCommand extends GameCommand {
    @Override
    public String execute() {
        if (!this.trimmedCommand.hasCommandType()) {
            return "drop command is not valid.";
        }

        Player player = getPlayer();
        String itemName = this.command.toLowerCase().replace("drop", "").trim();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "drop only works with exactly one item.";
        }

        if (itemName.isEmpty()) {
            return "No item specified";
        }
        for (String entityName : this.trimmedCommand.getEntities()) {
            GameEntity itemToDrop = gameTracker.findEntityInInventory(entityName, player);
            if (itemToDrop != null) {
                player.removeFromInventory(itemToDrop);
                player.getCurrentLocation().addEntity(itemToDrop);
                return "You dropped the " + itemToDrop.getEntityName();
            }
        }


        return "You don't have that item in your inventory.";
    }
}
