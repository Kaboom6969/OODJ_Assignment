package Interfaces;

import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.BaseEntity.BaseEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public interface OwnEntities extends OwnerShip
{
    default List<LazyEntityList<? extends BaseEntity>> getEntities()
    {
        List<LazyEntityList<? extends BaseEntity>> lazyEntityLists = new ArrayList<>();
        try
        {
            for (Field field : this.getClass().getDeclaredFields())
            {
                field.setAccessible(true);
                if (field.getType().equals(LazyEntityList.class))
                {
                    lazyEntityLists.add((LazyEntityList<? extends BaseEntity>) field.get(this));
                }
            }
        } catch (IllegalAccessException e) {throw new RuntimeException(e);}
        return lazyEntityLists;
    }
}

