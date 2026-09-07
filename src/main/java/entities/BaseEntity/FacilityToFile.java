package entities.BaseEntity;


import Interfaces.ConvertToFileData;

import java.util.Objects;

public class FacilityToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "FC";

    public enum FacilityType
    {
        CONSULTATION_ROOM,
        WARD,
        LABORATORY,
        IMAGING_ROOM
    }

    private String name;
    private FacilityType facilityType;
    private int capacity;
    private boolean available;

    public String getName()
    {
        return name;
    }

    public FacilityType getFacilityType()
    {
        return facilityType;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCapacity(int capacity)
    {
        if (capacity < 1) throw new IllegalArgumentException("Capacity cannot be less than 1");
        this.capacity = capacity;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public FacilityToFile(String id, String name, FacilityType facilityType, int capacity, boolean available)
    {
        super(id);
        this.name = name;
        this.facilityType = facilityType;
        setCapacity(capacity);
        this.available = available;
    }

    public FacilityToFile(String[] data)
    {
        this
                (
                        data[0],
                        data[1],
                        FacilityType.valueOf(data[2].toUpperCase().trim()),
                        Integer.parseInt(data[3]),
                        Boolean.parseBoolean(data[4])
                );
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
                        this.getFacilityType() + "|" +
                        this.getCapacity() + "|" +
                        this.isAvailable();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        FacilityToFile facility = (FacilityToFile) o;
        if (!Objects.equals(name, facility.name)) return false;
        if (facilityType != facility.facilityType) return false;
        if (capacity != facility.capacity) return false;
        return available == facility.available;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), name, facilityType, capacity, available);
    }
}
