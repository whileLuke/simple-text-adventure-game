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
                return "You can't go to " + locationName;
            }
            Location destination = path.getPathTo();
            player.setCurrentLocation(destination);
            return "You have gone to " + destination.getEntityName() + ": " + destination.getEntityDescription();
        }

        return "You can't go there.";
    }
}

