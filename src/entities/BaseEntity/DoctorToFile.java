package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDate;

public class DoctorToFile extends UserWithDetails implements ConvertToFileData
{
    public static final String PREFIX = "DT";


    public DoctorToFile(String id) {super(id);}

    public DoctorToFile(String id, String name, String password, Gender gender, LocalDate dateOfBirth)
    {
        super(id, name, password, gender, dateOfBirth);
    }

    public DoctorToFile(String[] data)
    {
        super(data);
    }

    public DoctorToFile(String id, String name, String password, Gender gender, String dateOfBirth, String departmentId)
    {
        super(id, name, password, gender, dateOfBirth);
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
                this.getDateOfBirth() + "\n";
    }
}
