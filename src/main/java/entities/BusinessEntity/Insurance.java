package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BillToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Insurance extends BusinessEntity<InsuranceToFile> implements OwnEntities, Linkable
{
    private LazyEntityList<PatientToFile> patients;
    private LazyEntityList<BillToFile> bills;

    public LazyEntityList<PatientToFile> getPatients()
    {
        return patients;
    }

    public LazyEntityList<BillToFile> getBills()
    {
        return bills;
    }

    public Insurance(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public Insurance(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, InsuranceToFile self,boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        patients = new LazyEntityList<PatientToFile>
        (
            linkerManagerHashMap.get(PatientToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(PatientToFile.PREFIX))
        );
        bills = new LazyEntityList<BillToFile>
        (
            linkerManagerHashMap.get(BillToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(BillToFile.PREFIX))
        );
    }
}
