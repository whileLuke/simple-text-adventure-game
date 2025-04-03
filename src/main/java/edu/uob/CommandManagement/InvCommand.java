package edu.uob.CommandManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.PlayerEntity;

import java.util.List;

public class InvCommand extends GameCommand {
    @Override
    public String executeCommand() {
        CommandTrimmer commandTrimmer = new CommandTrimmer(this.gameTracker);
        CommandComponents commandComponents = commandTrimmer.parseCommand(this.command);
        if (!commandComponents.getEntities().isEmpty()) {
            return "You can't use inv with entities. Just use 'inv'.";
        }

        PlayerEntity player = this.getPlayer();
        List<GameEntity> inventory = player.getInventory();

        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder response = new StringBuilder();
        response.append("Your inventory is:\n");
        for (GameEntity item : inventory) {
            response.append(item.getEntityName()).append(": ");
            response.append(item.getEntityDescription()).append("\n");
        }
        return response.toString();
    }
}