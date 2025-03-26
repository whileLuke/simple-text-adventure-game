package edu.uob;

import java.util.Map;

public class OtherCommand extends GameCommand {
    @Override
    public String execute() {
        // Look for matching action
        for (Map.Entry<String, GameAction> entry : gameTracker.getActionMap().entrySet()) {
            String trigger = entry.getKey();
            if (command.toLowerCase().contains(trigger)) {
                GameAction action = entry.getValue();

                // Check if all action-specific entities are available
                boolean allEntitiesAvailable = checkEntityAvailability(action);

                if (!allEntitiesAvailable) {
                    return "You can't do that here.";
                }

                // Handle consumed entities
                handleConsumedEntities(action);

                // Handle produced entities
                handleProducedEntities(action);

                // Return narration
                if (!action.getNarration().isEmpty()) {
                    return action.getNarration().get(0);
                }

                return "You " + trigger + " successfully.";
            }
        }

        return "I don't understand that command.";
    }

    private boolean checkEntityAvailability(GameAction action) {
        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();

        // Check artifacts
        for (String artifact : action.getArtefacts()) {
            if (gameTracker.findEntityInLocation(artifact, currentLocation) == null &&
                    gameTracker.findEntityInInventory(artifact, player) == null) {
                return false;
            }
        }

        // Check furniture
        for (String furniture : action.getFurniture()) {
            if (gameTracker.findEntityInLocation(furniture, currentLocation) == null) {
                return false;
            }
        }

        // Check characters
        for (String character : action.getCharacters()) {
            if (gameTracker.findEntityInLocation(character, currentLocation) == null) {
                return false;
            }
        }

        return true;
    }

    private void handleConsumedEntities(GameAction action) {
        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();

        for (String consumed : action.getConsumed()) {
            GameEntity entity = gameTracker.findEntityInLocation(consumed, currentLocation);
            if (entity != null) {
                currentLocation.removeEntity(entity);
            } else {
                entity = gameTracker.findEntityInInventory(consumed, player);
                if (entity != null) {
                    player.removeFromInventory(entity);
                }
            }
        }
    }

    private void handleProducedEntities(GameAction action) {
        Player player = getPlayer();
        Location currentLocation = player.getCurrentLocation();

        for (String produced : action.getProduced()) {
            Artefact newEntity = new Artefact(produced, "A " + produced);
            currentLocation.addEntity(newEntity);
        }
    }
}

/*
@Override
    public String execute() {
        // Look for matching action
        for (Map.Entry<String, GameAction> entry : gameState.getActions().entrySet()) {
            String trigger = entry.getKey();
            if (command.toLowerCase().contains(trigger)) {
                GameAction action = entry.getValue();

                // Check if all subjects are available
                boolean allSubjectsAvailable = true;
                Player player = getPlayer();
                for (String subject : action.getSubjects()) {
                    GameEntity entity = gameState.findEntityInLocation(subject, player.getCurrentLocation());
                    if (entity == null) {
                        entity = gameState.findEntityInInventory(subject, player);
                    }
                    if (entity == null) {
                        allSubjectsAvailable = false;
                        break;
                    }
                }

                if (!allSubjectsAvailable) {
                    return "You can't do that here.";
                }

                // Handle consumed entities
                for (String consumed : action.getConsumed()) {
                    GameEntity entity = gameState.findEntityInLocation(consumed, player.getCurrentLocation());
                    if (entity != null) {
                        player.getCurrentLocation().removeEntity(entity);
                    } else {
                        entity = gameState.findEntityInInventory(consumed, player);
                        if (entity != null) {
                            player.removeFromInventory(entity);
                        }
                    }
                }

                // Handle produced entities
                for (String produced : action.getProduced()) {
                    Artifact newEntity = new Artifact(produced, "A " + produced);
                    player.getCurrentLocation().addEntity(newEntity);
                }

                // Return narration
                if (!action.getNarration().isEmpty()) {
                    return action.getNarration().get(0);
                }

                return "You " + trigger + " successfully.";
            }
        }

        return "I don't understand that command.";
    }
 */