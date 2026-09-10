package Tools;

import Exceptions.ConvertMapExceptions.MapEmptyException;
import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Interfaces.*;
import Tools.FileHandler.FileDataHandler;
import Tools.LinkerHandlers.LinkerHandler;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.*;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Doctor;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.*;

public class HospitalEntityAllocator
{
    public record FilePrefixMatchRecord(boolean isAllMatch,String propertiesName){}
    private Map<String, FileDataHandler> prefixFileMap;
    private Path linkerDirectory;
    private Path entityDirectory;

    //private EntityHandler entityHandler;

    public HospitalEntityAllocator(Path linkerDirectory,Path entityDirectory)
    {
        this.linkerDirectory = linkerDirectory;
        this.entityDirectory = entityDirectory;
        prefixFileMap = new HashMap<String, FileDataHandler>();
        classInit();
//        if (!filePrefixMatchRecord.isAllMatch())
//        {
//            throw new IllegalStateException("Prefix check failed,"+filePrefixMatchRecord.propertiesName+" problem");
//        }
    }
    private void classInit()
    {
        for (Map.Entry<String,Class<? extends BaseEntity>> entityMap : EntityConvertManager.getEntityMap().entrySet())
        {
            prefixFileMap.put(entityMap.getKey(),new FileDataHandler(entityDirectory.resolve(entityMap.getValue().getSimpleName() + ".txt")));
        }
    }

//    private FilePrefixMatchRecord classInit()
//    {
//        for(Field field : this.getClass().getDeclaredFields())
//        {
//            if (field.getType() != EntityFile.class) continue;
//            field.setAccessible(true);
//            EntityFile entityFile = null;
//            try
//            {
//                entityFile = (EntityFile) field.get(this);
//            }
//            catch (IllegalAccessException e)
//            {
//                System.err.printf("%s's fileDataHandler is inaccessible!",field.getName());
//                return new FilePrefixMatchRecord(false, field.getName());
//            }
//            try
//            {
//                String prefixInFile = entityFile.mainFile.findPrefixStrict();
//                if (!prefixInFile.equals(entityFile.prefix))
//                {
//                    System.err.printf
//                            ("Prefix in file: %s is not match the prefix:%s,prefix in file:%s\n",
//                                entityFile.mainFile.getFile().getName(),
//                                entityFile.prefix,
//                                prefixInFile
//                            );
//                    prefixFileMap = null;
//                    return new FilePrefixMatchRecord(false, field.getName());
//                }
//                prefixFileMap.put(entityFile.prefix, entityFile.mainFile);
//            }
//            catch (NullPointerException e)
//            {
//                System.err.printf("%s's properties or it self is null!",field.getName());
//                prefixFileMap = null;
//                return new FilePrefixMatchRecord(false, field.getName());
//            }
//            catch (IdPrefixNotMatchException e)
//            {
//                System.err.printf("%s's file's prefix inside is not matched!",field.getName());
//                prefixFileMap = null;
//                return new FilePrefixMatchRecord(false, field.getName());
//            }
//            catch (IdPrefixNotFoundException e)
//            {
//                System.err.printf("%s's file's prefix not found!",field.getName());
//                prefixFileMap = null;
//                return new FilePrefixMatchRecord(false, field.getName());
//            }
//
//
//        }
//        return new FilePrefixMatchRecord(true, null);
//    }

//    public Department getDepartment(String id)
//    {
//        LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory,DoctorToFile.class,DepartmentToFile.class);
//        List<String> doctorIds = new ArrayList<>();
//        if (linkerHandler.getLinkers() != null) doctorIds = linkerHandler.getLinkers().findBasedOnKey(id);
//        if(doctorIds == null) doctorIds = new ArrayList<>();
//        return new Department(id,prefixFileMap.get(PrefixFinder.findPrefix(id)),doctorIds,prefixFileMap.get(PrefixFinder.findPrefix(DoctorToFile.PREFIX)));
//    }
//
//    public Doctor getDoctor(String id)
//    {
//        LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory,DoctorToFile.class,DepartmentToFile.class);
//        List<String> departmentIds = new ArrayList<>();
//        if (linkerHandler.getLinkers() != null) departmentIds = linkerHandler.getLinkers().findBasedOnKey(id);
//        if(departmentIds == null) departmentIds = new ArrayList<>();
//        if(departmentIds.size() > 1) throw new RuntimeException("err stub");
//        return new Doctor(id,prefixFileMap.get(PrefixFinder.findPrefix(id)),departmentIds.isEmpty() ? null : departmentIds.getFirst(),prefixFileMap.get(DepartmentToFile.PREFIX));
//    }

