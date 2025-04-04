package edu.uob.EntityManagement;

import edu.uob.ActionManagement.GameAction;
import edu.uob.GameManagement.GameTracker;

public class EntityProcessor {
    private final GameTracker gameTracker;

    public EntityProcessor(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public void processConsumedEntities(GameAction gameAction, LocationEntity playerLocation, PlayerEntity playerEntity) {
        LocationEntity storeroomLocation = this.gameTracker.getLocation("storeroom");

        for (String consumedEntity : gameAction.getConsumedList()) {
            if (consumedEntity.equalsIgnoreCase("health")) {
                playerEntity.decreaseHealth();
                continue;
            }

            if (removeLocationPath(consumedEntity, playerLocation)) continue;
            this.removeEntity(consumedEntity, playerEntity, playerLocation, storeroomLocation);
        }
    }

    private void removeEntity(String entityName,
                              PlayerEntity currentPlayer,
                              LocationEntity currentLocation,
                              LocationEntity storeroomLocation) {
        GameEntity entityToRemove;

        entityToRemove = this.gameTracker.findEntity(entityName, currentPlayer.getPlayerInventory());
        if (entityToRemove != null) {
            currentPlayer.removeFromInventory(entityToRemove);
            if (storeroomLocation != null) storeroomLocation.addEntity(entityToRemove);
            return;
        }

        entityToRemove = this.gameTracker.findEntity(entityName, currentLocation.getEntityList());
        if (entityToRemove != null) {
            currentLocation.removeEntity(entityToRemove);
            if (storeroomLocation != null) storeroomLocation.addEntity(entityToRemove);
            return;
        }

        for (LocationEntity location : this.gameTracker.getLocationMap().values()) {
            if (location == currentLocation) continue;

            entityToRemove = this.gameTracker.findEntity(entityName, location.getEntityList());
            if (entityToRemove != null) {
                location.removeEntity(entityToRemove);
                if (storeroomLocation != null) storeroomLocation.addEntity(entityToRemove);
                return;
            }
        }
    }

    private boolean removeLocationPath(String locationName, LocationEntity currentLocation) {
        LocationEntity locationToConsume = this.gameTracker.getLocation(locationName);
        if (locationToConsume != null) {
            currentLocation.getPathMap().remove(locationName.toLowerCase());
            return true;
        }
        return false;
    }

    public void processProducedEntities(GameAction gameAction, LocationEntity currentLocation, PlayerEntity playerEntity) {
        LocationEntity storeroomLocation = this.gameTracker.getLocation("storeroom");

        for (String producedEntity : gameAction.getProducedList()) {
            if (producedEntity.equalsIgnoreCase("health")) {
                if (playerEntity.getHealth() < 3) playerEntity.increaseHealth();
                continue;
            }

            if (tryAddLocationPath(producedEntity, currentLocation)) continue;
            if (tryMoveFromInventoryToLocation(producedEntity, playerEntity, currentLocation)) continue;
            if (isEntityInOtherPlayerInventory(producedEntity, playerEntity)) continue;
            if (tryMoveFromOtherLocationToHere(producedEntity, currentLocation, storeroomLocation)) continue;
            this.tryMoveFromStoreroomToLocation(producedEntity, storeroomLocation, currentLocation);
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

    private boolean tryMoveFromInventoryToLocation(String entityName,
                                                   PlayerEntity playerEntity, LocationEntity currentLocation) {
        GameEntity entityToMove = this.gameTracker.findEntity(entityName, playerEntity.getPlayerInventory());

        if (entityToMove != null) {
            playerEntity.removeFromInventory(entityToMove);
            currentLocation.addEntity(entityToMove);
            return true;
        }
        return false;
    }

    private boolean isEntityInOtherPlayerInventory(String entityName, PlayerEntity currentPlayer) {
        for (PlayerEntity otherPlayer : this.gameTracker.getPlayerMap().values()) {
            if (otherPlayer == currentPlayer) continue;

            for (GameEntity entity : otherPlayer.getPlayerInventory()) {
                if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryMoveFromOtherLocationToHere(String entityName, LocationEntity currentLocation, LocationEntity storeroomLocation) {
        for (LocationEntity locationEntity : this.gameTracker.getLocationMap().values()) {
            if (locationEntity == currentLocation || (storeroomLocation != null && locationEntity == storeroomLocation)) {
                continue;
            }

            GameEntity entityToMove = this.gameTracker.findEntity(entityName, locationEntity.getEntityList());
            if (entityToMove != null) {
                locationEntity.removeEntity(entityToMove);
                currentLocation.addEntity(entityToMove);
                return true;
            }
        }
        return false;
    }

    private void tryMoveFromStoreroomToLocation(String entityName,
                                                LocationEntity storeroomLocation, LocationEntity currentLocation) {
        if (storeroomLocation == null) return;

        GameEntity entityToMove = this.gameTracker.findEntity(entityName, storeroomLocation.getEntityList());
        if (entityToMove != null) {
            storeroomLocation.removeEntity(entityToMove);
            currentLocation.addEntity(entityToMove);
        }
    }
}
