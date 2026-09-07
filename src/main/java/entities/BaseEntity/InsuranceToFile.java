package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.util.Objects;

public class InsuranceToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "IN";

    private String companyName;
    private int coveragePercentage;
    private boolean accepted;

    public String getCompanyName()
    {
        return companyName;
    }

    public int getCoveragePercentage()
    {
        return coveragePercentage;
    }

    public boolean isAccepted()
    {
        return accepted;
    }

    public void setCoveragePercentage(int coveragePercentage)
    {
        if (coveragePercentage < 0 || coveragePercentage > 100)
            throw new IllegalArgumentException("Coverage percentage should be between 0 and 100");
        this.coveragePercentage = coveragePercentage;
    }

    public void setAccepted(boolean accepted)
    {
        this.accepted = accepted;
    }

    public InsuranceToFile(String id, String companyName, int coveragePercentage, boolean accepted)
    {
        super(id);
        this.companyName = companyName;
        setCoveragePercentage(coveragePercentage);
        this.accepted = accepted;
    }

    public InsuranceToFile(String[] data)
    {
        this
                (
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        Boolean.parseBoolean(data[3])
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
                        this.getCompanyName() + "|" +
                        this.getCoveragePercentage() + "|" +
                        this.isAccepted();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        InsuranceToFile insurance = (InsuranceToFile) o;
        if (!Objects.equals(companyName, insurance.companyName)) return false;
        if (coveragePercentage != insurance.coveragePercentage) return false;
        return accepted == insurance.accepted;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), companyName, coveragePercentage, accepted);
    }
}