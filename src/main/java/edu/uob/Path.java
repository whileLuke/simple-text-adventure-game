package edu.uob;

public class Path extends GameEntity {
    private Location pathFrom;
    private Location pathTo;

    public Path(String name, String description, Location pathFrom, Location pathTo) {
        super(name, description);
        this.pathFrom = pathFrom;
        this.pathTo = pathTo;
    }

    public Location getPathFrom() {
        return pathFrom;
    }

    public void setPathFrom(Location pathFrom) {
        this.pathFrom = pathFrom;
    }

    public Location getPathTo() {
        return pathTo;
    }

    public void setPathTo(Location pathTo) {
        this.pathTo = pathTo;
    }
}

