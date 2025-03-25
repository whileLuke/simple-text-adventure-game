package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class EntityParser {
    private GameTracker gameTracker;
    private Map<String, String> entityTypeMap;

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

            List<Graph> locations = sections.get(0).getSubgraphs();
            for (Graph locationGraph : locations) this.parseLocation(locationGraph);

            List<Edge> paths = sections.get(1).getEdges();
            for (Edge path : paths) this.parsePath(path);

        } catch (Exception ignored) {
        }
    }

    //TODO: Clean this up
    private void parseLocation(Graph locationGraph) {
        Node locationNode = locationGraph.getNodes(false).get(0);
        String locationName = locationNode.getId().getId();
        String locationDescription = locationNode.getAttribute("description");
        if (locationDescription == null) locationDescription = "";

        Location location = new Location(locationName, locationDescription);
        this.gameTracker.addLocation(location);

        for (Graph subgraph : locationGraph.getSubgraphs()) {
            String subgraphName = subgraph.getId().getId();

            for (Node entityNode : subgraph.getNodes(false)) {
                String entityId = entityNode.getId().getId();
                String entityDescription = entityNode.getAttribute("description");
                if (entityDescription == null) entityDescription = "No description available";

                String entityType = determineEntityType(subgraphName);

                this.entityTypeMap.put(entityId.toLowerCase(), entityType);

                GameEntity entity = createEntity(entityType, entityId, entityDescription);
                location.addEntity(entity);
            }
        }
    }

    private String determineEntityType(String subGraphName) {
        switch (subGraphName.toLowerCase()) {
            case "artefacts":
                return "artefact";
            case "furniture":
                return "furniture";
            case "characters":
                return "character";
            default:
                return "artefact"; // default fallback
        }
    }

    private GameEntity createEntity(String entityType, String entityId, String entityDescription) {
        switch (entityType) {
            case "furniture":
                return new Furniture(entityId, entityDescription);
            case "character":
                return new Character(entityId, entityDescription);
            case "artefact":
            default:
                return new Artefact(entityId, entityDescription);
        }
    }

    public String getEntityType(String entityName) {
        return this.entityTypeMap.getOrDefault(entityName.toLowerCase(), "artifact");
        //If doesnt exist, return "artifact"
    }

    //TODO: Clean this up and remove +
    private void parsePath(Edge path) {
        Node fromNode = path.getSource().getNode();
        Node toNode = path.getTarget().getNode();
        String fromName = fromNode.getId().getId();
        String toName = toNode.getId().getId();

        Location from = this.gameTracker.getLocation(fromName);
        Location to = this.gameTracker.getLocation(toName);

        if (from != null && to != null) {
            Path pathObj = new Path("path_" + fromName + "_to_" + toName,
                    "A path from " + fromName + " to " + toName, from, to);
            from.addPath(toName, pathObj);
        }
    }
}
