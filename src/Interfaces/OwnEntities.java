package Interfaces;

import Iterator.LazyEntityList;
import entities.BaseEntity.BaseEntity;

public interface OwnEntities<T extends BaseEntity>
{
    public LazyEntityList<T> getEntities();

    public void setEntities(LazyEntityList<T> entities);
    public T getEntity(int index);

    public T getEntity(String id);

    public void setEntity(int index, T entity);
}
