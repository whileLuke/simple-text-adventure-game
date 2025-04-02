package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

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

    public void parse(File entityFile) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entityFile);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            List<Graph> sections = wholeDocument.getSubgraphs();

            this.parseLocations(sections.get(0).getSubgraphs());
            this.parsePaths(sections.get(1).getEdges());

        } catch (Exception e) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Error parsing entity file: ");
            errorMessage.append(e.getMessage());
        }
    }

    private void parseLocations(List<Graph> locations) {
        for (Graph location : locations) {
            this.parseLocation(location);
        }
    }

    private void parsePaths(List<Edge> paths) {
        for (Edge path : paths) {
            this.parsePath(path);
        }
    }

    private void parseLocation(Graph locationGraph) {
        Node locationNode = locationGraph.getNodes(false).get(0);
        String locationName = locationNode.getId().getId();
        String locationDescription = locationNode.getAttribute("description");
        if (locationDescription == null) locationDescription = "";

        Location location = new Location(locationName, locationDescription);
        this.gameTracker.addLocation(location);

        this.parseLocationSubgraphs(locationGraph.getSubgraphs(), location);
    }

    private void parseLocationSubgraphs(List<Graph> subgraphs, Location location) {
        for (Graph subgraph : subgraphs) {
            String subgraphName = subgraph.getId().getId();
            for (Node node : subgraph.getNodes(false)) {
                this.parseEntityNode(node, subgraphName, location);
            }
        }
    }

    private void parseEntityNode(Node entityNode, String subgraphName, Location location) {
        String entityId = entityNode.getId().getId();
        String entityDescription = entityNode.getAttribute("description");
        if (entityDescription == null) entityDescription = "No description available";

        String entityType = this.determineEntityType(subgraphName);

        // Store entity type mapping (lowercase for case-insensitive lookup)
        this.entityTypeMap.put(entityId.toLowerCase(), entityType);

        GameEntity entity = this.createEntity(entityType, entityId, entityDescription);
        location.addEntity(entity);
    }

    private String determineEntityType(String subGraphName) {
        if ("artefacts".equalsIgnoreCase(subGraphName)) {
            return "artefact";
        } else if ("furniture".equalsIgnoreCase(subGraphName)) {
            return "furniture";
        } else if ("characters".equalsIgnoreCase(subGraphName)) {
            return "character";
        } else {
            return "artefact"; // default fallback
        }
    }

    private GameEntity createEntity(String entityType, String entityId, String entityDescription) {
        if ("furniture".equals(entityType)) {
            return new Furniture(entityId, entityDescription);
        } else if ("character".equals(entityType)) {
            return new Character(entityId, entityDescription);
        } else {
            return new Artefact(entityId, entityDescription);
        }
    }

    public String getEntityType(String entityName) {
        return this.entityTypeMap.getOrDefault(entityName.toLowerCase(), "artefact");
    }

    private void parsePath(Edge path) {
        Node fromNode = path.getSource().getNode();
        Node toNode = path.getTarget().getNode();
        String fromName = fromNode.getId().getId();
        String toName = toNode.getId().getId();

        Location from = this.gameTracker.getLocation(fromName);
        Location to = this.gameTracker.getLocation(toName);

        if (from != null && to != null) {
            this.createAndAddPath(from, to, fromName, toName);
        }
    }

    private void createAndAddPath(Location from, Location to, String fromName, String toName) {
        StringBuilder pathName = new StringBuilder();
        pathName.append("path_");
        pathName.append(fromName);
        pathName.append("_to_");
        pathName.append(toName);

        StringBuilder pathDescription = new StringBuilder();
        pathDescription.append("A path from ");
        pathDescription.append(fromName);
        pathDescription.append(" to ");
        pathDescription.append(toName);

        Path pathObj = new Path(pathName.toString(), pathDescription.toString(), from, to);
        from.addPath(toName.toLowerCase(), pathObj);
    }
}
