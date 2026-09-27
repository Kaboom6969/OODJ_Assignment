package entities.BusinessEntity;

import Exceptions.LinkerExceptions.LinkerRequireOneOnlyException;
import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorShiftToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;
import java.util.List;

public class DoctorShift extends BusinessEntity<DoctorShiftToFile> implements OwnEntity, Linkable
{
    private LazyEntity<DoctorToFile> belongsToDoctor;
    public DoctorToFile getBelongsToDoctor()
    {
        return belongsToDoctor.getSelf();
    }

    public void setBelongsToDoctor(DoctorToFile doctorToFile)
    {
        belongsToDoctor.changeSelf(doctorToFile);
    }

    public DoctorShift(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap,boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public DoctorShift(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, DoctorShiftToFile self,boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        boolean requireOne = !isJustConstruct;
        belongsToDoctor = new LazyEntity<DoctorToFile>
        (
            linkerManagerHashMap.get(DoctorToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(DoctorToFile.PREFIX))
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
                linkerManager.includeClass(DoctorToFile.class)
            )
            {
                if (!linkerManager.isThisRequireOne()) throw new LinkerRequireOneOnlyException();
            }
        }
        return linkerManagers;
    }
}
