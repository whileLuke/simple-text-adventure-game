package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class MyTestsComprehensive {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  String sendCommandToServer(String command) {
      // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
      /*return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { */return server.handleCommand(command);/*},
      "Server took too long to respond (probably stuck in an infinite loop)");*/
  }

  @Test
  void testInventory(){
      String response = sendCommandToServer("simon: get potion");
      assertTrue(response.contains("potion"));
      response = sendCommandToServer("simon: inventory");
      assertTrue(response.contains("potion"));
      response = sendCommandToServer("simon: inv");
      assertTrue(response.contains("potion"));
      response = sendCommandToServer("simon: inventory potion");
      assertTrue(response.equals("You can't look in the inventory of other entities!"));
      response = sendCommandToServer("simon: inventory trapdoor");
      assertTrue(response.equals("You can't look in the inventory of other entities!"));
      response = sendCommandToServer("simon: inventory elf");
      assertTrue(response.equals("You can't look in the inventory of other entities!"));
      response = sendCommandToServer("simon: inventory cabin");
      assertTrue(response.equals("You can't look in the inventory of other entities!"));
  }


  // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
  @Test
  void testLook() {
    String response = sendCommandToServer("simon: look");
    response = response.toLowerCase();
    assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    response = sendCommandToServer("simon: look potion");
    assertTrue(response.equals("Don't look at other entities, they don't like it!"));
  }

  // Test that we can pick something up and that it appears in our inventory
  @Test
  void testGet()
  {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
  }

  // Test that we can goto a different location (we won't get very far if we can't move around the game !)
  @Test
  void testGoto()
  {
      String response = sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

  @Test
  void testDrop(){
      String response;
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still in the inventory after an attempt was made to drop it");
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      sendCommandToServer("simon: drop potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still in the inventory after an attempt was made to drop it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Potion is not present in the room after an attempt was made to drop it");
  }

  @Test
  void testAction(){
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Key present in the forest");
      sendCommandToServer("simon: get key");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Key is in the inventory");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is no longer present in the forest");
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
      response = sendCommandToServer("simon: open trapdoor with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
  }

  @Test
  void testHealthAction(){
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("forest"), "Player is in the forest");
      assertTrue(response.contains("key"), "Key not present in the forest");
      sendCommandToServer("simon: get key");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Key is in the inventory");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is no longer present in the forest");
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
      assertFalse(response.contains("cellar"), "Path to cellar should not exist yet");
      response = sendCommandToServer("simon: open trapdoor with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key has been consumed");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is not present in the cabin");
      assertTrue(response.contains("cellar"), "There is no path to the cellar");
      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("elf"));
      assertTrue(this.server.getCurrentPlayer().getHealth() == 3);
      response = sendCommandToServer("simon: fight elf");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
      assertTrue(this.server.getCurrentPlayer().getHealth() == 2);
      response = sendCommandToServer("simon: hit elf");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
      assertTrue(this.server.getCurrentPlayer().getHealth() == 1);
      response = sendCommandToServer("simon: drink potion");
      assertTrue(response.equals("You drink the potion and your health improves"));
      assertTrue(this.server.getCurrentPlayer().getHealth() == 2);
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion has been consumed");
  }

  /*@Test
  void testTripleHealthAction(){
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: get coin");
      assertTrue(response.contains("coin"), "Did not see the coin in the inventory");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("coin"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("forest"), "Player is in the forest");
      assertTrue(response.contains("key"), "Key not present in the forest");
      sendCommandToServer("simon: get key");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Key is in the inventory");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is no longer present in the forest");
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
      assertFalse(response.contains("cellar"), "Path to cellar should not exist yet");
      response = sendCommandToServer("simon: open trapdoor with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key has been consumed");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is not present in the cabin");
      assertTrue(response.contains("cellar"), "There is no path to the cellar");
      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("elf"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      response = sendCommandToServer("simon: drink potion");
      assertTrue(response.equals("You drink the potion and your health improves"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion has been consumed");
      response = sendCommandToServer("simon: fight elf");
      assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
      assertTrue(response.contains("You have died and lost all your items, you return to the start of the game!"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("cabin"));
      assertTrue(response.contains("axe"));
      response = sendCommandToServer("simon: inv");
      assertFalse(response.contains("coin"));
  }*/

  @Test
  void testInvalidUsernames(){
        String response = sendCommandToServer("simo6: look");
        assertTrue(response.equals("Invalid username. Use only letters, spaces, apostrophes and hyphens"), "Invalid username passed as valid username");
        response = sendCommandToServer("simo@: look");
        assertTrue(response.equals("Invalid username. Use only letters, spaces, apostrophes and hyphens"), "Invalid username passed as valid username");
        response = sendCommandToServer("simo : look");
        assertFalse(response.equals("Invalid username. Use only letters, spaces, apostrophes and hyphens"), "Invalid username passed as valid username");
        response = sendCommandToServer("simo-: look");
        assertFalse(response.equals("Invalid username. Use only letters, spaces, apostrophes and hyphens"), "Invalid username passed as valid username");
        response = sendCommandToServer("simo': look");
        assertFalse(response.equals("Invalid username. Use only letters, spaces, apostrophes and hyphens"), "Invalid username passed as valid username");
  }

  @Test
  void testWorldRetention(){
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("boris: look");
      assertTrue(response.contains("simon"));
      assertFalse(response.contains("potion"));
  }

  @Test
  void testCaseInsensitivity(){
      String response = sendCommandToServer("simon: LoOk");
      response = response.toLowerCase();
      assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
      response = sendCommandToServer("siMOn: LoOk");
      response = response.toLowerCase();
      assertTrue(response.contains("simon"), "Did not see the name of the current room in response to look");
      sendCommandToServer("simon: gEt pOtIoN");
      response = sendCommandToServer("simon: iNv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      sendCommandToServer("simon: DrOp PoTiOn");
      response = sendCommandToServer("simon: InV");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still in the inventory after an attempt was made to drop it");
      response = sendCommandToServer("simon: dRinK pOtIon");
      assertTrue(response.equals("You drink the potion and your health improves"));
      response = sendCommandToServer("simon: HeAlTh");
      assertTrue(response.contains("3"));
  }

  @Test
  void testStoreroom(){
      assertNotNull(server.getLocation("storeroom"));
      assertTrue(server.getLocation("storeroom").getPathMap().size() == 0);
  }

  @Test
  void testSeePlayers(){
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
      assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
      assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
      assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
      assertTrue(response.contains("forest"), "Did not see available paths in response to look");
      response = sendCommandToServer("frank: look");
      response = response.toLowerCase();
      assertTrue(response.contains("simon"), "Did not see Simon in the same room");
  }

  @Test
  void testMultipleTriggers(){
      String response = sendCommandToServer("simon: goto forest");
      assertTrue(response.contains("You have gone to the forest"));
      response = sendCommandToServer("simon: get key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: open unlock trapdoor with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
  }

  @Test
  void testDeath(){
      String response;
      response = sendCommandToServer("boris: look");
      assertFalse(response.contains("simon"), "Did not see Simon in the same room");
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("boris: look");
      assertTrue(response.contains("simon"), "Did not see Simon in the same room");
      response = sendCommandToServer("boris: look simon");
      assertTrue(response.equals("No player interaction allowed"), "Did not see Simon in the same room");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("boris: look");
      assertFalse(response.contains("simon"), "Did not see Simon in the same room");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("forest"), "Player is in the forest");
      assertTrue(response.contains("key"), "Key not present in the forest");
      sendCommandToServer("simon: get key");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Key is in the inventory");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is no longer present in the forest");
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
      assertFalse(response.contains("cellar"), "Path to cellar should not exist yet");
      response = sendCommandToServer("simon: unlock trapdoor with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key has been consumed");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is not present in the cabin");
      assertTrue(response.contains("cellar"), "There is no path to the cellar");
      response = sendCommandToServer("boris: look");
      assertTrue(response.contains("simon"), "Did not see Simon in the same room");
      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("boris: look");
      assertFalse(response.contains("simon"), "Did not see Simon in the same room");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("elf"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      response = sendCommandToServer("simon: fight elf");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("2"));
      response = sendCommandToServer("simon: fight elf");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
      assertTrue(server.getCurrentPlayer().getHealth() == 1);
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("1"));
      response = sendCommandToServer("simon: fight elf");
      assertTrue(response.contains("You have died and lost all your items, you return to the start of the game!"));
      response = sendCommandToServer("boris: look");
      assertTrue(response.contains("simon"), "Did not see Simon in the same room");
      response = sendCommandToServer("boris: goto cellar");
      assertTrue(response.equals("You have gone to the cellar"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      response = sendCommandToServer("simon: look");
      assertFalse(response.contains("boris"), "Did not see Simon in the same room");
      assertTrue(response.contains("cabin"));
      assertTrue(response.contains("cellar"));
      response = sendCommandToServer("simon: inventory");
      assertFalse(response.contains("potion"));
      response = sendCommandToServer("simon: goto cellar");
      assertTrue(response.equals("You have gone to the cellar"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("boris"));
      assertTrue(response.contains("potion"));
  }

  @Test
  void testValidActions(){
      //test all valid and invalid commands
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: drink");
      assertTrue(response.equals("You can't do that here"));
      response = sendCommandToServer("simon: potion");
      assertTrue(response.equals("No valid action found"));
      response = sendCommandToServer("simon: drink potion with spanner");
      assertTrue(response.equals("You drink the potion and your health improves"));
      assertTrue(server.getLocation("storeroom").getEntityList().toString().contains("potion"));
      response = sendCommandToServer("simon: inv");
      assertFalse(response.contains("potion"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      response = sendCommandToServer("simon: get potion");
      assertTrue(response.equals("Specified item not in room or can't be picked up"));
      setup();
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: drink potion with key");
      assertTrue(response.equals("You can't do that here"));
      response = sendCommandToServer("simon: get axe");
      assertTrue(response.equals("You have picked up the axe"));
      response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("You have gone to the forest"));
      response = sendCommandToServer("simon: cut cut down chop tree!!!");
      assertTrue(response.equals("You cut down the tree with the axe"));
      response = sendCommandToServer("simon: inv");
      assertFalse(response.contains("log"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("log"));
      response = sendCommandToServer("simon: get log");
      assertTrue(response.equals("You have picked up the log"));
      response = sendCommandToServer("simon: get key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: get coin");
      assertTrue(response.equals("You have picked up the coin"));
      response = sendCommandToServer("simon: unlock trapdoor");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("cellar"));
      response = sendCommandToServer("simon: goto cellar");
      assertTrue(response.equals("You have gone to the cellar"));
      assertTrue(server.getLocation("cellar").getEntityList().toString().contains("elf"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("Elf"));
      response = sendCommandToServer("simon: pay elf");
      assertTrue(response.equals("You pay the elf your silver coin and he produces a shovel"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("shovel"));
      response = sendCommandToServer("simon: inv");
      assertFalse(response.contains("shovel"));
      response = sendCommandToServer("simon: get shovel");
      assertTrue(response.equals("You have picked up the shovel"));
      response  = sendCommandToServer("simon: look");
      assertTrue(response.contains("cabin"));
      response = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("You have gone to the forest"));
      response = sendCommandToServer("simon: goto riverbank");
      assertTrue(response.equals("You have gone to the riverbank"));
      response = sendCommandToServer("simon: get horn");
      assertTrue(response.equals("You have picked up the horn"));
      response = sendCommandToServer("simon: bridge river");
      assertTrue(response.equals("You bridge the river with the log and can now reach the other side"));
      response = sendCommandToServer("simon: goto clearing");
      assertTrue(response.equals("You have gone to the clearing"));
      assertTrue(server.getLocation("storeroom").getEntityList().toString().contains("lumberjack"));
      assertFalse(server.getLocation("clearing").getEntityList().toString().contains("lumberjack"));
      response = sendCommandToServer("simon: blow horn");
      assertTrue(response.equals("You blow the horn and as if by magic, a lumberjack appears !"));
      assertTrue(server.getLocation("clearing").getEntityList().toString().contains("lumberjack"));
      assertFalse(server.getLocation("storeroom").getEntityList().toString().contains("lumberjack"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("cutter"));
      assertFalse(server.getLocation("clearing").getEntityList().toString().contains("gold"));
      assertTrue(server.getLocation("storeroom").getEntityList().toString().contains("gold"));
      response = sendCommandToServer("simon: dig with shovel");
      assertTrue(response.equals("You dig into the soft ground and unearth a pot of gold !!!"));
      assertFalse(server.getLocation("storeroom").getEntityList().toString().contains("gold"));
      assertTrue(server.getLocation("clearing").getEntityList().toString().contains("gold"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("hole"));
      assertTrue(response.contains("gold"));
  }

  @Test
  void testNullCommands(){
      String response = sendCommandToServer(null);
      assertTrue(response.equals("No command or username provided"));
      response  = sendCommandToServer("");
      assertTrue(response.equals("No command or username provided"));
      response  = sendCommandToServer(": look");
      assertTrue(response.equals("No command or username provided"));
      response  = sendCommandToServer("simon: ");
      assertTrue(response.equals("No valid action found"));
      response  = sendCommandToServer("simon:");
      assertTrue(response.equals("No command or username provided"));
      response  = sendCommandToServer(" : ");
      assertTrue(response.equals("No valid action found"));
  }

  //Test using inv and look
  @Test
  void testInvalidCommands(){
  }

  @Test
  void testVariedWhitespace(){
      String response;
      sendCommandToServer("simon:get potion");
      response = sendCommandToServer("simon:   drink");
      assertTrue(response.equals("You can't do that here"));
      response = sendCommandToServer("simon:   potion  ");
      assertTrue(response.equals("No valid action found"));
      response = sendCommandToServer("simon: drinkpotion with spanner");
      assertFalse(response.equals("You drink the potion and your health improves"));
      response = sendCommandToServer("simon: goto    forest");
      assertTrue(response.contains("You have gone to the forest"));
      response = sendCommandToServer("simon: get  key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: goto    cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: unlock trapdoorwith key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
  }

  @Test
  void testDecoratedCommands(){
      String response;
      sendCommandToServer("simon:get potion please");
      response = sendCommandToServer("simon: inventory please");
      assertTrue(response.contains("potion"));
      response = sendCommandToServer("simon:   drink but maybe lorem ipsum dolor potion ");
      assertTrue(response.equals("You drink the potion and your health improves"));
  }

  @Test
  void testWordOrdering(){
      String response;
      sendCommandToServer("simon:get potion");
      response = sendCommandToServer("simon:   potion drink");
      assertTrue(response.equals("You drink the potion and your health improves"));
  }

  @Test
  void testPartialCommands(){
      String response = sendCommandToServer("simon: goto forest");
      assertTrue(response.contains("You have gone to the forest"));
      response = sendCommandToServer("simon: get key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: unlock with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
  }

  @Test
  void testExtraneousEntities(){
      String response = sendCommandToServer("simon: goto forest key");
      assertFalse(response.contains("You have gone to the forest"));
      response = sendCommandToServer("simon: goto forest");
      assertTrue(response.contains("You have gone to the forest"));
      response = sendCommandToServer("simon: get key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: unlock with key and cellar");
      assertFalse(response.equals("You unlock the door and see steps leading down into a cellar"));
  }

  @Test
  void testAmbiguousCommands(){
      /*String response = sendCommandToServer("simon:   potion drink");
      assertFalse(response.equals("You drink the potion and your health improves"));
      assertTrue(response.equals("More than one action possible, be specific"));
      response = sendCommandToServer("simon:   potion drink coin");
      assertTrue(response.equals("You drink the potion and your health improves coin"));*/
      String response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("You have gone to the forest"));
      response = sendCommandToServer("simon: get key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: unlock with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
      response  = sendCommandToServer("simon: goto cellar");
      assertTrue(response.equals("You have gone to the cellar"));
      response = sendCommandToServer("simon: hit and pay elf");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
      response  = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: get coin");
      assertTrue(response.equals("You have picked up the coin"));
      response = sendCommandToServer("simon: goto cellar");
      assertTrue(response.equals("You have gone to the cellar"));
      response = sendCommandToServer("simon: hit and pay elf");
      assertTrue(response.equals("Multiple actions possible, be specific"));
      response = sendCommandToServer("simon: hit elf get");
      assertTrue(response.equals("You can't perform more than one action at once"));
  }

  @Test
  void testCompositeCommands(){
      String response = sendCommandToServer("simon: get axe and coin");
      assertTrue(response.contains("You can only pick up one item!"));
  }

  @Test
  void testWrongLocation(){
      String response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("You have gone to the forest"));
      response = sendCommandToServer("simon: get key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: unlock trapdoor with key");
      assertTrue(response.equals("You can't do that here"));
  }

  @Test
  void testRepeatedActions(){
      String response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("You have gone to the forest"));
      response = sendCommandToServer("simon: get key");
      assertTrue(response.equals("You have picked up the key"));
      response = sendCommandToServer("simon: goto cabin");
      assertTrue(response.equals("You have gone to the cabin"));
      response = sendCommandToServer("simon: unlock with key");
      assertTrue(response.equals("You unlock the door and see steps leading down into a cellar"));
      response  = sendCommandToServer("simon: goto cellar");
      assertTrue(response.equals("You have gone to the cellar"));
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("elf"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      response = sendCommandToServer("simon: fight elf oogabooga");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("2"));
      response = sendCommandToServer("simon: fight hit attack elf awoo");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
      assertTrue(server.getCurrentPlayer().getHealth() == 1);
      response = sendCommandToServer("simon: health");
      assertTrue(response.contains("1"));
      response = sendCommandToServer("simon: fight elf");
      assertTrue(response.contains("You have died and lost all your items, you return to the start of the game!"));
      response  = sendCommandToServer("simon: goto cellar");
      assertTrue(response.equals("You have gone to the cellar"));
      response = sendCommandToServer("simon: fight fight elf oogabooga");
      assertTrue(response.equals("You attack the elf, but he fights back and you lose some health"));
  }

  @Test
  void testRepeatedCommands(){
      String response = sendCommandToServer("simon: get coin");
      assertTrue(response.equals("You have picked up the coin"));
      response = sendCommandToServer("simon: look");
      assertFalse(response.contains("coin"));
      response = sendCommandToServer("simon: inv");
      assertTrue(response.contains("coin"));
      response = sendCommandToServer("simon: get coin");
      assertTrue(response.equals("Specified item not in room or can't be picked up"));
      response = sendCommandToServer("simon: look");
      assertFalse(response.contains("coin"));
      response = sendCommandToServer("simon: inv");
      assertTrue(response.contains("coin"));
      response = sendCommandToServer("simon: drop coin");
      assertTrue(response.equals("You have dropped the coin"));
      response = sendCommandToServer("simon: drop coin");
      assertTrue(response.equals("You do not have the specified item in your inventory"));
      response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("You have gone to the forest"));
      response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("There is no path to the forest"));
      response = sendCommandToServer("simon: goto riverbank");
      assertTrue(response.equals("You have gone to the riverbank"));
      response = sendCommandToServer("simon: get horn");
      assertTrue(response.equals("You have picked up the horn"));
      response = sendCommandToServer("simon: blow horn");
      assertTrue(response.equals("You blow the horn and as if by magic, a lumberjack appears !"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("cutter"));
      response = sendCommandToServer("simon: blow horn");
      assertTrue(response.equals("You blow the horn and as if by magic, a lumberjack appears !"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("cutter"));
      response = sendCommandToServer("simon: goto forest");
      assertTrue(response.equals("You have gone to the forest"));
      response = sendCommandToServer("simon: blow horn");
      assertTrue(response.equals("You blow the horn and as if by magic, a lumberjack appears !"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("cutter"));
      response = sendCommandToServer("simon: goto riverbank");
      assertTrue(response.equals("You have gone to the riverbank"));
      response = sendCommandToServer("simon: look");
      assertFalse(response.contains("cutter"));
      setup();
      response = sendCommandToServer("simon: get get coin");
      assertFalse(response.equals("You have picked up the coin"));
  }

  @Test
  void testDecorativeTriggers(){
      String response = sendCommandToServer("simon: get coin open");
      assertTrue(response.equals("You have picked up the coin"));
  }
}