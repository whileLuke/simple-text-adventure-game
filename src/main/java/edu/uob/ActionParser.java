package edu.uob;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class ActionParser {
    public void parse(String[] args) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse("config" + File.separator + "basic-actions.xml");
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();

            for (int i = 0; i < actions.getLength(); i++) {
                Node actionNode = actions.item(i);

                if (actionNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element actionElement = (Element) actionNode;

                    // Get triggers
                    List<String> triggers = getTextValues(actionElement, "triggers", "keyphrase");

                    // Get subjects
                    List<String> subjects = getTextValues(actionElement, "subjects", "entity");

                    // Get consumed items
                    List<String> consumed = getTextValues(actionElement, "consumed", "entity");

                    // Get produced items
                    List<String> produced = getTextValues(actionElement, "produced", "entity");

                    // Get narration
                    String narration = getElementText(actionElement, "narration");

                    // Print the information
                    System.out.println("Action with triggers: " + triggers);
                    System.out.println("Requires: " + subjects);
                    System.out.println("Consumes: " + consumed);
                    System.out.println("Produces: " + produced);
                    System.out.println("Narration: " + narration + "\n");
                }
            }


            // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
            Element firstAction = (Element)actions.item(1);
            Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
            // Get the first trigger phrase
            String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }
    private static List<String> getTextValues(Element parent, String containerTag, String childTag) {
        List<String> values = new LinkedList<>();
        Element container = (Element) parent.getElementsByTagName(containerTag).item(0);

        if (container != null) {
            NodeList childNodes = container.getElementsByTagName(childTag);
            for (int i = 0; i < childNodes.getLength(); i++) {
                values.add(childNodes.item(i).getTextContent());
            }
        }

        return values;
    }

    private static String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
