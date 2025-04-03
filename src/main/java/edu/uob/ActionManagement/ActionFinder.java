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

        this.findActionsWithTriggers(commandLowerCase, potentialActions, true);

        if (potentialActions.isEmpty()) {
            this.findActionsWithTriggers(commandLowerCase, potentialActions, false);
        }

        return potentialActions;
    }

    private void findActionsWithTriggers(String commandText, List<GameAction> actionList,
                                         boolean multipleWordTriggers) {
        for (Map.Entry<String, GameAction> gameEntry : gameTracker.getActionMap().entrySet()) {
            String triggerString = gameEntry.getKey();
            boolean isMultipleWords = triggerString.contains(" ");

            boolean multipleWordMatch = multipleWordTriggers && isMultipleWords &&
                    commandText.contains(triggerString);
            boolean singleWordMatch = !multipleWordTriggers && !isMultipleWords &&
                    this.containsWholeWord(commandText, triggerString);

            boolean actionToAdd = multipleWordMatch || singleWordMatch;

            if (actionToAdd) addUniqueAction(actionList, gameEntry.getValue());
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
