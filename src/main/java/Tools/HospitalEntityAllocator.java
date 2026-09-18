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

import javax.naming.OperationNotSupportedException;
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
            id,prefixFileMap.get(prefix), fileDataHandlerHashMap,linkerManagerHashMap,null,false
        );
        return (T) EntityConvertManager.getBusinessConvertMap().get(prefix).apply(businessEntityConstructor);
    }
    public <T extends BusinessEntity<?>> void deleteBusinessEntity (T businessEntity)
    {
        deleteBusinessEntity(businessEntity.getId());
    }

    public void deleteBusinessEntity(String id)
    {

        String prefix = PrefixFinder.findPrefix(id);
        Class<? extends BusinessEntity<?>> businessEntityClass = EntityConvertManager.getBusinessEntityMap().get(prefix);
        Class<? extends BaseEntity> baseEntityClass = EntityConvertManager.getEntityMap().get(prefix);
        if (businessEntityClass == null || baseEntityClass == null)
        {
            throw new IllegalArgumentException("Unknown business entity prefix: " + prefix);
        }
        BaseEntity entity = getEntity(id);
        if (entity == null)
        {
            throw new IllegalArgumentException("Entity not found: " + id);
        }
        for (Field field : businessEntityClass.getDeclaredFields())
        {
            if (field.getType() != LazyEntity.class && field.getType() != LazyEntityList.class) continue;
            Class<? extends BaseEntity> otherClass =
                    ((Class<?>) ((ParameterizedType)
                            field.getGenericType())
                            .getActualTypeArguments()[0])
                            .asSubclass(BaseEntity.class);
            LinkerHandler linkerHandler = new LinkerHandler(linkerDirectory, baseEntityClass, otherClass);
            linkerHandler.updatePartialLinker(new LinkerManager(baseEntityClass,otherClass),id);
            linkerHandler.saveLinkers();
        }
        try
        {
            removeEntity(getEntity(id));
        } catch (EntityNotMatchException | EntityNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }


    public <Q extends BaseEntity & ConvertToFileData,T extends BusinessEntity<Q>> T convertToBusinessEntity(Q entity,boolean isEntityNew)
    {
        HashMap<String,LinkerManager> linkerManagerHashMap = new HashMap<>();
        HashMap<String,FileDataHandler> fileDataHandlerHashMap = new HashMap<>();
        String prefix = PrefixFinder.findPrefix(entity.getIdPrefix());
        Class<? extends BusinessEntity<?>> businessEntityClass = EntityConvertManager.getBusinessEntityMap().get(prefix);
        Class<? extends BaseEntity> baseEntityClass = entity.getClass();
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
            entity.getId(),prefixFileMap.get(prefix), fileDataHandlerHashMap,linkerManagerHashMap,entity,isEntityNew
        );
        return (T) EntityConvertManager.getBusinessConvertMap().get(prefix).apply(businessEntityConstructor);

    }
    public <T extends BaseEntity & ConvertToFileData> void assignNewId (T entity)
    {
        List<BaseEntity> allEntity = getAllEntities(entity.getIdPrefix());
        List<Integer> allNumbers = new ArrayList<>();
        for (BaseEntity baseEntity : allEntity)
        {
            allNumbers.add(baseEntity.getIdNumber());
        }
        if (allNumbers.isEmpty())
        {
            try
            {
                entity.setIdNumber(1); return;
            } catch (OperationNotSupportedException e)
            {
                throw new RuntimeException(e);
            }

        }
        allNumbers.sort(Integer::compareTo);
        for (int i = 0; i <= allNumbers.size() - 1; i++)
        {
            if (i == 0) continue;
            if (allNumbers.get(i).equals(allNumbers.get(i-1))) throw new RuntimeException("Duplicate id");
            if (allNumbers.get(i)-allNumbers.get(i-1) > 1)
            {
                try
                {
                    entity.setIdNumber(allNumbers.get(i-1) + 1);return;
                } catch (OperationNotSupportedException e)
                {
                    throw new RuntimeException(e);
                }
            }
            if(i == allNumbers.size()-1)
            {
                try
                {
                    entity.setIdNumber(allNumbers.get(i) + 1);
                    return;
                } catch (OperationNotSupportedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        try
        {
            entity.setIdNumber(allNumbers.getFirst()+1);
        } catch (OperationNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }
    public <T extends BaseEntity & ConvertToFileData> void addEntityForceNewId(T entity)
    {
        assignNewId(entity);
        try
        {
            addEntity(entity);
        } catch (EntityRepeatedException e)
        {
            throw new RuntimeException(e);
        }
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
                    record.getId(), selfFile, relatedFiles, ownLinkerManagers,record,false);
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
        if (businessEntity.getId() == null) assignNewId(businessEntity.getSelf());
        List<LazyEntityList<? extends BaseEntity>> lazyEntityLists = new ArrayList<>();
        List<LazyEntity<? extends BaseEntity>> lazyEntitySingleList = new ArrayList<>();
        if (businessEntity instanceof OwnEntities ownEntities)
        {
            //entity
            lazyEntityLists = ownEntities.getEntities();
            for (LazyEntityList<? extends BaseEntity> lazyEntityList : lazyEntityLists)
            {
                for (int i = 0; i < lazyEntityList.size(); i++)
                {
                    if (!lazyEntityList.isChanged(i)) continue;
                    if (lazyEntityList.get(i).getId() == null) assignNewId(lazyEntityList.get(i));
                }
            }
        }
        if (businessEntity instanceof OwnEntity ownEntity)
        {
            lazyEntitySingleList = ownEntity.getEntity();
            for (LazyEntity<? extends BaseEntity> lazyEntity : lazyEntitySingleList)
            {
                if (!lazyEntity.isSelfAlrChanged()) continue;
                if (lazyEntity.getId() == null) assignNewId(lazyEntity.getSelf());
            }
        }
        //linker
        saveLinkers(businessEntity);
        //entity
        saveLazyEntityListList(lazyEntityLists);
        saveLazyEntitySingleList(lazyEntitySingleList);
        //self
        upsertEntity(businessEntity.getSelf());

    }

    private void saveLazyEntityListList(List<LazyEntityList<? extends BaseEntity>> lazyEntityLists)
    {
        for(LazyEntityList<?> lazyEntityList : lazyEntityLists)
        {
            for (int i = 0; i < lazyEntityList.size(); i++)
            {
                if (!lazyEntityList.isChanged(i)) continue;
                upsertEntity(lazyEntityList.get(i));
                lazyEntityList.markAsSaved(i);
            }
        }
    }

    private void saveLazyEntitySingleList(List<LazyEntity<? extends BaseEntity>> lazyEntitySingleList)
    {
        for (LazyEntity<? extends BaseEntity> lazyEntity : lazyEntitySingleList)
        {
            if (!lazyEntity.isSelfAlrChanged()) continue;
            upsertEntity(lazyEntity.getSelf());
            lazyEntity.updateBackup();
        }
    }


    public <T extends BaseEntity> T getEntity(String id)
    {
        EntityHandler entityHandler = getEntityHandler(id);
        if (entityHandler == null) return null;
        return entityHandler.getEntity(id);
    }

    private <T extends BaseEntity & ConvertToFileData> void addEntity (T entity) throws EntityRepeatedException
    {
        EntityHandler entityHandler = getEntityHandler(entity.getId());
        if (entityHandler == null) throw new IdPrefixNotFoundException(entity.getId());
        entityHandler.addEntity(entity);
    }
    private <T extends BaseEntity & ConvertToFileData> void upsertEntity(T entity)
    {
        EntityHandler entityHandler = getEntityHandler(entity.getId());
        if (entityHandler == null) throw new IdPrefixNotFoundException(entity.getId());
        entityHandler.upsertEntity(entity);
    }
    private void removeEntity(BaseEntity entity) throws EntityNotMatchException, EntityNotFoundException
    {
        EntityHandler entityHandler = getEntityHandler(entity.getId());
        if (entityHandler == null) throw new IdPrefixNotFoundException(entity.getId());
        entityHandler.deleteEntity(entity, EntityHandler.MatchLogic.CODE_ONLY);
    }

    private  <T extends BaseEntity & ConvertToFileData> void updateEntity(T entity) throws EntityNotFoundException
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

    private  <T extends BaseEntity> List<T> getAllEntities(String prefix)
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
