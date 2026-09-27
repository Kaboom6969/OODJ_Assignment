package Interfaces;

import entities.BaseEntity.BaseEntity;
import entities.BusinessEntity.BusinessEntity;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public interface Linkable
{
    default List<LinkerManager> getLinkerManagerWithoutValidate ()
    {
        List<LinkerManager> list = new ArrayList<>();
        Class<?> classThatCallRightNow = this.getClass();
        Class<? extends BaseEntity> classBaseEntitySelf;
        String selfId = "";
        if (this instanceof BusinessEntity<?> businessEntity)
        {
            selfId = businessEntity.getId();
        }
        if (selfId == null || selfId.isEmpty())
        {
            throw new IllegalStateException("Self ID is empty");
        }
        classBaseEntitySelf =
                ((Class<?>) ((ParameterizedType)
                        classThatCallRightNow.getGenericSuperclass()).
                        getActualTypeArguments()[0]).
                        asSubclass(BaseEntity.class);
        for (Field field : classThatCallRightNow.getDeclaredFields())
        {
            if (field.getType().equals(LazyEntity.class))
            {
                LinkerManager linkerManager = getLinkerManagerWithField(classBaseEntitySelf, field);
                try
                {
                    LazyEntity<? extends BaseEntity> lazyEntityOther = (LazyEntity<? extends BaseEntity>) field.get(this);
                    if (lazyEntityOther != null && lazyEntityOther.getId() != null)
                    {
                        linkerManager.addLinker(new Linker(selfId, lazyEntityOther.getId()));
                    }
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
                    LinkerManager linkerManager = getLinkerManagerWithField(classBaseEntitySelf,field);
                    LazyEntityList<? extends BaseEntity> lazyEntityOther = (LazyEntityList<? extends BaseEntity>) field.get(this);
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
    static LinkerManager getLinkerManagerWithField(Class<? extends BaseEntity> classSelf, Field field)
    {
        field.setAccessible(true);
        if (field.getType().equals(LazyEntity.class) || field.getType().equals(LazyEntityList.class))
        {
            Class<? extends BaseEntity> classBaseEntityOther =
                    ((Class<?>) ((ParameterizedType)
                            field.getGenericType()).
                            getActualTypeArguments()[0]).
                            asSubclass(BaseEntity.class);
            return new LinkerManager(classSelf, classBaseEntityOther);
        }
        return null;
    }
    default List<LinkerManager> getLinkerManager()
    {
        return getLinkerManagerWithoutValidate();
    }
}

