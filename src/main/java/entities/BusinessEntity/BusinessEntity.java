package entities.BusinessEntity;

import Interfaces.ConvertToFileData;
import entities.LazyEntity.LazyEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BaseEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public abstract class BusinessEntity<T extends BaseEntity & ConvertToFileData>
{
    private FileDataHandler selfFile;
    protected LazyEntity<T> self;

    public BusinessEntity(String id, FileDataHandler selfFile)
    {
        this(id, selfFile, null);
    }

    public BusinessEntity(FileDataHandler selfFile, T self)
    {
        this(java.util.Objects.requireNonNull(self, "Self is required").getId(), selfFile, self);
    }

    public BusinessEntity(String id, FileDataHandler selfFile, T self)
    {
        java.util.Objects.requireNonNull(selfFile, "Self file handler is required");
        if (self == null && id == null)throw new NullPointerException("Self or Id is required");
        EntityHandler handler = new EntityHandler(selfFile);
        if (self == null)
        {
            this.self = new LazyEntity<T>(id, handler);
        }
        else
        {
            if (id != null && !id.equals(self.getId()))
                throw new IllegalArgumentException("Self ID does not match: " + id);
            this.self = new LazyEntity<T>(self, handler);
            this.self.updateBackup();
        }
    }

    public T getSelf()
    {
        return self.getSelf();
    }

    public String getId()
    {
        return self.getId();
    }
}
