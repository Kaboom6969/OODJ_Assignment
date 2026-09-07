package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class MedicalManager extends BusinessEntity<MedicalManagerToFile> implements OwnEntities, Linkable
{
    private LazyEntityList<DoctorToFile> doctors;

    public LazyEntityList<DoctorToFile> getDoctors()
    {
        return doctors;
    }

    public MedicalManager(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);
        doctors = new LazyEntityList<DoctorToFile>
        (
            linkerManagerHashMap.get(DoctorToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(DoctorToFile.PREFIX))
        );
    }
}
