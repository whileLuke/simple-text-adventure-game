package edu.uob;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class ActionParser {
    private Set<GameAction> actionSet;
    private EntityParser entityParser;

    public ActionParser(EntityParser entityParser) {
        this.actionSet = new HashSet<>();
        this.entityParser = entityParser;
    }

    public Set<GameAction> getActionSet() {
        return this.actionSet;
    }

    public void parse(File actionsFile) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actions = root.getElementsByTagName("action");

            for (int i = 0; i < actions.getLength(); i++) {
                Element currentAction = (Element) actions.item(i);

                // Parse triggers
                List<String> triggerList = new LinkedList<>();
                Element triggers = (Element) currentAction.getElementsByTagName("triggers").item(0);
                if (triggers != null) {
                    NodeList keyphrases = triggers.getElementsByTagName("keyphrase");
                    for (int j = 0; j < keyphrases.getLength(); j++) {
                        String currentTrigger = keyphrases.item(j).getTextContent().toLowerCase();
                        triggerList.add(currentTrigger);
                    }
                }

                // Parse subjects
                List<String> artifactList = new LinkedList<>();
                List<String> furnitureList = new LinkedList<>();
                List<String> characterList = new LinkedList<>();

                Element subjects = (Element) currentAction.getElementsByTagName("subjects").item(0);
                if (subjects != null) {
                    NodeList entities = subjects.getElementsByTagName("entity");
                    for (int j = 0; j < entities.getLength(); j++) {
                        String currentSubject = entities.item(j).getTextContent().toLowerCase();
                        String subjectType = this.findEntityType(currentSubject);
                        switch (subjectType) {
                            case "artefact":
                                artifactList.add(currentSubject);
                                break;
                            case "furniture":
                                furnitureList.add(currentSubject);
                                break;
                            case "character":
                                characterList.add(currentSubject);
                                break;
                        }
                    }
                }

                // Parse consumed entities
                List<String> consumedList = new LinkedList<>();
                Element consumed = (Element) currentAction.getElementsByTagName("consumed").item(0);
                if (consumed != null) {
                    NodeList entities = consumed.getElementsByTagName("entity");
                    for (int j = 0; j < entities.getLength(); j++) {
                        String currentConsumed = entities.item(j).getTextContent().toLowerCase();
                        consumedList.add(currentConsumed);
                    }
                }

                // Parse produced entities
                List<String> producedList = new LinkedList<>();
                Element produced = (Element) currentAction.getElementsByTagName("produced").item(0);
                if (produced != null) {
                    NodeList entities = produced.getElementsByTagName("entity");
                    for (int j = 0; j < entities.getLength(); j++) {
                        String currentProduced = entities.item(j).getTextContent().toLowerCase();
                        producedList.add(currentProduced);
                    }
                }

                // Parse narration
                List<String> narrationList = new LinkedList<>();
                NodeList narrations = currentAction.getElementsByTagName("narration");
                for (int j = 0; j < narrations.getLength(); j++) {
                    String narration = narrations.item(j).getTextContent();
                    narrationList.add(narration);
                }

                GameAction gameAction = new GameAction(triggerList, artifactList, furnitureList, characterList, consumedList, producedList, narrationList);
                actionSet.add(gameAction);
            }
        } catch(ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error parsing actions file: " + e.getMessage());
        }
    }

    private String findEntityType(String entityName) {
        return this.entityParser.getEntityType(entityName);
    }
}
