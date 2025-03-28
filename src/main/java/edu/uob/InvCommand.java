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
            response.append(item.getName()).append(": ");
            response.append(item.getDescription()).append("\n");
        }
        return response.toString();
    }
}