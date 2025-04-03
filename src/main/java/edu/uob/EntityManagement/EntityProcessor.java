package edu.uob.EntityManagement;

import edu.uob.ActionManagement.GameAction;
import edu.uob.GameManagement.GameTracker;

public class EntityProcessor {
    private final GameTracker gameTracker;

    public EntityProcessor(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public void processConsumedEntities(GameAction action, LocationEntity currentLocation, PlayerEntity player) {
        LocationEntity storeroom = this.gameTracker.getLocation("storeroom");

        for (String consumed : action.getConsumed()) {
            if (consumed.equalsIgnoreCase("health")) {
                player.decreaseHealth();
                continue;
            }

            if (tryRemoveLocationPath(consumed, currentLocation)) {
                continue;
            }

            if (tryRemoveEntityFromInventory(consumed, player, storeroom)) {
                continue;
            }

            if (tryRemoveEntityFromLocation(consumed, currentLocation, storeroom)) {
                continue;
            }

            this.tryRemoveEntityFromAnyLocation(consumed, storeroom);
        }
    }

    private boolean tryRemoveEntityFromAnyLocation(String entityName, LocationEntity storeroom) {
        for (LocationEntity location : this.gameTracker.getLocationMap().values()) {
            GameEntity entityToRemove = this.findEntityInLocation(entityName, location);
            if (entityToRemove != null) {
                location.removeEntity(entityToRemove);
                if (storeroom != null) {
                    storeroom.addEntity(entityToRemove);
                }
                return true;
            }
        }
        return false;
    }

    private boolean tryRemoveLocationPath(String locationName, LocationEntity currentLocation) {
        LocationEntity locationToConsume = this.gameTracker.getLocation(locationName);
        if (locationToConsume != null) {
            if (currentLocation.getPathMap().containsKey(locationName.toLowerCase())) {
                currentLocation.getPathMap().remove(locationName.toLowerCase());
            }
            return true;
        }
        return false;
    }

    private boolean tryRemoveEntityFromLocation(String entityName, LocationEntity location, LocationEntity storeroom) {
        GameEntity entityToRemove = this.findEntityInLocation(entityName, location);

        if (entityToRemove != null) {
            location.removeEntity(entityToRemove);
            if (storeroom != null) {
                storeroom.addEntity(entityToRemove);
            }
            return true;
        }
        return false;
    }

    private GameEntity findEntityInLocation(String entityName, LocationEntity location) {
        return this.gameTracker.findEntityInLocation(entityName, location);
        /*for (GameEntity entity : location.getEntityList()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;*/
    }

    private boolean tryRemoveEntityFromInventory(String entityName, PlayerEntity player, LocationEntity storeroom) {
        GameEntity entityToRemove = this.findEntityInInventory(entityName, player);

        if (entityToRemove != null) {
            player.removeFromInventory(entityToRemove);
            if (storeroom != null) {
                storeroom.addEntity(entityToRemove);
            }
            return true;
        }
        return false;
    }

    private GameEntity findEntityInInventory(String entityName, PlayerEntity playerName) {
        return this.gameTracker.findEntityInInventory(entityName, playerName);
        /*for (GameEntity entity : player.getInventory()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;*/
    }

    public void processProducedEntities(GameAction action, LocationEntity currentLocation, PlayerEntity player) {
        LocationEntity storeroom = this.gameTracker.getLocation("storeroom");

        for (String produced : action.getProduced()) {
            if (produced.equalsIgnoreCase("health")) {
                if (player.getHealth() < 3) player.increaseHealth();
                continue;
            }

            if (tryAddLocationPath(produced, currentLocation)) {
                continue;
            }

            if (tryMoveFromInventoryToLocation(produced, player, currentLocation)) {
                continue;
            }

            if (isEntityInOtherPlayerInventory(produced, player)) {
                continue;
            }

            if (tryMoveFromOtherLocationToHere(produced, currentLocation, storeroom)) {
                continue;
            }

            this.tryMoveFromStoreroomToLocation(produced, storeroom, currentLocation);
        }
    }

    private boolean tryAddLocationPath(String locationName, LocationEntity currentLocation) {
        LocationEntity targetLocation = this.gameTracker.getLocation(locationName);

        if (targetLocation != null) {
            GamePath gamePathToLocation = new GamePath(targetLocation);
            currentLocation.addPath(locationName.toLowerCase(), gamePathToLocation);
            return true;
        }
        return false;
    }

    private boolean tryMoveFromInventoryToLocation(String entityName, PlayerEntity player, LocationEntity currentLocation) {
        GameEntity entityToMove = this.findEntityInInventory(entityName, player);

        if (entityToMove != null) {
            player.removeFromInventory(entityToMove);
            currentLocation.addEntity(entityToMove);
            return true;
        }
        return false;
    }

    private boolean isEntityInOtherPlayerInventory(String entityName, PlayerEntity currentPlayer) {
        for (PlayerEntity otherPlayer : this.gameTracker.getPlayerMap().values()) {
            if (otherPlayer == currentPlayer) continue;

            for (GameEntity entity : otherPlayer.getInventory()) {
                if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryMoveFromOtherLocationToHere(String entityName, LocationEntity currentLocation, LocationEntity storeroom) {
        for (LocationEntity location : this.gameTracker.getLocationMap().values()) {
            if (location == currentLocation || (storeroom != null && location == storeroom)) {
                continue;
            }

            GameEntity entityToMove = this.findEntityInLocation(entityName, location);
            if (entityToMove != null) {
                location.removeEntity(entityToMove);
                currentLocation.addEntity(entityToMove);
                return true;
            }
        }
        return false;
    }

    private boolean tryMoveFromStoreroomToLocation(String entityName, LocationEntity storeroom, LocationEntity currentLocation) {
        if (storeroom == null) {
            return false;
        }

        GameEntity entityToMove = this.findEntityInLocation(entityName, storeroom);
        if (entityToMove != null) {
            storeroom.removeEntity(entityToMove);
            currentLocation.addEntity(entityToMove);
            return true;
        }
        return false;
    }
}
