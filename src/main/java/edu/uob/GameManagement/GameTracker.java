package edu.uob.GameManagement;

import edu.uob.ActionManagement.GameAction;
import edu.uob.EntityManagement.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameTracker {
    private final Map<String, LocationEntity> locationMap;
    private final Map<String, PlayerEntity> playerMap;
    private final Map<String, GameAction> actionMap;

    public GameTracker() {
        this.locationMap = new LinkedHashMap<>();
        this.playerMap = new HashMap<>();
        this.actionMap = new HashMap<>();
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
        this.actionMap.put(trigger.toLowerCase(), action);
    }

    public GameAction getAction(String trigger) {
        return this.actionMap.get(trigger.toLowerCase());
    }

    public Map<String, GameAction> getActionMap() {
        return this.actionMap;
    }

    public GameEntity findEntityInLocation(String entityName, LocationEntity location) {
        return this.findEntityInCollection(entityName, location.getEntityList());
    }

    public GameEntity findEntityInInventory(String entityName, PlayerEntity player) {
        return this.findEntityInCollection(entityName, player.getInventory());
    }

    private GameEntity findEntityInCollection(String entityName, Collection<GameEntity> entities) {
        for (GameEntity entity : entities) {
            if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;
    }

    public String getEntityType(String entityName) {
        for (LocationEntity location : this.locationMap.values()) {
            for (GameEntity entity : location.getEntityList()) {
                if (entity.getEntityName().equalsIgnoreCase(entityName)) {
                    if (entity instanceof ArtefactEntity) return "artefact";
                    if (entity instanceof FurnitureEntity) return "furniture";
                    if (entity instanceof CharacterEntity) return "character";
                    return "entity";
                }
            }
        }

        for (PlayerEntity player : this.playerMap.values()) {
            for (GameEntity item : player.getInventory()) {
                if (item.getEntityName().equalsIgnoreCase(entityName)) {
                    if (item instanceof ArtefactEntity) return "artefact";
                    if (item instanceof FurnitureEntity) return "furniture";
                    if (item instanceof CharacterEntity) return "character";
                    return "entity";
                }
            }
        }

        return "artefact";
    }
}

