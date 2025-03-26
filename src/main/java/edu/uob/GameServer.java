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
    private GameTracker gameTracker;
    private ActionParser actionParser;
    private EntityParser entityParser;

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
        this.entityParser = new EntityParser(this.gameTracker);
        this.actionParser = new ActionParser(this.entityParser);

        this.entityParser.parse(entitiesFile);
        this.actionParser.parse(actionsFile);

        for (GameAction gameAction : this.actionParser.getActionSet()) {
            for (String trigger : gameAction.getTriggers()) {
                this.gameTracker.addAction(trigger, gameAction);
            }
        }


        //LOAD IN GAME STATE LOAD IN ACTIONS.
        //subclass entity with furniture
        //inheritance
        //PARSER TO PARSE THE PARSER

        //create classes for entities actions

        //so basically parse evreything into the hashmaps and hashsets here
        //then handling the command is easy
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        System.out.println("Processing command: " + command);
        int numberOfCommands = 0;
        String returnString = "";
        String originalCommand = command;
        command = command.toLowerCase();
        if (command.contains("inv") || command.contains("inventory")) {
            returnString = this.handleInvCommand(command);
            numberOfCommands++;
        }
        if (command.contains("get")) {
            returnString = this.handleGetCommand(command);
            numberOfCommands++;
        }
        if (command.contains("drop")) {
            returnString = this.handleDropCommand(command);
            numberOfCommands++;
        }
        if(command.contains("goto")) {
            returnString = this.handleGotoCommand(command);
            numberOfCommands++;
        }
        if(command.contains("look")) {
            returnString = this.handleLookCommand(command);
            numberOfCommands++;
            System.out.println("Look command returned: " + returnString);
        }
        if(numberOfCommands >= 2) return "Too many commands";
        else if (numberOfCommands == 0) {
            returnString = this.handleOtherCommand(command);
            System.out.println("Other command returned: " + returnString);
        }

        if (returnString == null || returnString.isEmpty()) {
            return "Command generated no output.";
        }

        return returnString;
    }


    /*public String handleCommand(String command) {
        STAGCommand cmd = null;
        String commandLower = command.toLowerCase();

        if (commandLower.contains("look")) {
            cmd = new LookCommand();
        } else if (commandLower.contains("inv") || commandLower.contains("inventory")) {
            cmd = new InvCommand();
        } else if (commandLower.contains("get")) {
            cmd = new GetCommand();
        } else if (commandLower.contains("drop")) {
            cmd = new DropCommand();
        } else if (commandLower.contains("goto")) {
            cmd = new GotoCommand();
        } else {
            cmd = new OtherCommand();
        }

        cmd.setCommand(command);
        cmd.setGameState(gameState);
        return cmd.execute();
    }

    // Rest of the class remains the same
}*/

    private String handleInvCommand(String command) {
        InvCommand invCommand = new InvCommand();
        invCommand.setCommand(command);
        invCommand.setGameTracker(this.gameTracker);
        return invCommand.execute();
    }

    private String handleGetCommand(String command) {
        GetCommand getCommand = new GetCommand();
        getCommand.setCommand(command);
        getCommand.setGameTracker(this.gameTracker);
        return getCommand.execute();
    }

    private String handleDropCommand(String command) {
        DropCommand dropCommand = new DropCommand();
        dropCommand.setCommand(command);
        dropCommand.setGameTracker(this.gameTracker);
        return dropCommand.execute();
    }

    private String handleGotoCommand(String command) {
        GotoCommand gotoCommand = new GotoCommand();
        gotoCommand.setCommand(command);
        gotoCommand.setGameTracker(this.gameTracker);
        return gotoCommand.execute();
    }

    private String handleLookCommand(String command) {
        System.out.println("Handling look command: " + command);
        LookCommand lookCommand = new LookCommand();
        lookCommand.setCommand(command);
        lookCommand.setGameTracker(this.gameTracker);
        String result = lookCommand.execute();
        System.out.println("Look command result: " + result);
        return result;
    }

    private String handleOtherCommand(String command) {
        OtherCommand otherCommand = new OtherCommand();
        otherCommand.setCommand(command);
        otherCommand.setGameTracker(this.gameTracker);
        return otherCommand.execute();
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
