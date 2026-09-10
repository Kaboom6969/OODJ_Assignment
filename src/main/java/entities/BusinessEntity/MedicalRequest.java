package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AssessmentResultToFile;
import entities.BaseEntity.AssessmentTypeToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.MedicalRequestToFile;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class MedicalRequest extends BusinessEntity<MedicalRequestToFile> implements OwnEntity, OwnEntities, Linkable
{
    private LazyEntity<MedicalRecordToFile> medicalRecord;
    private LazyEntity<AssessmentTypeToFile> assessmentType;
    private LazyEntityList<AssessmentResultToFile> assessmentResults;

    public MedicalRecordToFile getMedicalRecord()
    {
        return medicalRecord.getSelf();
    }

    public void setMedicalRecord(MedicalRecordToFile medicalRecordToFile)
    {
        medicalRecord.changeSelf(medicalRecordToFile);
    }

    public AssessmentTypeToFile getAssessmentType()
    {
        return assessmentType.getSelf();
    }

    public void setAssessmentType(AssessmentTypeToFile assessmentTypeToFile)
    {
        assessmentType.changeSelf(assessmentTypeToFile);
    }

    public LazyEntityList<AssessmentResultToFile> getAssessmentResults()
    {
        return assessmentResults;
    }

    public MedicalRequest(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null);
    }

    public MedicalRequest(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, MedicalRequestToFile self)
    {
        super(selfId, selfFile, self);
        medicalRecord = new LazyEntity<MedicalRecordToFile>
        (
            linkerManagerHashMap.get(MedicalRecordToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRecordToFile.PREFIX))
        );
        assessmentType = new LazyEntity<AssessmentTypeToFile>
        (
            linkerManagerHashMap.get(AssessmentTypeToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(AssessmentTypeToFile.PREFIX))
        );
        assessmentResults = new LazyEntityList<AssessmentResultToFile>
        (
            linkerManagerHashMap.get(AssessmentResultToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(AssessmentResultToFile.PREFIX))
        );
    }
}
