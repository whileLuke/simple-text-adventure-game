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

    public void parseActionsFile(File actionsFile) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(actionsFile);
            Element rootElement = document.getDocumentElement();
            NodeList actionsList = rootElement.getElementsByTagName("action");
            this.processActions(actionsList);
        } catch(Exception ignored) { }
    }

    private void processActions(NodeList actionsList) {
        for (int i = 0; i < actionsList.getLength(); i++) {
            Element currentAction = (Element) actionsList.item(i);

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

    private List<String> parseTriggers(Element actionElement) {
        List<String> triggerList = new LinkedList<>();
        Element triggersElement = (Element) actionElement.getElementsByTagName("triggers").item(0);
        if (triggersElement != null) {
            NodeList keyphrasesList = triggersElement.getElementsByTagName("keyphrase");
            for (int j = 0; j < keyphrasesList.getLength(); j++) {
                String currentTrigger = keyphrasesList.item(j).getTextContent().toLowerCase();
                triggerList.add(currentTrigger);
            }
        }
        return triggerList;
    }

    private void parseSubjects(Element actionElement, List<String> artifactList,
                               List<String> furnitureList, List<String> characterList) {
        Element subjectsElement = (Element) actionElement.getElementsByTagName("subjects").item(0);
        if (subjectsElement != null) {
            NodeList entitiesList = subjectsElement.getElementsByTagName("entity");
            for (int j = 0; j < entitiesList.getLength(); j++) {
                String currentSubject = entitiesList.item(j).getTextContent().toLowerCase();
                String subjectType = this.findEntityType(currentSubject);
                this.categoriseEntity(subjectType, currentSubject, artifactList, furnitureList, characterList);
            }
        }
    }

    private void categoriseEntity(String subjectType, String currentSubject,
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

    private List<String> parseEntityList(Element actionElement, String elementName) {
        List<String> entityList = new LinkedList<>();
        Element gameElement = (Element) actionElement.getElementsByTagName(elementName).item(0);
        if (gameElement != null) {
            NodeList entitiesList = gameElement.getElementsByTagName("entity");
            for (int j = 0; j < entitiesList.getLength(); j++) {
                String entityName = entitiesList.item(j).getTextContent().toLowerCase();
                entityList.add(entityName);
            }
        }
        return entityList;
    }

    private List<String> parseNarration(Element actionElement) {
        List<String> narrationList = new LinkedList<>();
        NodeList narrations = actionElement.getElementsByTagName("narration");
        for (int j = 0; j < narrations.getLength(); j++) {
            String narrationString = narrations.item(j).getTextContent();
            narrationList.add(narrationString);
        }
        return narrationList;
    }

    private String findEntityType(String entityName) {
        return this.entityParser.getEntityType(entityName);
    }
}

