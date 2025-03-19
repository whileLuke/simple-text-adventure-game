package edu.uob;

public abstract class STAGCommand {
    String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String execute() {
        return command;
    }
}
