package edu.uob.EntityManagement;

import java.util.*;

public class LocationEntity extends GameEntity {

    private final List<GameEntity> entityList;
    private final Map<String, GamePath> pathMap;

    public LocationEntity(String locationName, String locationDescription) {
        super(locationName, locationDescription);
        this.entityList = new LinkedList<>();
        this.pathMap = new HashMap<>();
    }

    public void addEntity(GameEntity gameEntity) {
        this.entityList.add(gameEntity);
    }

    public void removeEntity(GameEntity gameEntity) {
        this.entityList.remove(gameEntity);
    }

    public List<GameEntity> getEntityList() {
        return this.entityList;
    }

    public void addPath(String direction, GamePath gamePath) {
        this.pathMap.put(direction, gamePath);
    }

    public GamePath getPath(String direction) {
        return this.pathMap.get(direction);
    }

    public Map<String, GamePath> getPathMap() {
        return this.pathMap;
    }

    public CharacterEntity getCharacter(String characterName) {
        for (GameEntity gameEntity : entityList) {
            if (gameEntity instanceof CharacterEntity &&
                    gameEntity.getEntityName().equalsIgnoreCase(characterName)) {
                return (CharacterEntity) gameEntity;
            }
        }
        return null;
    }

    public ArtefactEntity getArtefact(String artefactName) {
        for (GameEntity gameEntity : entityList) {
            if (gameEntity instanceof ArtefactEntity &&
                    gameEntity.getEntityName().equalsIgnoreCase(artefactName)) {
                return (ArtefactEntity) gameEntity;
            }
        }
        return null;
    }

}
