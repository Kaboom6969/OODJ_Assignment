package entities;

import java.time.LocalDate;

public class Doctor extends UserWithDetails
{
    public static final String PREFIX = "DT";

    public Doctor(String id, String name, String password, Gender gender, LocalDate dateOfBirth)
    {
        super(id, name, password, gender, dateOfBirth);
    }

    public Doctor(String[] data)
    {
        super(data);
    }

    public Doctor(String id, String name, String password, Gender gender, String dateOfBirth)
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
        return this.getId() + "|" + this.getName() + "|" + this.getPassword() + "|" + this.getGender() + "|" + this.getDateOfBirth() + "\n";
    }
}
