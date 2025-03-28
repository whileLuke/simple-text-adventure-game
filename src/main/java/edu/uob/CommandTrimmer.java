package edu.uob;

import java.util.*;

public class CommandTrimmer {
    private GameTracker gameTracker;

    public CommandTrimmer(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public CommandComponents parseCommand(String command) {
        String[] words = command.toLowerCase().split("\\s+");
        Set<String> validComponents = buildValidCommandComponents();
        String commandType = null;
        Set<String> extractedEntities = new HashSet<>();
        for (String word : words) {
            // Check for command type first
            if (isCommandType(word)) {
                commandType = word;
            }

            // Check for entities
            if (validComponents.contains(word)) {
                extractedEntities.add(word);
            }
        }

        return new CommandComponents(commandType, extractedEntities);
    }

    /**
     * Build a set of all valid command components
     * @return Set of valid components (keywords, entity names, etc.)
     */
    private Set<String> buildValidCommandComponents() {
        Set<String> validComponents = new HashSet<>();

        validComponents.addAll(Arrays.asList(
                "get", "goto", "look", "drop", "inventory", "inv"
        ));

        for (String actionTrigger : this.gameTracker.getActionMap().keySet()) {
            validComponents.add(actionTrigger.toLowerCase());
        }

        for (Location location : this.gameTracker.getLocationMap().values()) {
            for (GameEntity entity : location.getEntityList()) {
                validComponents.add(entity.getName().toLowerCase());
            }
        }

        Player player = this.gameTracker.getPlayer("player"); // Assumes a default player exists
        if (player != null) {
            for (GameEntity item : player.getInventory()) {
                validComponents.add(item.getName().toLowerCase());
            }
        }

        return validComponents;
    }

    private boolean isCommandType(String word) {
        return Arrays.asList(
                "get", "goto", "look", "drop", "inventory", "inv"
        ).contains(word);
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
