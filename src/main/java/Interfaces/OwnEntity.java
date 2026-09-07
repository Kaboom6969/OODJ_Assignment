package Interfaces;

import entities.BaseEntity.BaseEntity;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public interface OwnEntity extends OwnerShip
{
    default List<LazyEntity<? extends BaseEntity>> getEntity()
    {
        List<LazyEntity<? extends BaseEntity>> lazyEntities = new ArrayList<>();
        try
        {
            for (Field field : this.getClass().getDeclaredFields())
            {
                field.setAccessible(true);
                if (field.getType().equals(LazyEntity.class))
                {
                    lazyEntities.add((LazyEntity<? extends BaseEntity>) field.get(this));
                }
            }
        } catch (IllegalAccessException e) {throw new RuntimeException(e);}
        return lazyEntities;
    }

}
