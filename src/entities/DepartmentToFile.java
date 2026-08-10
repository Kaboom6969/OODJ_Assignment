package entities;

import Interfaces.ConvertToFileData;

import java.io.Serializable;
import java.util.ArrayList;

public class DepartmentToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "DP";
    private String name;


    public DepartmentToFile(String id)
    {
        super(id);
    }

    @Override
    public String getIdPrefix()
    {
        return PREFIX;
    }

    @Override
    public String toFileData()
    {
        return "";
    }
}
