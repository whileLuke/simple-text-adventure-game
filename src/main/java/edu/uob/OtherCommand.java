package edu.uob;

import java.util.*;

public class OtherCommand extends GameCommand {
    private static final String HEALTH_ATTRIBUTE = "health";
    private static final String STOREROOM_NAME = "storeroom";

    @Override
    public String execute() {
        if (this.gameTracker == null) return "Game state not initialized.";

        Player player = this.getPlayer();
        if (player == null) return "Player not found";

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return "Location not found";

        List<GameAction> validActions = findMatchingActions(currentLocation, player);

        if (validActions.isEmpty()) {
            return "You can't do that here. You don't have all the required items or your command was ambiguous.";
        }

        if (validActions.size() > 1) {
            return "There is more than one action possible - which one do you want to perform?";
        }

        return executeAction(validActions.get(0), currentLocation, player);
    }

    private String executeAction(GameAction action, Location currentLocation, Player player) {
        processConsumedEntities(action, currentLocation, player);
        processProducedEntities(action, currentLocation, player);

        boolean playerDied = applyHealthEffects(action, player);

        if (playerDied || player.isDead()) {
            return handlePlayerDeath(player, currentLocation);
        }

        if (action.getNarration().isEmpty()) {
            return "You successfully performed the action.";
        } else {
            return action.getNarration().get(0);
        }
    }

    private List<GameAction> findMatchingActions(Location currentLocation, Player player) {
        List<GameAction> possibleActions = identifyPotentialActions();

        if (possibleActions.isEmpty()) {
            return possibleActions;
        }

        Set<String> commandEntities = extractCommandEntities();
        return filterValidActions(possibleActions, commandEntities, currentLocation, player);
    }

    private List<GameAction> identifyPotentialActions() {
        List<GameAction> potentialActions = new LinkedList<>();
        String[] commandWords = this.command.toLowerCase().split("\\s+");

        for (String word : commandWords) {
            if (this.gameTracker.getActionMap().containsKey(word)) {
                potentialActions.add(this.gameTracker.getActionMap().get(word));
            }
        }

        return potentialActions;
    }

    private Set<String> extractCommandEntities() {
        if (this.trimmedCommand != null) {
            return this.trimmedCommand.getEntities();
        }
        return new HashSet<>();
    }

    private List<GameAction> filterValidActions(List<GameAction> potentialActions,
                                                Set<String> commandEntities,
                                                Location currentLocation,
                                                Player player) {
        List<GameAction> validActions = new LinkedList<>();

        for (GameAction action : potentialActions) {
            if (isActionExecutable(action, commandEntities, currentLocation, player)) {
                validActions.add(action);
            }
        }

        return validActions;
    }

    private boolean isActionExecutable(GameAction action, Set<String> commandEntities,
                                       Location currentLocation, Player player) {
        Set<String> requiredEntities = collectRequiredEntities(action);

        // Special case for actions without required entities
        if (requiredEntities.isEmpty()) {
            return commandEntities.isEmpty();
        }

        boolean atLeastOneEntityMentioned = hasMatchingEntity(commandEntities, requiredEntities);
        boolean allEntitiesValid = areAllEntitiesValid(commandEntities, requiredEntities);
        boolean allEntitiesAvailable = areRequiredEntitiesAvailable(requiredEntities, currentLocation, player);
        boolean mentionedEntitiesAvailable = areCommandEntitiesAvailable(commandEntities, currentLocation, player);

        return atLeastOneEntityMentioned &&
                allEntitiesValid &&
                mentionedEntitiesAvailable &&
                allEntitiesAvailable;
    }

    private Set<String> collectRequiredEntities(GameAction action) {
        Set<String> allRequiredEntities = new HashSet<>();
        allRequiredEntities.addAll(action.getArtefacts());
        allRequiredEntities.addAll(action.getFurniture());
        allRequiredEntities.addAll(action.getCharacters());
        return allRequiredEntities;
    }

