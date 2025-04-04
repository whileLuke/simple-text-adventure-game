package edu.uob.ActionManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;
import edu.uob.GameManagement.GameHelper;
import edu.uob.GameManagement.GameTracker;

import java.util.HashSet;
import java.util.Set;

public class ActionValidator {
    private final GameTracker gameTracker;
    private final GameHelper gameHelper;

    public ActionValidator(GameTracker gameTracker, GameHelper gameHelper) {
        this.gameTracker = gameTracker;
        this.gameHelper = gameHelper;
    }

    public boolean isActionExecutable(GameAction gameAction, Set<String> commandEntities,
                                      LocationEntity currentLocation, PlayerEntity playerEntity) {
        Set<String> requiredEntities = this.collectRequiredEntities(gameAction);

        if (requiredEntities.isEmpty()) return false;
        if (!this.areAllCommandEntitiesValid(commandEntities, requiredEntities)) return false;
        if (!this.hasAtLeastOneEntityMentioned(commandEntities, requiredEntities)) return false;
        if (!this.areAllRequiredEntitiesAvailable(requiredEntities, currentLocation, playerEntity)) return false;

        for (String commandEntity : commandEntities) {
            if (this.gameHelper.isEntityInList(commandEntity, gameAction.getProduced())) return false;
        }
        return true;
    }

    private Set<String> collectRequiredEntities(GameAction gameAction) {
        Set<String> allRequiredEntities = new HashSet<>();
        allRequiredEntities.addAll(gameAction.getArtefacts());
        allRequiredEntities.addAll(gameAction.getFurniture());
        allRequiredEntities.addAll(gameAction.getCharacters());
        return allRequiredEntities;
    }

    private boolean areAllCommandEntitiesValid(Set<String> commandEntities, Set<String> requiredEntities) {
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
        if (this.gameTracker.getLocationMap().containsKey(entityName.toLowerCase())) return true;

        for (GameEntity itemEntity : player.getPlayerInventory()) {
            if (itemEntity.getEntityName().equalsIgnoreCase(entityName)) return true;
        }

        for (GameEntity locationEntity : location.getEntityList()) {
            if (locationEntity.getEntityName().equalsIgnoreCase(entityName)) return true;
        }
        return false;
    }

}
