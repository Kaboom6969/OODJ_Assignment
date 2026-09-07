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

    public MedicalManagerToFile(String id, String name, String password, Gender gender, LocalDate dateOfBirth)
    {
        super(id, name, password, gender, dateOfBirth);
    }

    public MedicalManagerToFile(String id, String name, String password, Gender gender, String dateOfBirth)
    {
        super(id, name, password, gender, dateOfBirth);
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
                        this.getGender() + "|" +
                        this.getDateOfBirth();
    }
}