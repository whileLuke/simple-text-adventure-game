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

    public ActionParser() {
        this.actionSet = new HashSet<GameAction>();
    }

    public Set<GameAction> getActionSet() {
        return this.actionSet;
    }

    public void parse(File actionsFile) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();

            int actionsIndex = 1;
            while (actionsIndex < actions.getLength()) {
                Element currentAction = (Element)actions.item(actionsIndex);
                List<String> triggersList = new LinkedList<>();
                NodeList  triggersElements = currentAction.getChildNodes();
                for (int triggerIndex = 0; triggerIndex < triggersElements.getLength(); triggerIndex++) {
                    Element triggers = (Element) currentAction.getElementsByTagName("triggers").item(triggerIndex);
                    NodeList keyphrases = triggers.getElementsByTagName("keyphrase");
                    for (int keyphraseIndex = 0; keyphraseIndex < keyphrases.getLength(); keyphraseIndex++) {
                        String currentTriggerPhrase = keyphrases.item(keyphraseIndex).getTextContent();
                        triggersList.add(currentTriggerPhrase);
                    }
                    //String currentTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
                    //triggersList.add(currentTriggerPhrase);
                }
                System.out.println("Found action with " + triggersList.size() + " triggers");

                actionsIndex = actionsIndex + 2;
            }
            //DO SOMETHING TO NEXTACTION

            //pass each action somewehre one at a time
            //or wahtever the user action is compare it to the actions list
            //por something
            //Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
            // Get the first trigger phrase
            //String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
            //assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
        } catch(ParserConfigurationException pce) {
            //fail("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch(SAXException saxe) {
            //fail("SAXException was thrown when attempting to read basic actions file");
        } catch(IOException ioe) {
            //fail("IOException was thrown when attempting to read basic actions file");
        }
    }
}
