package edu.uob;

public class HealthCommand extends GameCommand {
    @Override
    public String execute() {
        /*if (!trimmedCommand.hasCommandType()) {
            return "Invalid health command.";
        }*/

        Player player = this.getPlayer();
        StringBuilder response = new StringBuilder();
        response.append("Your current health is: ").append(player.getHealth());
        return response.toString();
    }
}