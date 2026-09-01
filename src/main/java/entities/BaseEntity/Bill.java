package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.util.Objects;

public class Bill extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "BL";
    private int money;

    public int getMoney()
    {
        return money;
    }

    public void setMoney(int money)
    {
        if (money < 0) throw new IllegalArgumentException("Money should be positive!");
        this.money = money;
    }

    public Bill(String[] fileData)
    {
        this(fileData[0], Integer.parseInt(fileData[1]));
    }

    public Bill(String id, int money)
    {
        super(id);
        this.money = money;
    }

    @Override
    public String getIdPrefix()
    {
        return PREFIX;
    }

    @Override
    public String toFileData()
    {
        return this.getId() + "|" + this.getMoney();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        if (!(this.money == ((Bill)o).money)) return false;
        return true;
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(),this.getMoney());
    }


}
