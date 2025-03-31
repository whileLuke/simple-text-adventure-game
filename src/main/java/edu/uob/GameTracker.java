package edu.uob;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameTracker {
    public Map<String, Location> locationMap;
    public Map<String, Player> playerMap;
    public Map<String, GameAction> actionMap;

    public GameTracker() {
        this.locationMap = new LinkedHashMap<>();
        this.playerMap = new HashMap<>();
        this.actionMap = new HashMap<>();
    }

    public void addLocation(Location location) {
        this.locationMap.put(location.getEntityName().toLowerCase(), location);
    }

    public Location getLocation(String locationName) {
        return this.locationMap.get(locationName.toLowerCase());
    }

    public Map<String, Location> getLocationMap() {
        return this.locationMap;
    }

    public void addPlayer(Player player) {
        this.playerMap.put(player.getEntityName().toLowerCase(), player);
    }

    public Player getPlayer(String playerName) {
        return this.playerMap.get(playerName.toLowerCase());
    }

    public boolean playerExists(String playerName) {
        return this.playerMap.containsKey(playerName.toLowerCase());
    }

    public void addAction(String trigger, GameAction action) {
        this.actionMap.put(trigger.toLowerCase(), action);
    }

    public GameAction getAction(String trigger) {
        return this.actionMap.get(trigger.toLowerCase());
    }

    public Map<String, GameAction> getActionMap() {
        return this.actionMap;
    }

    public GameEntity findEntityInLocation(String entityName, Location location) {
        for (GameEntity gameEntity : location.getEntityList()) {
            if (gameEntity.getEntityName().equalsIgnoreCase(entityName)) {
                return gameEntity;
            }
        }
        return null;
    }

    public GameEntity findEntityInInventory(String entityName, Player player) {
        for (GameEntity gameEntity : player.getInventory()) {
            if (gameEntity.getEntityName().equalsIgnoreCase(entityName)) {
                return gameEntity;
            }
        }
        return null;
    }

    public String getEntityType(String entityName) {
        for (Location location : this.locationMap.values()) {
            for (GameEntity entity : location.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                    if (entity instanceof Artefact) return "artefact";
                    if (entity instanceof Furniture) return "furniture";
                    if (entity instanceof Character) return "character";
                    return "entity";
                }
            }
        }

        for (Player player : this.playerMap.values()) {
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(entityName)) {
                    if (item instanceof Artefact) return "artefact";
                    if (item instanceof Furniture) return "furniture";
                    if (item instanceof Character) return "character";
                    return "entity";
                }
            }
        }

        return "artefact";
    }
}

