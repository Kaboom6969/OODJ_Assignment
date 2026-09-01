package entities.LazyEntity;

import Exceptions.LazyEntityExceptions.LazyEntityListExceptions.LazyEntityListEntityCantGetException;
import Interfaces.ConvertToFileData;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BaseEntity;

import java.util.*;
import java.util.function.Consumer;

public class LazyEntityList<T extends BaseEntity & ConvertToFileData> implements Iterable<T>
{
    private List<LazyEntity<T>> lazyEntityList;

    private EntityHandler entityHandler;


    public int size()
    {
        return lazyEntityList.size();
    }

    public LazyEntity<T> lazyGet(int index)
    {
        return lazyEntityList.get(index);
    }

    public LazyEntityList(List<String> entityIdList, FileDataHandler entityFile)
    {
        lazyEntityList = new ArrayList<LazyEntity<T>>();
        this.entityHandler = new EntityHandler(entityFile);
        for (String entityId : entityIdList)
        {
            lazyEntityList.add(new LazyEntity<>(entityId, entityHandler));
        }
    }

    public T get(int index)
    {
        return lazyEntityList.get(index).getSelf();
    }

    public T get(String id)
    {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
        for (int i = 0; i<lazyEntityList.size(); i++)
        {
            if (lazyEntityList.get(i).getId().equals(id)) return this.get(i);
        }
        throw new LazyEntityListEntityCantGetException("No such entity with id " + id);
    }

    public void add(String id)
    {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
        lazyEntityList.add(new LazyEntity<>(id, entityHandler));
    }

    public void add(LazyEntity<T> entity)
    {
        lazyEntityList.add(entity);
    }

    public void add(T entity)
    {
        lazyEntityList.add(new LazyEntity<>(entity,entityHandler));
    }
    public void set(int index,String id)
    {
        lazyEntityList.set(index,new LazyEntity<>(id,entityHandler));
    }
    public void set(int index, LazyEntity<T> entity)
    {
        lazyEntityList.set(index, entity);
    }

    public void set(int index, T entity)
    {
        lazyEntityList.set(index,new LazyEntity<>(entity,entityHandler));
    }

    public void remove(LazyEntity<T> entity)
    {
        lazyEntityList.remove(entity);
    }

    public void remove(T entity)
    {
        lazyEntityList.remove(new LazyEntity<>(entity,entityHandler));
    }

    public void remove(int index)
    {
        lazyEntityList.remove(index);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new LazyEntityIterator<T>(lazyEntityList);
    }

    @Override
    public void forEach(Consumer<? super T> action)
    {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator()
    {
        return Iterable.super.spliterator();
    }
}

class LazyEntityIterator<T extends BaseEntity & ConvertToFileData> implements Iterator<T>
{
    private List<LazyEntity<T>> lazyEntityList;


    private int idListPointer;

    LazyEntityIterator()
    {
        idListPointer = 0;
    }

    public LazyEntityIterator(List<LazyEntity<T>> lazyEntityList)
    {
        this.lazyEntityList = lazyEntityList;
    }
    @Override
    public boolean hasNext()
    {
        return idListPointer < lazyEntityList.size();
    }

    @Override
    public T next()
    {
        if (hasNext())
        {
            T entity = lazyEntityList.get(idListPointer++).getSelf();
            return entity;
        }
        throw new NoSuchElementException();
    }


    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
