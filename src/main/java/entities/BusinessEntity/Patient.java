package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Patient extends BusinessEntity<PatientToFile> implements OwnEntity, OwnEntities, Linkable
{
    private LazyEntity<InsuranceToFile> insurance;
    private LazyEntityList<AppointmentToFile> appointments;

    public InsuranceToFile getInsurance()
    {
        return insurance.getSelf();
    }

    public void setInsurance(InsuranceToFile insuranceToFile)
    {
        insurance.changeSelf(insuranceToFile);
    }

    public LazyEntityList<AppointmentToFile> getAppointments()
    {
        return appointments;
    }

    public Patient(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);
        insurance = new LazyEntity<InsuranceToFile>
        (
            linkerManagerHashMap.get(InsuranceToFile.PREFIX).findBasedOnKey(selfId).getFirst(),
            new EntityHandler(fileDataHandlerHashMap.get(InsuranceToFile.PREFIX))
        );
        appointments = new LazyEntityList<AppointmentToFile>
        (
            linkerManagerHashMap.get(AppointmentToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(AppointmentToFile.PREFIX))
        );
    }
}
