package edu.uob;

public class GetCommand extends GameCommand {
    @Override
    public String execute() {
        String itemName = this.command.substring(this.command.toLowerCase().indexOf("get") + 3).trim();
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
    }
}
