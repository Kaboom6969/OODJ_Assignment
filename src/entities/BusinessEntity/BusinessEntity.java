package entities.BusinessEntity;

import Interfaces.ConvertToFileData;
import entities.LazyEntity.LazyEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BaseEntity;

public abstract class BusinessEntity<T extends BaseEntity & ConvertToFileData>
{
    private FileDataHandler selfFile;
    protected LazyEntity<T> self;

    public BusinessEntity(String id,FileDataHandler selfFile)
    {
        this.self = new LazyEntity<T>(id,new EntityHandler(selfFile));
    }
}
