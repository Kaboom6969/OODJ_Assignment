package entities.BusinessEntity;

import Exceptions.LinkerExceptions.LinkerRequireOneOnlyException;
import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.DoctorShiftToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.LazyEntity.LazyEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Doctor extends BusinessEntity<DoctorToFile> implements OwnEntity, OwnEntities,Linkable
{
    private LazyEntity<DepartmentToFile> belongsToDepartment;
    private LazyEntity<MedicalManagerToFile> belongsToMedicalManager;
    private LazyEntityList<DoctorShiftToFile> doctorShifts;
    private LazyEntityList<AppointmentToFile> appointments;
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
            linkerManagerHashMap.get(DepartmentToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(DepartmentToFile.PREFIX))
        );
        belongsToMedicalManager = new LazyEntity<MedicalManagerToFile>
        (
                linkerManagerHashMap.get(MedicalManagerToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
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
    @Override
    public List<LinkerManager> getLinkerManager()
    {
        List<LinkerManager> linkerManagers = getLinkerManagerWithoutValidate();
        for (LinkerManager linkerManager : linkerManagers)
        {
            if
            (
                linkerManager.includeClass(DepartmentToFile.class) ||
                linkerManager.includeClass(MedicalManagerToFile.class)
            )
            {
                if (!linkerManager.isThisRequireOne()) throw new LinkerRequireOneOnlyException();
            }
        }
        return linkerManagers;
    }

}
