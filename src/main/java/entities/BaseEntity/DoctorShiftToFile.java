package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class DoctorShiftToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "SH";

    private LocalDate shiftDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public LocalDate getShiftDate()
    {
        return shiftDate;
    }

    public LocalTime getStartTime()
    {
        return startTime;
    }

    public LocalTime getEndTime()
    {
        return endTime;
    }

    public DoctorShiftToFile(String id, LocalDate shiftDate, LocalTime startTime, LocalTime endTime)
    {
        super(id);
        if (!endTime.isAfter(startTime))
            throw new IllegalArgumentException("End time should be after start time");
        this.shiftDate = shiftDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DoctorShiftToFile(String[] data)
    {
        this
                (
                        data[0],
                        LocalDate.parse(data[1]),
                        LocalTime.parse(data[2]),
                        LocalTime.parse(data[3])
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
                        this.getShiftDate() + "|" +
                        this.getStartTime() + "|" +
                        this.getEndTime();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        DoctorShiftToFile shift = (DoctorShiftToFile) o;
        if (!Objects.equals(shiftDate, shift.shiftDate)) return false;
        if (!Objects.equals(startTime, shift.startTime)) return false;
        return Objects.equals(endTime, shift.endTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), shiftDate, startTime, endTime);
    }
}