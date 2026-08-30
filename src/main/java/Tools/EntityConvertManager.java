package Tools;

import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;
import Tools.FileHandler.FileDataHandler;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.BaseEntity;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.HashMap;
import java.util.function.Function;

public class EntityConvertManager
{
    private static HashMap<String,Function<String[], BaseEntity>> convertMap;
    private static HashMap<String,Class<? extends BaseEntity>> entityMap;
    private static HashMap<Class<? extends BaseEntity>,String> prefixMap;

    public EntityConvertManager()
    {
        if (convertMap == null) mapInit();
    }
    public <T extends BaseEntity> T convertEntity(String[] data)
    {
        String prefix = PrefixFinder.findPrefix(data[0]);
        if (convertMap.get(prefix) == null) throw new IdPrefixNotFoundException("Convert Failed: No mapping found in this prefix %s".formatted(prefix));
        return (T)convertMap.get(prefix).apply(data);
    }

    public static HashMap<String,Function<String[], BaseEntity>> getConvertMap()
    {
        return convertMap;
    }

    public static HashMap<String, Class<? extends BaseEntity>> getEntityMap()
    {
        return entityMap;
    }

    public static HashMap<Class<? extends BaseEntity>, String> getPrefixMap()
    {
        return prefixMap;
    }

    public static void mapInit()
    {
        convertMap = new HashMap<>();
        entityMap = new HashMap<>();
        prefixMap = new HashMap<>();
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .acceptPackages("entities")
                .scan())
        {
            ClassInfoList subclasses = scanResult.getSubclasses("entities.BaseEntity.BaseEntity");
            for (Class<?> clazz : subclasses.loadClasses())
            {
                if (!java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()))
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
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
