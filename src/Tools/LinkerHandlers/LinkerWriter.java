package Tools.LinkerHandlers;

import Tools.EntityConvertManager;
import Tools.FileHandler.FileDataHandler;
import entities.Linker.LinkerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinkerWriter
{
    private LinkerManager linkerManager;
    private Path directory;
    private String fileName;

    public LinkerWriter(LinkerManager linkerManager, Path directory)
    {
        this.directory = directory;
        if (linkerManager == null) return;
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
            Files.write(directory.resolve(fileName), linkerManager.toFileData().getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
