package Iterator;

import Exceptions.LazyListEntityCantGetException;
import Tools.EntityHandler;
import Tools.FileDataHandler;
import entities.BaseEntity.BaseEntity;

import java.util.*;
import java.util.function.Consumer;

public class LazyEntityList<T extends BaseEntity> implements Iterable<T>
{
    private List<T> entityList;
    private List<String> entityIdList;
    private FileDataHandler entityFile;
    private EntityHandler entityHandler;




    public LazyEntityList(List<String> entityIdList, FileDataHandler entityFile)
    {
        this.entityList = new ArrayList<T>(Collections.nCopies(entityIdList.size(),null));
        this.entityIdList = entityIdList;
        this.entityFile = entityFile;
        this.entityHandler = new EntityHandler(entityFile);
    }

    public T get(int index)
    {
        if (index < 0 || index >= entityList.size())
            throw new IndexOutOfBoundsException("Index out of bounds exception");
        if (entityList.get(index) == null)
        {
            T entity = null;
            if ((entity = entityHandler.getEntity(entityIdList.get(index))) == null)
            {
                throw new LazyListEntityCantGetException("Entity not found,check your file!");
            }
            entityList.set(index, entity);
        }
        return entityList.get(index);
    }

    public T get(String id)
    {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
        int index = 0;
        if ((index = entityIdList.indexOf(id)) == -1) throw new NoSuchElementException("id should be in list!");
        return this.get(index);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new LazyEntityIterator<T>(entityList,entityIdList,entityFile);
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

class LazyEntityIterator<T extends BaseEntity> implements Iterator<T>
{
    private List<T> entityList;
    private List<String> entityIdList;
    private EntityHandler entityHandler;

    private int idListPointer;

    LazyEntityIterator()
    {
        idListPointer = 0;
    }

    public LazyEntityIterator(List<T> entityList,List<String> entityIdList, FileDataHandler entityFile)
    {
        this();
        this.entityList = entityList;
        this.entityIdList = entityIdList;
        entityHandler = new EntityHandler(entityFile);
    }
    @Override
    public boolean hasNext()
    {
        return idListPointer < entityIdList.size();
    }

    @Override
    public T next()
    {
        if (hasNext())
        {
            if(entityList.get(idListPointer) == null)
            {
                entityList.set(idListPointer,entityHandler.getEntity(entityIdList.get(idListPointer)));
            }
            idListPointer++;
            return entityList.get(idListPointer-1);
        }
        throw new NoSuchElementException();
    }


    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
