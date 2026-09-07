package entities.BaseEntity.Users;

import Interfaces.ConvertToFileData;

import java.time.LocalDate;

public class DoctorToFile extends UserWithDetails implements ConvertToFileData
{
    public static final String PREFIX = "DT";


    public DoctorToFile(String id) {super(id);}

    public DoctorToFile(String id, String name, String password,String email, Gender gender, LocalDate dateOfBirth, String phoneNumber)
    {
        super(id, name, password,email, gender, dateOfBirth, phoneNumber);
    }

    public DoctorToFile(String[] data)
    {
        super(data);
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
                this.getEmail() + "|" +
                this.getGender() + "|" +
                this.getDateOfBirth() + "|" +
                this.getPhoneNumber();
    }
}
