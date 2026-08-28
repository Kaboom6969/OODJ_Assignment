package entities.BaseEntity;

import Interfaces.ConvertToFileData;

public class DepartmentToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "DP";

    public String getName()
    {
        return name;
    }

    private String name;


    public DepartmentToFile(String id)
    {
        super(id);
    }
    public DepartmentToFile(String id,String name)
    {
        super(id);
        this.name = name;
    }

    public DepartmentToFile(String[] data)
    {
        this(data[0],data[1]);
    }

    @Override
    public String getIdPrefix()
    {
        return PREFIX;
    }

    @Override
    public String toFileData()
    {
        return getId() + "|" + getName();
    }
}
