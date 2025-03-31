package edu.uob;

public class GetCommand extends GameCommand {
    @Override
    public String execute() {
        // Validate command type
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid get command.";
        }

        // Find the first entity that matches an item in the current location
        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();
        StringBuilder response = new StringBuilder();

        // If no entities were extracted, return error
        if (trimmedCommand.getEntities().isEmpty()) {
            return "No item specified.";
        }

        // Try to find the first matching item in the location
        for (String entityName : trimmedCommand.getEntities()) {
            GameEntity item = gameTracker.findEntityInLocation(entityName, currentLocation);

            if (item != null) {
                if (!(item instanceof Artefact)) {
                    return "The item " + entityName + " cannot be taken.";
                }

                currentLocation.removeEntity(item);
                player.addToInventory(item);
                return "You picked up the " + entityName + ".";
            }
        }

        return "The specified item is not here.";
    }
    /*public String execute() {
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid get command.";
        }
        String itemName = this.command.substring(this.command.toLowerCase().indexOf("get") + 3).trim();
        itemName = itemName.toLowerCase();
        if (itemName.isEmpty()) return "No item name specified.";

        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();
        GameEntity item = gameTracker.findEntityInLocation(itemName, currentLocation);
        StringBuilder response = new StringBuilder();

        if (item == null) {
            response.append("The item ").append(itemName).append(" is not here.\n");
            return response.toString();
        }

        if(!(item instanceof Artefact)) {
            response.append("The item ").append(itemName).append(" cannot be taken.\n");
            return response.toString();
        }
        currentLocation.removeEntity(item);
        player.addToInventory(item);

        response.append("You picked up the ").append(itemName).append(".\n");
        return response.toString();
    }*/
}
