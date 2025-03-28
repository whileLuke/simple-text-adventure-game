package edu.uob;

public class GotoCommand extends GameCommand {
    @Override
    public String execute() {
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid goto command.";
        }

        if (trimmedCommand.getEntities().isEmpty()) {
            return "You didn't specify a location";
        }

        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();

        for (String locationName : trimmedCommand.getEntities()) {
            Path path = currentLocation.getPath(locationName);

            if (path == null) {
                return "You can't go to " + locationName;
            }

            player.setCurrentLocation(path.getPathTo());
            return "You have gone to " + locationName + ".";
        }

        return "You can't go there.";
    }
}

