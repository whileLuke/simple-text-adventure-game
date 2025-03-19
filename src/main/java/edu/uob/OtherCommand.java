package edu.uob;

public class OtherCommand extends STAGCommand {

    //TODO: implement execute for othercommands. Should also make sure verbosity doesnt exist, have to just do like contains one of the item or locatio names instead for all the other commands rather than looking for their exact position.
    public String execute() {
        return command;
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