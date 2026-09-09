package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.PrescriptionToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Prescription extends BusinessEntity<PrescriptionToFile> implements OwnEntity, Linkable
{
    private LazyEntity<MedicalRecordToFile> medicalRecord;

    public MedicalRecordToFile getMedicalRecord()
    {
        return medicalRecord.getSelf();
    }

    public void setMedicalRecord(MedicalRecordToFile medicalRecordToFile)
    {
        medicalRecord.changeSelf(medicalRecordToFile);
    }

    public Prescription(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);
        medicalRecord = new LazyEntity<MedicalRecordToFile>
        (
            linkerManagerHashMap.get(MedicalRecordToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRecordToFile.PREFIX))
        );
    }
}
