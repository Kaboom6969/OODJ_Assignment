package entities.BaseEntity.Users;


import Interfaces.ConvertToFileData;

public class AdminToFile extends User implements ConvertToFileData
{
    public static final String PREFIX = "AD";
    public AdminToFile(String id, String name, String password)
    {
        super(id, name, password);
    }

    public AdminToFile(String[] fileData)
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
