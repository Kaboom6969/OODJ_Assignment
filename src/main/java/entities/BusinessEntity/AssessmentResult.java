package entities.BusinessEntity;

import Exceptions.LinkerExceptions.LinkerRequireOneOnlyException;
import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.*;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;
import java.util.List;

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

    public AssessmentResult(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap,boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public AssessmentResult(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, AssessmentResultToFile self,boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        boolean requireOne = !isJustConstruct;
        medicalRecord = new LazyEntity<MedicalRecordToFile>
        (
            linkerManagerHashMap.get(MedicalRecordToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRecordToFile.PREFIX))
        );

        assessmentType = new LazyEntity<AssessmentTypeToFile>
        (
            linkerManagerHashMap.get(AssessmentTypeToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(AssessmentTypeToFile.PREFIX))
        );

        medicalRequest = new LazyEntity<MedicalRequestToFile>
        (
            linkerManagerHashMap.get(MedicalRequestToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRequestToFile.PREFIX))
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
                linkerManager.includeClass(MedicalRecordToFile.class) ||
                linkerManager.includeClass(AssessmentTypeToFile.class) ||
                linkerManager.includeClass(MedicalRequestToFile.class)
            )
            {
                if (!linkerManager.isThisRequireOne()) throw new LinkerRequireOneOnlyException();
            }
        }
        return linkerManagers;
    }
}
