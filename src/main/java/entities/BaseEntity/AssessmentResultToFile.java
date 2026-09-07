package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDateTime;
import java.util.Objects;

public class AssessmentResultToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "RS";

    private String result;
    private String remark;
    private LocalDateTime completedTime;

    public String getResult()
    {
        return result;
    }

    public String getRemark()
    {
        return remark;
    }

    public LocalDateTime getCompletedTime()
    {
        return completedTime;
    }

    public AssessmentResultToFile(String id, String result, String remark, LocalDateTime completedTime)
    {
        super(id);
        this.result = result;
        this.remark = remark;
        this.completedTime = completedTime;
    }

    public AssessmentResultToFile(String[] data)
    {
        this
                (
                        data[0],
                        data[1],
                        data[2],
                        LocalDateTime.parse(data[3])
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
                        this.getResult() + "|" +
                        this.getRemark() + "|" +
                        this.getCompletedTime();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        AssessmentResultToFile resultToFile = (AssessmentResultToFile) o;
        if (!Objects.equals(result, resultToFile.result)) return false;
        if (!Objects.equals(remark, resultToFile.remark)) return false;
        return Objects.equals(completedTime, resultToFile.completedTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), result, remark, completedTime);
    }
}