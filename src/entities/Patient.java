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
    public Patient(String id, String name, String password,Gender gender, String dateOfBirth)
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
        return this.getId() + "|" + this.getName() + "|" + this.getPassword() + "|" + this.getGender() + "|" + this.getDateOfBirth() + "\n";
    }
    @Override
    public String toString()
    {
        return
                "Name:" + this.getName() +  "\n"
                +"Password:" + this.getPassword() + "\n"
                +"Gender:" +this.getGender() + "\n"
                +"Date Of Birth:" + this.getDateOfBirth();

    }

}
