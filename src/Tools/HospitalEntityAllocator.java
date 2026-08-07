package Tools;

import Exceptions.*;
import entities.Admin;
import entities.BaseEntity;
import entities.Doctor;
import entities.Patient;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class HospitalEntityAllocator
{
    public record FilePrefixMatchRecord(boolean isAllMatch,String propertiesName){}
    private Map<String,FileDataHandler> prefixFileMap;
    private EntityFile adminEntityFile = new EntityFile(Admin.PREFIX);
    private EntityFile patientEntityFile = new EntityFile(Patient.PREFIX);
    private EntityFile doctorEntityFile = new EntityFile(Doctor.PREFIX);
    //private EntityHandler entityHandler;

    public HospitalEntityAllocator(Path adminPath, Path patientPath, Path doctorPath)
    {
        adminEntityFile.fileDataHandler = new FileDataHandler(adminPath);
        patientEntityFile.fileDataHandler = new FileDataHandler(patientPath);
        doctorEntityFile.fileDataHandler = new FileDataHandler(doctorPath);
        prefixFileMap = new HashMap<String, FileDataHandler>();
        FilePrefixMatchRecord filePrefixMatchRecord = classInit();
        if (!filePrefixMatchRecord.isAllMatch())
        {
            throw new IllegalStateException("Prefix check failed,"+filePrefixMatchRecord.propertiesName+" problem");
        }
    }

    private FilePrefixMatchRecord classInit()
    {
        for(Field field : this.getClass().getDeclaredFields())
        {
            if (field.getType() != EntityFile.class) continue;
            field.setAccessible(true);
            EntityFile entityFile = null;
            try
            {
                entityFile = (EntityFile) field.get(this);
            }
            catch (IllegalAccessException e)
            {
                System.err.printf("%s's fileDataHandler is inaccessible!",field.getName());
                return new FilePrefixMatchRecord(false, field.getName());
            }
            try
            {
                String prefixInFile = entityFile.fileDataHandler.findPrefixStrict();
                if (!prefixInFile.equals(entityFile.prefix))
                {
                    System.err.printf
                            ("Prefix in file: %s is not match the prefix:%s,prefix in file:%s\n",
                                entityFile.fileDataHandler.getFile().getName(),
                                entityFile.prefix,
                                prefixInFile
                            );
                    prefixFileMap = null;
                    return new FilePrefixMatchRecord(false, field.getName());
                }
                prefixFileMap.put(entityFile.prefix, entityFile.fileDataHandler);
            }
            catch (NullPointerException e)
            {
                System.err.printf("%s's properties or it self is null!",field.getName());
                prefixFileMap = null;
                return new FilePrefixMatchRecord(false, field.getName());
            }
            catch (IdPrefixNotMatchException e)
            {
                System.err.printf("%s's file's prefix inside is not matched!",field.getName());
                prefixFileMap = null;
                return new FilePrefixMatchRecord(false, field.getName());
            }
            catch (IdPrefixNotFoundException e)
            {
                System.err.printf("%s's file's prefix not found!",field.getName());
                prefixFileMap = null;
                return new FilePrefixMatchRecord(false, field.getName());
            }


        }
        return new FilePrefixMatchRecord(true, null);
    }

    public <T extends BaseEntity> T getEntity(String id)
    {
        EntityHandler entityHandler = getEntityHandler(id);
        if (entityHandler == null) return null;
        return entityHandler.getEntity(id);
    }

    public void addEntity (BaseEntity entity) throws EntityRepeatedException
    {
        EntityHandler entityHandler = getEntityHandler(entity.getId());
        if (entityHandler == null) throw new IdPrefixNotFoundException(entity.getId());
        entityHandler.addEntity(entity);
    }

    public void removeEntity(BaseEntity entity) throws EntityNotMatchException, EntityNotFoundException
    {
        EntityHandler entityHandler = getEntityHandler(entity.getId());
        if (entityHandler == null) throw new IdPrefixNotFoundException(entity.getId());
        entityHandler.deleteEntity(entity, EntityHandler.MatchLogic.CODE_ONLY);
    }

    public void updateEntity(BaseEntity entity) throws EntityNotFoundException
    {
        EntityHandler entityHandler = getEntityHandler(entity.getId());
        if (entityHandler == null) throw new IdPrefixNotFoundException(entity.getId());
        entityHandler.updateEntity(entity);
    }

    private EntityHandler getEntityHandler(String id)
    {
        if (id == null) return null;
        String prefix = FileDataHandler.prefixFinder(id);
        return new EntityHandler(prefixFileMap.get(prefix));
    }

}
class EntityFile
{
    public FileDataHandler fileDataHandler;
    public final String prefix;

    public EntityFile(String prefix)
    {
        this.prefix = prefix;
    }
}
