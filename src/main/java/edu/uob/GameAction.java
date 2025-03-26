package edu.uob;

import java.util.List;

public class GameAction
{
    private List<String> triggers;
    //private List<String> subjects;
    private List<String> consumed;
    private List<String> produced;
    private List<String> narration;
    private List<String> artefacts;
    private List<String> furniture;
    private List<String> characters;

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

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public List<String> getConsumed() {
        return this.consumed;
    }

    public void setConsumed(List<String> consumed) {
        this.consumed = consumed;
    }

    public List<String> getProduced() {
        return this.produced;
    }

    public void setProduced(List<String> produced) {
        this.produced = produced;
    }

    public List<String> getNarration() {
        return narration;
    }

    public void setNarration(List<String> narration) {
        this.narration = narration;
    }


    //maybe create a hashmap or h
    //ashset or something.
}
