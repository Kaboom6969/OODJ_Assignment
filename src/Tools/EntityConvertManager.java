package Tools;

import Exceptions.IdPrefixNotFoundException;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.DepartmentToFile;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Department;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class EntityConvertManager
{
    private static HashMap<String,Function<String[], BaseEntity>> convertMap;

    public EntityConvertManager()
    {
        if (convertMap == null) convertMapInit();
    }
    public <T extends BaseEntity> T convertEntity(String[] data)
    {
        String prefix = FileDataHandler.prefixFinder(data[0]);
        if (convertMap.get(prefix) == null) throw new IdPrefixNotFoundException("Convert Failed: No mapping found in this prefix %s".formatted(prefix));
        return (T)convertMap.get(prefix).apply(data);
    }


    public BusinessEntity<?> convertBusinessEntity(BaseEntity self, List<Object> parameters)
    {
        switch (self.getIdPrefix())
        {
            case DepartmentToFile.PREFIX:
                return new Department((DepartmentToFile) self,(List<String>)parameters.get(0),(FileDataHandler) parameters.get(1));
            default:
                throw new IllegalStateException();
        }
    }
    public void convertMapInit()
    {
        convertMap = new HashMap<String,Function<String[],BaseEntity>>();
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

                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
