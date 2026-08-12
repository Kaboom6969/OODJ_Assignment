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



    public LinkerHandler(Path file)
    {
        linkerGetter = new LinkerGetter(file);
        linkerWriter = new LinkerWriter(null,file.getParent().toAbsolutePath());
    }

    public LinkerManager getLinkers()
    {
        return linkerGetter.getAllLinkers();
    }
}
