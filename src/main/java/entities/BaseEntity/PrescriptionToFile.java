package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDateTime;
import java.util.Objects;

public class PrescriptionToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "PR";

    private String medicationName;
    private String dosage;
    private String frequency;
    private int durationDays;
    private String instructions;
    private LocalDateTime issuedAt;

    public String getMedicationName()
    {
        return medicationName;
    }

    public String getDosage()
    {
        return dosage;
    }

    public String getFrequency()
    {
        return frequency;
    }

    public int getDurationDays()
    {
        return durationDays;
    }

    public String getInstructions()
    {
        return instructions;
    }

    public LocalDateTime getIssuedAt()
    {
        return issuedAt;
    }

    public PrescriptionToFile(String id, String medicationName, String dosage,
                              String frequency, int durationDays,
                              String instructions, LocalDateTime issuedAt)
    {
        super(id);

        if (durationDays < 1)
            throw new IllegalArgumentException("Duration cannot be less than 1 day");

        if (issuedAt == null)
            throw new IllegalArgumentException("Issued time cannot be null");

        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.instructions = instructions;
        this.issuedAt = issuedAt;
    }

    public PrescriptionToFile(String[] data)
    {
        this
                (
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        Integer.parseInt(data[4]),
                        data[5],
                        LocalDateTime.parse(data[6])
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
                        this.getMedicationName() + "|" +
                        this.getDosage() + "|" +
                        this.getFrequency() + "|" +
                        this.getDurationDays() + "|" +
                        this.getInstructions() + "|" +
                        this.getIssuedAt();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        PrescriptionToFile prescription = (PrescriptionToFile) o;
        if (!Objects.equals(medicationName, prescription.medicationName)) return false;
        if (!Objects.equals(dosage, prescription.dosage)) return false;
        if (!Objects.equals(frequency, prescription.frequency)) return false;
        if (durationDays != prescription.durationDays) return false;
        if (!Objects.equals(instructions, prescription.instructions)) return false;
        return Objects.equals(issuedAt, prescription.issuedAt);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), medicationName, dosage,
                frequency, durationDays, instructions, issuedAt);
    }
}