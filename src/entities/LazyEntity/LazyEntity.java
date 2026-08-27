package entities.LazyEntity;

import Exceptions.LazyEntityExceptions.LazyEntityCantGetException;
import Interfaces.ConvertToFileData;
import Tools.EntityHandler;
import entities.BaseEntity.BaseEntity;

public class LazyEntity<T extends BaseEntity & ConvertToFileData>
{
    private EntityHandler entityHandler;
    private String id;
    private T self;


    public LazyEntity(String id, EntityHandler entityHandler)
    {
        this.self = null;
        this.id = id;
        this.entityHandler = entityHandler;
    }

    public LazyEntity(T self, EntityHandler entityHandler)
    {
        this.self = self;
        this.id = self.getId();
        this.entityHandler = entityHandler;
    }

    public T getSelf()
    {
        try
        {
            if (self == null) self = entityHandler.getEntity(id);
        }
        catch (IdPrefixNotMatchException e)
        {
            throw new LazyEntityCantGetException(e);
        }
        if (self == null) throw new LazyEntityCantGetException("Entity is not file in this file:"+entityHandler.getFileName());
        return self;
    }

    public String getId() {return id;}
    public void setSelf(T entity)
    {
        this.id = entity.getId();
        this.self = entity;
    }


}
