package Tools;

import Exceptions.EntityNotFoundException;
import Exceptions.EntityNotMatchException;
import Exceptions.IdPrefixNotMatchException;
import Exceptions.EntityRepeatedException;
import entities.BaseEntity;
import jdk.jshell.spi.ExecutionControl;

import java.util.Arrays;

public class EntityHandler
{
    public enum ReviewStyle
    {
        STRICT,NORMAL
    }
    public enum MatchLogic
    {
        CODE_ONLY, EXACT_DATA
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

    public <T extends BaseEntity> void addEntity(T Entity) throws IdRepeatedException
    {
        addEntity(Entity, ReviewStyle.STRICT);
    }

    public <T extends BaseEntity> void addEntity(T Entity, ReviewStyle style) throws IdRepeatedException
    {
        if (style == ReviewStyle.STRICT)
        {
            if (fileDataHandler.getDataFromSpecificId(Entity.getId()) != null)
            {
                throw new IdRepeatedException("Entity already exists!");
            }
        }
        fileDataHandler.addData(Entity.toFileData());
    }

    public <T extends BaseEntity> void updateEntity(T Entity) throws IdNotFoundException
    {
        Integer row = fileDataHandler.getRowFromSpecificId(Entity.getId());
        if (row == null) throw new IdNotFoundException("Entity in file is not founded!");
        fileDataHandler.updateData(Entity.toFileData(), row);
    }

}
