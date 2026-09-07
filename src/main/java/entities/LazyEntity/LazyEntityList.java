package entities.LazyEntity;

import Exceptions.LazyEntityExceptions.LazyEntityListExceptions.LazyEntityListEntityCantGetException;
import Exceptions.LazyEntityExceptions.LazyEntityListExceptions.LazyEntityListEntityRepeatedException;
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
    private record RepeatReport (boolean isRepeated,List<List<Integer>> RepeatedIdIndex){}


    public int size()
    {
        return lazyEntityList.size();
    }

    public boolean isChanged(int index)
    {
        return lazyEntityList
                .get(index)
                .isSelfAlrChanged();
    }

    public void markAsSaved(int index)
    {
        lazyEntityList
                .get(index)
                .updateBackup();
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

    public LazyEntityList(List<String> entityIdList, EntityHandler entityFile)
    {
        lazyEntityList = new ArrayList<LazyEntity<T>>();
        this.entityHandler = entityFile;
        for (String entityId : entityIdList)
        {
            lazyEntityList.add(new LazyEntity<>(entityId, entityHandler));
        }
    }

    public List<String> getIds()
    {
        List<String> ids = new ArrayList<>();
        for(LazyEntity<T> lazyEntity : lazyEntityList)
        {
            ids.add(lazyEntity.getId());
        }
        return ids;
    }
    private RepeatReport repeatCheck()
    {
        HashMap<String,List<Integer>> repeatedMap = new HashMap<>();
        boolean isRepeated = false;
        for (int i = 0; i<lazyEntityList.size(); i++)
        {
            String entityId = lazyEntityList.get(i).getId();
            if (!repeatedMap.containsKey(entityId))
            {
                repeatedMap.put(entityId, new ArrayList<>());
            }
            else {isRepeated = true;}
            repeatedMap.get(entityId).add(i);
        }
        return new RepeatReport(isRepeated,new ArrayList<>(repeatedMap.values().stream().filter(list -> list.size() > 1).toList()));
    }

    private boolean isRepeated(String entityId,int excludedIndex)
    {
        for (LazyEntity<T> lazyEntity : lazyEntityList)
        {
            if (lazyEntity.getId().equals(entityId) && !(lazyEntityList.indexOf(lazyEntity)==(excludedIndex)))
            {
                return true;
            }
        }
        return false;
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
        if (isRepeated(id,-1)) throw new LazyEntityListEntityRepeatedException("repeated entity with id " + id);
        lazyEntityList.add(new LazyEntity<>(id, entityHandler));
    }

    public void add(LazyEntity<T> entity)
    {
        if (isRepeated(entity.getId(),-1)) throw new LazyEntityListEntityRepeatedException("repeated entity with id " + entity.getId());
        lazyEntityList.add(entity);
    }

    public void add(T entity)
    {
        if (isRepeated(entity.getId(),-1)) throw new LazyEntityListEntityRepeatedException("repeated entity with id " + entity.getId());
        lazyEntityList.add(new LazyEntity<>(entity,entityHandler));
    }
    public void set(int index,String id)
    {
        if (isRepeated(id,index)) throw new LazyEntityListEntityRepeatedException("repeated entity with id " + id);
        lazyEntityList.set(index,new LazyEntity<>(id,entityHandler));
    }
    public void set(int index, LazyEntity<T> entity)
    {
        if (isRepeated(entity.getId(),index)) throw new LazyEntityListEntityRepeatedException("repeated entity with id " + entity.getId());
        lazyEntityList.set(index, entity);
    }

    public void set(int index, T entity)
    {
        if (isRepeated(entity.getId(),index)) throw new LazyEntityListEntityRepeatedException("repeated entity with id " + entity.getId());
        lazyEntityList.set(index,new LazyEntity<>(entity,entityHandler));
    }

    public void remove(LazyEntity<T> entity)
    {
        lazyEntityList.remove(entity);
    }

    public void remove(T entity)
    {
        for (int i = 0; i<lazyEntityList.size(); i++)
        {
            if (lazyEntityList.get(i).getSelf().equals(entity)) lazyEntityList.remove(i);
        }
    }

    public void remove(String id)
    {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
        for (int i = 0; i<lazyEntityList.size(); i++)
        {
            if (lazyEntityList.get(i).getSelf().getId().equals(id))
            {
                lazyEntityList.remove(i); break;
            }
        }
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
