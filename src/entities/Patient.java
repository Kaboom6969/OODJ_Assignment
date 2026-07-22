package entities;

import java.lang.reflect.Method;
import java.time.LocalDate;

public class Patient extends UserWithDetails
{

    public final static String PREFIX = "PT";
    public Patient(String[] data)
    {
        super(data);
    }
    public Patient(String id, String name, String password,Gender gender, LocalDate dateOfBirth)
    {
        super(id,name,password,gender,dateOfBirth);
    }

    @Override
    public String getIdPrefix()
    {
        return PREFIX;
    }
    @Override
    public String toFileData()
    {
        return this.getId() + "|" + this.getName() + "|" + this.getPassword() + "\n";
    }

}
