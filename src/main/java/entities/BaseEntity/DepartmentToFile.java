package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        if (!(Objects.equals(name, ((DepartmentToFile)o).name))) return false;
        return true;
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(),this.getName());
    }
}
