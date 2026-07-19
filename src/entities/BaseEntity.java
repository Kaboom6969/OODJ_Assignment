package entities;

public abstract class BaseEntity
{
    private Integer idNumber;
    private String id; //no setter

    public abstract String getIdPrefix();
    public Integer getIdNumber() {return idNumber;}
    public String getId()
    {
        String prefixTemp = getIdPrefix();
        if (prefixTemp == null)  prefixTemp = "";
        if (getIdNumber() == null) return null;
        return prefixTemp + getIdNumber();
    }
    public void setIdNumber(Integer idNumber)
    {
        if (idNumber != null && idNumber < 0) throw new IllegalArgumentException("Id number should not be less than 0!");
        this.idNumber = idNumber;
    }

    public BaseEntity(String id)
    {
        this.id = id;
    }
}
