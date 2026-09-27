package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorShiftToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Doctor extends BusinessEntity<DoctorToFile> implements OwnEntity, OwnEntities,Linkable
{
    private final LazyEntity<DepartmentToFile> belongsToDepartment;
    private final LazyEntity<MedicalManagerToFile> belongsToMedicalManager;
    private final LazyEntityList<DoctorShiftToFile> doctorShifts;
    private final LazyEntityList<AppointmentToFile> appointments;
    public DepartmentToFile getBelongsToDepartment()
    {
        return belongsToDepartment.getSelf();
    }

    public void setBelongsToDepartment(DepartmentToFile departmentToFile)
    {
        belongsToDepartment.changeSelf(departmentToFile);
    }

    public MedicalManagerToFile getBelongsToMedicalManager()
    {
        return belongsToMedicalManager.getSelf();
    }
    public void setBelongsToMedicalManager(MedicalManagerToFile medicalManagerToFile)
    {
        belongsToMedicalManager.changeSelf(medicalManagerToFile);
    }

    public LazyEntityList<DoctorShiftToFile> getDoctorShifts()
    {
        return doctorShifts;
    }

    public LazyEntityList<AppointmentToFile> getAppointments()
    {
        return appointments;
    }

    public Doctor(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap,HashMap<String,LinkerManager> linkerManagerHashMap, boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null, isJustConstruct);
    }

    public Doctor(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap,HashMap<String,LinkerManager> linkerManagerHashMap, DoctorToFile self,boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        boolean requireOne = !isJustConstruct;
        belongsToDepartment = new LazyEntity<DepartmentToFile>
        (
            linkerManagerHashMap.get(DepartmentToFile.PREFIX).findBasedOnKeyOneResult(selfId,false),
            new EntityHandler(fileDataHandlerHashMap.get(DepartmentToFile.PREFIX))
        );
        belongsToMedicalManager = new LazyEntity<MedicalManagerToFile>
        (
                linkerManagerHashMap.get(MedicalManagerToFile.PREFIX).findBasedOnKeyOneResult(selfId,false),
                new EntityHandler(fileDataHandlerHashMap.get(MedicalManagerToFile.PREFIX))
        );
        doctorShifts = new LazyEntityList<DoctorShiftToFile>
        (
                linkerManagerHashMap.get(DoctorShiftToFile.PREFIX).findBasedOnKey(selfId),
                new EntityHandler(fileDataHandlerHashMap.get((DoctorShiftToFile.PREFIX)))
        );
        appointments = new LazyEntityList<AppointmentToFile>
        (
                linkerManagerHashMap.get(AppointmentToFile.PREFIX).findBasedOnKey(selfId),
                new EntityHandler(fileDataHandlerHashMap.get(AppointmentToFile.PREFIX))
        );
    }

}
