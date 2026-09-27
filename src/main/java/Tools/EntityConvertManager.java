package Tools;

import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Tools.FileHandler.FileDataHandler;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.BaseEntity;
import entities.BusinessEntity.BusinessEntity;
import entities.Linker.LinkerManager;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public class EntityConvertManager
{
    private static HashMap<String,Function<String[], BaseEntity>> convertMap;
    private static HashMap<String,Class<? extends BaseEntity>> entityMap;
    private static HashMap<Class<? extends BaseEntity>,String> prefixMap;
    private static HashMap<String, Class<? extends BusinessEntity<?>>> businessEntityMap;
    private static HashMap<String,Function<BusinessEntityConstructor,BusinessEntity<?>>> businessConvertMap;

    public record BusinessEntityConstructor
    (
            String selfId,
            FileDataHandler selfFile,
            HashMap<String,FileDataHandler> fileDataHandlerHashMap,
            HashMap<String, LinkerManager> linkerManagerHashMap,
            BaseEntity self,
            boolean isJustConstruct
    )
    {
        public BusinessEntityConstructor
        {
            if (self != null && !Objects.equals(self.getId(), selfId))
            {
                throw new IllegalArgumentException
                (
                    "Self Ids are not equal"
                );
            }
        }
        public BusinessEntityConstructor(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap,boolean isJustConstruct)
        {
            this(selfId,selfFile,fileDataHandlerHashMap,linkerManagerHashMap,null,isJustConstruct);
        }

    }

    public EntityConvertManager()
    {
        if (convertMap == null || entityMap == null || prefixMap == null) mapInit();
    }
    public <T extends BaseEntity> T convertEntity(String[] data)
    {
        String prefix = PrefixFinder.findPrefix(data[0]);
        if (convertMap.get(prefix) == null) throw new IdPrefixNotFoundException("Convert Failed: No mapping found in this prefix %s".formatted(prefix));
        return (T)convertMap.get(prefix).apply(data);
    }

    public static HashMap<String, Class<? extends BusinessEntity<?>>> getBusinessEntityMap()
    {
        if (businessEntityMap == null) mapInit();
        return businessEntityMap;
    }

    public static HashMap<String,Function<BusinessEntityConstructor,BusinessEntity<?>>> getBusinessConvertMap()
    {
        if (businessConvertMap == null) mapInit();
        return businessConvertMap;
    }
    public static HashMap<String,Function<String[], BaseEntity>> getConvertMap()
    {
        if  (convertMap == null) mapInit();
        return convertMap;
    }

    public static HashMap<String, Class<? extends BaseEntity>> getEntityMap()
    {
        if (entityMap == null) mapInit();
        return entityMap;
    }

    public static HashMap<Class<? extends BaseEntity>, String> getPrefixMap()
    {
        if (prefixMap == null) mapInit();
        return prefixMap;
    }

    public static void mapInit()
    {
        convertMap = new HashMap<>();
        entityMap = new HashMap<>();
        prefixMap = new HashMap<>();
        businessEntityMap = new HashMap<>();
        businessConvertMap = new HashMap<>();
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .acceptPackages("entities")
                .scan())
        {
            ClassInfoList subclasses = scanResult.getSubclasses("entities.BaseEntity.BaseEntity");
            for (Class<?> clazz : subclasses.loadClasses())
            {
                if (!Modifier.isAbstract(clazz.getModifiers()))
                {
                    Function<String[],BaseEntity> constructEntity = data -> {
                        try {
                            return (BaseEntity) clazz.getConstructor(String[].class).newInstance((Object) data);
                        } catch (Exception e) {
                            throw new RuntimeException("Instance Fail", e);
                        }
                    };
                    convertMap.put(clazz.getField("PREFIX").get(null).toString(),constructEntity);
                    entityMap.put(clazz.getField("PREFIX").get(null).toString(), (Class<? extends BaseEntity>) clazz);
                    prefixMap.put((Class<? extends BaseEntity>) clazz,clazz.getField("PREFIX").get(null).toString());
                }
            }
            ClassInfoList businessSubclasses =
                    scanResult.getSubclasses(BusinessEntity.class.getName());

            for (Class<?> clazz : businessSubclasses.loadClasses())
            {
                if (!Modifier.isAbstract(clazz.getModifiers()))
                {
                    Class<? extends BaseEntity> baseEntityClass =
                            ((Class<?>) ((ParameterizedType)
                                    clazz.getGenericSuperclass())
                                    .getActualTypeArguments()[0])
                                    .asSubclass(BaseEntity.class);
                    Function<BusinessEntityConstructor,BusinessEntity<?>> constructEntity = data -> {
                        try {
                            return (BusinessEntity<?>) clazz.getConstructor(String.class, FileDataHandler.class,HashMap.class,HashMap.class,baseEntityClass,boolean.class).
                                    newInstance(data.selfId,data.selfFile,data.fileDataHandlerHashMap,data.linkerManagerHashMap,data.self,data.isJustConstruct);
                        } catch (Exception e) {
                            throw new RuntimeException("Instance Fail", e);
                        }
                    };
                    String prefix = prefixMap.get(baseEntityClass);
                    if (prefix == null)  throw new IllegalStateException ("No prefix found for " + baseEntityClass.getName());
                    if (businessEntityMap.containsKey(prefix)) throw new IllegalStateException ("Repeated business entity prefix: " + prefix);
                    Class<? extends BusinessEntity<?>> businessEntityClass = (Class<? extends BusinessEntity<?>>) clazz;
                    businessEntityMap.put(prefix, businessEntityClass);
                    businessConvertMap.put(prefix,constructEntity);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
