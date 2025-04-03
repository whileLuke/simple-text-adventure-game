package edu.uob.CommandManagement;

import edu.uob.EntityManagement.PlayerEntity;

public class HealthCommand extends GameCommand {
    @Override
    public String executeCommand() {
        /*if (!trimmedCommand.hasCommandType()) {
            return "Invalid health command.";
        }*/

        PlayerEntity player = this.getPlayer();
        StringBuilder response = new StringBuilder();
        response.append("Your current health is: ").append(player.getHealth());
        return response.toString();
    }
}