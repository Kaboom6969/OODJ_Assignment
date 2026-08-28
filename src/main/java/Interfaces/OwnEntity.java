package Interfaces;

import entities.BaseEntity.BaseEntity;

public interface OwnEntity<T extends BaseEntity>
{
    public T getEntity();

    public void setEntity(T entity);
}
