package Tools;

import Exceptions.IdPrefixNotFoundException;
import Exceptions.IdPrefixNotMatchException;
import entities.Admin;
import entities.Doctor;
import entities.Patient;

import java.lang.reflect.Field;
import java.nio.file.Path;

public class HospitalEntityAllocator
{
    public record FilePrefixMatchRecord(boolean isAllMatch,String propertiesName){}
    private EntityFile adminEntityFile = new EntityFile(Admin.PREFIX);
    private EntityFile patientEntityFile = new EntityFile(Patient.PREFIX);
    private EntityFile doctorEntityFile = new EntityFile(Doctor.PREFIX);

    public HospitalEntityAllocator(Path adminPath, Path patientPath, Path doctorPath)
    {
        adminEntityFile.fileDataHandler = new FileDataHandler(adminPath);
        patientEntityFile.fileDataHandler = new FileDataHandler(patientPath);
        doctorEntityFile.fileDataHandler = new FileDataHandler(doctorPath);
        FilePrefixMatchRecord filePrefixMatchRecord = filePrefixCheck();
        if (!filePrefixMatchRecord.isAllMatch())
        {
            throw new IllegalStateException("Prefix check failed,"+filePrefixMatchRecord.propertiesName+" problem");
        }
    }

    private FilePrefixMatchRecord filePrefixCheck()
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
                    return new FilePrefixMatchRecord(false, field.getName());
                }
            }
            catch (NullPointerException e)
            {
                System.err.printf("%s's properties or it self is null!",field.getName());
                return new FilePrefixMatchRecord(false, field.getName());
            }
            catch (IdPrefixNotMatchException e)
            {
                System.err.printf("%s's file's prefix inside is not matched!",field.getName());
                return new FilePrefixMatchRecord(false, field.getName());
            }
            catch (IdPrefixNotFoundException e)
            {
                System.err.printf("%s's file's prefix not found!",field.getName());
                return new FilePrefixMatchRecord(false, field.getName());
            }


        }
        return new FilePrefixMatchRecord(true, null);
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
