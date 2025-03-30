package edu.uob;

import java.util.*;

public class CommandTrimmer {
    private GameTracker gameTracker;

    public CommandTrimmer(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public CommandComponents parseCommand(String command) {
        String[] words = command.toLowerCase().split("\\s+");
        Set<String> validCommandTypes = new HashSet<>(Arrays.asList(
                "get", "goto", "look", "drop", "inventory", "inv"
        ));

        String commandType = null;
        Set<String> extractedEntities = new HashSet<>();

        for (String word : words) {
            // Check for exact command type match first
            if (validCommandTypes.contains(word)) {
                commandType = word;
            }
            // Check if it's a trigger word for custom actions
            else if (gameTracker.getActionMap().containsKey(word)) {
                // Don't mark as command type, but note it's a valid action word
                continue;
            }
            // Then check for entities
            else if (isValidEntity(word)) {
                extractedEntities.add(word);
            }
        }

        return new CommandComponents(commandType, extractedEntities);
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
