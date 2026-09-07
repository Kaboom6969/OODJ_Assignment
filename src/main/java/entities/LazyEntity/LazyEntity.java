package entities.LazyEntity;

import Exceptions.IdPrefixExceptions.IdPrefixNotMatchException;
import Exceptions.LazyEntityExceptions.LazyEntityCantGetException;
import Interfaces.ConvertToFileData;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BaseEntity;

import javax.print.attribute.standard.Copies;

public class LazyEntity<T extends BaseEntity & ConvertToFileData>
{
    private EntityHandler entityHandler;
    private String id;
    private T self;
    private String selfBackup;


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

    public LazyEntity(String id, FileDataHandler fileDataHandler)
    {
        this.self = null;
        this.id = id;
        this.entityHandler = new EntityHandler(fileDataHandler);
    }

    public LazyEntity(T self, FileDataHandler fileDataHandler)
    {
        this.self = self;
        this.id = self.getId();
        this.entityHandler = new EntityHandler(fileDataHandler);
    }

    public void updateBackup()
    {
        this.selfBackup = self.toFileData();
    }

    public boolean isSelfAlrChanged()
    {
        if (self == null) return false;
        return !self.toFileData().equals(selfBackup);
    }
    public T getSelf()
    {
        try
        {
            if (self == null)
            {
                self = entityHandler.getEntity(id);
                if (self == null) throw new LazyEntityCantGetException("Entity is not file in this file:"+entityHandler.getFileName());
                selfBackup = self.toFileData();
            }
        }
        catch (IdPrefixNotMatchException e)
        {
            throw new LazyEntityCantGetException(e);
        }

        return self;
    }

    public String getId() {return id;}

    public void changeSelf(T entity)
    {
        this.id = entity.getId();
        this.self = entity;
    }


}
