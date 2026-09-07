package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AssessmentResultToFile;
import entities.BaseEntity.AssessmentTypeToFile;
import entities.BaseEntity.MedicalRequestToFile;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class AssessmentType extends BusinessEntity<AssessmentTypeToFile> implements OwnEntities, Linkable
{
    private LazyEntityList<MedicalRequestToFile> medicalRequests;
    private LazyEntityList<AssessmentResultToFile> assessmentResults;

    public LazyEntityList<MedicalRequestToFile> getMedicalRequests()
    {
        return medicalRequests;
    }

    public LazyEntityList<AssessmentResultToFile> getAssessmentResults()
    {
        return assessmentResults;
    }

    public AssessmentType(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);

        medicalRequests = new LazyEntityList<MedicalRequestToFile>
        (
            linkerManagerHashMap.get(MedicalRequestToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRequestToFile.PREFIX))
        );

        assessmentResults = new LazyEntityList<AssessmentResultToFile>
        (
            linkerManagerHashMap.get(AssessmentResultToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(AssessmentResultToFile.PREFIX))
        );
    }
}
