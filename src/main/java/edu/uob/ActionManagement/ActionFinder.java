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

    public List<GameAction> findMatchingActions(String command) {
        List<GameAction> potentialActions = new LinkedList<>();
        String commandLowerCase = command.toLowerCase();

        findActionsWithTriggers(commandLowerCase, potentialActions, true);

        if (potentialActions.isEmpty()) {
            findActionsWithTriggers(commandLowerCase, potentialActions, false);
        }

        return potentialActions;
    }

    private void findActionsWithTriggers(String commandText, List<GameAction> actionList, boolean multipleWordTriggers) {
        for (Map.Entry<String, GameAction> entry : gameTracker.getActionMap().entrySet()) {
            String trigger = entry.getKey();
            boolean isMultipleWords = trigger.contains(" ");

            boolean actionToAdd = (multipleWordTriggers && isMultipleWords) ||
                    (containsWholeWord(commandText, trigger));
            if (actionToAdd) addUniqueAction(actionList, entry.getValue());
        }
    }

    private void addUniqueAction(List<GameAction> actions, GameAction action) {
        if (!actions.contains(action)) {
            actions.add(action);
        }
    }

    private boolean containsWholeWord(String text, String word) {
        StringBuilder containingPattern = new StringBuilder();
        containingPattern.append(".*").append("\\b").append(word).append("\\b").append(".*");
        return text.matches(String.valueOf(containingPattern));
    }
}
