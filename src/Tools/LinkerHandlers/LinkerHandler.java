package Tools.LinkerHandlers;

import Tools.FileHandler.FileDataHandler;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LinkerHandler
{
    private LinkerGetter linkerGetter;
    private LinkerWriter linkerWriter;



    public LinkerHandler(Path directory)
    {
        linkerGetter = new LinkerGetter(directory);
        linkerWriter = new LinkerWriter(null,directory);
    }

    public LinkerManager getLinkers()
    {
        return linkerGetter.getAllLinkers();
    }
}
