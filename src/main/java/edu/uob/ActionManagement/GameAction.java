package edu.uob.ActionManagement;

import java.util.List;

public class GameAction
{
    private final List<String> triggersList;
    private final List<String> consumedList;
    private final List<String> producedList;
    private final List<String> narrationList;
    private final List<String> artefactsList;
    private final List<String> furnitureList;
    private final List<String> charactersList;
    private final int healthChange;

    public GameAction (List<String> triggerList, List<String> artefactList,
                       List<String> furnitureList, List<String> characterList,
                       List<String> consumedList, List<String> producedList, List<String>narrationList) {
        this.triggersList = triggerList;
        this.artefactsList = artefactList;
        this.furnitureList = furnitureList;
        this.charactersList = characterList;
        this.consumedList = consumedList;
        this.producedList = producedList;
        this.narrationList = narrationList;
        this.healthChange = 0;
    }

    public List<String> getTriggersList() {
        return this.triggersList;
    }

    public List<String> getArtefactsList() {
        return this.artefactsList;
    }

    public List<String> getFurnitureList() {
        return this.furnitureList;
    }

    public List<String> getCharactersList() {
        return this.charactersList;
    }

    public List<String> getConsumedList() {
        return this.consumedList;
    }

    public List<String> getProducedList() {
        return this.producedList;
    }

    public List<String> getNarrationList() {
        return narrationList;
    }

    public int getHealthChange() {
        return healthChange;
    }
}
