package entities.BusinessEntity;

import entities.BaseEntity.BaseEntity;

public abstract class BusinessEntity<T extends BaseEntity>
{
    protected T self;

    public BusinessEntity(T self)
    {
        this.self = self;
    }
}
