package edu.uob.ActionManagement;

import edu.uob.GameManagement.GameTracker;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class ActionFinder {
    private final GameTracker gameTracker;

    public ActionFinder(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public List<GameAction> findMatchingActions(String playerCommand) {
        List<GameAction> potentialActions = new LinkedList<>();
        String commandLowerCase = playerCommand.toLowerCase();

        for (Map.Entry<String, List<GameAction>> mapEntry : this.gameTracker.getTriggerActionMap().entrySet()) {
            String triggerString = mapEntry.getKey();

            if (containsWholeWord(commandLowerCase, triggerString)) {
                for (GameAction gameAction : mapEntry.getValue()) {
                    if (!potentialActions.contains(gameAction)) {
                        potentialActions.add(gameAction);
                    }
                }
            }
        }

        return potentialActions;
    }

    private boolean containsWholeWord(String playerCommand, String triggerString) {
        StringBuilder containingPattern = new StringBuilder();
        containingPattern.append(".*").append("\\b").append(triggerString).append("\\b").append(".*");
        return playerCommand.matches(String.valueOf(containingPattern));
    }
}