package edu.uob.CommandManagement;

import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.PlayerEntity;

import java.util.List;

public class InvCommand extends GameCommand {
    @Override
    public String executeCommand() {
        CommandComponents commandComponents = this.trimmedCommand;
        if (!commandComponents.getEntities().isEmpty()) {
            return "You can't use inv with entities. Just use 'inv'.";
        }

        PlayerEntity player = this.getPlayer();
        List<GameEntity> playerInventory = player.getPlayerInventory();

        if (playerInventory.isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Your inventory is:\n");
        for (GameEntity gameEntity : playerInventory) {
            responseBuilder.append(gameEntity.getEntityName()).append(": ");
            responseBuilder.append(gameEntity.getEntityDescription()).append("\n");
        }
        return responseBuilder.toString();
    }
}