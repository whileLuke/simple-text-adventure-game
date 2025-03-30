package edu.uob;

import java.util.List;

public class InvCommand extends GameCommand {
    @Override
    public String execute() {
        Player player = this.getPlayer();
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