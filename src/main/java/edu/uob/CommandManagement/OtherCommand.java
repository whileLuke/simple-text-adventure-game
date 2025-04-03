package edu.uob.CommandManagement;

import edu.uob.ActionManagement.ActionFinder;
import edu.uob.ActionManagement.ActionValidator;
import edu.uob.ActionManagement.GameAction;
import edu.uob.EntityManagement.EntityProcessor;
import edu.uob.GameManagement.HealthManager;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

import java.util.*;

public class OtherCommand extends GameCommand {
    private EntityProcessor entityProcessor;
    private HealthManager healthManager;

    @Override
    public String executeCommand() {
        if (!this.initializeComponents()) return "Could not perform action.";

        PlayerEntity player = getPlayer();
        if (player == null) return "Player could not be found.";

        LocationEntity currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return "Location could not be found";

        Set<String> commandEntities = extractCommandEntities();
        GameAction selectedAction = findValidAction(commandEntities, currentLocation, player);

        if (selectedAction == null) return "You can't do that.";

        return executeAction(selectedAction, currentLocation, player);
    }

    private boolean initializeComponents() {
        if (gameTracker == null) return false;
        entityProcessor = new EntityProcessor(gameTracker);
        healthManager = new HealthManager(gameTracker);
        return true;
    }

    private GameAction findValidAction(Set<String> commandEntities,
                                       LocationEntity currentLocation,
                                       PlayerEntity playerEntity) {
        ActionValidator actionValidator = new ActionValidator(this.gameTracker);
        ActionFinder actionFinder = new ActionFinder(this.gameTracker);

        List<GameAction> potentialActions = actionFinder.findMatchingActions(this.gameCommand);
        if (potentialActions.isEmpty()) return null;

        List<GameAction> validActions = new LinkedList<>();
        for (GameAction gameAction : potentialActions) {
            if (actionValidator.isActionExecutable(gameAction, commandEntities, currentLocation, playerEntity)) {
                validActions.add(gameAction);
            }
        }

        if (validActions.size() != 1) return null;

        return validActions.get(0);
    }

    private String executeAction(GameAction action, LocationEntity currentLocation, PlayerEntity player) {
        this.entityProcessor.processConsumedEntities(action, currentLocation, player);
        this.entityProcessor.processProducedEntities(action, currentLocation, player);

        boolean playerDied = this.healthManager.applyHealthEffects(action, player);

        if (playerDied || player.isDead()) {
            return this.healthManager.handlePlayerDeath(player, currentLocation);
        }

        if (action.getNarration().isEmpty()) {
            return "You successfully performed the action.";
        } else {
            return action.getNarration().get(0);
        }
    }

    private Set<String> extractCommandEntities() {
        CommandTrimmer trimmer = new CommandTrimmer(this.gameTracker);
        CommandComponents components = trimmer.parseCommand(this.gameCommand);
        return components.getEntities();
    }
}
