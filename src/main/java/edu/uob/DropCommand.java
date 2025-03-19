package edu.uob;

public class DropCommand extends STAGCommand {
    @Override
    public String execute() {
        String itemName = this.command.substring(command.toLowerCase().indexOf("drop") + 4).trim();
        if (itemName.isEmpty()) return "No item specified.";

        Player player = getPlayer();
        GameEntity item = gameTracker.findEntityInInventory(itemName, player);
        StringBuilder response = new StringBuilder();
        if (item == null) {
            response.append("You do not have the ").append(itemName).append(" in your inventory.\n");
            return response.toString();
        }

        player.removeFromInventory(item);
        player.getCurrentLocation().addEntity(item);

        response.append("You dropped the ").append(itemName).append(".");
        return response.toString();
    }
}
