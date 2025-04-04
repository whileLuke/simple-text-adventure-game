package edu.uob.ActionManagement;

import edu.uob.GameManagement.GameHelper;
import edu.uob.GameManagement.GameTracker;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class ActionFinder {
    private final GameTracker gameTracker;
    private final GameHelper gameHelper;

    public ActionFinder(GameTracker gameTracker, GameHelper gameHelper) {
        this.gameTracker = gameTracker;
        this.gameHelper = gameHelper;
    }

    public List<GameAction> findMatchingActions(String playerCommand) {
        List<GameAction> potentialActions = new LinkedList<>();
        String commandLowerCase = playerCommand.toLowerCase();

        for (Map.Entry<String, List<GameAction>> mapEntry : this.gameTracker.getTriggerActionMap().entrySet()) {
            String triggerString = mapEntry.getKey();

            if (this.gameHelper.containsWord(commandLowerCase, triggerString)) {
                for (GameAction gameAction : mapEntry.getValue()) {
                    if (!potentialActions.contains(gameAction)) {
                        potentialActions.add(gameAction);
                    }
                }
            }
        }

        return potentialActions;
    }
}