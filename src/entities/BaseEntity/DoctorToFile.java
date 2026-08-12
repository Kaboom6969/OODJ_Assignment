package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDate;

public class DoctorToFile extends UserWithDetails implements ConvertToFileData
{
    public static final String PREFIX = "DT";
    private String departmentId;

    public String getDepartmentId()
    {
        return departmentId;
    }

    public DoctorToFile(String id) {super(id);}

    public DoctorToFile(String id, String name, String password, Gender gender, LocalDate dateOfBirth, String departmentId)
    {
        super(id, name, password, gender, dateOfBirth);
        this.departmentId = departmentId;
    }

    public DoctorToFile(String[] data)
    {
        super(data);
        this.departmentId = data[5];
    }

    public DoctorToFile(String id, String name, String password, Gender gender, String dateOfBirth, String departmentId)
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
        return
                this.getId() + "|" +
                this.getName() + "|" +
                this.getPassword() + "|" +
                this.getGender() + "|" +
                this.getDateOfBirth() + "|" +
                this.getDepartmentId() + "\n";
    }
}
