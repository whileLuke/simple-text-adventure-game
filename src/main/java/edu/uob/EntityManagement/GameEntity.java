package edu.uob.EntityManagement;

public abstract class GameEntity
{
    private final String entityName;
    private final String entityDescription;

    public GameEntity(String entityName, String entityDescription)
    {
        this.entityName = entityName;
        this.entityDescription = entityDescription;
    }

    public String getEntityName()
    {
        return entityName;
    }

    public String getEntityDescription()
    {
        return entityDescription;
    }
}
