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

    public T getSelf()
    {
        if (self == null) self = entityHandler.getEntity(id);
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
