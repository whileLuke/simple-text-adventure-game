package edu.uob.CommandManagement;

import edu.uob.ActionManagement.ActionFinder;
import edu.uob.ActionManagement.ActionValidator;
import edu.uob.ActionManagement.GameAction;
import edu.uob.EntityManagement.EntityProcessor;
import edu.uob.GameManagement.GameHelper;
import edu.uob.GameManagement.GameTracker;
import edu.uob.GameManagement.HealthManager;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

import java.util.*;

public class CustomCommand extends GameCommand {
    private EntityProcessor entityProcessor;
    private HealthManager healthManager;
    private ActionValidator actionValidator;
    private ActionFinder actionFinder;

    @Override
    public String executeCommand() {
        if (this.gameTracker == null) return "Could not perform action.";
        if (this.entityProcessor == null) this.initialiseComponents();

        PlayerEntity player = getPlayer();
        if (player == null) return "Player could not be found.";

        LocationEntity currentLocation = player.getPlayerLocation();
        if (currentLocation == null) return "Location could not be found";

        Set<String> commandEntities = this.extractCommandEntities();
        GameAction selectedAction = this.findValidAction(commandEntities, currentLocation, player);

        if (selectedAction == null) return "You can't do that.";

        return this.executeAction(selectedAction, currentLocation, player);
    }

    @Override
    public void setGameTracker(GameTracker gameTracker) {
        super.setGameTracker(gameTracker);
        this.initialiseComponents();
    }

    private void initialiseComponents() {
        GameHelper gameHelper = new GameHelper();
        this.entityProcessor = new EntityProcessor(this.gameTracker);
        this.healthManager = new HealthManager(this.gameTracker);
        this.actionValidator = new ActionValidator(this.gameTracker, gameHelper);
        this.actionFinder = new ActionFinder(this.gameTracker, gameHelper);
    }

    private GameAction findValidAction(Set<String> commandEntities,
                                       LocationEntity currentLocation, PlayerEntity playerEntity) {
        List<GameAction> potentialActions = this.actionFinder.findMatchingActions(this.gameCommand);
        List<GameAction> validActions = new LinkedList<>();
        for (GameAction gameAction : potentialActions) {
            if (this.actionValidator.isActionExecutable(gameAction, commandEntities, currentLocation, playerEntity)) {
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

        if (playerDied || player.isPlayerDead()) {
            return this.healthManager.handlePlayerDeath(player, currentLocation);
        }

        if (action.getNarration().isEmpty()) return "You successfully performed the action.";
        else return action.getNarration().get(0);
    }

    private Set<String> extractCommandEntities() {
        CommandTrimmer trimmer = new CommandTrimmer(this.gameTracker);
        CommandComponents components = trimmer.parseCommand(this.gameCommand);
        return components.getEntities();
    }
}
