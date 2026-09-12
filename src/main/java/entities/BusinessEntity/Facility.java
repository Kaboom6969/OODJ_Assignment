package entities.BusinessEntity;

import Exceptions.LinkerExceptions.LinkerRequireOneOnlyException;
import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;
import java.util.List;

public class Facility extends BusinessEntity<FacilityToFile> implements OwnEntity, OwnEntities, Linkable
{
    private LazyEntity<DepartmentToFile> belongsToDepartment;
    private LazyEntityList<AppointmentToFile> appointments;

    public DepartmentToFile getBelongsToDepartment()
    {
        return belongsToDepartment.getSelf();
    }

    public void setBelongsToDepartment(DepartmentToFile departmentToFile)
    {
        belongsToDepartment.changeSelf(departmentToFile);
    }

    public LazyEntityList<AppointmentToFile> getAppointments()
    {
        return appointments;
    }

    public Facility(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public Facility(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, FacilityToFile self, boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        boolean requireOne = !isJustConstruct;
        belongsToDepartment = new LazyEntity<DepartmentToFile>
        (
            linkerManagerHashMap.get(DepartmentToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(DepartmentToFile.PREFIX))
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
                linkerManager.includeClass(DepartmentToFile.class)
            )
            {
                if (!linkerManager.isThisRequireOne()) throw new LinkerRequireOneOnlyException();
            }
        }
        return linkerManagers;
    }
}