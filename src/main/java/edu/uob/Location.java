package edu.uob;

import java.util.*;

public class Location extends GameEntity {

    private List<GameEntity> entityList;
    private Map<String, Path> pathMap;

    public Location(String locationName, String locationDescription) {
        super(locationName, locationDescription);
        this.entityList = new LinkedList<GameEntity>();
        this.pathMap = new HashMap<String, Path>();
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

    public void addPath(String direction, Path path) {
        this.pathMap.put(direction, path);
    }

    public Path getPath(String direction) {
        return this.pathMap.get(direction);
    }

    public Map<String, Path> getPathMap() {
        return this.pathMap;
    }

}
