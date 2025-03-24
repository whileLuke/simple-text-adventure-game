package edu.uob;

import java.util.LinkedList;
import java.util.List;

public class Player extends GameEntity {
    private Location currentLocation;
    private List<GameEntity> inventory;

    public Player(String name) {
        super(name, "Player");
        this.inventory = new LinkedList<>();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    public List<GameEntity> getInventory() {
        return inventory;
    }

    public void setInventory(List<GameEntity> inventory) {
        this.inventory = inventory;
    }

    public void addToInventory(GameEntity item) {
        this.inventory.add(item);
    }

    public void removeFromInventory(GameEntity item) {
        this.inventory.remove(item);
    }
}
