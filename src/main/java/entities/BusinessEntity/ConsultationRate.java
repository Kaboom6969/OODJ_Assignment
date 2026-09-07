package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.BillToFile;
import entities.BaseEntity.ConsultationRateToFile;
import entities.BaseEntity.DepartmentToFile;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class ConsultationRate extends BusinessEntity<ConsultationRateToFile> implements OwnEntity, OwnEntities, Linkable
{
    private LazyEntity<DepartmentToFile> belongsToDepartment;
    private LazyEntityList<BillToFile> bills;

    public DepartmentToFile getBelongsToDepartment()
    {
        return belongsToDepartment.getSelf();
    }

    public void setBelongsToDepartment(DepartmentToFile departmentToFile)
    {
        belongsToDepartment.changeSelf(departmentToFile);
    }

    public LazyEntityList<BillToFile> getBills()
    {
        return bills;
    }

    public ConsultationRate(String selfId, FileDataHandler selfFile, HashMap<String, FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);

        belongsToDepartment = new LazyEntity<DepartmentToFile>
        (
            linkerManagerHashMap.get(DepartmentToFile.PREFIX).findBasedOnKey(selfId).getFirst(),
            new EntityHandler(fileDataHandlerHashMap.get(DepartmentToFile.PREFIX))
        );

        bills = new LazyEntityList<BillToFile>
        (
            linkerManagerHashMap.get(BillToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(BillToFile.PREFIX))
        );
    }
}