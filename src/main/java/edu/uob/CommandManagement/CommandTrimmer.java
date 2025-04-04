package edu.uob.CommandManagement;

import edu.uob.EntityManagement.*;
import edu.uob.GameManagement.GameTracker;

import java.util.*;

public class CommandTrimmer {
    private final GameTracker gameTracker;
    private final Set<String> validCommandTypes;

    public CommandTrimmer(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
        this.validCommandTypes = this.addValidCommands();
    }

    public CommandComponents parseCommand(String command) {
        StringTokenizer stringTokeniser = new StringTokenizer(command.toLowerCase());
        String commandType = null;
        Set<String> mentionedEntities = new HashSet<>();

        while (stringTokeniser.hasMoreTokens()) {
            String potentialEntity = stringTokeniser.nextToken();
            if (this.validCommandTypes.contains(potentialEntity)) commandType = potentialEntity;
            else if (this.isEntityNotAction(potentialEntity) && this.isValidEntity(potentialEntity)) {
                mentionedEntities.add(potentialEntity);
            }
        }

        return new CommandComponents(commandType, mentionedEntities);
    }

    private Set<String> addValidCommands() {
        Set<String> commandTypes = new HashSet<>();
        commandTypes.add("get");
        commandTypes.add("goto");
        commandTypes.add("look");
        commandTypes.add("drop");
        commandTypes.add("inventory");
        commandTypes.add("inv");
        commandTypes.add("health");
        return commandTypes;
    }

    private boolean isEntityNotAction(String potentialEntity) {
        return !this.gameTracker.getActionMap().containsKey(potentialEntity);
    }


    private boolean isValidEntity(String gameEntity) {
        if (this.gameTracker.getLocationMap().containsKey(gameEntity.toLowerCase())) {
            return true;
        }
        return this.isEntityInLocations(gameEntity) ||
                this.isEntityInInventories(gameEntity) ||
                this.isEntityInPaths(gameEntity);
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

    private boolean isEntityInInventories(String gameEntity) {
        for (PlayerEntity player : this.gameTracker.getPlayerMap().values()) {
            for (GameEntity playerItem : player.getPlayerInventory()) {
                if (playerItem.getEntityName().equalsIgnoreCase(gameEntity)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEntityInPaths(String gameEntity) {
        for (LocationEntity locationEntity : this.gameTracker.getLocationMap().values()) {
            if (locationEntity.getPathMap().containsKey(gameEntity)) {
                return true;
            }
        }
        return false;
    }
}