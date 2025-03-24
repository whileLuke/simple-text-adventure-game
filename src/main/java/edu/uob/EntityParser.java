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

    public EntityParser(GameTracker gameTracker) {
        this.gameTracker = gameTracker;
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
        String locationDesc = locationNode.getAttribute("description");
        if (locationDesc == null) locationDesc = "";

        Location location = new Location(locationName, locationDesc);
        this.gameTracker.addLocation(location);

        for (Node entityNode : locationGraph.getNodes(false)) {
            String entityId = entityNode.getId().getId();

            if (entityId.equals(locationName)) continue;

            String entityDescription = entityNode.getAttribute("description");
            if (entityDescription == null) entityDescription = "No description available";

            GameEntity entity;
            if (entityNode.getAttribute("type") != null) {
                String entityType = entityNode.getAttribute("type");
                if (entityType.equals("furniture")) {
                    entity = new Furniture(entityId, entityDescription);
                } else if (entityType.equals("character")) {
                    entity = new Character(entityId, entityDescription);
                } else {
                    entity = new Artifact(entityId, entityDescription);
                }
            } else {
                entity = new Artifact(entityId, entityDescription);
            }

            location.addEntity(entity);
        }
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
