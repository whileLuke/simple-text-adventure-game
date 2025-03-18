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
            Element firstAction = (Element)actions.item(1);
            //pass each action somewehre one at a time
            //or wahtever the user action is compare it to the actions list
            //por something
            Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
            // Get the first trigger phrase
            String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
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
