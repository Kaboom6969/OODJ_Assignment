package Tools;

import Exceptions.EntityNotFoundException;
import Exceptions.EntityNotMatchException;
import Exceptions.IdPrefixNotMatchException;
import Exceptions.EntityRepeatedException;
import Interfaces.ConvertToFileData;
import entities.BaseEntity;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public <T extends BaseEntity> List<T> getAllEntities()
    {
        List<T> allEntities = new ArrayList<>();
        String[][] allData = fileDataHandler.getAllData();
        for (String[] allDatum : allData)
        {
            allEntities.add(ecm.convertEntity(allDatum));
        }
        return allEntities;
    }

    public <T extends BaseEntity> T getEntity(String id)
    {
        String[] data = fileDataHandler.getDataInformationFromSpecificId(id).data();
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

    public <T extends BaseEntity & ConvertToFileData> void addEntity(T Entity) throws EntityRepeatedException
    {
        addEntity(Entity, ReviewStyle.STRICT);
    }

    public <T extends BaseEntity & ConvertToFileData> void addEntity(T Entity, ReviewStyle style) throws EntityRepeatedException
    {
        if (style == ReviewStyle.STRICT)
        {
            if (fileDataHandler.getDataInformationFromSpecificId(Entity.getId()).data() != null)
            {
                throw new EntityRepeatedException("Entity already exists!");
            }
        }
        fileDataHandler.addData(Entity.toFileData());
    }

    public <T extends BaseEntity & ConvertToFileData> void updateEntity(T Entity) throws EntityNotFoundException
    {
        Integer row = fileDataHandler.getDataInformationFromSpecificId(Entity.getId()).row();
        if (row == null) throw new EntityNotFoundException("Entity in file is not founded!");
        fileDataHandler.updateData(Entity.toFileData(), row);
    }

    public <T extends BaseEntity> void deleteEntity(T Entity, MatchLogic matchLogic) throws EntityNotFoundException, EntityNotMatchException
    {
        FileDataHandler.DataInformation entityDataInFile = fileDataHandler.getDataInformationFromSpecificId(Entity.getId());
        if (entityDataInFile.isEmpty()) throw new EntityNotFoundException("Entity in file is not founded!");
        switch (matchLogic)
        {
            case CODE_ONLY: break;
            case EXACT_DATA: break;


        }
        fileDataHandler.deleteRow(entityDataInFile.row());
    }

}
