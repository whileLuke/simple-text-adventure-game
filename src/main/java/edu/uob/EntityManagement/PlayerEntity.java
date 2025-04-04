package edu.uob.EntityManagement;

import java.util.LinkedList;
import java.util.List;

public class PlayerEntity extends GameEntity {
    private LocationEntity playerLocation;
    private final List<GameEntity> playerInventory;
    private int playerHealth;

    public PlayerEntity(String playerName) {
        super(playerName, "A player");
        this.playerInventory = new LinkedList<>();
        this.playerHealth = 3;
    }

    public LocationEntity getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(LocationEntity location) {
        this.playerLocation = location;
    }

    public List<GameEntity> getPlayerInventory() {
        return playerInventory;
    }

    public void addToInventory(GameEntity item) {
        this.playerInventory.add(item);
    }

    public void removeFromInventory(GameEntity item) {
        this.playerInventory.remove(item);
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

    public boolean isPlayerDead() {
        return this.playerHealth <= 0;
    }

    public void resetHealth() {
        this.playerHealth = 3;
    }
}
