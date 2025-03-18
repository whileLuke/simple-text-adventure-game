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
}
