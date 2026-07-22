package Tools;

import Exceptions.IdPrefixNotMatchException;
import entities.BaseEntity;
import entities.EntityConvertManager;

import java.nio.file.Path;
import java.util.function.Function;

public class EntityHandler
{
    public static EntityConvertManager ecm = new EntityConvertManager();
    private FileDataHandler fileDataHandler;

    public EntityHandler(FileDataHandler fileDataHandler)
    {
        this.fileDataHandler = fileDataHandler;
    }


    public <T extends BaseEntity> T getEntity(int rowInFile)
    {

        String[] data = fileDataHandler.getDataFromSpecificRow(rowInFile);
        String prefix = FileDataHandler.prefixFinder(data[0]);

        try
        {
            return (T) ecm.convertMap.get(prefix).apply(data);
        }
        catch (IdPrefixNotMatchException e)
        {
            System.err.println("Please Check Your File,The idPrefix didn't match!\n" + e.getMessage());
            return null;
        }
    }
}
