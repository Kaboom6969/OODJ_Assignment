package entities;

import java.lang.reflect.Method;

public class Patient extends User
{

    public Patient(String[] data)
    {
        this(data[0],data[1],data[2]);
    }
    public Patient(String id, String name, String password)
    {
        super(id,name,password);
    }
    @Override
    public String getIdPrefix() {
        return "PT";
    }

    @Override
    public String toFileData()
    {
        return this.getId() + "|" + this.getName() + "|" + this.getPassword() + "\n";
    }

}
