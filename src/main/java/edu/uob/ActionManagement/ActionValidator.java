package edu.uob.ActionManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;
import edu.uob.GameManagement.GameTracker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActionValidator {
    private GameTracker gameTracker;

    public ActionValidator(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public boolean isActionExecutable(GameAction action, Set<String> commandEntities,
                                      LocationEntity currentLocation, PlayerEntity player) {
        Set<String> requiredEntities = this.collectRequiredEntities(action);

        for (String commandEntity : commandEntities) {
            if (isEntityInList(commandEntity, action.getProduced())) {
                return false;
            }
        }

        if (requiredEntities.isEmpty()) {
            return false;
        }
        if (!areAllCommandEntitiesValidForAction(commandEntities, requiredEntities)) {
            return false;
        }
        if (!hasAtLeastOneEntityMentioned(commandEntities, requiredEntities)) {
            return false;
        }
        if (!areAllRequiredEntitiesAvailable(requiredEntities, currentLocation, player)) {
            return false;
        }
        return true;
    }

    private Set<String> collectRequiredEntities(GameAction action) {
        Set<String> allRequiredEntities = new HashSet<>();
        allRequiredEntities.addAll(action.getArtefacts());
        allRequiredEntities.addAll(action.getFurniture());
        allRequiredEntities.addAll(action.getCharacters());
        return allRequiredEntities;
    }

    private boolean isEntityInList(String entity, List<String> list) {
        for (String item : list) {
            if (item.equalsIgnoreCase(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean areAllCommandEntitiesValidForAction(Set<String> commandEntities, Set<String> requiredEntities) {
        for (String commandEntity : commandEntities) {
            boolean isValidEntity = false;
            for (String requiredEntity : requiredEntities) {
                if (requiredEntity.equalsIgnoreCase(commandEntity)) {
                    isValidEntity = true;
                    break;
                }
            }
            if (!isValidEntity) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAtLeastOneEntityMentioned(Set<String> commandEntities, Set<String> requiredEntities) {
        if (commandEntities.isEmpty()) {
            return false;
        }

        for (String commandEntity : commandEntities) {
            for (String requiredEntity : requiredEntities) {
                if (requiredEntity.equalsIgnoreCase(commandEntity)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean areAllRequiredEntitiesAvailable(Set<String> requiredEntities,
                                                    LocationEntity currentLocation,
                                                    PlayerEntity player) {
        for (String entity : requiredEntities) {
            if (!isEntityAccessible(entity, currentLocation, player)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEntityAccessible(String entityName, LocationEntity location, PlayerEntity player) {
        for (GameEntity itemEntity : player.getInventory()) {
            if (itemEntity.getEntityName().equalsIgnoreCase(entityName)) {
                return true;
            }
        }

        for (GameEntity locationEntity : location.getEntityList()) {
            if (locationEntity.getEntityName().equalsIgnoreCase(entityName)) {
                return true;
            }
        }
        return false;
    }

}
