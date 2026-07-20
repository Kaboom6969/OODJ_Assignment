package entities;


import Exceptions.IdPrefixNotMatchException;

public abstract class BaseEntity
{
    private final Integer idNumber;

    public abstract String getIdPrefix();
    public abstract String toFileData();
    public Integer getIdNumber() {return idNumber;}
    public String getId()
    {
        String prefixTemp = getIdPrefix();
        if (prefixTemp == null)  prefixTemp = "";
        if (getIdNumber() == null) return null;
        return prefixTemp + getIdNumber();
    }

    public BaseEntity(String id)
    {
        int prefixNumber = getIdPrefix().length();
        if (id.length() <= prefixNumber) throw new IllegalArgumentException("Id is broken!");
        if (!id.substring(0, prefixNumber).equals(getIdPrefix())) throw new IdPrefixNotMatchException("Id prefix didn't match!");
        try
        {
            this.idNumber = (Integer.parseInt(id.substring(prefixNumber)));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Id number is broke!");
        }
        if (idNumber < 0) throw new IllegalArgumentException("Id number should not be less than 0!");
    }

}
