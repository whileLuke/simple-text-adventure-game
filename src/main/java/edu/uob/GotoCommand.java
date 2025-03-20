package edu.uob;

public class GotoCommand extends GameCommand {
    @Override
    public String execute() {
        String locationName = this.command.substring(command.toLowerCase().indexOf("goto") + 4).trim();
        if (locationName.isEmpty()) return "No location specified.\n";

        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();
        Path path = currentLocation.getPath(locationName);
        StringBuilder response = new StringBuilder();
        if (path == null) {
            response.append("You can't go to ").append(locationName).append(".\n");
            return response.toString();
        }

        player.setCurrentLocation(path.getPathTo());
        response.append("You go to ").append(locationName).append(".\n");
        return response.toString();
    }
}
