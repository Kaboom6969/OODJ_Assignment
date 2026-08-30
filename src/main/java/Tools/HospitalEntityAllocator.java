package Tools;

import Exceptions.ConvertMapExceptions.MapEmptyException;
import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Exceptions.IdPrefixExceptions.IdPrefixNotMatchException;
import Exceptions.LinkerExceptions.LinkerNotFoundException;
import Interfaces.ConvertToFileData;
import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Tools.FileHandler.FileDataHandler;
import Tools.LinkerHandlers.LinkerHandler;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.*;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Doctor;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;
import jdk.jshell.spi.ExecutionControl;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalEntityAllocator
{
    public record FilePrefixMatchRecord(boolean isAllMatch,String propertiesName){}
    private Map<String, FileDataHandler> prefixFileMap;
    private Path linkerDirectory;
    private EntityFile adminEntityFile = new EntityFile(Admin.PREFIX);
    private EntityFile patientEntityFile = new EntityFile(Patient.PREFIX);
    private EntityFile doctorEntityFile = new EntityFile(DoctorToFile.PREFIX);

    private EntityFile departmentEntityFile = new EntityFile(DepartmentToFile.PREFIX);
    //private EntityHandler entityHandler;

    public HospitalEntityAllocator(Path linkerDirectory,Path adminPath, Path patientPath, Path doctorPath, Path DepartmentPath)
    {
        this.linkerDirectory = linkerDirectory;
        adminEntityFile.mainFile = new FileDataHandler(adminPath);
        patientEntityFile.mainFile = new FileDataHandler(patientPath);
        doctorEntityFile.mainFile = new FileDataHandler(doctorPath);
        departmentEntityFile.mainFile = new FileDataHandler(DepartmentPath);
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
        LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory,DoctorToFile.class,DepartmentToFile.class);
        List<String> doctorIds = new ArrayList<>();
        if (linkerHandler.getLinkers() != null) doctorIds = linkerHandler.getLinkers().findBasedOnKey(id);
        if(doctorIds == null) doctorIds = new ArrayList<>();
        return new Department(id,departmentEntityFile.mainFile,doctorIds,doctorEntityFile.mainFile);
    }

    public Doctor getDoctor(String id)
    {
        LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory,DoctorToFile.class,DepartmentToFile.class);
        List<String> departmentIds = new ArrayList<>();
        if (linkerHandler.getLinkers() != null) departmentIds = linkerHandler.getLinkers().findBasedOnKey(id);
        if(departmentIds == null) departmentIds = new ArrayList<>();
        if(departmentIds.size() > 1) throw new RuntimeException("err stub");
        return new Doctor(id,doctorEntityFile.mainFile,departmentIds.getFirst(),departmentEntityFile.mainFile);
    }

    public <T extends BusinessEntity<?> & OwnEntities & Linkable> void saveChanges(T businessEntity)
    {
        //linker first
        List<LinkerManager> linkerManagerList = businessEntity.getLinkerManager();
        String selfPrefix = businessEntity.getSelf().getIdPrefix();
        Class<? extends BaseEntity> selfClass = EntityConvertManager.getEntityMap().get(selfPrefix);
        Class<? extends BaseEntity> secondClass = null;
        for (LinkerManager linkerManager : linkerManagerList)
        {
            List<Linker> linkerList = linkerManager.getLinkers();
            LinkerManager.KeyLocation keyLocation = linkerManager.getKeyLocation(businessEntity.getSelf().getId());
            secondClass = linkerManager.getClassBasedOnKeyLocation(LinkerManager.switchKeyLocation(keyLocation));
            if (secondClass == null) throw new MapEmptyException("ConvertMap is Empty");
            LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory,selfClass,secondClass);
            linkerHandler.updatePartialLinker(linkerManager,businessEntity.getSelf().getId());
        }
        throw new RuntimeException("Test");
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
        String prefix = PrefixFinder.findPrefix(id);
        return new EntityHandler(prefixFileMap.get(prefix));
    }

}
class EntityFile
{
    public FileDataHandler mainFile;
    public final String prefix;

    public EntityFile(String prefix)
    {
        this.prefix = prefix;
    }
}
