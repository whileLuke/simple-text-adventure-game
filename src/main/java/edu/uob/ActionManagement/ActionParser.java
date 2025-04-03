package edu.uob.ActionManagement;

import edu.uob.EntityManagement.EntityParser;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class ActionParser {
    private final Set<GameAction> actionSet;
    private final EntityParser entityParser;

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
            this.processActions(actions);
        } catch(Exception ignored) { }
    }

    private void processActions(NodeList actions) {
        for (int i = 0; i < actions.getLength(); i++) {
            Element currentAction = (Element) actions.item(i);

            List<String> triggerList = this.parseTriggers(currentAction);
            List<String> artifactList = new LinkedList<>();
            List<String> furnitureList = new LinkedList<>();
            List<String> characterList = new LinkedList<>();

            this.parseSubjects(currentAction, artifactList, furnitureList, characterList);
            List<String> consumedList = this.parseEntityList(currentAction, "consumed");
            List<String> producedList = this.parseEntityList(currentAction, "produced");
            List<String> narrationList = this.parseNarration(currentAction);

            GameAction gameAction = new GameAction(
                    triggerList, artifactList, furnitureList, characterList,
                    consumedList, producedList, narrationList
            );
            this.actionSet.add(gameAction);
        }
    }

    private List<String> parseTriggers(Element action) {
        List<String> triggerList = new LinkedList<>();
        Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
        if (triggers != null) {
            NodeList keyphrases = triggers.getElementsByTagName("keyphrase");
            for (int j = 0; j < keyphrases.getLength(); j++) {
                String currentTrigger = keyphrases.item(j).getTextContent().toLowerCase();
                triggerList.add(currentTrigger);
            }
        }
        return triggerList;
    }

    private void parseSubjects(Element action, List<String> artifactList,
                               List<String> furnitureList, List<String> characterList) {
        Element subjects = (Element) action.getElementsByTagName("subjects").item(0);
        if (subjects != null) {
            NodeList entities = subjects.getElementsByTagName("entity");
            for (int j = 0; j < entities.getLength(); j++) {
                String currentSubject = entities.item(j).getTextContent().toLowerCase();
                String subjectType = this.findEntityType(currentSubject);
                this.categorizeEntity(subjectType, currentSubject, artifactList, furnitureList, characterList);
            }
        }
    }

    private void categorizeEntity(String subjectType, String currentSubject,
                                  List<String> artifactList, List<String> furnitureList,
                                  List<String> characterList) {
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

    private List<String> parseEntityList(Element action, String elementName) {
        List<String> entityList = new LinkedList<>();
        Element element = (Element) action.getElementsByTagName(elementName).item(0);
        if (element != null) {
            NodeList entities = element.getElementsByTagName("entity");
            for (int j = 0; j < entities.getLength(); j++) {
                String entityName = entities.item(j).getTextContent().toLowerCase();
                entityList.add(entityName);
            }
        }
        return entityList;
    }

    private List<String> parseNarration(Element action) {
        List<String> narrationList = new LinkedList<>();
        NodeList narrations = action.getElementsByTagName("narration");
        for (int j = 0; j < narrations.getLength(); j++) {
            String narration = narrations.item(j).getTextContent();
            narrationList.add(narration);
        }
        return narrationList;
    }

    private String findEntityType(String entityName) {
        return this.entityParser.getEntityType(entityName);
    }
}

