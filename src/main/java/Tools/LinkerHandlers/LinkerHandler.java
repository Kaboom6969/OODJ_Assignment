package Tools.LinkerHandlers;

import Exceptions.LinkerExceptions.LinkerRepeatedException;
import entities.BaseEntity.BaseEntity;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public boolean addLinker(Linker linker)
    {
        return linkerManager.addLinker(linker);
    }



    public void updatePartialLinker(LinkerManager linkerManager, String mainKey)
    {
        LinkerManager.KeyLocation keyLocation = linkerManager.getKeyLocation(mainKey);
        LinkerManager linkerManagerFiltered = linkerManager.filterBasedOnKey(mainKey);
        List<Linker> linkersToUpdate = linkerManagerFiltered.getLinkers();
        List<Linker> linkers = new ArrayList<>(this.getLinkers().getLinkers());
        Set<Integer> indexForAll = new HashSet<>();
        Set<Integer> indexForAlreadyExist = new HashSet<>();
        Set<Integer> indexForNew;
        for (Linker linker : linkers)
        {
            if (linker.getData(keyLocation).equals(mainKey))
            {
                if(!linkersToUpdate.contains(linker)) this.linkerManager.removeLinker(linker);
                else indexForAlreadyExist.add(linkersToUpdate.indexOf(linker));
            }
        }
        for(int i = 0; i < linkersToUpdate.size(); i++) {indexForAll.add(i);}
        indexForAll.removeAll(indexForAlreadyExist); indexForNew = indexForAll;
        for (int index : indexForNew)
        {
            this.linkerManager.addLinker(linkersToUpdate.get(index));
        }
    }




}
