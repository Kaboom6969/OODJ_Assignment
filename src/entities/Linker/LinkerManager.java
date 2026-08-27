package entities.Linker;

import Exceptions.ConvertMapExceptions.MapEmptyException;
import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Exceptions.LinkerExceptions.LinkerNotFoundException;
import Exceptions.LinkerExceptions.LinkerRepeatedException;
import Interfaces.ConvertToFileData;
import Tools.EntityConvertManager;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

public class LinkerManager implements ConvertToFileData
{
    private Class<? extends BaseEntity> classFirst;
    private Class<? extends BaseEntity> classSecond;
    public List<Linker> linkers;

    public LinkerManager()
    {
        linkers = new ArrayList<Linker>();
    }

    public LinkerManager(Class<? extends BaseEntity> first, Class<? extends BaseEntity> second)
    {
        classFirst = first;
        classSecond = second;
        this();
    }

    public LinkerManager(Class<? extends BaseEntity> first, Class<? extends BaseEntity> second,List<Linker> linkers)
    {
        classFirst = first;
        classSecond = second;
        this.linkers = linkers;
    }

    public void addLinker(Linker linker)
    {
        for(Linker l : linkers)
        {
            if(l.equals(linker)) throw new LinkerRepeatedException("Linker already exists");
        }
        linkers.add(linker);
    }

    public void removeLinker(Linker linker)
    {
        if (!linkers.remove(linker)) throw new LinkerNotFoundException("Linker not found");
    }

    public void setLinker(int index, Linker linker)
    {
        for (int i = 0; i < linkers.size(); i++)
        {
            if (i == index) continue;
            if (linkers.get(i).equals(linker)) throw new LinkerRepeatedException("Linker already exists");
        }
        linkers.set(index, linker);
    }


    public List<String> findBasedOnKey(String key)
    {
        String prefixFirst = EntityConvertManager.getPrefixMap().get(classFirst);
        String prefixSecond = EntityConvertManager.getPrefixMap().get(classSecond);
        String keyPrefix = FileDataHandler.prefixFinder(key);
        if (prefixFirst == null || prefixSecond == null) throw new MapEmptyException("ConvertMap is Empty");
        if (keyPrefix.equals(prefixFirst)) {return findBasedOnFirst(key);}
        else if (keyPrefix.equals(prefixSecond)) {return findBasedOnSecond(key);}
        throw new IdPrefixNotFoundException("key prefix is not matched in this linker!");
    }
    public List<String> findBasedOnFirst(String first)
    {
        List<String> ans = new ArrayList<>();
        for (Linker linker : linkers)
        {
            if (linker.first.equals(first))
            {
                ans.add(linker.second);
            }
        }
        if (ans.isEmpty()) return new ArrayList<>();
        return ans;
    }

    public List<String> findBasedOnSecond(String second)
    {
        List<String> ans = new ArrayList<>();
        for (Linker linker : linkers)
        {
            if (linker.second.equals(second))
            {
                ans.add(linker.first);
            }
        }
        if (ans.isEmpty()) return new ArrayList<>();
        return ans;
    }

    @Override
    public String toFileData()
    {
        List<String> ans = new ArrayList<>();
        for (Linker linker : linkers)
        {
            ans.add(linker.first + "|" + linker.second);
        }
        return String.join("\n", ans);
    }
}
