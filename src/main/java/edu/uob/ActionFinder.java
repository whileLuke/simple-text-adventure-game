package edu.uob;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class ActionFinder {
    private GameTracker gameTracker;

    public ActionFinder(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public List<GameAction> findMatchingActions(String command) {
        List<GameAction> potentialActions = new LinkedList<>();
        String commandLowerCase = command.toLowerCase();

        for (Map.Entry<String, GameAction> entry : gameTracker.getActionMap().entrySet()) {
            String trigger = entry.getKey();
            if (containsWholeWord(commandLowerCase, trigger) && trigger.contains(" ")) {
                addUniqueAction(potentialActions, entry.getValue());
            }
        }

        if (potentialActions.isEmpty()) {
            for (Map.Entry<String, GameAction> entry : gameTracker.getActionMap().entrySet()) {
                String trigger = entry.getKey();
                if (containsWholeWord(commandLowerCase, trigger)) {
                    addUniqueAction(potentialActions, entry.getValue());
                }
            }
        }

        return potentialActions;
    }

    private void addUniqueAction(List<GameAction> actions, GameAction action) {
        if (!actions.contains(action)) {
            actions.add(action);
        }
    }

    private boolean containsWholeWord(String text, String word) {
        String pattern = "\\b" + word + "\\b";
        return text.matches(".*" + pattern + ".*");
    }
}
