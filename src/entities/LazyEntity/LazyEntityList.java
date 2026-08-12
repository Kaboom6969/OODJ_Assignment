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




    public LazyEntityList(List<String> entityIdList, FileDataHandler entityFile)
    {
        lazyEntityList = new ArrayList<>(entityIdList.size());
        this.entityHandler = new EntityHandler(entityFile);
        for (int i = 0; i<entityIdList.size(); i++)
        {
            lazyEntityList.set(i, new LazyEntity<T>(entityIdList.get(i), entityHandler));
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
            idListPointer++;
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
