package Tools;

import Exceptions.IdPrefixNotMatchException;
import entities.BaseEntity;

public class EntityHandler
{
    public enum ReviewStyle
    {
        STRICT,NORMAL
    }
    public static EntityConvertManager ecm = new EntityConvertManager();
    private FileDataHandler fileDataHandler;

    public EntityHandler(FileDataHandler fileDataHandler)
    {
        this.fileDataHandler = fileDataHandler;
    }

    public BaseEntity getEntity(String id)
    {
        String[] data = fileDataHandler.getDataFromSpecificId(id);
        if (data == null) return null;
        try
        {
            return ecm.convertEntity(data);
        }
        catch (IdPrefixNotMatchException e)
        {
            System.err.println("Please Check Your File,The idPrefix didn't match!\n" + e.getMessage());
            return null;
        }
    }

    public BaseEntity getEntity(int rowInFile)
    {
        String[] data = fileDataHandler.getDataFromSpecificRow(rowInFile);
        String prefix = FileDataHandler.prefixFinder(data[0]);

        try
        {
            return ecm.convertEntity(data);
        }
        catch (IdPrefixNotMatchException e)
        {
            System.err.println("Please Check Your File,The idPrefix didn't match!\n" + e.getMessage());
            return null;
        }
    }


    public <T extends BaseEntity> void addEntity(T Entity,ReviewStyle style)
    {
        fileDataHandler.addData(Entity.toFileData());
    }

}
