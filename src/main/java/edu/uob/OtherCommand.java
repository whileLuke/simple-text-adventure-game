package edu.uob;

import java.util.*;

public class OtherCommand extends GameCommand {
    @Override
    public String execute() {
        if (this.gameTracker == null) return "Game state not initialized.";
        Player player = getPlayer();
        if (player == null) return "Player not found";

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return "Location not found";

        String[] commandWords = command.toLowerCase().split("\\s+");
        List<GameAction> possibleActions = new ArrayList<>();

        // Find all potential trigger matches
        for (String word : commandWords) {
            if (gameTracker.getActionMap().containsKey(word)) {
                GameAction action = gameTracker.getActionMap().get(word);
                possibleActions.add(action);
            }
        }

        if (possibleActions.isEmpty()) {
            return "That action is not possible.";
        }

        Set<String> commandEntities = new HashSet<>();
        if (this.trimmedCommand != null) {
            commandEntities = this.trimmedCommand.getEntities();
        }

        // Filter actions to only those that have all required entities available
        List<GameAction> validActions = new ArrayList<>();
        for (GameAction action : possibleActions) {
            Set<String> allRequiredEntities = new HashSet<>();
            allRequiredEntities.addAll(action.getArtefacts());
            allRequiredEntities.addAll(action.getFurniture());
            allRequiredEntities.addAll(action.getCharacters());

            if (allRequiredEntities.isEmpty()) {
                /*if (!commandEntities.isEmpty()) {
                    // If there are entities in the command but none required, this isn't the right action
                    continue;
                }*/
                if (commandEntities.isEmpty()) {
                    validActions.add(action);
                }
                continue;
            }

            // Check if all required entities are mentioned in the command
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

            // Check for extraneous entities in the command
            if (atLeastOneEntityMentioned && allEntitiesValid &&
                    checkEntitiesAvailable(commandEntities, currentLocation, player) &&
                    checkAllRequiredEntitiesAvailable(allRequiredEntities, currentLocation, player)) {
                validActions.add(action);
            }
        }

        // If no valid actions after filtering
        if (validActions.isEmpty()) {
            return "You can't do that here. You don't have all the required items or your command was ambiguous.";
        }

        // If more than one valid action remains, then we have ambiguity
        if (validActions.size() > 1) {
            return "There is more than one action possible - which one do you want to perform?";
        }

        // We have exactly one valid action
        GameAction matchedAction = validActions.get(0);

        // Handle consumed and produced entities
        handleConsumedEntities(matchedAction, currentLocation, player);
        handleProducedEntities(matchedAction, currentLocation, player);

        // Apply health changes
        int healthChange = matchedAction.getHealthChange();
        boolean playerDied = false;

        if (healthChange != 0) {
            if (healthChange > 0) {
                // Handle health production - increase up to max health
                for (int i = 0; i < healthChange; i++) {
                    if (player.getHealth() < 3) {  // Max health is 3
                        player.increaseHealth();
                    }
                }
            } else {
                // Handle health consumption
                for (int i = 0; i < Math.abs(healthChange); i++) {
                    player.decreaseHealth();
                    // Check for death after EACH health decrease
                    if (player.isDead()) {
                        playerDied = true;
                        break;  // Stop decreasing health once the player is dead
                    }
                }
            }
        }

        // Process death if needed
        if (playerDied || player.isDead()) {
            for (GameEntity item : new LinkedList<>(player.getInventory())) {
                player.removeFromInventory(item);
                currentLocation.addEntity(item);
            }

            player.resetHealth();
            Location startLocation = gameTracker.getLocationMap().values().iterator().next();
            player.setCurrentLocation(startLocation);

            StringBuilder response = new StringBuilder();
            response.append("You have died and lost all your items. You've been returned to the ");
            response.append(startLocation.getEntityName());
            response.append(" with full health! ");
            response.append("\n");
            response.append("You are in the ");
            response.append(startLocation.getEntityName());
            response.append(": ");
            response.append(startLocation.getEntityDescription());
            return response.toString();
        }

        if (matchedAction.getNarration().isEmpty()) {
            return "You successfully performed the action.";
        } else return matchedAction.getNarration().get(0);
    }

    private boolean checkAllRequiredEntitiesAvailable(Set<String> requiredEntities, Location currentLocation, Player player) {
        for (String entity : requiredEntities) {
            boolean entityAvailable = false;

            // Check player inventory
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(entity)) {
                    entityAvailable = true;
                    break;
                }
            }

            // Check current location
            if (!entityAvailable) {
                for (GameEntity locationEntity : currentLocation.getEntityList()) {
                    if (locationEntity.getEntityName().equalsIgnoreCase(entity)) {
                        entityAvailable = true;
                        break;
                    }
                }
            }

