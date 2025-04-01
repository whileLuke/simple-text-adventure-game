package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private final GameTracker gameTracker;
    private String currentCommand;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        this.gameTracker = new GameTracker();
        EntityParser entityParser = new EntityParser(this.gameTracker);
        ActionParser actionParser = new ActionParser(entityParser);

        entityParser.parse(entitiesFile);
        actionParser.parse(actionsFile);

        for (GameAction gameAction : actionParser.getActionSet()) {
            for (String trigger : gameAction.getTriggers()) {
                this.gameTracker.addAction(trigger, gameAction);
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "Empty command";
        }

        command = CommandProcessor.processCommand(command);
        String lowercaseCommand = command.toLowerCase();
        this.currentCommand = lowercaseCommand;

        int commandKeywords = 0;
        if (containsWord(lowercaseCommand, "inv") || containsWord(lowercaseCommand, "inventory")) commandKeywords++;
        if (containsWord(lowercaseCommand, "get")) commandKeywords++;
        if (containsWord(lowercaseCommand, "drop")) commandKeywords++;
        if (containsWord(lowercaseCommand, "goto")) commandKeywords++;
        if (containsWord(lowercaseCommand, "look")) commandKeywords++;

        if (commandKeywords > 1) {
            return "Too many commands";
        }

        // Handle basic commands
        if (containsWord(lowercaseCommand, "look")) {
            return handleLookCommand();
        }
        if (containsWord(lowercaseCommand, "inv") || containsWord(lowercaseCommand, "inventory")) {
            return handleInvCommand();
        }
        if (containsWord(lowercaseCommand, "get")) {
            return handleGetCommand();
        }
        if (containsWord(lowercaseCommand, "drop")) {
            return handleDropCommand();
        }
        if (containsWord(lowercaseCommand, "goto")) {
            return handleGotoCommand();
        }
        if (containsWord(lowercaseCommand, "health")) {
            return handleHealthCommand();
        }
        return handleOtherCommand();
    }

    private boolean containsWord(String text, String word) {
        return text.matches(".*\\b" + word + "\\b.*");
    }

    private String handleInvCommand() {
        InvCommand invCommand = new InvCommand();
        invCommand.setCommand(this.currentCommand);
        invCommand.setGameTracker(this.gameTracker);
        return invCommand.execute();
    }

    private String handleGetCommand() {
        GetCommand getCommand = new GetCommand();
        getCommand.setCommand(this.currentCommand);
        getCommand.setGameTracker(this.gameTracker);
        return getCommand.execute();
    }

    private String handleDropCommand() {
        DropCommand dropCommand = new DropCommand();
        dropCommand.setCommand(this.currentCommand);
        dropCommand.setGameTracker(this.gameTracker);
        return dropCommand.execute();
    }

    private String handleGotoCommand() {
        GotoCommand gotoCommand = new GotoCommand();
        gotoCommand.setCommand(this.currentCommand);
        gotoCommand.setGameTracker(this.gameTracker);
        return gotoCommand.execute();
    }

    private String handleLookCommand() {
        System.out.println("Handling look command: " + this.currentCommand);
        LookCommand lookCommand = new LookCommand();
        lookCommand.setCommand(this.currentCommand);
        lookCommand.setGameTracker(this.gameTracker);
        String result = lookCommand.execute();
        System.out.println("Look command result: " + result);
        return result;
    }

    private String handleHealthCommand() {
        HealthCommand healthCommand = new HealthCommand();
        healthCommand.setCommand(this.currentCommand);
        healthCommand.setGameTracker(this.gameTracker);
        return healthCommand.execute();
    }

    private String handleOtherCommand() {
        OtherCommand otherCommand = new OtherCommand();
        otherCommand.setCommand(this.currentCommand);
        otherCommand.setGameTracker(this.gameTracker);
        return otherCommand.execute();
    }

    public Player getCurrentPlayer() {
        if (this.currentCommand != null && this.currentCommand.contains(":")) {
            String playerName = this.currentCommand.split(":", 2)[0].trim();
            return this.gameTracker.getPlayer(playerName);
        }

        return this.gameTracker.getPlayer("player");
    }

    public Location getLocation(String locationName) {
        return this.gameTracker.getLocation(locationName);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
