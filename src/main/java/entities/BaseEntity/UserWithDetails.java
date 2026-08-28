package entities.BaseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
    private LocalDate dateOfBirth;
    private Gender gender;

    public UserWithDetails(String id) {super(id);}
    public UserWithDetails(String id,String name,String password,Gender gender,LocalDate dateOfBirth)
    {
        super(id,name,password);
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
    public UserWithDetails(String id,String name,String password,Gender gender,String dateOfBirth)
    {
        super(id,name,password);
        this.gender = gender;
        this.dateOfBirth = LocalDate.parse(dateOfBirth,LocalDateParser.getDateFormatterAuto(dateOfBirth));
    }
    public UserWithDetails(String[] data)
    {
        super(data);
        this.gender = Gender.valueOf(data[3].toUpperCase().trim());
        this.dateOfBirth = LocalDate.parse(data[4],LocalDateParser.getDateFormatterAuto(data[4]));
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

