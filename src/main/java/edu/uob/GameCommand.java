package edu.uob;

public abstract class GameCommand {
    String command;
    GameTracker gameTracker;
    String playerName;


    public void setCommand(String command) {
        /*String[] commandParts = command.split(":", 2);
        if (commandParts.length != 2) {
            return;
        }
        this.playerName = commandParts[0].trim();
        this.command = commandParts[1].trim();*/
        if (command.contains(":")) {
            String[] commandParts = command.split(":", 2);
            this.playerName = commandParts[0].trim();
            this.command = commandParts[1].trim();
        } else {
            this.playerName = "null";
            this.command = command.trim();
        }
    }

    public String getCommand() {
        return this.command;
    }

    public void setGameTracker(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public abstract String execute();

    public Player getPlayer() {
        /*if (gameTracker == null) return null;
        if (!gameTracker.playerExists(playerName)) {
            Player newPlayer = new Player(playerName);
            Location startLocation = null;
            for (Location location : gameTracker.getLocationMap().values()) {
                startLocation = location;
                break;
            }
            newPlayer.setCurrentLocation(startLocation);
            gameTracker.addPlayer(newPlayer);
        }
        return gameTracker.getPlayer(playerName);*/
        System.out.println("Getting player: " + playerName);
        System.out.println("GameTracker exists: " + (gameTracker != null));
        if (gameTracker == null) return null;
        if (!gameTracker.playerExists(playerName)) {
            //this.gameTracker = new GameTracker();
            System.out.println("Creating new player");
            Player newPlayer = new Player(playerName);
            Location startLocation = null;
            for (Location location : gameTracker.getLocationMap().values()) {
                startLocation = location;
                System.out.println("Found start location: " + location.getName());
                break;
            }
            if (startLocation == null) {
                System.out.println("ERROR: No locations found!");
                return null;
            }
            newPlayer.setCurrentLocation(startLocation);
            this.gameTracker.addPlayer(newPlayer);
        }
        return gameTracker.getPlayer(playerName);
    }
}
