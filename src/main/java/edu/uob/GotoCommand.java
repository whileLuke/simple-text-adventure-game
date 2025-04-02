package edu.uob;

public class GotoCommand extends GameCommand {
    @Override
    public String execute() {
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid goto command.";
        }

        if (trimmedCommand.getEntities().size() != 1) {
            return "goto only works with exactly one location.";
        }

        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();

        for (String locationName : trimmedCommand.getEntities()) {
            Path path = currentLocation.getPath(locationName);

            if (path == null) {
                StringBuilder message = new StringBuilder();
                message.append("You can't go to ").append(locationName);
                return message.toString();
            }
            Location destination = path.pathTo();
            player.setCurrentLocation(destination);

            StringBuilder response = new StringBuilder();
            response.append("You have gone to ").append(destination.getEntityName());
            response.append(": ").append(destination.getEntityDescription());
            return response.toString();
        }

        return "You can't go there.";
    }
}

