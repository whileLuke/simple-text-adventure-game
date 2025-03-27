package edu.uob;

import java.util.*;

public class CommandTrimmer {
    private GameTracker gameTracker;

    public CommandTrimmer(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    /**
     * Normalize and extract valid command components
     * @param command The original command string
     * @return Normalized command with only valid components
     */
    public CommandComponents parseCommand(String command) {
        // Convert to lowercase and split into words
        String[] words = command.toLowerCase().split("\\s+");

        // Build set of valid components
        Set<String> validComponents = buildValidCommandComponents();

        // Find command type and valid components
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

        // Add basic command keywords
        validComponents.addAll(Arrays.asList(
                "get", "goto", "look", "drop", "inventory", "inv"
        ));

        // Add all known actions from game tracker
        for (String actionTrigger : gameTracker.getActionMap().keySet()) {
            validComponents.add(actionTrigger.toLowerCase());
        }

        // Add all game entities (from locations and player inventory)
        for (Location location : gameTracker.getLocationMap().values()) {
            for (GameEntity entity : location.getEntityList()) {
                validComponents.add(entity.getName().toLowerCase());
            }
        }

        // Add inventory items
        Player player = gameTracker.getPlayer("player"); // Assumes a default player exists
        if (player != null) {
            for (GameEntity item : player.getInventory()) {
                validComponents.add(item.getName().toLowerCase());
            }
        }

        return validComponents;
    }

    /**
     * Check if a word is a valid command type
     * @param word The word to check
     * @return true if the word is a command type, false otherwise
     */
    private boolean isCommandType(String word) {
        return Arrays.asList(
                "get", "goto", "look", "drop", "inventory", "inv"
        ).contains(word);
    }

    /**
     * Represents the parsed components of a command
     */
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
