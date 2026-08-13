package Tools.LinkerHandlers;

import Tools.EntityConvertManager;
import Tools.FileHandler.FileDataHandler;
import entities.Linker.Linker;

public class LinkerFileNameGetter
{
    public static String getFileName(Linker linker)
    {
        String firstId = linker.first;
        String SecondId = linker.second;
        String firstPrefix = FileDataHandler.prefixFinder(firstId);
        String secondPrefix = FileDataHandler.prefixFinder(SecondId);
        String firstClassName = EntityConvertManager.getEntityMap().get(firstPrefix).getSimpleName();
        String secondClassName = EntityConvertManager.getEntityMap().get(secondPrefix).getSimpleName();
        return firstClassName + "_" + secondClassName + "_" + "linker.txt";
    }
}
