package entities.BusinessEntity;

import Exceptions.LinkerExceptions.LinkerRequireOneOnlyException;
import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AssessmentTypeToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.PrescriptionToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;
import java.util.List;

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

    public Prescription(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap,boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public Prescription(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, PrescriptionToFile self,boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        boolean requireOne = !isJustConstruct;
        medicalRecord = new LazyEntity<MedicalRecordToFile>
        (
            linkerManagerHashMap.get(MedicalRecordToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRecordToFile.PREFIX))
        );
    }

    @Override
    public List<LinkerManager> getLinkerManager()
    {
        List<LinkerManager> linkerManagers = getLinkerManagerWithoutValidate();
        for (LinkerManager linkerManager : linkerManagers)
        {
            if
            (
                linkerManager.includeClass(MedicalRecordToFile.class)
            )
            {
                if (!linkerManager.isThisRequireOne()) throw new LinkerRequireOneOnlyException();
            }
        }
        return linkerManagers;
    }
}
