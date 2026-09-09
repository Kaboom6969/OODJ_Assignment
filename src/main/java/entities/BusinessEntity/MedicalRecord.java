package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.*;
import entities.LazyEntity.LazyEntity;
import entities.LazyEntity.LazyEntityList;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class MedicalRecord extends BusinessEntity<MedicalRecordToFile> implements OwnEntity, OwnEntities, Linkable
{
    private LazyEntity<AppointmentToFile> appointment;
    private LazyEntity<BillToFile> bill;
    private LazyEntityList<PrescriptionToFile> prescriptions;
    private LazyEntityList<MedicalRequestToFile> medicalRequests;
    private LazyEntityList<AssessmentResultToFile> assessmentResults;

    public AppointmentToFile getAppointment()
    {
        return appointment.getSelf();
    }

    public void setAppointment(AppointmentToFile appointmentToFile)
    {
        appointment.changeSelf(appointmentToFile);
    }

    public BillToFile getBill()
    {
        return bill.getSelf();
    }

    public void setBill(BillToFile billToFile)
    {
        bill.changeSelf(billToFile);
    }

    public LazyEntityList<PrescriptionToFile> getPrescriptions()
    {
        return prescriptions;
    }

    public LazyEntityList<MedicalRequestToFile> getMedicalRequests()
    {
        return medicalRequests;
    }

    public LazyEntityList<AssessmentResultToFile> getAssessmentResults()
    {
        return assessmentResults;
    }

    public MedicalRecord(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);
        appointment = new LazyEntity<AppointmentToFile>
        (
            linkerManagerHashMap.get(AppointmentToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(AppointmentToFile.PREFIX))
        );
        bill = new LazyEntity<BillToFile>
        (
            linkerManagerHashMap.get(BillToFile.PREFIX).findBasedOnKeyOneResult(selfId, false),
            new EntityHandler(fileDataHandlerHashMap.get(BillToFile.PREFIX))
        );
        prescriptions = new LazyEntityList<PrescriptionToFile>
        (
            linkerManagerHashMap.get(PrescriptionToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(PrescriptionToFile.PREFIX))
        );
        medicalRequests = new LazyEntityList<MedicalRequestToFile>
        (
            linkerManagerHashMap.get(MedicalRequestToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRequestToFile.PREFIX))
        );
        assessmentResults = new LazyEntityList<AssessmentResultToFile>
        (
            linkerManagerHashMap.get(AssessmentResultToFile.PREFIX).findBasedOnKey(selfId),
            new EntityHandler(fileDataHandlerHashMap.get(AssessmentResultToFile.PREFIX))
        );
    }
}
