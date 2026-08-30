package Interfaces;

import entities.LazyEntity.LazyEntityList;
import entities.BaseEntity.BaseEntity;

import java.util.List;

public interface OwnEntities
{
    public List<LazyEntityList<? extends BaseEntity>> getEntities();

}
