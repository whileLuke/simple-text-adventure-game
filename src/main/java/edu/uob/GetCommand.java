package edu.uob;

public class GetCommand extends GameCommand {
    @Override
    public String execute() {
        // Validate command type
        if (!this.trimmedCommand.hasCommandType()) {
            return "get command is not valid.";
        }

        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();
        StringBuilder response = new StringBuilder();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "get only works with exactly one item.";
        }

        for (String entityName : this.trimmedCommand.getEntities()) {
            GameEntity itemToGet = this.gameTracker.findEntityInLocation(entityName, currentLocation);

            if (itemToGet != null) {
                if (!(itemToGet instanceof Artefact)) {
                    return "The item " + entityName + " cannot be taken.";
                }

                currentLocation.removeEntity(itemToGet);
                player.addToInventory(itemToGet);
                return "You picked up the " + entityName + ".";
            }
        }

        return "The specified item is not here.";
    }
}
