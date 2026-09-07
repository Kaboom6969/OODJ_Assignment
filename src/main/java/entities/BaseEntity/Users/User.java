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
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User(String[] fileData)
    {
        this(fileData[0],fileData[1],fileData[2],fileData[3]);
    }


    public String getName() {return name;}
    public String getPassword() {return password;}

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        if (!(Objects.equals(name, ((User)o).name))) return false;
        if (!Objects.equals(password, ((User)o).password)) return false;
        if (!Objects.equals(email, ((User)o).email)) return false;
        return true;
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(),this.getName(),this.getPassword(),this.email);
    }



}
