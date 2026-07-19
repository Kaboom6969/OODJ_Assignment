package entities;

public class Bill extends BaseEntity
{
    private int money;
    public int getMoney() {return money;}
    public void setMoney(int money)
    {
        if (money < 0) throw new IllegalArgumentException("Money should be positive!");
        this.money = money;
    }

    public Bill (String[] fileData)
    {
        this(fileData[0],Integer.parseInt(fileData[1]));
    }
    public Bill(String id,int money)
    {
        super(id);
        this.money = money;
    }

    public String getIdPrefix()
    {
        return "BL";
    }


}
