package edu.uob;

import java.util.*;

public class Location extends GameEntity {

    private List<GameEntity> entityList;
    private Map<String, Path> pathMap;

    public Location(String name, String description) {
        super(name, description);
        this.entityList = new LinkedList<GameEntity>();
        this.pathMap = new HashMap<String, Path>();
    }

    public void addEntity(GameEntity entity) {
        this.entityList.add(entity);
    }

    public void removeEntity(GameEntity entity) {
        this.entityList.remove(entity);
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