    private boolean hasMatchingEntity(Set<String> commandEntities, Set<String> requiredEntities) {
        for (String requiredEntity : requiredEntities) {
            for (String commandEntity : commandEntities) {
                if (requiredEntity.equalsIgnoreCase(commandEntity)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areAllEntitiesValid(Set<String> commandEntities, Set<String> requiredEntities) {
        for (String commandEntity : commandEntities) {
            boolean isValid = false;
            for (String requiredEntity : requiredEntities) {
                if (requiredEntity.equalsIgnoreCase(commandEntity)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                return false;
            }
        }
        return true;
    }

    private boolean applyHealthEffects(GameAction action, Player player) {
        int healthChange = action.getHealthChange();

        if (healthChange == 0) {
            return false;
        }

        if (healthChange > 0) {
            return increasePlayerHealth(player, healthChange);
        } else {
            return decreasePlayerHealth(player, Math.abs(healthChange));
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
        transferAllItemsToLocation(player, currentLocation);
        player.resetHealth();

        Location startLocation = findStartLocation();
        player.setCurrentLocation(startLocation);

        return formatPlayerDeathMessage(startLocation);
    }

    private Location findStartLocation() {
        // Simply get the first location as the start location
        return this.gameTracker.getLocationMap().values().iterator().next();
    }

    private String formatPlayerDeathMessage(Location startLocation) {
        StringBuilder message = new StringBuilder();
        message.append("You have died and lost all your items. You've been returned to the ");
        message.append(startLocation.getEntityName()).append(" with full health! ");
        message.append("\n").append("You are in the ").append(startLocation.getEntityName());
        message.append(": ").append(startLocation.getEntityDescription());
        return message.toString();
    }

    private void transferAllItemsToLocation(Player player, Location location) {
        LinkedList<GameEntity> inventory = new LinkedList<>(player.getInventory());
        for (GameEntity item : inventory) {
            player.removeFromInventory(item);
            location.addEntity(item);
        }
    }

    private boolean areRequiredEntitiesAvailable(Set<String> requiredEntities,
                                                 Location currentLocation,
                                                 Player player) {
        for (String entity : requiredEntities) {
            if (!isEntityAccessible(entity, currentLocation, player)) {
                return false;
            }
        }
        return true;
    }

    private boolean areCommandEntitiesAvailable(Set<String> entities,
                                                Location currentLocation,
                                                Player player) {
        for (String entity : entities) {
            if (!isEntityAccessible(entity, currentLocation, player)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEntityAccessible(String entityName, Location location, Player player) {
        // Check player inventory first
        for (GameEntity item : player.getInventory()) {
            if (item.getEntityName().equalsIgnoreCase(entityName)) {
                return true;
            }
        }

        // Then check current location
        for (GameEntity locationEntity : location.getEntityList()) {
            if (locationEntity.getEntityName().equalsIgnoreCase(entityName)) {
                return true;
            }
        }
        return false;
    }

    private void processConsumedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation(STOREROOM_NAME);

        for (String consumed : action.getConsumed()) {
            if (consumed.equalsIgnoreCase(HEALTH_ATTRIBUTE)) {
                player.decreaseHealth();
                continue;
            }

            if (tryRemoveLocationPath(consumed, currentLocation)) {
                continue;
            }

            if (tryRemoveEntityFromLocation(consumed, currentLocation, storeroom)) {
                continue;
            }

            tryRemoveEntityFromInventory(consumed, player, storeroom);
        }
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
        for (GameEntity entity : location.getEntityList()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;
    }

    private void tryRemoveEntityFromInventory(String entityName, Player player, Location storeroom) {
        GameEntity entityToRemove = findEntityInInventory(entityName, player);

        if (entityToRemove != null) {
            player.removeFromInventory(entityToRemove);
            if (storeroom != null) {
                storeroom.addEntity(entityToRemove);
            }
        }
    }

    private GameEntity findEntityInInventory(String entityName, Player player) {
        for (GameEntity entity : player.getInventory()) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;
    }

    private void processProducedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation(STOREROOM_NAME);

        for (String produced : action.getProduced()) {
            if (produced.equalsIgnoreCase(HEALTH_ATTRIBUTE)) {
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
            // Skip current location and storeroom
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
