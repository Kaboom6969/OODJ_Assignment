package entities.BusinessEntity;

import Exceptions.LinkerExceptions.LinkerRequireOneOnlyException;
import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.*;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;
import java.util.List;

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

    public MedicalRequest(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public MedicalRequest(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, MedicalRequestToFile self,boolean isJustConstruct)
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
        assessmentResults = new LazyEntityList<AssessmentResultToFile>
        (
            linkerManagerHashMap.get(AssessmentResultToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(AssessmentResultToFile.PREFIX))
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
                linkerManager.includeClass(AssessmentTypeToFile.class)
            )
            {
                if (!linkerManager.isThisRequireOne()) throw new LinkerRequireOneOnlyException();
            }
        }
        return linkerManagers;
    }
}
