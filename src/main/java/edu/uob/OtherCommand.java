package edu.uob;

import java.util.*;

public class OtherCommand extends GameCommand {
    @Override
    public String execute() {
        if (this.gameTracker == null) return "Game state not initialized.";
        Player player = this.getPlayer();
        if (player == null) return "Player not found";

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return "Location not found";

        List<GameAction> validActions = this.findValidActions(currentLocation, player);

        if (validActions.isEmpty()) {
            return "You can't do that here. You don't have all the required items or your command was ambiguous.";
        }

        if (validActions.size() > 1) {
            return "There is more than one action possible - which one do you want to perform?";
        }

        GameAction matchedAction = validActions.get(0);

        this.handleConsumedEntities(matchedAction, currentLocation, player);
        this.handleProducedEntities(matchedAction, currentLocation, player);

        boolean playerDied = this.applyHealthChanges(matchedAction, player);

        if (playerDied || player.isDead()) {
            return this.handlePlayerDeath(player, currentLocation);
        }

        if (matchedAction.getNarration().isEmpty()) {
            return "You successfully performed the action.";
        } else {
            return matchedAction.getNarration().get(0);
        }
    }

    private List<GameAction> findValidActions(Location currentLocation, Player player) {
        List<GameAction> possibleActions = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(this.command.toLowerCase());
        while (stringTokenizer.hasMoreTokens()) {
            String word = stringTokenizer.nextToken();
            if (this.gameTracker.getActionMap().containsKey(word)) {
                GameAction action = this.gameTracker.getActionMap().get(word);
                possibleActions.add(action);
            }
        }

        if (possibleActions.isEmpty()) {
            return possibleActions;
        }

        Set<String> commandEntities = new HashSet<>();
        if (this.trimmedCommand != null) {
            commandEntities = this.trimmedCommand.getEntities();
        }

        List<GameAction> validActions = new LinkedList<>();
        for (GameAction action : possibleActions) {
            if (this.isValidAction(action, commandEntities, currentLocation, player)) {
                validActions.add(action);
            }
        }

        return validActions;
    }

    private boolean isValidAction(GameAction action, Set<String> commandEntities,
                                  Location currentLocation, Player player) {
        Set<String> allRequiredEntities = new HashSet<>();
        allRequiredEntities.addAll(action.getArtefacts());
        allRequiredEntities.addAll(action.getFurniture());
        allRequiredEntities.addAll(action.getCharacters());

        if (allRequiredEntities.isEmpty()) {
            return commandEntities.isEmpty();
        }

        boolean atLeastOneEntityMentioned = false;
        boolean allEntitiesValid = true;

        for (String commandEntity : commandEntities) {
            boolean isValidEntityForAction = false;
            for (String requiredEntity : allRequiredEntities) {
                if (requiredEntity.equalsIgnoreCase(commandEntity)) {
                    isValidEntityForAction = true;
                    break;
                }
            }

            if (!isValidEntityForAction) {
                allEntitiesValid = false;
                break;
            }
        }

        for (String requiredEntity : allRequiredEntities) {
            for (String commandEntity : commandEntities) {
                if (requiredEntity.equalsIgnoreCase(commandEntity)) {
                    atLeastOneEntityMentioned = true;
                    break;
                }
            }
            if (atLeastOneEntityMentioned) break;
        }

        return atLeastOneEntityMentioned && allEntitiesValid &&
                this.checkEntitiesAvailable(commandEntities, currentLocation, player) &&
                this.checkAllRequiredEntitiesAvailable(allRequiredEntities, currentLocation, player);
    }

    private boolean applyHealthChanges(GameAction matchedAction, Player player) {
        int healthChange = matchedAction.getHealthChange();

        if (healthChange == 0) {
            return false;
        }

        if (healthChange > 0) {
            return this.increasePlayerHealth(player, healthChange);
        } else {
            return this.decreasePlayerHealth(player, Math.abs(healthChange));
        }
    }

    private boolean increasePlayerHealth(Player player, int amount) {
        for (int i = 0; i < amount; i++) {
            if (player.getHealth() < 3) {
                player.increaseHealth();
            }
        }
        return false;
    }

    private boolean decreasePlayerHealth(Player player, int amount) {
        for (int i = 0; i < amount; i++) {
            player.decreaseHealth();
            if (player.isDead()) {
                return true;
            }
        }
        return false;
    }

    private String handlePlayerDeath(Player player, Location currentLocation) {
        this.dropAllPlayerItems(player, currentLocation);
        player.resetHealth();

        Location startLocation = this.gameTracker.getLocationMap().values().iterator().next();
        player.setCurrentLocation(startLocation);

        StringBuilder response = new StringBuilder();
        response.append("You have died and lost all your items. You've been returned to the ");
        response.append(startLocation.getEntityName()).append(" with full health! ");
        response.append("\n").append("You are in the ").append(startLocation.getEntityName());
        response.append(": ").append(startLocation.getEntityDescription());
        return response.toString();
    }

    private void dropAllPlayerItems(Player player, Location currentLocation) {
        LinkedList<GameEntity> inventory = new LinkedList<>(player.getInventory());
        for (GameEntity item : inventory) {
            player.removeFromInventory(item);
            currentLocation.addEntity(item);
        }
    }