    public <T extends BusinessEntity<?>> T getBusinessEntity(String id)
    {
        HashMap<String,LinkerManager> linkerManagerHashMap = new HashMap<>();
        HashMap<String,FileDataHandler> fileDataHandlerHashMap = new HashMap<>();
        String prefix = PrefixFinder.findPrefix(id);
        Class<? extends BusinessEntity<?>> businessEntityClass = EntityConvertManager.getBusinessEntityMap().get(prefix);
        Class<? extends BaseEntity> baseEntityClass = EntityConvertManager.getEntityMap().get(prefix);
        for (Field field : businessEntityClass.getDeclaredFields())
        {
            if (field.getType() != LazyEntity.class && field.getType() != LazyEntityList.class) continue;
            Class<? extends BaseEntity> otherClass =
                ((Class<?>) ((ParameterizedType)
                field.getGenericType())
                .getActualTypeArguments()[0])
                .asSubclass(BaseEntity.class);
            LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory,baseEntityClass,otherClass);
            FileDataHandler fileDataHandler = prefixFileMap.get(EntityConvertManager.getPrefixMap().get(otherClass));
            linkerManagerHashMap.put(EntityConvertManager.getPrefixMap().get(otherClass),linkerHandler.getLinkerManager());
            if (fileDataHandler == null)
                throw new IllegalStateException("No entity file handler for " + otherClass.getName()); fileDataHandlerHashMap.put(EntityConvertManager.getPrefixMap().get(otherClass),fileDataHandler);
        }
        EntityConvertManager.BusinessEntityConstructor businessEntityConstructor = new EntityConvertManager.BusinessEntityConstructor
        (
          id,prefixFileMap.get(prefix), fileDataHandlerHashMap,linkerManagerHashMap
        );
        return (T) EntityConvertManager.getBusinessConvertMap().get(prefix).apply(businessEntityConstructor);
    }
    public <T extends BusinessEntity<?>> List<T> getAllBusinessEntities(String prefix)
    {
        Class<? extends BusinessEntity<?>> businessEntityClass = EntityConvertManager.getBusinessEntityMap().get(prefix);
        Class<? extends BaseEntity> baseEntityClass = EntityConvertManager.getEntityMap().get(prefix);
        FileDataHandler selfFile = prefixFileMap.get(prefix);
        var factory = EntityConvertManager.getBusinessConvertMap().get(prefix);
        if (businessEntityClass == null || baseEntityClass == null
                || selfFile == null || factory == null)
            throw new IllegalArgumentException("Missing entity mapping or file handler for " + prefix);

        List<BaseEntity> records = new EntityHandler(selfFile).getAllEntities();
        List<T> result = new ArrayList<>(records.size());
        if (records.isEmpty()) return result;
        Set<String> ids = new java.util.HashSet<>();
        for (BaseEntity record : records)
        {
            if (!baseEntityClass.isInstance(record))
                throw new IllegalStateException("Wrong entity type in " + selfFile.getFile());
            if (!ids.add(record.getId()))
                throw new IllegalStateException("Duplicate entity ID: " + record.getId());
        }

        HashMap<String, FileDataHandler> relatedFiles = new HashMap<>();
        HashMap<String, LinkerManager> allLinkerManagers = new HashMap<>();
        for (Field field : businessEntityClass.getDeclaredFields())
        {
            if (field.getType() != LazyEntity.class && field.getType() != LazyEntityList.class)
                continue;

            Class<? extends BaseEntity> otherClass =
                    ((Class<?>) ((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0]).asSubclass(BaseEntity.class);
            String otherPrefix = EntityConvertManager.getPrefixMap().get(otherClass);
            FileDataHandler relatedFile = prefixFileMap.get(otherPrefix);
            if (otherPrefix == null || relatedFile == null)
                throw new IllegalStateException("Missing related type or file handler: " + otherClass.getName());
            if (allLinkerManagers.containsKey(otherPrefix)) continue;

            LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory, baseEntityClass, otherClass);
            relatedFiles.put(otherPrefix, relatedFile);
            allLinkerManagers.put(otherPrefix, linkerHandler.getLinkerManager());
        }

        for (BaseEntity record : records)
        {
            HashMap<String, LinkerManager> ownLinkerManagers = new HashMap<>();
            for (var entry : allLinkerManagers.entrySet())
            {
                ownLinkerManagers.put(entry.getKey(),
                        entry.getValue().filterBasedOnKey(record.getId()));
            }

            EntityConvertManager.BusinessEntityConstructor context = new EntityConvertManager.BusinessEntityConstructor(
                    record.getId(), selfFile, relatedFiles, ownLinkerManagers,record);
            result.add((T) businessEntityClass.cast(factory.apply(context)));
        }

        return result;
    }
    private <T extends BusinessEntity<?> & OwnerShip & Linkable> void saveLinkers(T businessEntity)
    {
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
            LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory, selfClass, secondClass);
            linkerHandler.updatePartialLinker(linkerManager, businessEntity.getSelf().getId());
            linkerHandler.saveLinkers();
        }
    }

    public <T extends BusinessEntity<?> & OwnerShip & Linkable> void saveChanges(T businessEntity)
    {
        //linker first
        saveLinkers(businessEntity);
        if (businessEntity instanceof OwnEntities ownEntities)
        {
            //entity second
            List<LazyEntityList<? extends BaseEntity>> lazyEntityLists = ownEntities.getEntities();
            for (LazyEntityList<? extends BaseEntity> lazyEntityList : lazyEntityLists)
            {
                EntityHandler entityHandler = null;
                for (int i = 0; i < lazyEntityList.size(); i++)
                {
                    if (!lazyEntityList.isChanged(i)) continue;
                    if (entityHandler == null) entityHandler = getEntityHandler(lazyEntityList.get(i).getId());
                    entityHandler.upsertEntity(lazyEntityList.get(i));
                    lazyEntityList.markAsSaved(i);
                }
            }
        }
        if (businessEntity instanceof OwnEntity ownEntity)
        {
            List<LazyEntity<? extends BaseEntity>> lazyEntityList = ownEntity.getEntity();
            EntityHandler entityHandler;
            for (LazyEntity<? extends BaseEntity> lazyEntity : lazyEntityList)
            {
                if (!lazyEntity.isSelfAlrChanged()) continue;
                entityHandler = getEntityHandler(lazyEntity.getId());
                entityHandler.upsertEntity(lazyEntity.getSelf());
                lazyEntity.updateBackup();
            }
        }
        //self last
        EntityHandler entityHandler = getEntityHandler(businessEntity.getSelf().getId());
        entityHandler.upsertEntity(businessEntity.getSelf());
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

    public  <T extends BaseEntity & ConvertToFileData> void updateEntity(T entity) throws EntityNotFoundException
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

    public <T extends BaseEntity> List<T> getAllEntities(String prefix)
    {
        FileDataHandler handler = prefixFileMap.get(prefix);
        if (handler == null) return new ArrayList<>();
        return new EntityHandler(handler).getAllEntities();
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
