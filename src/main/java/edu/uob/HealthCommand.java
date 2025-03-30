package edu.uob;

public class HealthCommand extends GameCommand {
    @Override
    public String execute() {
        if (!trimmedCommand.hasCommandType()) {
            return "Invalid health command.";
        }

        Player player = getPlayer();
        return "Your current health is: " + player.getHealth();
    }
}