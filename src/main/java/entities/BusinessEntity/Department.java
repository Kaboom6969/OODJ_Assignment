package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.ConsultationRateToFile;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Department extends BusinessEntity<DepartmentToFile> implements OwnEntities,Linkable
{
    private final LazyEntityList<DoctorToFile> doctors;
    private final LazyEntityList<FacilityToFile> facilities;
    private final LazyEntityList<ConsultationRateToFile> consultations;

    public LazyEntityList<DoctorToFile> getDoctors()
    {
        return doctors;
    }
    public LazyEntityList<FacilityToFile> getFacilities()
    {
        return facilities;
    }
    public LazyEntityList<ConsultationRateToFile> getConsultations()
    {
        return consultations;
    }
    public Department(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String,LinkerManager> linkerManagerHashMap,boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public Department(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String,LinkerManager> linkerManagerHashMap, DepartmentToFile self, boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        doctors = new LazyEntityList<DoctorToFile>
        (
                linkerManagerHashMap.get(DoctorToFile.PREFIX).findBasedOnKey(selfId),
                new EntityHandler(fileDataHandlerHashMap.get(DoctorToFile.PREFIX))
        );
        facilities = new LazyEntityList<FacilityToFile>
        (
                linkerManagerHashMap.get(FacilityToFile.PREFIX).findBasedOnKey(selfId),
                new EntityHandler(fileDataHandlerHashMap.get(FacilityToFile.PREFIX))
        );
        consultations = new LazyEntityList<ConsultationRateToFile>
        (
                linkerManagerHashMap.get(ConsultationRateToFile.PREFIX).findBasedOnKey(selfId),
                new EntityHandler(fileDataHandlerHashMap.get(ConsultationRateToFile.PREFIX))
        );
    }
}
