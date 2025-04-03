package edu.uob;

import java.util.LinkedList;

public class HealthManager {
    private final GameTracker gameTracker;

    public HealthManager(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public boolean applyHealthEffects(GameAction action, Player player) {
        int healthChange = action.getHealthChange();

        if (healthChange == 0) {
            return false;
        }

        if (healthChange > 0) {
            return this.increasePlayerHealth(player, healthChange);
        } else {
            return this.decreasePlayerHealth(player, Math.abs(healthChange));
        }
    }

    private boolean increasePlayerHealth(Player player, int amount) {
        for (int i = 0; i < amount; i++) {
            if (player.getHealth() < 3) {
                player.increaseHealth();
            }
        }
        return false;
    }

    private boolean decreasePlayerHealth(Player player, int amount) {
        for (int i = 0; i < amount; i++) {
            player.decreaseHealth();
            if (player.isDead()) {
                return true;
            }
        }
        return false;
    }

    public String handlePlayerDeath(Player player, Location currentLocation) {
        this.transferAllItemsToLocation(player, currentLocation);
        player.resetHealth();

        Location startLocation = this.findStartLocation();
        player.setCurrentLocation(startLocation);

        return this.formatPlayerDeathMessage(startLocation);
    }

    private Location findStartLocation() {
        return this.gameTracker.getLocationMap().values().iterator().next();
    }

    private String formatPlayerDeathMessage(Location startLocation) {
        StringBuilder message = new StringBuilder();
        message.append("You have died and lost all your items. You've been returned to the ");
        message.append(startLocation.getEntityName()).append(" with full health! ");
        message.append("\n").append("You are in the ").append(startLocation.getEntityName());
        message.append(": ").append(startLocation.getEntityDescription());
        return message.toString();
    }

    private void transferAllItemsToLocation(Player player, Location location) {
        LinkedList<GameEntity> inventory = new LinkedList<>(player.getInventory());
        for (GameEntity item : inventory) {
            player.removeFromInventory(item);
            location.addEntity(item);
        }
    }
}
