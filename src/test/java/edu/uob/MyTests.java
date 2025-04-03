package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.uob.GameManagement.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.*;


public class MyTests {
    private GameServer gameServer;
    private File entitiesFile;
    private File actionsFile;

    @BeforeEach
    public void setUp() {
        // Adjust paths as needed for your project structure
        entitiesFile = new File("config/extended-entities.dot");
        actionsFile = new File("config/extended-actions.xml");
        gameServer = new GameServer(entitiesFile, actionsFile);
    }

    // Basic Command Tests
    @Test
    public void testInventoryCommand() {
        // Test inventory when empty
        String emptyInvResponse = gameServer.handleCommand("luke: inv");
        assertTrue(emptyInvResponse.contains("Your inventory is empty"));

        // Test getting and then checking inventory
        gameServer.handleCommand("luke: get axe");
        String invResponse = gameServer.handleCommand("luke: inv");
        assertTrue(invResponse.contains("axe"));
    }

    @Test
    public void testGetCommand() {
        // Test getting existing item
        String getResponse = gameServer.handleCommand("luke: get axe");
        assertTrue(getResponse.contains("You picked up the axe"));

        // Test getting non-existent item
        String missingItemResponse = gameServer.handleCommand("luke: get nonexistent");
        assertTrue(missingItemResponse.contains("is not here"));

        // Test getting non-artefact item
        String nonArtefactResponse = gameServer.handleCommand("luke: get trapdoor");
        assertTrue(nonArtefactResponse.contains("cannot be taken"));
    }

    @Test
    public void testDropCommand() {
        // First get an item
        gameServer.handleCommand("luke: get axe");

        // Then drop it
        String dropResponse = gameServer.handleCommand("luke: drop axe");
        assertTrue(dropResponse.contains("You dropped the axe"));

        // Try dropping non-existent item
        String missingDropResponse = gameServer.handleCommand("luke: drop nonexistent");
        assertTrue(missingDropResponse.contains("do not have"));
    }

    @Test
    public void testLookCommand() {
        // Test look command shows location details
        String lookResponse = gameServer.handleCommand("luke: look");
        assertTrue(lookResponse.contains("You are in"));
        assertTrue(lookResponse.contains("You can see:"));
        assertTrue(lookResponse.contains("You can see paths to:"));
    }

    @Test
    public void testGotoCommand() {
        // Test valid goto
        String gotoResponse = gameServer.handleCommand("luke: goto forest");
        assertTrue(gotoResponse.contains("You go to forest"));

        // Test invalid goto
        String invalidGotoResponse = gameServer.handleCommand("luke: goto nonexistent");
        assertTrue(invalidGotoResponse.contains("can't go to"));
    }

    // Custom Action Tests from extended-actions.xml
    @Test
    public void testOpenTrapdoor() {
        // First get the key
        gameServer.handleCommand("luke: get key");

        // Then open trapdoor
        String openResponse = gameServer.handleCommand("luke: open trapdoor");
        assertTrue(openResponse.contains("You unlock the door"));
        assertTrue(openResponse.contains("steps leading down into a cellar"));
    }

    @Test
    public void testChopTree() {
        // First get the axe
        gameServer.handleCommand("luke: get axe");

        // Then chop tree
        String chopResponse = gameServer.handleCommand("luke: chop tree");
        assertTrue(chopResponse.contains("You cut down the tree"));

        // Verify log is produced
        String lookResponse = gameServer.handleCommand("luke: look");
        assertTrue(lookResponse.contains("log"));
    }

    @Test
    public void testDrinkPotion() {
        // Get and drink potion
        gameServer.handleCommand("luke: get potion");
        String drinkResponse = gameServer.handleCommand("luke: drink potion");
        assertTrue(drinkResponse.contains("You drink the potion"));
        assertTrue(drinkResponse.contains("health improves"));
    }

    @Test
    public void testFightElf() {
        // Simulate fight
        String fightResponse = gameServer.handleCommand("luke: fight elf");
        assertTrue(fightResponse.contains("You attack the elf"));
        assertTrue(fightResponse.contains("fights back"));
        assertTrue(fightResponse.contains("lose some health"));
    }

    @Test
    public void testPayElf() {
        // First get a coin
        gameServer.handleCommand("luke: get coin");

        // Pay elf
        String payResponse = gameServer.handleCommand("luke: pay elf");
        assertTrue(payResponse.contains("You pay the elf"));
        assertTrue(payResponse.contains("produces a shovel"));
    }

    @Test
    public void testBridgeRiver() {
        // First chop tree to get log
        gameServer.handleCommand("luke: get axe");
        gameServer.handleCommand("luke: chop tree");

        // Bridge river
        String bridgeResponse = gameServer.handleCommand("luke: bridge river");
        assertTrue(bridgeResponse.contains("You bridge the river"));
        assertTrue(bridgeResponse.contains("can now reach the other side"));
    }

    @Test
    public void testDigGround() {
        // First get shovel
        gameServer.handleCommand("luke: get coin");
        gameServer.handleCommand("luke: pay elf");

        // Dig ground
        String digResponse = gameServer.handleCommand("luke: dig ground");
        assertTrue(digResponse.contains("You dig into the soft ground"));
        assertTrue(digResponse.contains("unearthed a pot of gold"));
    }

    @Test
    public void testBlowHorn() {
        // Get horn
        gameServer.handleCommand("luke: get horn");

        // Blow horn
        String blowResponse = gameServer.handleCommand("luke: blow horn");
        assertTrue(blowResponse.contains("You blow the horn"));
        assertTrue(blowResponse.contains("lumberjack appears"));
    }

    // Multiple Command Interaction Tests
    @Test
    public void testMultiStepScenario() {
        // A complex scenario testing multiple interactions
        gameServer.handleCommand("luke: get key");
        gameServer.handleCommand("luke: open trapdoor");
        gameServer.handleCommand("luke: get coin");
        gameServer.handleCommand("luke: pay elf");
        gameServer.handleCommand("luke: get axe");
        gameServer.handleCommand("luke: chop tree");
        gameServer.handleCommand("luke: bridge river");
        gameServer.handleCommand("luke: get shovel");
        gameServer.handleCommand("luke: dig ground");

        // Final look to verify state
        String finalLookResponse = gameServer.handleCommand("luke: look");
        assertTrue(finalLookResponse.contains("gold"));
        assertTrue(finalLookResponse.contains("hole"));
    }

    // Edge Case Tests
    @Test
    public void testInvalidCommands() {
        String[] invalidCommands = {
                "luke: dance",
                "luke: jump",
                "luke: fly",
                "luke: teleport"
        };

        for (String cmd : invalidCommands) {
            String response = gameServer.handleCommand(cmd);
            assertTrue(response.contains("I don't understand"));
        }
    }
}