package edu.uob;

import java.util.*;

public class OtherCommand extends GameCommand {
    private ActionValidator actionValidator = new ActionValidator(this.gameTracker);
    private EntityProcessor entityProcessor = new EntityProcessor(this.gameTracker);
    private HealthManager healthManager = new HealthManager(this.gameTracker);
    private ActionFinder actionMatcher = new ActionFinder(this.gameTracker);

    @Override
    public String executeCommand() {
        if (this.actionValidator == null) {
            this.actionValidator = new ActionValidator(this.gameTracker);
            this.entityProcessor = new EntityProcessor(this.gameTracker);
            this.healthManager = new HealthManager(this.gameTracker);
        }

        if (this.gameTracker == null) return "Game tracker is null.";

        Player player = this.getPlayer();
        if (player == null) return "Player could not be found.";

        Location currentLocation = player.getCurrentLocation();
        if (currentLocation == null) return "Location could not be found";

        Set<String> commandEntities = extractCommandEntities();
        List<GameAction> potentialActions = this.actionMatcher.findMatchingActions(this.command);
        if (potentialActions.isEmpty()) return "You can't do that.";

        List<GameAction> validActions = new LinkedList<>();
        for (GameAction gameAction : potentialActions) {
            if (this.actionValidator.isActionExecutable(gameAction, commandEntities, currentLocation, player)) {
                validActions.add(gameAction);
            }
        }

        if (validActions.isEmpty()) return "You can't do that.";

        if (validActions.size() > 1) return "You tried to do more than one action. You can't.";

        return executeAction(validActions.get(0), currentLocation, player);
    }

    private String executeAction(GameAction action, Location currentLocation, Player player) {
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
        CommandComponents components = trimmer.parseCommand(this.command);
        return components.getEntities();
    }
}
