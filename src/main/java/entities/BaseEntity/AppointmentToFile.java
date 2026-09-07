package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDateTime;
import java.util.Objects;

public class AppointmentToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "AP";

    public enum AppointmentStatus
    {
        BOOKED,
        RESCHEDULED,
        COMPLETED,
        CANCELLED
    }

    private LocalDateTime appointmentTime;
    private String reason;
    private AppointmentStatus status;

    public LocalDateTime getAppointmentTime()
    {
        return appointmentTime;
    }

    public String getReason()
    {
        return reason;
    }

    public AppointmentStatus getStatus()
    {
        return status;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime)
    {
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.RESCHEDULED;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public void setStatus(AppointmentStatus status)
    {
        this.status = status;
    }

    public AppointmentToFile(String id, LocalDateTime appointmentTime, String reason, AppointmentStatus status)
    {
        super(id);
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
    }

    public AppointmentToFile(String[] data)
    {
        this
                (
                        data[0],
                        LocalDateTime.parse(data[1]),
                        data[2],
                        AppointmentStatus.valueOf(data[3].toUpperCase().trim())
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
                        this.getAppointmentTime() + "|" +
                        this.getReason() + "|" +
                        this.getStatus();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        AppointmentToFile appointment = (AppointmentToFile) o;
        if (!Objects.equals(appointmentTime, appointment.appointmentTime)) return false;
        if (!Objects.equals(reason, appointment.reason)) return false;
        return status == appointment.status;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), appointmentTime, reason, status);
    }
}
