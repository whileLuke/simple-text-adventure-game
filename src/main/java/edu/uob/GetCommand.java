package edu.uob;

public class GetCommand extends GameCommand {
    @Override
    public String execute() {
        if (!this.trimmedCommand.hasCommandType()) {
            return "get command is not valid.";
        }

        Player player = this.getPlayer();
        Location currentLocation = player.getCurrentLocation();

        if (this.trimmedCommand.getEntities().size() != 1) {
            return "get only works with exactly one item.";
        }

        for (String entityName : this.trimmedCommand.getEntities()) {
            return this.tryToGetEntity(entityName, player, currentLocation);
        }

        return "The specified item is not here.";
    }

    private String tryToGetEntity(String entityName, Player player, Location currentLocation) {
        GameEntity itemToGet = this.gameTracker.findEntityInLocation(entityName, currentLocation);

        if (itemToGet != null) {
            if (!(itemToGet instanceof Artefact)) {
                StringBuilder response = new StringBuilder();
                response.append("The item ");
                response.append(entityName);
                response.append(" cannot be taken.");
                return response.toString();
            }

            currentLocation.removeEntity(itemToGet);
            player.addToInventory(itemToGet);

            StringBuilder response = new StringBuilder();
            response.append("You picked up the ");
            response.append(entityName);
            response.append(".");
            return response.toString();
        }

        return "The specified item is not here.";
    }
}