    private boolean checkAllRequiredEntitiesAvailable(Set<String> requiredEntities, Location currentLocation, Player player) {
        for (String entity : requiredEntities) {
            if (!this.isEntityAvailable(entity, currentLocation, player)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkEntitiesAvailable(Set<String> entities, Location currentLocation, Player player) {
        for (String entity : entities) {
            if (!this.isEntityAvailable(entity, currentLocation, player)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEntityAvailable(String entityName, Location location, Player player) {
        for (GameEntity item : player.getInventory()) {
            if (item.getEntityName().equalsIgnoreCase(entityName)) {
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

    private void handleConsumedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation("storeroom");

        for (String consumed : action.getConsumed()) {
            if (consumed.equalsIgnoreCase("health")) {
                player.decreaseHealth();
                continue;
            }

            if (this.handleConsumedLocation(consumed, currentLocation)) {
                continue;
            }

            if (this.consumeEntityFromLocation(consumed, currentLocation, storeroom)) {
                continue;
            }

            this.consumeEntityFromInventory(consumed, player, storeroom);
        }
    }

    private boolean handleConsumedLocation(String locationName, Location currentLocation) {
        Location locationToConsume = this.gameTracker.getLocation(locationName);
        if (locationToConsume != null) {
            if (currentLocation.getPathMap().containsKey(locationName.toLowerCase())) {
                currentLocation.getPathMap().remove(locationName.toLowerCase());
            }
            return true;
        }
        return false;
    }

    private boolean consumeEntityFromLocation(String entityName, Location location, Location storeroom) {
        GameEntity entityInLocation = null;
        for (GameEntity entity : location.getEntityList()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                entityInLocation = entity;
                break;
            }
        }

        if (entityInLocation != null) {
            location.removeEntity(entityInLocation);
            if (storeroom != null) {
                storeroom.addEntity(entityInLocation);
            }
            return true;
        }
        return false;
    }

    private void consumeEntityFromInventory(String entityName, Player player, Location storeroom) {
        GameEntity entityInInventory = null;
        for (GameEntity entity : player.getInventory()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                entityInInventory = entity;
                break;
            }
        }

        if (entityInInventory != null) {
            player.removeFromInventory(entityInInventory);
            if (storeroom != null) {
                storeroom.addEntity(entityInInventory);
            }
        }
    }

    private void handleProducedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation("storeroom");

        for (String produced : action.getProduced()) {
            if (produced.equalsIgnoreCase("health")) {
                if (player.getHealth() < 3) player.increaseHealth();
                continue;
            }

            if (this.handleProducedLocation(produced, currentLocation)) {
                continue;
            }

            if (this.moveFromInventoryToLocation(produced, player, currentLocation)) {
                continue;
            }

            if (this.isEntityInOtherPlayerInventory(produced, player)) {
                continue;
            }

            if (this.moveFromOtherLocationToHere(produced, currentLocation, storeroom)) {
                continue;
            }
            this.moveFromStoreroomToLocation(produced, storeroom, currentLocation);
        }
    }

    private boolean handleProducedLocation(String locationName, Location currentLocation) {
        Location existingLocation = this.gameTracker.getLocation(locationName);

        if (existingLocation != null) {

            Path pathToLocation = new Path(existingLocation);
            currentLocation.addPath(locationName.toLowerCase(), pathToLocation);
            return true;
        }
        return false;
    }

    private boolean moveFromInventoryToLocation(String entityName, Player player, Location currentLocation) {
        GameEntity entityInPlayersInventory = null;
        for (GameEntity entity : player.getInventory()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                entityInPlayersInventory = entity;
                break;
            }
        }

        if (entityInPlayersInventory != null) {
            player.removeFromInventory(entityInPlayersInventory);
            currentLocation.addEntity(entityInPlayersInventory);
            return true;
        }
        return false;
    }

    private boolean isEntityInOtherPlayerInventory(String entityName, Player currentPlayer) {
        for (Player otherPlayer : this.gameTracker.playerMap.values()) {
            if (otherPlayer == currentPlayer) continue;

            for (GameEntity entity : otherPlayer.getInventory()) {
                if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moveFromOtherLocationToHere(String entityName, Location currentLocation, Location storeroom) {
        GameEntity existingEntity = null;
        Location entityLocation = null;

        // Search for the entity in all locations
        for (Location location : this.gameTracker.getLocationMap().values()) {
            // Skip current location and storeroom when searching
            if (location == currentLocation || (storeroom != null && location == storeroom)) {
                continue;
            }

            for (GameEntity entity : location.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                    existingEntity = entity;
                    entityLocation = location;
                    break;
                }
            }

            if (existingEntity != null) {
                break;
            }
        }

        if (existingEntity != null && entityLocation != null) {
            entityLocation.removeEntity(existingEntity);
            currentLocation.addEntity(existingEntity);
            return true;
        }
        return false;
    }

    private boolean moveFromStoreroomToLocation(String entityName, Location storeroom, Location currentLocation) {
        if (storeroom == null) {
            return false;
        }

        GameEntity entityInStoreroom = null;
        for (GameEntity entity : storeroom.getEntityList()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                entityInStoreroom = entity;
                break;
            }
        }

        if (entityInStoreroom != null) {
            storeroom.removeEntity(entityInStoreroom);
            currentLocation.addEntity(entityInStoreroom);
            return true;
        }
        return false;
    }
}
