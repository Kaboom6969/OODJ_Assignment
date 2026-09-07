package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDateTime;
import java.util.Objects;

public class MedicalRequestToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "RQ";

    public enum RequestStatus
    {
        PENDING,
        APPROVED,
        IN_PROGRESS,
        COMPLETED,
        REJECTED
    }

    private LocalDateTime requestTime;
    private String remark;
    private RequestStatus status;

    public LocalDateTime getRequestTime()
    {
        return requestTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public RequestStatus getStatus()
    {
        return status;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setStatus(RequestStatus status)
    {
        this.status = status;
    }

    public MedicalRequestToFile(String id, LocalDateTime requestTime, String remark, RequestStatus status)
    {
        super(id);
        this.requestTime = requestTime;
        this.remark = remark;
        this.status = status;
    }

    public MedicalRequestToFile(String[] data)
    {
        this
                (
                        data[0],
                        LocalDateTime.parse(data[1]),
                        data[2],
                        RequestStatus.valueOf(data[3].toUpperCase().trim())
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
                        this.getRequestTime() + "|" +
                        this.getRemark() + "|" +
                        this.getStatus();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        MedicalRequestToFile request = (MedicalRequestToFile) o;
        if (!Objects.equals(requestTime, request.requestTime)) return false;
        if (!Objects.equals(remark, request.remark)) return false;
        return status == request.status;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), requestTime, remark, status);
    }
}