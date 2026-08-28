package Tools.LinkerHandlers;

import entities.BaseEntity.BaseEntity;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.nio.file.Files;
import java.nio.file.Path;

public class LinkerHandler
{
    private Class<? extends BaseEntity> class1;
    private Class<? extends BaseEntity> class2;
    private LinkerManager  linkerManager;
    private LinkerGetter linkerGetter;
    private LinkerWriter linkerWriter;
    private String fileName;



    public LinkerHandler(Path directory, Class<? extends BaseEntity> linkClass1,Class<? extends BaseEntity> linkClass2 )
    {
        LinkerFileNameGetter.FileNamePack fileNamePack = LinkerFileNameGetter.getFileName(linkClass1,linkClass2);
        fileName = fileNamePack.fileName();
        if (fileNamePack.orderChanged()) {class1 = linkClass2; class2 = linkClass1;}
        else {class1 = linkClass1; class2 = linkClass2;}
        linkerGetter = new LinkerGetter(directory,fileName,class1,class2);
        linkerManagerInit();
        linkerWriter = new LinkerWriter(linkerManager,directory,fileName);
        if (Files.notExists(directory.resolve(fileName))) linkerWriter.createFile();
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
        else linkerManager = new LinkerManager(class1,class2);

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
