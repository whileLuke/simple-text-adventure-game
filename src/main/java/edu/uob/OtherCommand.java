package edu.uob;

import java.util.*;

public class OtherCommand extends GameCommand {
    @Override
    public String execute() {
        // Validate that gameTracker is set
        if (gameTracker == null) {
            return "Game state not initialized.";
        }

        Player player = getPlayer();
        if (player == null) return "Player not found";

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return "Location not found";

        // First, find a matching trigger in the command
        String[] commandWords = command.toLowerCase().split("\\s+");
        GameAction matchedAction = null;

        // Try to find a trigger match
        for (String word : commandWords) {
            if (gameTracker.getActionMap().containsKey(word)) {
                if (matchedAction != null) {
                    return "There is more than one action possible - which one do you want to perform?";
                }
                matchedAction = gameTracker.getActionMap().get(word);
            }
        }

        if (matchedAction == null) {
            return "I don't understand that command.";
        }

        // Extract entities from the command
        Set<String> commandEntities = new HashSet<>();
        if (trimmedCommand != null) {
            commandEntities = trimmedCommand.getEntities();
        }

        // Get all the required entities for this action
        Set<String> allRequiredEntities = new HashSet<>();
        allRequiredEntities.addAll(matchedAction.getArtefacts());
        allRequiredEntities.addAll(matchedAction.getFurniture());
        allRequiredEntities.addAll(matchedAction.getCharacters());

        // If there are required entities, check if the command mentions any of them OR any other entities
        if (!allRequiredEntities.isEmpty() && !commandEntities.isEmpty()) {
            // We just need to ensure that at least one entity is mentioned
            // We don't need to validate that it's a required entity, as that will be handled
            // by the availability check later. This allows "chop axe" to work if both axe and tree
            // are available entities.
        }

        // Check if ALL required entities for the action are available
        if (!checkAllRequiredEntitiesAvailable(matchedAction, currentLocation, player)) {
            return "You can't do that here. You don't have all the required items.";
        }

        // Handle consumed and produced entities
        handleConsumedEntities(matchedAction, currentLocation, player);
        handleProducedEntities(matchedAction, currentLocation);

        int healthChange = matchedAction.getHealthChange();
        if (healthChange != 0) {
            if (healthChange > 0) {
                for (int i = 0; i < healthChange; i++) {
                    player.increaseHealth();
                }
            } else {
                for (int i = 0; i < Math.abs(healthChange); i++) {
                    player.decreaseHealth();
                }

                // Check if player died
                if (player.isDead()) {
                    // Drop all inventory items at current location
                    for (GameEntity item : new LinkedList<>(player.getInventory())) {
                        player.removeFromInventory(item);
                        currentLocation.addEntity(item);
                    }

                    // Reset health and move to start location
                    player.resetHealth();
                    Location startLocation = gameTracker.getLocationMap().values().iterator().next();
                    player.setCurrentLocation(startLocation);

                    return "You have died and lost all your items. You've been returned to " +
                            startLocation.getEntityName() + " with full health.";
                }
            }
        }

        // Return narration or default success message
        return matchedAction.getNarration().isEmpty()
                ? "You successfully performed the action."
                : matchedAction.getNarration().get(0);
    }

    // Check if ALL required entities for the action are available
    private boolean checkAllRequiredEntitiesAvailable(GameAction action, Location currentLocation, Player player) {
        List<String> requiredArtifacts = action.getArtefacts();
        List<String> requiredFurniture = action.getFurniture();
        List<String> requiredCharacters = action.getCharacters();

        // If no required entities, action is always valid
        if (requiredArtifacts.isEmpty() && requiredFurniture.isEmpty() && requiredCharacters.isEmpty()) {
            return true;
        }

        // Check if all required artifacts are available (in inventory or location)
        for (String artifact : requiredArtifacts) {
            boolean hasArtifact = false;

            // Check if player has this artifact in inventory
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(artifact)) {
                    hasArtifact = true;
                    break;
                }
            }

            // If not in inventory, check if it's in the current location
            if (!hasArtifact) {
                for (GameEntity entity : currentLocation.getEntityList()) {
                    if (entity.getEntityName().equalsIgnoreCase(artifact)) {
                        hasArtifact = true;
                        break;
                    }
                }
            }

            // If required artifact is not available anywhere, return false
            if (!hasArtifact) {
                return false;
            }
        }

        // Check if all required furniture items are in the location
        for (String furniture : requiredFurniture) {
            boolean hasFurniture = false;

            for (GameEntity entity : currentLocation.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(furniture)) {
                    hasFurniture = true;
                    break;
                }
            }

            if (!hasFurniture) {
                return false;
            }
        }

        // Check if all required characters are in the location
        for (String character : requiredCharacters) {
            boolean hasCharacter = false;

            for (GameEntity entity : currentLocation.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(character)) {
                    hasCharacter = true;
                    break;
                }
            }

            if (!hasCharacter) {
                return false;
            }
        }

        // All required entities are available
        return true;
    }

    // Handle removal of consumed entities
    private void handleConsumedEntities(GameAction action, Location currentLocation, Player player) {
        Location storeroom = gameTracker.getLocation("storeroom");

        for (String consumed : action.getConsumed()) {
            // Try to find in location first
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

    // Handle creation of produced entities
    private void handleProducedEntities(GameAction action, Location currentLocation) {
        Location storeroom = gameTracker.getLocation("storeroom");

        for (String produced : action.getProduced()) {
            // Check if this produced item is actually a location
            Location existingLocation = gameTracker.getLocation(produced);

            if (existingLocation != null) {
                // If it's a location, create a path to it
                Path pathToLocation = new Path("path_" + currentLocation.getEntityName() + "_to_" + produced,
                        "A path from " + currentLocation.getEntityName() + " to " + produced,
                        currentLocation, existingLocation);

                // Add the path to the current location
                currentLocation.addPath(produced.toLowerCase(), pathToLocation);
            } else {
                // First, check if the entity exists in any other location
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

                // If found in another location, move it to current location
                if (existingEntity != null && entityLocation != null) {
                    entityLocation.removeEntity(existingEntity);
                    currentLocation.addEntity(existingEntity);
                    continue;
                }

                // Check if entity exists in storeroom
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
                    // Move from storeroom to current location
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

                    // If it doesn't exist anywhere, create a new one
                }
            }
        }
    }
}
