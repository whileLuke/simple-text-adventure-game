package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EntityParser {
    public void parse(File entityFile) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entityFile);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            // The locations will always be in the first subgraph
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            Graph firstLocation = locations.get(0);
            Node locationDetails = firstLocation.getNodes(false).get(0);
            // Yes, you do need to get the ID twice !
            String locationName = locationDetails.getId().getId();
            // The paths will always be in the second subgraph
            ArrayList<Edge> paths = sections.get(1).getEdges();
            Edge firstPath = paths.get(0);
            Node fromLocation = firstPath.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = firstPath.getTarget().getNode();
            String toName = toLocation.getId().getId();
        } catch (FileNotFoundException fnfe) {
        } catch (ParseException pe) {
        }
    }
}
