package edu.uob;

import java.util.LinkedList;
import java.util.List;

public class Player extends GameEntity {
    private Location currentLocation;
    private List<GameEntity> inventory;
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

    public void setInventory(List<GameEntity> inventory) {
        this.inventory = inventory;
    }

    public void addToInventory(GameEntity item) {
        this.inventory.add(item);
    }

    public void removeFromInventory(GameEntity item) {
        this.inventory.remove(item);
    }

    public void clearInventory() {
        this.inventory.clear();
    }

    public int getHealth() {
        return this.playerHealth;
    }

    public void setHealth(int health) {
        this.playerHealth = health;
    }

    public void increaseHealth() {
        if (this.playerHealth < 3) {
            this.playerHealth++;
        }
    }

    public void decreaseHealth() {
        this.playerHealth--;
    }

    public boolean isDead() {
        return this.playerHealth <= 0;
    }

    public void resetHealth() {
        this.playerHealth = 3;
    }
}
