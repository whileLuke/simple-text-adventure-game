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

        // Check if all required entities are mentioned in the command
        Set<String> requiredEntities = new HashSet<>();
        requiredEntities.addAll(matchedAction.getArtefacts());
        requiredEntities.addAll(matchedAction.getFurniture());
        requiredEntities.addAll(matchedAction.getCharacters());

        // If there are required entities, check if at least one is mentioned
        if (!requiredEntities.isEmpty()) {
            boolean hasAnyRequiredEntity = false;
            for (String entity : requiredEntities) {
                if (commandEntities.contains(entity)) {
                    hasAnyRequiredEntity = true;
                    break;
                }
            }

            if (!hasAnyRequiredEntity) {
                return "You need to specify what to use with this action.";
            }
        }

        // Check action availability (entities must be in location or inventory)
        if (!checkActionAvailability(matchedAction, commandEntities, currentLocation, player)) {
            return "You can't do that here.";
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

    // Check if all required entities for the action are available
    private boolean checkActionAvailability(GameAction action, Set<String> commandEntities,
                                            Location currentLocation, Player player) {
        // Get all entity types from the action
        List<String> actionArtifacts = action.getArtefacts();
        List<String> actionFurniture = action.getFurniture();
        List<String> actionCharacters = action.getCharacters();

        // If no required entities, action is valid
        if (actionArtifacts.isEmpty() && actionFurniture.isEmpty() && actionCharacters.isEmpty()) {
            return true;
        }

        // For each entity mentioned in the command, check if it's a required entity
        for (String commandEntity : commandEntities) {
            boolean isRequiredArtifact = false;
            boolean isRequiredFurniture = false;
            boolean isRequiredCharacter = false;

            // Check if this is a required artifact
            for (String artifact : actionArtifacts) {
                if (artifact.equalsIgnoreCase(commandEntity)) {
                    isRequiredArtifact = true;
                    // Check if player has this artifact
                    boolean hasArtifact = false;
                    for (GameEntity item : player.getInventory()) {
                        if (item.getEntityName().equalsIgnoreCase(artifact)) {
                            hasArtifact = true;
                            break;
                        }
                    }
                    if (!hasArtifact) {
                        // Required artifact mentioned but not in inventory
                        boolean inLocation = false;
                        for (GameEntity entity : currentLocation.getEntityList()) {
                            if (entity.getEntityName().equalsIgnoreCase(artifact)) {
                                inLocation = true;
                                break;
                            }
                        }
                        if (!inLocation) {
                            // Required artifact is neither in inventory nor location
                            return false;
                        }
                    }
                }
            }

            // Check furniture and characters (these are typically in location)
            for (String furniture : actionFurniture) {
                if (furniture.equalsIgnoreCase(commandEntity)) {
                    isRequiredFurniture = true;
                    boolean furniturePresent = false;
                    for (GameEntity entity : currentLocation.getEntityList()) {
                        if (entity.getEntityName().equalsIgnoreCase(furniture)) {
                            furniturePresent = true;
                            break;
                        }
                    }
                    if (!furniturePresent) return false;
                }
            }

            for (String character : actionCharacters) {
                if (character.equalsIgnoreCase(commandEntity)) {
                    isRequiredCharacter = true;
                    boolean characterPresent = false;
                    for (GameEntity entity : currentLocation.getEntityList()) {
                        if (entity.getEntityName().equalsIgnoreCase(character)) {
                            characterPresent = true;
                            break;
                        }
                    }
                    if (!characterPresent) return false;
                }
            }

            // If this is a mentioned entity but not a required one, that's fine
            if (!isRequiredArtifact && !isRequiredFurniture && !isRequiredCharacter) {
                continue;
            }
        }

        // Check if all required entities from the action are mentioned in the command
        // If not all required entities are mentioned, that's handled elsewhere

        return true;
    }

    // Handle removal of consumed entities
    private void handleConsumedEntities(GameAction action, Location currentLocation, Player player) {
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
            }
        }
    }

    // Handle creation of produced entities
    private void handleProducedEntities(GameAction action, Location currentLocation) {
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
                // Otherwise create it as a regular entity as before
                String entityType = gameTracker.getEntityType(produced);
                GameEntity newEntity;

                if ("furniture".equals(entityType)) {
                    newEntity = new Furniture(produced, "A " + produced);
                } else if ("character".equals(entityType)) {
                    newEntity = new Character(produced, "A character named " + produced);
                } else {
                    // Default to artefact
                    newEntity = new Artefact(produced, "A " + produced);
                }

                currentLocation.addEntity(newEntity);
            }
        }
    }
}
