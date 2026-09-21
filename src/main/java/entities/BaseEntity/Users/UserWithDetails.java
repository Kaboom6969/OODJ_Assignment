package entities.BaseEntity.Users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
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
        this.dateOfBirth = LocalDate.parse(dateOfBirth, LocalDateTool.getDateFormatterAuto(dateOfBirth));
        this.phoneNumber = phoneNumber;
    }
    public UserWithDetails(String[] data)
    {
        super(data);
        this.gender = Gender.valueOf(data[4].toUpperCase().trim());
        this.dateOfBirth = LocalDate.parse(data[5], LocalDateTool.getDateFormatterAuto(data[5]));
        this.phoneNumber = data[6];
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public void setDateOfBirth(LocalDate dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
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
class LocalDateTool
{
    public static DateTimeFormatter getDateFormatterAuto(String date)
    {
        char delimiter = '\0';
        byte delimiterCount = 0;

        int firstDelimiterIndex = -1;
        int lastDelimiterIndex = -1;

        for (int i = 0; i < date.length(); i++)
        {
            if (delimiterCount > 2) throw new DateTimeParseException("Date Format is incorrect", date, i);
            if (!Character.isDigit(date.charAt(i)) && !Character.isLetter(date.charAt(i)) && delimiter == '\0')
            {
                delimiter = date.charAt(i);
                delimiterCount++;
                firstDelimiterIndex = i;
                lastDelimiterIndex = i;
                continue;
            }
            if (delimiter != '\0' && date.charAt(i) == delimiter)
            {
                delimiterCount++;
                if (firstDelimiterIndex == -1) firstDelimiterIndex = i;
                lastDelimiterIndex = i;
                continue;
            }
            if (delimiter != '\0' && !Character.isDigit(date.charAt(i)) && !Character.isLetter(date.charAt(i)))
            {
                throw new DateTimeParseException("Date Format is incorrect", date, i);
            }
        }
        if (delimiterCount != 2)
            throw new DateTimeParseException
            (
                "Date Format is incorrect",
                date,
                lastDelimiterIndex
            );

        String first = date.substring(0, firstDelimiterIndex);
        String second = date.substring(firstDelimiterIndex + 1, lastDelimiterIndex);
        String third = date.substring(lastDelimiterIndex + 1);
        // yyyy-MM-dd
        if (first.length() == 4)
        {
            return DateTimeFormatter.ofPattern("yyyy" + delimiter + "M" + delimiter + "d");
        }

        // dd-MM-yyyy / MM-dd-yyyy
        if (third.length() == 4)
        {
            int firstValue = Integer.parseInt(first);
            int secondValue = Integer.parseInt(second);
            if (firstValue > 12)
            {
                return DateTimeFormatter.ofPattern("d" + delimiter + "M" + delimiter + "yyyy");
            }
            if (secondValue > 12)
            {
                return DateTimeFormatter.ofPattern("M" + delimiter + "d" + delimiter + "yyyy");
            }
            return DateTimeFormatter.ofPattern("d" + delimiter + "M" + delimiter + "yyyy");
        }

        throw new DateTimeParseException("Unable to determine Date Format", date, 0);
    }
}

