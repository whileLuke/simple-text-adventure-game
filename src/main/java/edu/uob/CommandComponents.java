package edu.uob;

import java.util.Set;

public class CommandComponents {
    private final String commandType;
    private final Set<String> entities;

    public CommandComponents(String commandType, Set<String> entities) {
        this.commandType = commandType;
        this.entities = entities;
    }

    public String getCommandType() {
        return this.commandType;
    }

    public Set<String> getEntities() {
        return this.entities;
    }

    public boolean hasCommandType() {
        return this.commandType != null;
    }
}