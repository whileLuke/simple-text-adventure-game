package edu.uob;

import java.util.*;

public class OtherCommand extends GameCommand {
    @Override
    public String execute() {
        // Remove any additional decorative words and normalize the command
        String normalisedCommand = normaliseCommand(command);

        // Extract unique entity names from the command
        Set<String> commandEntities = extractEntitiesFromCommand(normalisedCommand);

        // Find potential matching actions
        List<GameAction> matchingActions = findMatchingActions(normalisedCommand, commandEntities);

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
        if (!checkActionAvailability(action, normalisedCommand, commandEntities)) {
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

    private Set<String> buildValidCommandComponents() {
        Set<String> validComponents = new HashSet<>();

        // Add basic command keywords
        validComponents.addAll(Arrays.asList("get", "goto", "look", "drop", "inventory", "inv"));

        // Add all known actions from game tracker
        for (String actionTrigger : gameTracker.getActionMap().keySet()) {
            validComponents.add(actionTrigger.toLowerCase());
        }

        // Add all game entities (from locations and player inventory)
        for (Location location : gameTracker.getLocationMap().values()) {
            for (GameEntity entity : location.getEntityList()) {
                validComponents.add(entity.getName().toLowerCase());
            }
        }

        // Add inventory items
        Player player = getPlayer();
        if (player != null) {
            for (GameEntity item : player.getInventory()) {
                validComponents.add(item.getName().toLowerCase());
            }
        }

        return validComponents;
    }

    private String normaliseCommand(String command) {
        // Convert to lowercase and split into words
        String[] words = command.toLowerCase().split("\\s+");
        Set<String> validComponents = buildValidCommandComponents();

        // Filter to keep only valid components
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (validComponents.contains(word)) {
                filteredWords.add(word);
            }
        }

        // Reconstruct the command with only valid words
        return String.join(" ", filteredWords);
    }

    // Find actions that match the command's trigger and potentially its entities
    private List<GameAction> findMatchingActions(String normalizedCommand, Set<String> commandEntities) {
        List<GameAction> matchingActions = new ArrayList<>();

        for (Map.Entry<String, GameAction> entry : gameTracker.getActionMap().entrySet()) {
            String trigger = entry.getKey().toLowerCase();
            GameAction action = entry.getValue();

            // Check if command contains the trigger
            if (normalizedCommand.contains(trigger)) {
                // Check if all action-specific entities are in the command
                Set<String> requiredEntities = new HashSet<>();

                // Add artefacts
                for (String artefact : action.getArtefacts()) {
                    requiredEntities.add(artefact.toLowerCase());
                }

                // Add furniture
                for (String furniture : action.getFurniture()) {
                    requiredEntities.add(furniture.toLowerCase());
                }

                // Add characters
                for (String character : action.getCharacters()) {
                    requiredEntities.add(character.toLowerCase());
                }

                // If no required entities, or all required entities are in the command
                if (requiredEntities.isEmpty() || commandEntities.containsAll(requiredEntities)) {
                    matchingActions.add(action);
                }
            }
        }

        return matchingActions;
    }

    // Check if all required entities for the action are available
    private boolean checkActionAvailability(GameAction action, String normalizedCommand, Set<String> commandEntities) {
        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();

        // Get all required and available entities
        Set<String> requiredEntities = new HashSet<>();

        // Add artefacts
        for (String artefact : action.getArtefacts()) {
            requiredEntities.add(artefact.toLowerCase());
        }

        // Add furniture
        for (String furniture : action.getFurniture()) {
            requiredEntities.add(furniture.toLowerCase());
        }

        // Add characters
        for (String character : action.getCharacters()) {
            requiredEntities.add(character.toLowerCase());
        }

        // Check if all required entities are available in location or inventory
        for (String requiredEntity : requiredEntities) {
            boolean entityFound = false;

            // Check in location
            for (GameEntity locationEntity : currentLocation.getEntityList()) {
                if (locationEntity.getName().toLowerCase().equals(requiredEntity)) {
                    entityFound = true;
                    break;
                }
            }

            // Check in inventory if not found in location
            if (!entityFound) {
                for (GameEntity inventoryItem : player.getInventory()) {
                    if (inventoryItem.getName().toLowerCase().equals(requiredEntity)) {
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

        // Check for extraneous entities
        Set<String> allGameEntities = getAllGameEntities();
        for (String entity : commandEntities) {
            if (!requiredEntities.contains(entity) && !allGameEntities.contains(entity)) {
                return false;
            }
        }

        return true;
    }

    // Extract unique entity names from the command
    private Set<String> extractEntitiesFromCommand(String normalizedCommand) {
        Set<String> allEntities = getAllGameEntities();
        Set<String> matchingEntities = new HashSet<>();

        for (String entity : allEntities) {
            if (normalizedCommand.contains(entity.toLowerCase())) {
                matchingEntities.add(entity.toLowerCase());
            }
        }

        return matchingEntities;
    }

    // Get all game entities as lowercase names
    private Set<String> getAllGameEntities() {
        Set<String> entities = new HashSet<>();

        for (Location location : gameTracker.getLocationMap().values()) {
            for (GameEntity entity : location.getEntityList()) {
                entities.add(entity.getName().toLowerCase());
            }
        }

        Player player = getPlayer();
        for (GameEntity item : player.getInventory()) {
            entities.add(item.getName().toLowerCase());
        }

        return entities;
    }

    // Handle removal of consumed entities
    private void handleConsumedEntities(GameAction action) {
        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();

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
        Location currentLocation = player.getCurrentLocation();

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

    private GameEntity findTemplateEntity(String entityName) {
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
}
