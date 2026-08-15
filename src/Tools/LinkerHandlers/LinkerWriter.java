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
    private LinkerManager linkerManager;
    private Path directory;
    private String fileName;

    public LinkerWriter(LinkerManager linkerManager, Path directory, String fileName)
    {
        this.directory = directory;
        if (linkerManager == null)
        {
            this.fileName = fileName;
            return;
        }
        setLinkerManager(linkerManager);
    }

    public void setLinkerManager(LinkerManager linkerManager)
    {
        this.linkerManager = linkerManager;
        this.fileName = LinkerFileNameGetter.getFileName(linkerManager.linkers.getFirst());
    }

    public void saveLinker()
    {
        try
        {
            if (linkerManager == null) Files.write(directory.resolve(fileName),new byte[0], StandardOpenOption.CREATE);
            else  Files.write(directory.resolve(fileName),linkerManager.toFileData().getBytes(), StandardOpenOption.CREATE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
