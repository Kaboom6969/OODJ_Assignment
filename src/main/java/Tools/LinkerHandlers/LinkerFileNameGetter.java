package Tools.LinkerHandlers;

import Tools.EntityConvertManager;
import Tools.FileHandler.FileDataHandler;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.BaseEntity;
import entities.Linker.Linker;

import java.util.Arrays;

public class LinkerFileNameGetter
{
    public record FileNamePack(String fileName,boolean orderChanged){}
    public static String getFileName(Linker linker)
    {
        String firstId = linker.first;
        String SecondId = linker.second;
        String firstPrefix = PrefixFinder.findPrefix(firstId);
        String secondPrefix = PrefixFinder.findPrefix(SecondId);
        String firstClassName = EntityConvertManager.getEntityMap().get(firstPrefix).getSimpleName();
        String secondClassName = EntityConvertManager.getEntityMap().get(secondPrefix).getSimpleName();
        return firstClassName + "_" + secondClassName + "_" + "linker.txt";
    }

    public static FileNamePack getFileName(Class<? extends BaseEntity> classOne,Class<? extends BaseEntity> classTwo)
    {
        String[] className = new String[2];
        className[0] = classOne.getSimpleName();
        className[1] = classTwo.getSimpleName();
        Arrays.sort(className);
        return new FileNamePack(className[0]+ "_" + className[1] + "_" + "linker.txt",!classOne.getSimpleName().equals(className[0]));

    }
}
