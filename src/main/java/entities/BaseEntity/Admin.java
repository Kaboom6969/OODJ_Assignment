package entities.BaseEntity;


import Interfaces.ConvertToFileData;

public class Admin extends User implements ConvertToFileData
{
    public static final String PREFIX = "AD";
    public Admin(String id, String name, String password)
    {
        super(id, name, password);
    }

    public Admin(String[] fileData)
    {
        super(fileData);
    }

    @Override
    public String getIdPrefix()
    {
        return PREFIX;
    }

    @Override
    public String toFileData()
    {
        return this.getId() + "|" + this.getName() + "|" + this.getPassword();
    }
}
