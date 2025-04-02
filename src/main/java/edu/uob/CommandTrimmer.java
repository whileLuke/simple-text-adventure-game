package edu.uob;

import java.util.*;

public class CommandTrimmer {
    private final GameTracker gameTracker;

    public CommandTrimmer(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public CommandComponents parseCommand(String command) {
        StringTokenizer tokenizer = new StringTokenizer(command.toLowerCase());
        Set<String> validCommandTypes = new HashSet<>();
        this.populateValidCommandTypes(validCommandTypes);

        String commandType = null;
        Set<String> mentionedEntities = new HashSet<>();

        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            if (validCommandTypes.contains(word)) {
                commandType = word;
            }
            else if (this.gameTracker.getActionMap().containsKey(word)) {
                continue;
            }
            else if (this.isValidEntity(word)) {
                mentionedEntities.add(word);
            }
        }

        return new CommandComponents(commandType, mentionedEntities);
    }

    private void populateValidCommandTypes(Set<String> validCommandTypes) {
        validCommandTypes.add("get");
        validCommandTypes.add("goto");
        validCommandTypes.add("look");
        validCommandTypes.add("drop");
        validCommandTypes.add("inventory");
        validCommandTypes.add("inv");
    }

    private boolean isValidEntity(String word) {
        if (this.isEntityInLocations(word) || this.isEntityInInventory(word) ||
                this.isEntityInPaths(word)) {
            return true;
        }
        return false;
    }

    private boolean isEntityInLocations(String word) {
        for (Location location : this.gameTracker.getLocationMap().values()) {
            for (GameEntity entity : location.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEntityInInventory(String word) {
        Player player = this.gameTracker.getPlayer("player");
        if (player != null) {
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEntityInPaths(String word) {
        for (Location location : this.gameTracker.getLocationMap().values()) {
            if (location.getPathMap().containsKey(word)) {
                return true;
            }
        }
        return false;
    }
}