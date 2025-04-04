package edu.uob.CommandManagement;

import edu.uob.EntityManagement.PlayerEntity;

public class HealthCommand extends GameCommand {
    @Override
    public String executeCommand() {
        PlayerEntity player = this.getPlayer();
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Your current health is: ").append(player.getHealth());
        return responseBuilder.toString();
    }
}