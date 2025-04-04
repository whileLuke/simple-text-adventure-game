package edu.uob.CommandManagement;

import java.util.Set;

public class CommandComponents {
    private final String commandType;
    private final Set<String> entitiesSet;

    public CommandComponents(String commandType, Set<String> entitiesSet) {
        this.commandType = commandType;
        this.entitiesSet = entitiesSet;
    }

    public Set<String> getEntities() {
        return this.entitiesSet;
    }

    public boolean hasCommandType() {
        return this.commandType != null;
    }
}