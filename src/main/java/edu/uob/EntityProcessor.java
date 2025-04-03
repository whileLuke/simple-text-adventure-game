package edu.uob;

public class EntityProcessor {
    private GameTracker gameTracker;

    public EntityProcessor(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public void processConsumedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation("storeroom");

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

            tryRemoveEntityFromAnyLocation(consumed, storeroom);
        }
    }

    private boolean tryRemoveEntityFromAnyLocation(String entityName, Location storeroom) {
        for (Location location : this.gameTracker.getLocationMap().values()) {
            GameEntity entityToRemove = findEntityInLocation(entityName, location);
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

    private boolean tryRemoveLocationPath(String locationName, Location currentLocation) {
        Location locationToConsume = this.gameTracker.getLocation(locationName);
        if (locationToConsume != null) {
            if (currentLocation.getPathMap().containsKey(locationName.toLowerCase())) {
                currentLocation.getPathMap().remove(locationName.toLowerCase());
            }
            return true;
        }
        return false;
    }

    private boolean tryRemoveEntityFromLocation(String entityName, Location location, Location storeroom) {
        GameEntity entityToRemove = findEntityInLocation(entityName, location);

        if (entityToRemove != null) {
            location.removeEntity(entityToRemove);
            if (storeroom != null) {
                storeroom.addEntity(entityToRemove);
            }
            return true;
        }
        return false;
    }

    private GameEntity findEntityInLocation(String entityName, Location location) {
        return this.gameTracker.findEntityInLocation(entityName, location);
        /*for (GameEntity entity : location.getEntityList()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;*/
    }

    private boolean tryRemoveEntityFromInventory(String entityName, Player player, Location storeroom) {
        GameEntity entityToRemove = findEntityInInventory(entityName, player);

        if (entityToRemove != null) {
            player.removeFromInventory(entityToRemove);
            if (storeroom != null) {
                storeroom.addEntity(entityToRemove);
            }
            return true;
        }
        return false;
    }

    private GameEntity findEntityInInventory(String entityName, Player playerName) {
        return this.gameTracker.findEntityInInventory(entityName, playerName);
        /*for (GameEntity entity : player.getInventory()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;*/
    }

    public void processProducedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation("storeroom");

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

            tryMoveFromStoreroomToLocation(produced, storeroom, currentLocation);
        }
    }

    private boolean tryAddLocationPath(String locationName, Location currentLocation) {
        Location targetLocation = this.gameTracker.getLocation(locationName);

        if (targetLocation != null) {
            Path pathToLocation = new Path(targetLocation);
            currentLocation.addPath(locationName.toLowerCase(), pathToLocation);
            return true;
        }
        return false;
    }

    private boolean tryMoveFromInventoryToLocation(String entityName, Player player, Location currentLocation) {
        GameEntity entityToMove = findEntityInInventory(entityName, player);

        if (entityToMove != null) {
            player.removeFromInventory(entityToMove);
            currentLocation.addEntity(entityToMove);
            return true;
        }
        return false;
    }

    private boolean isEntityInOtherPlayerInventory(String entityName, Player currentPlayer) {
        for (Player otherPlayer : this.gameTracker.getPlayerMap().values()) {
            if (otherPlayer == currentPlayer) continue;

            for (GameEntity entity : otherPlayer.getInventory()) {
                if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryMoveFromOtherLocationToHere(String entityName, Location currentLocation, Location storeroom) {
        for (Location location : this.gameTracker.getLocationMap().values()) {
            if (location == currentLocation || (storeroom != null && location == storeroom)) {
                continue;
            }

            GameEntity entityToMove = findEntityInLocation(entityName, location);
            if (entityToMove != null) {
                location.removeEntity(entityToMove);
                currentLocation.addEntity(entityToMove);
                return true;
            }
        }
        return false;
    }

    private boolean tryMoveFromStoreroomToLocation(String entityName, Location storeroom, Location currentLocation) {
        if (storeroom == null) {
            return false;
        }

        GameEntity entityToMove = findEntityInLocation(entityName, storeroom);
        if (entityToMove != null) {
            storeroom.removeEntity(entityToMove);
            currentLocation.addEntity(entityToMove);
            return true;
        }
        return false;
    }
}
