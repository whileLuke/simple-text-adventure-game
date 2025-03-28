package edu.uob;

import java.util.*;

public class OtherCommand extends GameCommand {
    @Override
    public String execute() {
        // Validate that gameTracker is set
        if (gameTracker == null) {
            return "Game state not initialized.";
        }

        // Extract entities from the command
        Set<String> commandEntities = trimmedCommand != null ? trimmedCommand.getEntities() : new HashSet<>();

        // Find potential matching actions
        List<GameAction> matchingActions = findMatchingActions(commandEntities);

        // Handle ambiguous or no matching actions
        if (matchingActions.isEmpty()) {
            return "I don't understand that command.";
        }

        if (matchingActions.size() > 1) {
            return "There is more than one action possible - which one do you want to perform?";
        }

        // At this point, we have exactly one matching action
        GameAction action = matchingActions.get(0);

        // Check action availability
        if (!checkActionAvailability(action, commandEntities)) {
            return "You can't do that here.";
        }

        // Handle consumed and produced entities
        handleConsumedEntities(action);
        handleProducedEntities(action);

        // Return narration or default success message
        return action.getNarration().isEmpty()
                ? "You successfully performed the action."
                : action.getNarration().get(0);
    }

    // Find actions that match the command's entities
    private List<GameAction> findMatchingActions(Set<String> commandEntities) {
        // Null check for gameTracker
        if (gameTracker == null || gameTracker.getActionMap() == null) {
            return new ArrayList<>();
        }

        List<GameAction> matchingActions = new ArrayList<>();

        for (GameAction action : gameTracker.getActionMap().values()) {
            // Check all possible subject combinations
            Set<String> requiredEntities = new HashSet<>();
            requiredEntities.addAll(action.getArtefacts());
            requiredEntities.addAll(action.getFurniture());
            requiredEntities.addAll(action.getCharacters());

            // Check if all required entities are in the command
            if (requiredEntities.isEmpty() || commandEntities.containsAll(requiredEntities)) {
                matchingActions.add(action);
            }
        }

        return matchingActions;
    }

    // Check if all required entities for the action are available
    private boolean checkActionAvailability(GameAction action, Set<String> commandEntities) {
        // Null checks
        Player player = getPlayer();
        if (player == null) return false;

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return false;

        // Get all required entities
        Set<String> requiredEntities = new HashSet<>();
        requiredEntities.addAll(action.getArtefacts());
        requiredEntities.addAll(action.getFurniture());
        requiredEntities.addAll(action.getCharacters());

        // Check if all required entities are available in location or inventory
        for (String requiredEntity : requiredEntities) {
            boolean entityFound = false;

            // Check in location
            for (GameEntity locationEntity : currentLocation.getEntityList()) {
                if (locationEntity.getName().equalsIgnoreCase(requiredEntity)) {
                    entityFound = true;
                    break;
                }
            }

            // Check in inventory if not found in location
            if (!entityFound) {
                for (GameEntity inventoryItem : player.getInventory()) {
                    if (inventoryItem.getName().equalsIgnoreCase(requiredEntity)) {
                        entityFound = true;
                        break;
                    }
                }
            }

            // If required entity not found, return false
            if (!entityFound) {
                return false;
            }
        }

        return true;
    }

    // Handle removal of consumed entities
    private void handleConsumedEntities(GameAction action) {
        Player player = getPlayer();
        if (player == null) return;

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return;

        for (String consumed : action.getConsumed()) {
            GameEntity entity = findEntityIgnoreCase(consumed, currentLocation, player);
            if (entity != null) {
                if (currentLocation.getEntityList().contains(entity)) {
                    currentLocation.removeEntity(entity);
                } else {
                    player.removeFromInventory(entity);
                }
            }
        }
    }

    // Handle creation of produced entities
    private void handleProducedEntities(GameAction action) {
        Player player = getPlayer();
        if (player == null) return;

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return;

        // Use a map to track the count of each entity to be produced
        Map<String, Integer> entityCounts = new HashMap<>();
        for (String produced : action.getProduced()) {
            entityCounts.put(produced.toLowerCase(),
                    entityCounts.getOrDefault(produced.toLowerCase(), 0) + 1);
        }

        // Produce the correct number of entities
        for (Map.Entry<String, Integer> entry : entityCounts.entrySet()) {
            String producedEntityName = entry.getKey();
            int count = entry.getValue();

            // Find a template entity to use for creation
            GameEntity templateEntity = findTemplateEntity(producedEntityName);

            // Produce specified number of entities
            for (int i = 0; i < count; i++) {
                GameEntity newEntity = createEntityFromTemplate(templateEntity, producedEntityName);
                if (newEntity != null) {
                    currentLocation.addEntity(newEntity);
                }
            }
        }
    }

    // Helper method to find an entity ignoring case
    private GameEntity findEntityIgnoreCase(String entityName, Location location, Player player) {
        // Check location entities
        for (GameEntity entity : location.getEntityList()) {
            if (entity.getName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }

        // Check player inventory
        for (GameEntity entity : player.getInventory()) {
            if (entity.getName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }

        return null;
    }

    // Find a template entity to use for creating new entities
    private GameEntity findTemplateEntity(String entityName) {
        // Null check for gameTracker
        if (gameTracker == null) return null;

        // Search through all existing entities
        List<GameEntity> allEntities = new ArrayList<>();
        for (Location location : gameTracker.getLocationMap().values()) {
            allEntities.addAll(location.getEntityList());
        }

        // Find an existing entity to use as a template
        for (GameEntity entity : allEntities) {
            if (entity.getName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }

        return null;
    }

    // Create a new entity based on a template
    private GameEntity createEntityFromTemplate(GameEntity templateEntity, String entityName) {
        if (templateEntity != null) {
            if (templateEntity instanceof Artefact) {
                return new Artefact(entityName, templateEntity.getDescription());
            } else if (templateEntity instanceof Furniture) {
                return new Furniture(entityName, templateEntity.getDescription());
            } else if (templateEntity instanceof Character) {
                return new Character(entityName, templateEntity.getDescription());
            }
        }

        // Fallback to generic creation if no template found
        return new Artefact(entityName, "A new " + entityName);
    }
}
