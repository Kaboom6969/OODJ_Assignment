package entities;

public class User extends BaseEntity
{
    private String name;
    private String password;

    public User(String id,String name,String password)
    {
        super(id);
        this.name = name;
        this.password = password;
    }

    public User(String[] fileData)
    {
        this(fileData[0],fileData[1],fileData[2]);
    }


    public String getName() {return name;}
    public String getPassword() {return password;}

    public String getIdPrefix()
    {
        return "US";
    }
}
