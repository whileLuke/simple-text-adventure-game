package edu.uob.GameManagement;

import edu.uob.ActionManagement.GameAction;
import edu.uob.EntityManagement.GameEntity;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.EntityManagement.PlayerEntity;

import java.util.LinkedList;

public class HealthManager {
    private final GameTracker gameTracker;

    public HealthManager(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
    }

    public boolean applyHealthEffects(GameAction action, PlayerEntity player) {
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

    private boolean increasePlayerHealth(PlayerEntity player, int amount) {
        for (int i = 0; i < amount; i++) {
            if (player.getHealth() < 3) {
                player.increaseHealth();
            }
        }
        return false;
    }

    private boolean decreasePlayerHealth(PlayerEntity player, int amount) {
        for (int i = 0; i < amount; i++) {
            player.decreaseHealth();
            if (player.isDead()) {
                return true;
            }
        }
        return false;
    }

    public String handlePlayerDeath(PlayerEntity player, LocationEntity currentLocation) {
        this.transferAllItemsToLocation(player, currentLocation);
        player.resetHealth();

        LocationEntity startLocation = this.findStartLocation();
        player.setCurrentLocation(startLocation);

        return this.formatPlayerDeathMessage(startLocation);
    }

    private LocationEntity findStartLocation() {
        return this.gameTracker.getLocationMap().values().iterator().next();
    }

    private String formatPlayerDeathMessage(LocationEntity startLocation) {
        StringBuilder message = new StringBuilder();
        message.append("You have died and lost all your items. You've been returned to the ");
        message.append(startLocation.getEntityName()).append(" with full health! ");
        message.append("\n").append("You are in the ").append(startLocation.getEntityName());
        message.append(": ").append(startLocation.getEntityDescription());
        return message.toString();
    }

    private void transferAllItemsToLocation(PlayerEntity player, LocationEntity location) {
        LinkedList<GameEntity> inventory = new LinkedList<>(player.getInventory());
        for (GameEntity item : inventory) {
            player.removeFromInventory(item);
            location.addEntity(item);
        }
    }
}
