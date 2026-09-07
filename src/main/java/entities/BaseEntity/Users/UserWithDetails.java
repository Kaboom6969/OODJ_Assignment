package entities.BaseEntity.Users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public abstract class UserWithDetails extends User
{
    public Gender getGender()
    {
        return gender;
    }

    public LocalDate getDateOfBirth()
    {
        return dateOfBirth;
    }

    public enum Gender
    {
        MALE,FEMALE,UNKNOWN
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;

    public UserWithDetails(String id) {super(id);}
    public UserWithDetails(String id,String name,String password,String email,Gender gender,LocalDate dateOfBirth,String phoneNumber)
    {
        super(id,name,password,email);
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }

    public UserWithDetails(String id, String name, String password,String email, Gender gender, String dateOfBirth,  String phoneNumber)
    {
        super(id, name, password, email);
        this.gender = gender;
        this.dateOfBirth = LocalDate.parse(dateOfBirth, LocalDateParser.getDateFormatterAuto(dateOfBirth));
        this.phoneNumber = phoneNumber;
    }
    public UserWithDetails(String[] data)
    {
        super(data);
        this.gender = Gender.valueOf(data[4].toUpperCase().trim());
        this.dateOfBirth = LocalDate.parse(data[5],LocalDateParser.getDateFormatterAuto(data[4]));
        this.phoneNumber = data[6];
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        if (!(this.dateOfBirth.equals(((UserWithDetails) o).dateOfBirth))) return false;
        if (!(this.gender.equals(((UserWithDetails) o).gender))) return false;
        if  (!(this.phoneNumber.equals(((UserWithDetails) o).phoneNumber))) return false;
        return true;
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(),this.dateOfBirth,this.gender,this.phoneNumber);
    }

}
class LocalDateParser
{
    public static DateTimeFormatter getDateFormatterAuto(String date)
    {
        char delimiter = '\0';
        byte delimiterCount = 0;
        int lastDelimiterIndex = -1;
        for(int i = 0; i < date.length(); i++)
        {
            if (delimiterCount > 2 || delimiterCount < 0) throw new DateTimeParseException("Date Format is incorrect",date,i);
            if (!Character.isDigit(date.charAt(i)) && !Character.isLetter(date.charAt(i)) && delimiter == '\0')
            {
                delimiter = date.charAt(i);
                delimiterCount++;
                lastDelimiterIndex = i;
                continue;
            }
            if (delimiter != '\0' && date.charAt(i) == delimiter)
            {
                delimiterCount++;
                lastDelimiterIndex = i;
                continue;
            }
            if (delimiter != '\0' && !Character.isDigit(date.charAt(i)))
            {
                throw new DateTimeParseException("Date Format is incorrect",date,i);
            }

        }
        if (delimiterCount != 2) throw new DateTimeParseException("Date Format is incorrect",date,lastDelimiterIndex);
        return DateTimeFormatter.ofPattern("y"+delimiter+"M"+delimiter+"d");
    }
}

