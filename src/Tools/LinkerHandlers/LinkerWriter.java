package Tools.LinkerHandlers;

import Tools.EntityConvertManager;
import Tools.FileHandler.FileDataHandler;
import entities.Linker.LinkerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LinkerWriter
{
    private final LinkerManager linkerManager;
    private Path directory;
    private String fileName;

    public LinkerWriter(LinkerManager linkerManager, Path directory, String fileName)
    {
        this.directory = directory;
        this.linkerManager = linkerManager;
        if (linkerManager == null || linkerManager.linkers.isEmpty())
        {
            this.fileName = fileName;
            return;
        }
        this.fileName = LinkerFileNameGetter.getFileName(linkerManager.linkers.getFirst());
    }

    public void createFile()
    {
        try
        {
            Files.write(directory.resolve(fileName),new byte[0], StandardOpenOption.CREATE);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void saveLinker()
    {
        try
        {
            Files.write(directory.resolve(fileName),linkerManager.toFileData().getBytes(), StandardOpenOption.CREATE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
