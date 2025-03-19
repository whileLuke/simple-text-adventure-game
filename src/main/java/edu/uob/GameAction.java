package edu.uob;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;

public class GameAction
{
    private List<String> triggers;
    private List<String> subjects;
    private List<String> consumed;
    private List<String> produced;
    private List<String> narration;

    public GameAction (List<String> triggerList, List<String> subjectList, List<String> consumedList, List<String> producedList, List<String>narrationList) {
        this.triggers = triggerList;
        this.subjects = subjectList;
        this.consumed = consumedList;
        this.produced = producedList;
        this.narration = narrationList;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getConsumed() {
        return consumed;
    }

    public void setConsumed(List<String> consumed) {
        this.consumed = consumed;
    }

    public List<String> getProduced() {
        return produced;
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
