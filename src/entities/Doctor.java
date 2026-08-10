package entities;

import Interfaces.ConvertToFileData;

import java.time.LocalDate;

public class Doctor extends UserWithDetails implements ConvertToFileData
{
    public static final String PREFIX = "DT";
    private String departmentId;

    public Doctor(String id, String name, String password, Gender gender, LocalDate dateOfBirth,String departmentId)
    {
        super(id, name, password, gender, dateOfBirth);
        this.departmentId = departmentId;
    }

    public Doctor(String[] data)
    {
        super(data);
        this.departmentId = data[5];
    }

    public Doctor(String id, String name, String password, Gender gender, String dateOfBirth,String departmentId)
    {
        super(id, name, password, gender, dateOfBirth);
        this.departmentId = departmentId;
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
}
