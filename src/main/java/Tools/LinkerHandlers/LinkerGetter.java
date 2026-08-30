package Tools.LinkerHandlers;

import Exceptions.ConvertMapExceptions.MapEmptyException;
import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Tools.EntityConvertManager;
import Tools.FileHandler.FileDataHandler;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.BaseEntity;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LinkerGetter
{
    private Class<? extends BaseEntity> class1;
    private Class<? extends BaseEntity> class2;
    private final Path directory;
    private String fileName;
    private Path file;

    public LinkerGetter(Path directory, String fileName, Class<? extends BaseEntity> class1, Class<? extends BaseEntity> class2)
    {
        this.directory = directory;
        this.fileName = fileName;
        this.file = directory.resolve(fileName);
        this.class1 = class1;
        this.class2 = class2;
    }

    private boolean isRowLegal(String[] data)
    {
        return data.length == 2;
    }

    private String[] parseRow(String data)
    {
        String[] dataArr = data.split(FileDataHandler.DEFAULT_SEPARATOR_REGEX);
        if (!isRowLegal(dataArr)) throw new  IllegalArgumentException("Invalid row length");
        return dataArr;
    }
    public LinkerManager getAllLinkers()
    {
        LinkerManager linkerManager = new LinkerManager(class1, class2);
        updateLinker(linkerManager);
        return linkerManager;
    }

    public void updateLinker(LinkerManager manager)
    {
        manager.clear();
        try(Stream<String> stream = Files.lines(file))
        {
            stream.map(this::parseRow)
                    .map(line -> new Linker(line[0],line[1]))
                    .forEach(manager::addLinker);
        }
        catch (NoSuchFileException e)
        {
            return;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    public LinkerManager getLinkersBasedOnKey(String key)
    {
        LinkerManager manager = new LinkerManager(class1, class2);
        String prefixFirst = EntityConvertManager.getPrefixMap().get(class1);
        String prefixSecond = EntityConvertManager.getPrefixMap().get(class2);
        String keyPrefix = PrefixFinder.findPrefix(key);
        int order;
        if (prefixFirst == null || prefixSecond == null) throw new MapEmptyException("ConvertMap is Empty");
        if (keyPrefix.equals(prefixFirst)) {order = 0;}
        else if (keyPrefix.equals(prefixSecond)) {order = 1;}
        else order = -1;
        if (order == -1) throw new IdPrefixNotFoundException("key prefix is not matched in this linker!");
        try(Stream<String> stream = Files.lines(file))
        {
            stream.map(this::parseRow)
                    .filter(line -> line[order].equals(key))
                    .map(line -> new Linker(line[0],line[1]))
                    .forEach(manager::addLinker);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return manager;
    }
    public LinkerManager getLinkersBasedOnFirst(String first)
    {
        LinkerManager manager = new LinkerManager(class1, class2);
        try(Stream<String> stream = Files.lines(file))
        {
            stream.map(this::parseRow)
                    .filter(line -> line[0].equals(first))
                    .map(line -> new Linker(line[0],line[1]))
                    .forEach(manager::addLinker);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return manager;
    }

    public LinkerManager getLinkersBasedOnSecond(String second)
    {
        LinkerManager manager = new LinkerManager(class1, class2);
        try(Stream<String> stream = Files.lines(file))
        {
            stream.map(this::parseRow)
                    .filter(line -> line[1].equals(second))
                    .map(line -> new Linker(line[0],line[1]))
                    .forEach(manager::addLinker);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return manager;
    }
}
