package edu.uob;

import edu.uob.ActionManagement.ActionParser;
import edu.uob.ActionManagement.GameAction;
import edu.uob.CommandManagement.*;
import edu.uob.EntityManagement.EntityParser;
import edu.uob.EntityManagement.LocationEntity;
import edu.uob.GameManagement.GameHelper;
import edu.uob.GameManagement.GameTracker;

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
    private final CommandProcessor commandProcessor;
    private final CommandCreator commandCreator;
    private final GameHelper gameHelper;
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
        this.gameHelper = new GameHelper();
        this.commandProcessor = new CommandProcessor(this.gameHelper);
        this.commandCreator = new CommandCreator(this.gameTracker, this.gameHelper);
        this.initialiseGame(entitiesFile, actionsFile);
    }

    private void initialiseGame(File entitiesFile, File actionsFile) {
        EntityParser entityParser = new EntityParser(this.gameTracker);
        ActionParser actionParser = new ActionParser(entityParser);

        entityParser.parseEntityFile(entitiesFile);
        actionParser.parseActionsFile(actionsFile);

        this.registerGameActions(actionParser);
    }

    private void registerGameActions(ActionParser actionParser) {
        for (GameAction gameAction : actionParser.getActionSet()) {
            for (String trigger : gameAction.getTriggersList()) {
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
        if (command == null || command.trim().isEmpty()) return "Command is empty.";

        String processedCommand = this.commandProcessor.processCommand(command);
        if (processedCommand == null) return "Command is invalid.";

        this.currentCommand = processedCommand.toLowerCase();

        if (hasMultipleKeywords(this.currentCommand)) {
            return "You can't do multiple commands at once.";
        }

        return this.executeCommand(processedCommand);
    }

    private boolean hasMultipleKeywords(String gameCommand) {
        int keywordCount = 0;

        if (this.gameHelper.containsWord(gameCommand, "inv") ||
                this.gameHelper.containsWord(gameCommand, "inventory")) keywordCount++;
        if (this.gameHelper.containsWord(gameCommand, "get")) keywordCount++;
        if (this.gameHelper.containsWord(gameCommand, "drop")) keywordCount++;
        if (this.gameHelper.containsWord(gameCommand, "goto")) keywordCount++;
        if (this.gameHelper.containsWord(gameCommand, "look")) keywordCount++;
        if (this.gameHelper.containsWord(gameCommand, "health")) keywordCount++;

        return keywordCount > 1;
    }

    private String executeCommand(String playerCommand) {
        GameCommand gameCommand = this.commandCreator.createCommand(playerCommand);

        if (gameCommand == null) return "Couldn't process your command. Make sure your player name is valid.";
        return gameCommand.executeCommand();
    }

    public LocationEntity getLocation(String locationName) {
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
            StringBuilder systemOutput = new StringBuilder();
            systemOutput.append("Server listening on port ").append(portNumber);
            System.out.println(systemOutput);
            while (!Thread.interrupted()) {
                try {
                    this.blockingHandleConnection(s);
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
                systemOutput.append("Received message from ").append(incomingCommand);
                System.out.println(systemOutput);
                String resultString = this.handleCommand(incomingCommand);
                writer.write(resultString);
                writer.write("\n");
                writer.write(END_OF_TRANSMISSION);
                writer.write("\n");
                writer.flush();
            }
        }
    }
}
