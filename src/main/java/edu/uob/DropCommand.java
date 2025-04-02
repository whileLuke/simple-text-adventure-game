package edu.uob;

public class DropCommand extends GameCommand {
    @Override
    public String execute() {
        if (!this.trimmedCommand.hasCommandType()) {
            return "drop command is not valid.";
        }

        Player player = this.getPlayer();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "drop only works with exactly one item.";
        }

        String itemName = this.command.toLowerCase().replace("drop", "").trim();
        if (itemName.isEmpty()) {
            return "No item specified";
        }

        for (String entityName : this.trimmedCommand.getEntities()) {
            return this.tryToDropEntity(entityName, player);
        }

        return "You don't have that item in your inventory.";
    }

    private String tryToDropEntity(String entityName, Player player) {
        GameEntity itemToDrop = this.gameTracker.findEntityInInventory(entityName, player);

        if (itemToDrop != null) {
            player.removeFromInventory(itemToDrop);
            player.getCurrentLocation().addEntity(itemToDrop);

            StringBuilder response = new StringBuilder();
            response.append("You dropped the ");
            response.append(itemToDrop.getEntityName());
            return response.toString();
        }

        return "You don't have that item in your inventory.";
    }
}

