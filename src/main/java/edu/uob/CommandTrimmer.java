package edu.uob;

import java.util.*;

public class CommandTrimmer {
    private final GameTracker gameTracker;

    public CommandTrimmer(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public CommandComponents parseCommand(String command) {
        String[] words = command.toLowerCase().split("\\s+");
        Set<String> validCommandTypes = new HashSet<>(Arrays.asList(
                "get", "goto", "look", "drop", "inventory", "inv"
        ));

        String commandType = null;
        Set<String> mentionedEntities = new HashSet<>();

        for (String word : words) {
            if (validCommandTypes.contains(word)) {
                commandType = word;
            }
            else if (this.gameTracker.getActionMap().containsKey(word)) {
                continue;
            }
            else if (isValidEntity(word)) {
                mentionedEntities.add(word);
            }
        }

        return new CommandComponents(commandType, mentionedEntities);
    }

    private boolean isValidEntity(String word) {
        // Check entities in locations
        for (Location location : this.gameTracker.getLocationMap().values()) {
            for (GameEntity entity : location.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(word)) {
                    return true;
                }
            }
        }

        // Check player's inventory
        Player player = this.gameTracker.getPlayer("player");
        if (player != null) {
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(word)) {
                    return true;
                }
            }
        }

        // Check paths
        for (Location location : this.gameTracker.getLocationMap().values()) {
            if (location.getPathMap().containsKey(word)) {
                return true;
            }
        }

        return false;
    }

    public static class CommandComponents {
        private String commandType;
        private Set<String> entities;

        public CommandComponents(String commandType, Set<String> entities) {
            this.commandType = commandType;
            this.entities = entities;
        }

        public String getCommandType() {
            return commandType;
        }

        public Set<String> getEntities() {
            return entities;
        }

        public boolean hasCommandType() {
            return commandType != null;
        }
    }
}
