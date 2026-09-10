package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DoctorShiftToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;

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

    public DoctorShift(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null);
    }

    public DoctorShift(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, DoctorShiftToFile self)
    {
        super(selfId, selfFile, self);
        belongsToDoctor = new LazyEntity<DoctorToFile>
        (
            linkerManagerHashMap.get(DoctorToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(DoctorToFile.PREFIX))
        );
    }
}
