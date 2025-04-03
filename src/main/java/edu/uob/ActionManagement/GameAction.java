package edu.uob.ActionManagement;

import java.util.List;

public class GameAction
{
    private final List<String> triggers;
    private final List<String> consumed;
    private final List<String> produced;
    private final List<String> narration;
    private final List<String> artefacts;
    private final List<String> furniture;
    private final List<String> characters;
    private final int healthChange;

    public GameAction (List<String> triggerList, List<String> artefactList,
                       List<String> furnitureList, List<String> characterList,
                       List<String> consumedList, List<String> producedList, List<String>narrationList) {
        this.triggers = triggerList;
        this.artefacts = artefactList;
        this.furniture = furnitureList;
        this.characters = characterList;
        this.consumed = consumedList;
        this.produced = producedList;
        this.narration = narrationList;
        this.healthChange = 0;
    }

    public List<String> getTriggers() {
        return this.triggers;
    }

    public List<String> getArtefacts() {
        return this.artefacts;
    }

    public List<String> getFurniture() {
        return this.furniture;
    }

    public List<String> getCharacters() {
        return this.characters;
    }

    public List<String> getConsumed() {
        return this.consumed;
    }

    public List<String> getProduced() {
        return this.produced;
    }

    public List<String> getNarration() {
        return narration;
    }

    public int getHealthChange() {
        return healthChange;
    }
}