            if (!entityAvailable) {
                return false;
            }
        }

        return true;
    }

    private boolean checkEntitiesAvailable(Set<String> entities, Location currentLocation, Player player) {
        for (String entity : entities) {
            boolean entityAvailable = false;

            // Check player inventory
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(entity)) {
                    entityAvailable = true;
                    break;
                }
            }

            // Check current location
            if (!entityAvailable) {
                for (GameEntity locationEntity : currentLocation.getEntityList()) {
                    if (locationEntity.getEntityName().equalsIgnoreCase(entity)) {
                        entityAvailable = true;
                        break;
                    }
                }
            }

            if (!entityAvailable) {
                return false;
            }
        }

        return true;
    }

    private void handleConsumedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation("storeroom");

        for (String consumed : action.getConsumed()) {
            if (consumed.equalsIgnoreCase("health")) {
                player.decreaseHealth();
                continue;
            }

            // Check if consumed entity is a location
            Location locationToConsume = this.gameTracker.getLocation(consumed);
            if (locationToConsume != null) {
                if (currentLocation.getPathMap().containsKey(consumed.toLowerCase())) {
                    currentLocation.getPathMap().remove(consumed.toLowerCase());
                }
                continue;
            }

            // Check if entity is in the current location
            GameEntity entityInLocation = null;
            for (GameEntity entity : currentLocation.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(consumed)) {
                    entityInLocation = entity;
                    break;
                }
            }

            if (entityInLocation != null) {
                currentLocation.removeEntity(entityInLocation);
                if (storeroom != null) {
                    storeroom.addEntity(entityInLocation);
                }
                continue;
            }

            // Try to find in inventory
            GameEntity entityInInventory = null;
            for (GameEntity entity : player.getInventory()) {
                if (entity.getEntityName().equalsIgnoreCase(consumed)) {
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
    }

    private void handleProducedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = this.gameTracker.getLocation("storeroom");

        for (String produced : action.getProduced()) {
            if (produced.equalsIgnoreCase("health")) {
                if (player.getHealth() < 3) player.increaseHealth();
                continue;
            }

            Location existingLocation = this.gameTracker.getLocation(produced);

            if (existingLocation != null) {
                Path pathToLocation = new Path("path_" + currentLocation.getEntityName() + "_to_" + produced,
                        "A path from " + currentLocation.getEntityName() + " to " + produced,
                        currentLocation, existingLocation);

                currentLocation.addPath(produced.toLowerCase(), pathToLocation);
            } else {
                GameEntity entityInPlayersInventory = null;
                for (GameEntity entity : player.getInventory()) {
                    if (entity.getEntityName().equalsIgnoreCase(produced)) {
                        entityInPlayersInventory = entity;
                        break;
                    }
                }

                if (entityInPlayersInventory != null) {
                    player.removeFromInventory(entityInPlayersInventory);
                    currentLocation.addEntity(entityInPlayersInventory);
                    continue;
                }

                boolean foundInOtherPlayer = false;
                for (Player otherPlayer : gameTracker.playerMap.values()) {
                    if (otherPlayer == player) continue;

                    for (GameEntity entity : otherPlayer.getInventory()) {
                        if (entity.getEntityName().equalsIgnoreCase(produced)) {
                            foundInOtherPlayer = true;
                            break;
                        }
                    }

                    if (foundInOtherPlayer) break;
                }

                if (foundInOtherPlayer) continue;

                GameEntity existingEntity = null;
                Location entityLocation = null;

                // Search for the entity in all locations
                for (Location location : gameTracker.getLocationMap().values()) {
                    // Skip current location and storeroom when searching
                    if (location == currentLocation || (storeroom != null && location == storeroom)) {
                        continue;
                    }

                    for (GameEntity entity : location.getEntityList()) {
                        if (entity.getEntityName().equalsIgnoreCase(produced)) {
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
                    continue;
                }

                GameEntity entityInStoreroom = null;
                if (storeroom != null) {
                    for (GameEntity entity : storeroom.getEntityList()) {
                        if (entity.getEntityName().equalsIgnoreCase(produced)) {
                            entityInStoreroom = entity;
                            break;
                        }
                    }
                }

                if (entityInStoreroom != null) {
                    storeroom.removeEntity(entityInStoreroom);
                    currentLocation.addEntity(entityInStoreroom);
                } else {
                    // Check if the entity already exists in the current location (to prevent duplicates)
                    boolean entityExists = false;
                    for (GameEntity entity : currentLocation.getEntityList()) {
                        if (entity.getEntityName().equalsIgnoreCase(produced)) {
                            entityExists = true;
                            break;
                        }
                    }
                }
            }
        }
    }
}
