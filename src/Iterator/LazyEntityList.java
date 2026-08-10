package Iterator;

import Tools.EntityHandler;
import Tools.FileDataHandler;
import entities.BaseEntity;

import java.util.*;
import java.util.function.Consumer;

public class LazyEntityList<T extends BaseEntity> implements Iterable<T>
{
    private List<T> entityList;
    private List<String> entityIdList;
    private FileDataHandler entityFile;

    private int idListPointer;

    private LazyEntityList()
    {
        idListPointer = 0;
    }

    public LazyEntityList(List<String> entityIdList, FileDataHandler entityFile)
    {
        this();
        this.entityList = new ArrayList<T>(Collections.nCopies(entityIdList.size(),null));
        this.entityIdList = entityIdList;
        this.entityFile = entityFile;
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
