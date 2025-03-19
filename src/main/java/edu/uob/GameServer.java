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
import com.alexmerz.graphviz.Parser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    ActionParser actionParser = new ActionParser();
    EntityParser entityParser = new EntityParser();
    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
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
        entityParser.parse(entitiesFile);
        actionParser.parse(actionsFile);
        this.readActionsFile(actionsFile);
        this.readEntitiesFile(entitiesFile);


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
        int numberOfCommands = 0;
        String returnString = "";
        if (command.toLowerCase().contains("inv") || command.toLowerCase().contains("inventory")) {
            returnString = handleInv(command);
            numberOfCommands++;
        }
        if (command.toLowerCase().contains("get")) {
            returnString = handleGet(command);
            numberOfCommands++;
        }
        if (command.toLowerCase().contains("drop")) {
            returnString = handleDrop(command);
            numberOfCommands++;
        }
        if(command.toLowerCase().contains("goto")) {
            returnString = handleGoto(command);
            numberOfCommands++;
        }
        if(command.toLowerCase().contains("look")) {
            returnString = handleLook(command);
            numberOfCommands++;
        }
        if(numberOfCommands >= 2) return "Too many commands";
        else if (numberOfCommands == 0) returnString = handleOtherCommand(command);
        return returnString.toString();
    }

    private String handleInv(String command) {
        InvCommand invCommand = new InvCommand();
    }

    private String handleGet(String command) {
    }

    private String handleDrop(String command) {
    }

    private String handleGoto(String command) {
    }

    private String handleLook(String command) {
    }

    private String handleOtherCommand(String command) {
    }

    void readActionsFile(File actionsFile) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse("config" + File.separator + "basic-actions.xml");
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
            Element firstAction = (Element)actions.item(1);
            Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
            // Get the first trigger phrase
            String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
            //assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
        } catch(ParserConfigurationException pce) {
            //fail("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch(SAXException saxe) {
            //fail("SAXException was thrown when attempting to read basic actions file");
        } catch(IOException ioe) {
            //fail("IOException was thrown when attempting to read basic actions file");
        }
    }

    private void readEntitiesFile(File entitiesFile) {
        try {
            Edge firstPath = getFirstPath();
            Node fromLocation = firstPath.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = firstPath.getTarget().getNode();
            String toName = toLocation.getId().getId();
            //assertEquals("cabin", fromName, "First path should have been from 'cabin'");
            //assertEquals("forest", toName, "First path should have been to 'forest'");

        } catch (FileNotFoundException fnfe) {
            //fail("FileNotFoundException was thrown when attempting to read basic entities file");
        } catch (ParseException pe) {
            //fail("ParseException was thrown when attempting to read basic entities file");
        }
    }

    private static Edge getFirstPath() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        List<Graph> sections = wholeDocument.getSubgraphs();

        // The locations will always be in the first subgraph
        List<Graph> locations = sections.get(0).getSubgraphs();
        Graph firstLocation = locations.get(0);
        Node locationDetails = firstLocation.getNodes(false).get(0);
        // Yes, you do need to get the ID twice !
        String locationName = locationDetails.getId().getId();
        //assertEquals("cabin", locationName, "First location should have been 'cabin'");

        // The paths will always be in the second subgraph
        List<Edge> paths = sections.get(1).getEdges();
        return paths.get(0);
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
