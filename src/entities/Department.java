package entities;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class Department extends BaseEntity
{
    public static final String PREFIX = "DP";
    private String name;
    public List<Doctor> ownDoctors;

    public void addDoctor(Doctor doctor)
    {
        ownDoctors.add(doctor);
    }

    public Department(String id)
    {
        super(id);
        ownDoctors = new ArrayList<>();
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
