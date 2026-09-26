package entities.BaseEntity.Users;

import entities.BaseEntity.BaseEntity;

import java.util.Objects;

public abstract class User extends BaseEntity
{
    private String name;
    private String password;

    public String getEmail()
    {
        return email;
    }

    private String email;
    public User(String id) {super(id);}

    public User(String id,String name,String password, String email)
    {
        super(id);
        setName(name);
        setPassword(password);
        setEmail(email);
    }

    public User(String[] fileData)
    {
        this(fileData[0],fileData[1],fileData[2],fileData[3]);
    }


    public String getName() {return name;}
    public String getPassword() {return password;}
    public void setName(String name)
    {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or empty");
        if (containsFileDelimiterOrLineBreak(name))
            throw new IllegalArgumentException("Name cannot contain | or line breaks");
        this.name = name;
    }

    public void setPassword(String password)
    {
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be null or empty");
        if (containsFileDelimiterOrLineBreak(password))
            throw new IllegalArgumentException("Password cannot contain | or line breaks");
        if (password.length() < 6) throw new IllegalArgumentException("Password length must be at least 6 characters");
        if (password.chars().noneMatch(Character::isDigit)) throw new IllegalArgumentException("Password must contains digits");
        if (password.chars().noneMatch(Character::isLetter)) throw new IllegalArgumentException("Password must contains letters");
        if (password.chars().noneMatch(Character::isUpperCase)) throw new IllegalArgumentException("Password must contains uppercase letters");
        if (password.chars().anyMatch(Character::isSpaceChar)) throw new IllegalArgumentException("Password cannot contains spaces");
        if (password.chars().noneMatch(Character::isLowerCase)) throw new IllegalArgumentException("Password must contains lowercase letters");
        this.password = password;
    }

    public void setEmail(String email)
    {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null or empty");
        if (containsFileDelimiterOrLineBreak(email))
            throw new IllegalArgumentException("Email cannot contain | or line breaks");
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) throw new IllegalArgumentException("Email format is invalid");
        if (email.indexOf('@', atIndex + 1) != -1) throw new IllegalArgumentException("Email format is invalid");
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);
        int dotIndex = domainPart.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == domainPart.length() - 1) throw new IllegalArgumentException("Email format is invalid");
        if (email.contains(" ")) throw new IllegalArgumentException("Email cannot contain spaces");
        this.email = email;
    }

    private static boolean containsFileDelimiterOrLineBreak(String value)
    {
        return value.contains("|") || value.contains("\n") || value.contains("\r");
    }
    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        if (!(Objects.equals(name, ((User)o).name))) return false;
        if (!Objects.equals(password, ((User)o).password)) return false;
        return Objects.equals(email, ((User) o).email);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(),this.getName(),this.getPassword(),this.email);
    }



}
