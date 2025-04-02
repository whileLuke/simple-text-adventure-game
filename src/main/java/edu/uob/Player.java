package edu.uob;

import java.util.LinkedList;
import java.util.List;

public class Player extends GameEntity {
    private Location currentLocation;
    private final List<GameEntity> inventory;
    private int playerHealth;

    public Player(String playerName) {
        super(playerName, "A player");
        this.inventory = new LinkedList<>();
        this.playerHealth = 3;
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

    public void addToInventory(GameEntity item) {
        this.inventory.add(item);
    }

    public void removeFromInventory(GameEntity item) {
        this.inventory.remove(item);
    }

    public int getHealth() {
        return this.playerHealth;
    }

    public void increaseHealth() {
        if (this.playerHealth < 3) {
            this.playerHealth++;
        }
    }

    public void decreaseHealth() {
        this.playerHealth--;
        if (this.playerHealth < 0) this.playerHealth = 0;
    }

    public boolean isDead() {
        return this.playerHealth <= 0;
    }

    public void resetHealth() {
        this.playerHealth = 3;
    }
}
