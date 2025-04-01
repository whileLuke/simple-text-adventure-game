package edu.uob;

public class CommandProcessor {
    public static String processCommand(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }

        String[] parts = command.split(":", 2);

        if (parts.length == 2) {
            String playerName = parts[0];
            String commandPart = parts[1].replaceAll("[^a-zA-Z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
            return playerName + ":" + commandPart;
        } else {
            return null;
        }
    }
}
