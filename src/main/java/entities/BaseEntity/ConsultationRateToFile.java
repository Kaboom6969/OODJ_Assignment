package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.util.Objects;

public class ConsultationRateToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "CR";

    private String name;
    private int price;
    private boolean active;

    public String getName()
    {
        return name;
    }

    public int getPrice()
    {
        return price;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPrice(int price)
    {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative");

        this.price = price;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public ConsultationRateToFile(String id, String name, int price, boolean active)
    {
        super(id);
        setName(name);
        setPrice(price);
        setActive(active);
    }

    public ConsultationRateToFile(String[] data)
    {
        this
                (
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        parseActive(data[3])
                );
    }

    private static boolean parseActive(String value)
    {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;

        throw new IllegalArgumentException("Active should be true or false");
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
                        this.getPrice() + "|" +
                        this.isActive();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        ConsultationRateToFile rate = (ConsultationRateToFile) o;
        if (!Objects.equals(name, rate.name)) return false;
        if (price != rate.price) return false;
        return active == rate.active;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), name, price, active);
    }
}