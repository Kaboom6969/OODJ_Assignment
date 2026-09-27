package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.util.Objects;

public class MedicalRecordToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "MR";

    private double temperature;
    private int heartRate;
    private int systolicPressure;
    private int diastolicPressure;
    private String diagnosis;
    private String consultationNote;

    public double getTemperature()
    {
        return temperature;
    }

    public int getHeartRate()
    {
        return heartRate;
    }

    public int getSystolicPressure()
    {
        return systolicPressure;
    }

    public int getDiastolicPressure()
    {
        return diastolicPressure;
    }

    public String getDiagnosis()
    {
        return diagnosis;
    }

    public String getConsultationNote()
    {
        return consultationNote;
    }

    public void setDiagnosis(String diagnosis)
    {
        this.diagnosis = diagnosis;
    }

    public void setConsultationNote(String consultationNote)
    {
        this.consultationNote = consultationNote;
    }

    public MedicalRecordToFile(String id, double temperature, int heartRate, int systolicPressure,
                               int diastolicPressure, String diagnosis, String consultationNote)
    {
        super(id);
        this.temperature = temperature;
        this.heartRate = heartRate;
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.diagnosis = diagnosis;
        this.consultationNote = consultationNote;
    }

    public MedicalRecordToFile(String[] data)
    {
        this
                (
                        data[0],
                        Double.parseDouble(data[1]),
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]),
                        Integer.parseInt(data[4]),
                        data[5],
                        data[6]
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
                        this.getTemperature() + "|" +
                        this.getHeartRate() + "|" +
                        this.getSystolicPressure() + "|" +
                        this.getDiastolicPressure() + "|" +
                        this.getDiagnosis() + "|" +
                        this.getConsultationNote();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        MedicalRecordToFile record = (MedicalRecordToFile) o;
        if (Double.compare(temperature, record.temperature) != 0) return false;
        if (heartRate != record.heartRate) return false;
        if (systolicPressure != record.systolicPressure) return false;
        if (diastolicPressure != record.diastolicPressure) return false;
        if (!Objects.equals(diagnosis, record.diagnosis)) return false;
        return Objects.equals(consultationNote, record.consultationNote);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), temperature, heartRate, systolicPressure,
                diastolicPressure, diagnosis, consultationNote);
    }
}
