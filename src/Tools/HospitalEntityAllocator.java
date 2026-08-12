package Tools;

import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Exceptions.IdPrefixExceptions.IdPrefixNotMatchException;
import Interfaces.ConvertToFileData;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.*;
import entities.BusinessEntity.Department;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalEntityAllocator
{
    public record FilePrefixMatchRecord(boolean isAllMatch,String propertiesName){}
    private Map<String, FileDataHandler> prefixFileMap;
    private EntityFile adminEntityFile = new EntityFile(Admin.PREFIX);
    private EntityFile patientEntityFile = new EntityFile(Patient.PREFIX);
    private EntityFile doctorEntityFile = new EntityFile(DoctorToFile.PREFIX);

    private EntityFile departmentEntityFile = new EntityFile(DepartmentToFile.PREFIX);
    //private EntityHandler entityHandler;

    public HospitalEntityAllocator(Path adminPath, Path patientPath, Path doctorPath, Path DepartmentPath, Path DepartmentDoctorLinkPath)
    {
        adminEntityFile.mainFile = new FileDataHandler(adminPath);
        patientEntityFile.mainFile = new FileDataHandler(patientPath);
        doctorEntityFile.mainFile = new FileDataHandler(doctorPath);
        departmentEntityFile.mainFile = new FileDataHandler(DepartmentPath);
        departmentEntityFile.linkFiles.set(0,new FileDataHandler(DepartmentDoctorLinkPath));
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
                String prefixInFile = entityFile.mainFile.findPrefixStrict();
                if (!prefixInFile.equals(entityFile.prefix))
                {
                    System.err.printf
                            ("Prefix in file: %s is not match the prefix:%s,prefix in file:%s\n",
                                entityFile.mainFile.getFile().getName(),
                                entityFile.prefix,
                                prefixInFile
                            );
                    prefixFileMap = null;
                    return new FilePrefixMatchRecord(false, field.getName());
                }
                prefixFileMap.put(entityFile.prefix, entityFile.mainFile);
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

    public Department getDepartment(String id)
    {
        List<String> doctorIds = new EntityHandler(departmentEntityFile.linkFiles.getFirst()).getLinker().findBasedOnSecond(id);
        return new Department(id,departmentEntityFile.mainFile,doctorIds,doctorEntityFile.mainFile);
    }

    public <T extends BaseEntity> T getEntity(String id)
    {
        EntityHandler entityHandler = getEntityHandler(id);
        if (entityHandler == null) return null;
        return entityHandler.getEntity(id);
    }

    public <T extends BaseEntity & ConvertToFileData> void addEntity (T entity) throws EntityRepeatedException
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

    public <T extends BaseEntity & ConvertToFileData> void updateEntity(T entity) throws EntityNotFoundException
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
    public FileDataHandler mainFile;
    public List<FileDataHandler> linkFiles;
    public final String prefix;

    public EntityFile(String prefix)
    {
        this.prefix = prefix;
    }
}
