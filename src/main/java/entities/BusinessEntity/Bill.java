package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BillToFile;
import entities.BaseEntity.ConsultationRateToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Bill extends BusinessEntity<BillToFile> implements OwnEntity, Linkable
{
    private LazyEntity<MedicalRecordToFile> medicalRecord;
    private LazyEntity<InsuranceToFile> insurance;
    private LazyEntity<ConsultationRateToFile> consultationRate;

    public MedicalRecordToFile getMedicalRecord()
    {
        return medicalRecord.getSelf();
    }

    public void setMedicalRecord(MedicalRecordToFile medicalRecordToFile)
    {
        medicalRecord.changeSelf(medicalRecordToFile);
    }

    public InsuranceToFile getInsurance()
    {
        return insurance.getSelf();
    }

    public void setInsurance(InsuranceToFile insuranceToFile)
    {
        insurance.changeSelf(insuranceToFile);
    }

    public ConsultationRateToFile getConsultationRate()
    {
        return consultationRate.getSelf();
    }

    public void setConsultationRate(ConsultationRateToFile consultationRateToFile)
    {
        consultationRate.changeSelf(consultationRateToFile);
    }

    public Bill(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);
        medicalRecord = new LazyEntity<MedicalRecordToFile>
        (
            linkerManagerHashMap.get(MedicalRecordToFile.PREFIX).findBasedOnKey(selfId).getFirst(),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRecordToFile.PREFIX))
        );
        insurance = new LazyEntity<InsuranceToFile>
        (
            linkerManagerHashMap.get(InsuranceToFile.PREFIX).findBasedOnKey(selfId).getFirst(),
            new EntityHandler(fileDataHandlerHashMap.get(InsuranceToFile.PREFIX))
        );
        consultationRate = new LazyEntity<ConsultationRateToFile>
        (
            linkerManagerHashMap.get(ConsultationRateToFile.PREFIX).findBasedOnKey(selfId).getFirst(),
            new EntityHandler(fileDataHandlerHashMap.get(ConsultationRateToFile.PREFIX))
        );
    }
}
