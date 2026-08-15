package Tools.LinkerHandlers;

import Tools.FileHandler.FileDataHandler;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LinkerGetter
{
    private final Path file;
    public LinkerGetter(Path file)
    {
        this.file = file;
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
        LinkerManager manager = new LinkerManager();
        try(Stream<String> stream = Files.lines(file))
        {
            stream.map(this::parseRow)
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
        LinkerManager manager = new LinkerManager();
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
        LinkerManager manager = new LinkerManager();
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
