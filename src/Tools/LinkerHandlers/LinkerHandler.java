package Tools.LinkerHandlers;

import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BaseEntity;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LinkerHandler
{
    private LinkerManager  linkerManager;
    private LinkerGetter linkerGetter;
    private LinkerWriter linkerWriter;
    private String fileName;



    public LinkerHandler(Path directory, Class<? extends BaseEntity> linkClass1,Class<? extends BaseEntity> linkClass2 )
    {
        fileName = LinkerFileNameGetter.getFileName(linkClass1,linkClass2);
        linkerGetter = new LinkerGetter(directory,fileName);
        linkerManagerInit();
        linkerWriter = new LinkerWriter(linkerManager,directory,fileName);
    }

    public LinkerWriter getWriter() {return linkerWriter;}

    public LinkerGetter getGetter()
    {
        return linkerGetter;
    }

    public LinkerManager getLinkers()
    {
        linkerGetter.updateLinker(linkerManager);
        return linkerManager;
    }

    public void linkerManagerInit()
    {
        if (linkerGetter.getAllLinkers() != null)
        {
            linkerManager = linkerGetter.getAllLinkers();
        }
        linkerManager = new LinkerManager();
    }

    public void saveLinkers()
    {
        linkerWriter.saveLinker();
    }

    public void addLinker(Linker linker)
    {
        linkerManager.addLinker(linker);
    }

}
