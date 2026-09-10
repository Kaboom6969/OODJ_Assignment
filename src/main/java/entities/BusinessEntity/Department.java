package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Tools.EntityHandler;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.ConsultationRateToFile;
import entities.BaseEntity.FacilityToFile;
import entities.LazyEntity.LazyEntityList;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Department extends BusinessEntity<DepartmentToFile> implements OwnEntities,Linkable
{
    private LazyEntityList<DoctorToFile> doctors;
    private LazyEntityList<FacilityToFile> facilities;
    private LazyEntityList<ConsultationRateToFile> consultations;

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
    public Department(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String,LinkerManager> linkerManagerHashMap)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null);
    }

    public Department(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String,LinkerManager> linkerManagerHashMap, DepartmentToFile self)
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
