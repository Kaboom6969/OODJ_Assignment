package entities.BaseEntity.Users;

import Interfaces.ConvertToFileData;
import entities.BaseEntity.Users.UserWithDetails;

import java.time.LocalDate;

public class MedicalManagerToFile extends UserWithDetails implements ConvertToFileData
{
    public static final String PREFIX = "MM";

    public MedicalManagerToFile(String id)
    {
        super(id);
    }

    public MedicalManagerToFile(String id, String name, String password,String email, Gender gender, LocalDate dateOfBirth, String phoneNumber)
    {
        super(id, name, password,email, gender, dateOfBirth, phoneNumber);
    }

    public MedicalManagerToFile(String id, String name, String password,String email, Gender gender, String dateOfBirth, String phoneNumber)
    {
        super(id, name, password,email, gender, dateOfBirth,phoneNumber);
    }

    public MedicalManagerToFile(String[] data)
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