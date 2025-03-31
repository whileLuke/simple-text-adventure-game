package edu.uob;

public abstract class GameEntity
{
    private String entityName;
    private String entityDescription;

    public GameEntity(String entityName, String entityDescription)
    {
        this.entityName = entityName;
        this.entityDescription = entityDescription;
    }

    public String getEntityName()
    {
        return entityName;
    }

    public void setEntityName (String entityName) {
        this.entityName = entityName;
    }

    public String getEntityDescription()
    {
        return entityDescription;
    }

    public void setEntityDescription(String entityDescription) {
        this.entityDescription = entityDescription;
    }
}
