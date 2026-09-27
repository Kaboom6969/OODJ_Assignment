package Tools.TestTools;

import Interfaces.Linkable;
import Tools.EntityConvertManager;
import Tools.HospitalEntityAllocator;
import Tools.LinkerHandlers.LinkerHandler;
import entities.BaseEntity.BaseEntity;
import entities.BusinessEntity.BusinessEntity;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;

import static Interfaces.Linkable.getLinkerManagerWithField;

public class EntityTester
{
    private final Path entityFilePath;
    private final Path linkerFilePath;

    public record TestResult(boolean isSuccess, String message) {}

    public EntityTester(Path linkerFilePath, Path entityFilePath)
    {
        this.entityFilePath = entityFilePath;
        this.linkerFilePath = linkerFilePath;
    }
    public TestResult callAllEntity()
    {
        try
        {
            HospitalEntityAllocator hea = new HospitalEntityAllocator(linkerFilePath, entityFilePath);
            for (String prefix : EntityConvertManager.getPrefixMap().values())
            {
                hea.getAllBusinessEntities(prefix);
            }
            return new TestResult(true, "Success");
        } catch (Exception e)
        {
            return new TestResult(false, e.getMessage());
        }
    }

    public TestResult callAllLinker()
    {
        try
        {
            for (Class<? extends BusinessEntity<?>> businessEntity : EntityConvertManager.getBusinessEntityMap().values())
            {
                Class<? extends BaseEntity> selfBaseEntity =
                        (Class<? extends BaseEntity>) ((ParameterizedType) businessEntity.getGenericSuperclass()).getActualTypeArguments()[0];
                for (Field field : businessEntity.getDeclaredFields())
                {
                    field.setAccessible(true);
                    if (field.getType().equals(LazyEntity.class) || field.getType().equals(LazyEntityList.class))
                    {
                        LinkerManager linkerManager = Linkable.getLinkerManagerWithField(selfBaseEntity, field);
                        if (linkerManager == null) continue;
                        LinkerHandler linkerHandler = new LinkerHandler(linkerFilePath, linkerManager.getClassFirst(), linkerManager.getClassSecond());
                    }
                }
            }
            return new TestResult(true, "Success");
        }  catch (Exception e)
        {
            return new TestResult(false, e.getMessage());
        }
    }
}
