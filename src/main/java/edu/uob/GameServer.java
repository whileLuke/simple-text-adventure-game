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
        StringBuilder entityFilePath = new StringBuilder();
        entityFilePath.append("config").append(File.separator).append("extended-entities.dot");
        File entitiesFile = Paths.get(entityFilePath.toString()).toAbsolutePath().toFile();

        StringBuilder actionFilePath = new StringBuilder();
        actionFilePath.append("config").append(File.separator).append("extended-actions.xml");
        File actionsFile = Paths.get(actionFilePath.toString()).toAbsolutePath().toFile();

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
        this.gameTracker = new GameTracker();
        this.initializeGame(entitiesFile, actionsFile);
    }

    private void initializeGame(File entitiesFile, File actionsFile) {
        EntityParser entityParser = new EntityParser(this.gameTracker);
        ActionParser actionParser = new ActionParser(entityParser);

        entityParser.parse(entitiesFile);
        actionParser.parse(actionsFile);

        this.registerGameActions(actionParser);
    }

    private void registerGameActions(ActionParser actionParser) {
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

        int commandKeywords = this.countCommandKeywords(lowercaseCommand);
        if (commandKeywords > 1) {
            return "Too many commands";
        }

        return this.executeCommand(lowercaseCommand);
    }

    private int countCommandKeywords(String command) {
        int count = 0;
        if (this.containsWord(command, "inv") || this.containsWord(command, "inventory")) count++;
        if (this.containsWord(command, "get")) count++;
        if (this.containsWord(command, "drop")) count++;
        if (this.containsWord(command, "goto")) count++;
        if (this.containsWord(command, "look")) count++;
        return count;
    }

    private String executeCommand(String command) {
        if (this.containsWord(command, "look")) {
            return this.handleLookCommand();
        }
        if (this.containsWord(command, "inv") || this.containsWord(command, "inventory")) {
            return this.handleInvCommand();
        }
        if (this.containsWord(command, "get")) {
            return this.handleGetCommand();
        }
        if (this.containsWord(command, "drop")) {
            return this.handleDropCommand();
        }
        if (this.containsWord(command, "goto")) {
            return this.handleGotoCommand();
        }
        if (this.containsWord(command, "health")) {
            return this.handleHealthCommand();
        }
        return this.handleOtherCommand();
    }

    private boolean containsWord(String text, String word) {
        StringBuilder wordChecker = new StringBuilder();
        wordChecker.append(".*\\b").append(word).append("\\b.*");
        return text.matches(wordChecker.toString());
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
        LookCommand lookCommand = new LookCommand();
        lookCommand.setCommand(this.currentCommand);
        lookCommand.setGameTracker(this.gameTracker);
        String result = lookCommand.execute();
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
                StringBuilder systemOutput = new StringBuilder();
                systemOutput.append("Received message from ");
                systemOutput.append(incomingCommand);
                System.out.println(systemOutput);
                String result = this.handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n");
                writer.write(END_OF_TRANSMISSION);
                writer.write("\n");
                writer.flush();
            }
        }
    }
}
