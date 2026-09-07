package Interfaces;

import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BaseEntity;
import entities.BusinessEntity.BusinessEntity;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public interface Linkable
{
    default List<LinkerManager> getLinkerManager()
    {
        List<LinkerManager> list = new ArrayList<>();
        Class<?> classThatCallRightNow = this.getClass();
        Class<? extends BaseEntity> classBaseEntitySelf;
        String selfId = "";
        if (this instanceof BusinessEntity<?> businessEntity)
        {
            selfId = businessEntity.getId();
        }
        if (selfId.isEmpty()) throw new RuntimeException("Self ID is empty");
        classBaseEntitySelf =
                ((Class<?>) ((ParameterizedType)
                        classThatCallRightNow.getGenericSuperclass()).
                        getActualTypeArguments()[0]).
                        asSubclass(BaseEntity.class);
        for (Field field : classThatCallRightNow.getDeclaredFields())
        {
            field.setAccessible(true);
            if (field.getType().equals(LazyEntity.class))
            {
                try
                {
                    LazyEntity<? extends BaseEntity> lazyEntityOther = (LazyEntity<? extends BaseEntity>) field.get(this);

                    Class<? extends BaseEntity> classBaseEntityOther =
                            ((Class<?>) ((ParameterizedType)
                                    field.getGenericType()).
                                    getActualTypeArguments()[0]).
                                    asSubclass(BaseEntity.class);
                    LinkerManager linkerManager = new LinkerManager(classBaseEntitySelf, classBaseEntityOther);
                    if (lazyEntityOther != null) linkerManager.addLinker(new Linker(selfId, lazyEntityOther.getId()));
                    list.add(linkerManager);
                } catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }

            if (field.getType().equals(LazyEntityList.class))
            {
                try
                {
                    LazyEntityList<? extends BaseEntity> lazyEntityOther = (LazyEntityList<? extends BaseEntity>) field.get(this);
                    Class<? extends BaseEntity> classBaseEntityOther =
                            ((Class<?>) ((ParameterizedType)
                                    field.getGenericType()).
                                    getActualTypeArguments()[0]).
                                    asSubclass(BaseEntity.class);
                    LinkerManager linkerManager = new LinkerManager(classBaseEntitySelf, classBaseEntityOther);
                    if (lazyEntityOther != null)
                    {
                        for (String id : lazyEntityOther.getIds())
                        {
                            linkerManager.addLinker(new Linker(selfId, id));
                        }
                    }
                    list.add(linkerManager);
                } catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }

        }
        return list;
    }
}

