package edu.uob.CommandManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.GameManagement.GameTracker;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

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
            String potentialEntity = tokenizer.nextToken();
            if (validCommandTypes.contains(potentialEntity)) {
                commandType = potentialEntity;
            }
            else if (!this.gameTracker.getActionMap().containsKey(potentialEntity) &&
                    this.isValidEntity(potentialEntity)) {
                mentionedEntities.add(potentialEntity);
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

    private boolean isValidEntity(String gameEntity) {
        if (this.isEntityInLocations(gameEntity) || this.isEntityInAllInventories(gameEntity) ||
                this.isEntityInPaths(gameEntity)) {
            return true;
        }
        return false;
    }

    private boolean isEntityInLocations(String gameEntity) {
        for (LocationEntity location : this.gameTracker.getLocationMap().values()) {
            for (GameEntity entity : location.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(gameEntity)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEntityInAllInventories(String gameEntity) {
        for (PlayerEntity player : this.gameTracker.getPlayerMap().values()) {
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(gameEntity)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEntityInPaths(String gameEntity) {
        for (LocationEntity location : this.gameTracker.getLocationMap().values()) {
            if (location.getPathMap().containsKey(gameEntity)) {
                return true;
            }
        }
        return false;
    }
}