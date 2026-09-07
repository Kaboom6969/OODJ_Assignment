package entities.BaseEntity.Users;

import Interfaces.ConvertToFileData;

import java.time.LocalDate;

public class PatientToFile extends UserWithDetails implements ConvertToFileData
{

    public final static String PREFIX = "PT";
    public PatientToFile(String[] data)
    {
        super(data);
    }
    public PatientToFile(String id, String name, String password,String email, Gender gender, LocalDate dateOfBirth, String phoneNumber)
    {
        super(id,name,password,email,gender,dateOfBirth,phoneNumber);
    }
    public PatientToFile(String id, String name, String password,String email, Gender gender, String dateOfBirth, String phoneNumber)
    {
        super(id,name,password,email,gender,dateOfBirth,phoneNumber);
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
    @Override
    public String toString()
    {
        return
                "Name:" + this.getName() +  "\n"
                +"Password:" + this.getPassword() + "\n"
                +"Gender:" +this.getGender() + "\n"
                +"Date Of Birth:" + this.getDateOfBirth();

    }

}
