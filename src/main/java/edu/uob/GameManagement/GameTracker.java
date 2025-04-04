package edu.uob.GameManagement;

import edu.uob.ActionManagement.GameAction;
import edu.uob.EntityManagement.*;

import java.util.*;

public class GameTracker {
    private final Map<String, LocationEntity> locationMap;
    private final Map<String, PlayerEntity> playerMap;
    private final Map<String, GameAction> actionMap;
    private final Map<String, List<GameAction>> triggerActionMap;

    public GameTracker() {
        this.locationMap = new LinkedHashMap<>();
        this.playerMap = new HashMap<>();
        this.actionMap = new HashMap<>();
        this.triggerActionMap = new HashMap<>();
    }

    public void addLocation(LocationEntity location) {
        this.locationMap.put(location.getEntityName().toLowerCase(), location);
    }

    public LocationEntity getLocation(String locationName) {
        return this.locationMap.get(locationName.toLowerCase());
    }

    public Map<String, LocationEntity> getLocationMap() {
        return this.locationMap;
    }

    public void addPlayer(PlayerEntity player) {
        this.playerMap.put(player.getEntityName().toLowerCase(), player);
    }

    public PlayerEntity getPlayer(String playerName) {
        return this.playerMap.get(playerName.toLowerCase());
    }

    public Map<String, PlayerEntity> getPlayerMap() { return this.playerMap; }

    public boolean playerExists(String playerName) {
        return this.playerMap.containsKey(playerName.toLowerCase());
    }

    public void addAction(String trigger, GameAction action) {
        String lowerCaseTrigger = trigger.toLowerCase();
        this.actionMap.put(lowerCaseTrigger, action);
        if (!triggerActionMap.containsKey(lowerCaseTrigger)) triggerActionMap.put(lowerCaseTrigger, new LinkedList<>());
        if (!triggerActionMap.get(lowerCaseTrigger).contains(action)) triggerActionMap.get(lowerCaseTrigger).add(action);
    }

    public Map<String, GameAction> getActionMap() {
        return this.actionMap;
    }

    public Map<String, List<GameAction>> getTriggerActionMap() { return this.triggerActionMap; }

    public GameEntity findEntity(String entityName, Collection<GameEntity> gameEntities) {
        for (GameEntity gameEntity : gameEntities) {
            if (gameEntity.getEntityName().equalsIgnoreCase(entityName)) {
                return gameEntity;
            }
        }
        return null;
    }
}

