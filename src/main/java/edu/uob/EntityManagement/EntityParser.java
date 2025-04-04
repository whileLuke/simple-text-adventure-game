package edu.uob.EntityManagement;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.GameManagement.GameTracker;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class EntityParser {
    private final GameTracker gameTracker;
    private final Map<String, String> entityTypeMap;

    public EntityParser(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
        this.entityTypeMap = new HashMap<>();
    }

    public void parseEntityFile(File entityFile) {
        try {
            Parser entityParser = new Parser();
            FileReader fileReader = new FileReader(entityFile);
            entityParser.parse(fileReader);
            Graph wholeDocument = entityParser.getGraphs().get(0);
            List<Graph> graphSections = wholeDocument.getSubgraphs();

            this.parseLocations(graphSections.get(0).getSubgraphs());
            this.parsePaths(graphSections.get(1).getEdges());

        } catch (Exception ignored) { }
    }

    private void parseLocations(List<Graph> locationsList) {
        for (Graph locationGraph : locationsList) this.parseLocation(locationGraph);
    }

    private void parsePaths(List<Edge> pathsList) {
        for (Edge pathEdge : pathsList) this.parsePath(pathEdge);
    }

    private void parseLocation(Graph locationGraph) {
        Node locationNode = locationGraph.getNodes(false).get(0);
        String locationName = locationNode.getId().getId();
        String locationDescription = locationNode.getAttribute("description");
        if (locationDescription == null) locationDescription = "";

        LocationEntity location = new LocationEntity(locationName, locationDescription);
        this.gameTracker.addLocation(location);

        this.parseLocationSubGraphs(locationGraph.getSubgraphs(), location);
    }

    private void parseLocationSubGraphs(List<Graph> subGraphsList, LocationEntity location) {
        for (Graph subGraph : subGraphsList) {
            String subGraphName = subGraph.getId().getId();
            for (Node node : subGraph.getNodes(false)) {
                this.parseEntityNode(node, subGraphName, location);
            }
        }
    }

    private void parseEntityNode(Node entityNode, String subGraphName, LocationEntity location) {
        String entityId = entityNode.getId().getId();
        String entityDescription = entityNode.getAttribute("description");
        if (entityDescription == null) entityDescription = "No description.";

        String entityType = this.determineEntityType(subGraphName);

        this.entityTypeMap.put(entityId.toLowerCase(), entityType);

        GameEntity gameEntity = this.createEntity(entityType, entityId, entityDescription);
        location.addEntity(gameEntity);
    }

    private String determineEntityType(String subGraphName) {
        if ("furniture".equalsIgnoreCase(subGraphName)) return "furniture";
        else if ("characters".equalsIgnoreCase(subGraphName)) return "character";
        else return "artefact";
    }

    private GameEntity createEntity(String entityType, String entityId, String entityDescription) {
        if ("furniture".equals(entityType)) return new FurnitureEntity(entityId, entityDescription);
        else if ("character".equals(entityType)) return new CharacterEntity(entityId, entityDescription);
        else return new ArtefactEntity(entityId, entityDescription);
    }

    public String getEntityType(String entityName) {
        return this.entityTypeMap.getOrDefault(entityName.toLowerCase(), "artefact");
    }

    private void parsePath(Edge pathEdge) {
        Node fromLocationNode = pathEdge.getSource().getNode();
        Node toLocationNode = pathEdge.getTarget().getNode();
        String fromLocationName = fromLocationNode.getId().getId();
        String toLocationName = toLocationNode.getId().getId();

        LocationEntity fromLocation = this.gameTracker.getLocation(fromLocationName);
        LocationEntity toLocation = this.gameTracker.getLocation(toLocationName);

        if (fromLocation != null && toLocation != null) {
            this.createAndAddPath(fromLocation, toLocation, toLocationName);
        }
    }

    private void createAndAddPath(LocationEntity fromLocation, LocationEntity toLocation, String toLocationName) {
        GamePath newGamePath = new GamePath(toLocation);
        fromLocation.addPath(toLocationName.toLowerCase(), newGamePath);
    }
}
