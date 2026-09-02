package entities.Linker;

import Exceptions.ConvertMapExceptions.MapEmptyException;
import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Exceptions.IdPrefixExceptions.IdPrefixNotMatchException;
import Exceptions.LinkerExceptions.LinkerNotFoundException;
import Exceptions.LinkerExceptions.LinkerRepeatedException;
import Interfaces.ConvertToFileData;
import Tools.EntityConvertManager;
import Tools.LinkerHandlers.LinkerFileNameGetter;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.BaseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkerManager implements ConvertToFileData
{
    public enum KeyLocation
    {
        FIRST,SECOND, NOT_FOUND
    }
    private Class<? extends BaseEntity> classFirst;
    private Class<? extends BaseEntity> classSecond;
    private List<Linker> linkers;

    public List<Linker> getLinkers()
    {
        return Collections.unmodifiableList(linkers);
    }

    private LinkerManager()
    {
        linkers = new ArrayList<Linker>();
    }

    public LinkerManager(Class<? extends BaseEntity> first, Class<? extends BaseEntity> second)
    {
        this();
        classSort(first, second);
    }

    public LinkerManager(Class<? extends BaseEntity> first, Class<? extends BaseEntity> second,List<Linker> linkers)
    {
        classSort(first, second);
        this.linkers = linkers;
    }

    private void classSort(Class<? extends BaseEntity> first, Class<? extends BaseEntity> second)
    {
        LinkerFileNameGetter.FileNamePack fileNamePack = LinkerFileNameGetter.getFileName(first, second);
        if (fileNamePack.orderChanged())
        {
            classFirst = second;
            classSecond = first;
        }
        else
        {
            classFirst = first;
            classSecond = second;
        }
    }

    private KeyLocation linkerPrefixCheck(Linker linker)
    {
        String classFirstPrefix = EntityConvertManager.getPrefixMap().get(classFirst);
        String classSecondPrefix = EntityConvertManager.getPrefixMap().get(classSecond);
        if (!PrefixFinder.findPrefix(linker.first).equals(PrefixFinder.findPrefix(classFirstPrefix))) return KeyLocation.FIRST;
        if (!PrefixFinder.findPrefix(linker.second).equals(PrefixFinder.findPrefix(classSecondPrefix))) return KeyLocation.SECOND;
        return KeyLocation.NOT_FOUND;
    }

    private Linker linkerAutoCheck(Linker linker)
    {
        if (linkerPrefixCheck(linker) != KeyLocation.NOT_FOUND)
        {
            linker = linker.swap();
            if (linkerPrefixCheck(linker) != KeyLocation.NOT_FOUND)
            {
                throw new IdPrefixNotMatchException("Id prefix in linker is not match!");
            }
        }
        return linker;
    }

    public boolean addLinker(Linker linker)
    {
        linker = linkerAutoCheck(linker);
        for(Linker l : linkers)
        {
            if(l.equals(linker)) return false;
        }
        linkers.add(linker);
        return true;
    }

    public void removeLinker(Linker linker)
    {
        linker = linkerAutoCheck(linker);
        if (!linkers.remove(linker)) throw new LinkerNotFoundException("Linker not found");
    }

    public void setLinker(int index, Linker linker)
    {
        linker = linkerAutoCheck(linker);
        for (int i = 0; i < linkers.size(); i++)
        {
            if (i == index) continue;
            if (linkers.get(i).equals(linker)) throw new LinkerRepeatedException("Linker already exists");
        }
        linkers.set(index, linker);
    }

    public void clear()
    {
        linkers.clear();
    }
    public LinkerManager filterBasedOnKey(String key)
    {
        KeyLocation keyLocation = getKeyLocation(key);
        List<Linker> linkersAfterFiltered = new ArrayList<>();
        for (Linker linker : linkers)
        {
            if (linker.getData(keyLocation).equals(key)) linkersAfterFiltered.add(linker);
        }
        return new LinkerManager(classFirst, classSecond, linkersAfterFiltered);
    }
    public KeyLocation getKeyLocation(String key)
    {
        String prefixFirst = EntityConvertManager.getPrefixMap().get(classFirst);
        String prefixSecond = EntityConvertManager.getPrefixMap().get(classSecond);
        String keyPrefix = PrefixFinder.findPrefix(key);
        if (prefixFirst == null || prefixSecond == null) throw new MapEmptyException("ConvertMap is Empty");
        if (keyPrefix.equals(prefixFirst)) {return KeyLocation.FIRST;}
        else if (keyPrefix.equals(prefixSecond)) {return KeyLocation.SECOND;}
        else return KeyLocation.NOT_FOUND;
    }
    public List<String> findBasedOnKey(String key)
    {
        KeyLocation keyLocation = getKeyLocation(key);
        if (keyLocation == KeyLocation.FIRST) {return findBasedOnFirst(key);}
        else if (keyLocation == KeyLocation.SECOND) {return findBasedOnSecond(key);}
        else if (keyLocation == KeyLocation.NOT_FOUND) throw new IdPrefixNotFoundException("key prefix is not matched in this linker!");
        else throw new RuntimeException("Unrecognized KeyLocation");
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
    public static KeyLocation switchKeyLocation(KeyLocation keyLocation)
    {
        switch (keyLocation)
        {
            case FIRST: return KeyLocation.SECOND;
            case SECOND: return KeyLocation.FIRST;
            case NOT_FOUND: return KeyLocation.NOT_FOUND;
            default: throw new RuntimeException("Unrecognized KeyLocation");
        }
    }

    public Class<? extends BaseEntity> getClassBasedOnKeyLocation(KeyLocation keyLocation)
    {
        switch (keyLocation)
        {
            case FIRST :
                return classFirst;
            case SECOND :
                return classSecond;
            case NOT_FOUND :
                throw new IdPrefixNotFoundException
                (
                    "Please give a workable Location"
                );
            default: throw new RuntimeException("Unrecognized KeyLocation");
        }
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
