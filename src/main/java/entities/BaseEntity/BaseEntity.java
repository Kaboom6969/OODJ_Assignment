package entities.BaseEntity;


import Exceptions.IdPrefixExceptions.IdPrefixNotMatchException;
import Exceptions.IdPrefixExceptions.IdPrefixOversizeException;

import java.util.Objects;

public abstract class BaseEntity
{
    private static int idNumberWidth = 0;
    private final Integer idNumber;


    public abstract String getIdPrefix();


    public Integer getIdNumber()
    {
        return idNumber;
    }

    public static void setIdNumberWidth(Integer idNumberWidth)
    {
        if (idNumberWidth == null || idNumberWidth < 0)
            throw new IllegalArgumentException("IdNumberWidth cannot be null or less than 0");
        BaseEntity.idNumberWidth = idNumberWidth;
    }

    public String getId()
    {
        String prefixTemp = getIdPrefix();
        if (prefixTemp == null) prefixTemp = "";
        if (getIdNumber() == null) return null;
        return prefixTemp + String.format("%0" + idNumberWidth + "d", getIdNumber());
    }

    public BaseEntity(String id)
    {
        int prefixNumber = getIdPrefix().length();
        if (id.length() <= prefixNumber) throw new IllegalArgumentException("Id is broken!");
        if (!id.substring(0, prefixNumber).equals(getIdPrefix()))
            throw new IdPrefixNotMatchException("Id prefix didn't match!");
        try
        {
            int number = Integer.parseInt(id.substring(prefixNumber));
            if (number < 0) throw new IllegalArgumentException("Id number cannot be negative");
            String str = String.valueOf(number);
            if (str.length() > BaseEntity.idNumberWidth)
                throw new IdPrefixOversizeException
                        (
                                "Id number width exceed the maximum allowed length\n" +
                                        "Id number digits:" + String.valueOf(number) + "\n" +
                                        "Id number width:" + idNumberWidth
                        );
            this.idNumber = number;
        } catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Id number is broke!");
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(this.getIdNumber(), ((BaseEntity) o).getIdNumber());
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(this.getIdNumber());
    }

}
