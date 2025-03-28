package edu.uob;

public class DropCommand extends GameCommand {
    @Override
    public String execute() {
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid drop command";
        }

        if (trimmedCommand.getEntities().isEmpty()) {
            return "No item specified";
        }

        Player player = getPlayer();
        for (String entityName : trimmedCommand.getEntities()) {
            GameEntity item = gameTracker.findEntityInInventory(entityName, player);

            if (item != null) {
                player.removeFromInventory(item);
                player.getCurrentLocation().addEntity(item);
                return "You dropped the " + entityName;
            }
        }

        return "You don't have that item in your inventory.";
    }
}
