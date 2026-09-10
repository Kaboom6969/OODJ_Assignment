package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AssessmentResultToFile;
import entities.BaseEntity.AssessmentTypeToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.MedicalRequestToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class AssessmentResult extends BusinessEntity<AssessmentResultToFile> implements OwnEntity, Linkable
{
    private LazyEntity<MedicalRecordToFile> medicalRecord;
    private LazyEntity<AssessmentTypeToFile> assessmentType;
    private LazyEntity<MedicalRequestToFile> medicalRequest;

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

    public MedicalRequestToFile getMedicalRequest()
    {
        return medicalRequest.getSelf();
    }

    public void setMedicalRequest(MedicalRequestToFile medicalRequestToFile)
    {
        medicalRequest.changeSelf(medicalRequestToFile);
    }

    public AssessmentResult(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null);
    }

    public AssessmentResult(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, AssessmentResultToFile self)
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

        medicalRequest = new LazyEntity<MedicalRequestToFile>
        (
            linkerManagerHashMap.get(MedicalRequestToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRequestToFile.PREFIX))
        );
    }
}
