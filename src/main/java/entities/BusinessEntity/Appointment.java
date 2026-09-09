package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.FeedbackToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;
import java.util.List;

public class Appointment extends BusinessEntity<AppointmentToFile> implements OwnEntity, Linkable
{
    private LazyEntity<PatientToFile> patient;
    private LazyEntity<DoctorToFile> doctor;
    private LazyEntity<FacilityToFile> facility;
    private LazyEntity<MedicalRecordToFile> medicalRecord;
    private LazyEntity<FeedbackToFile> feedback;

    public PatientToFile getPatient()
    {
        return patient.getSelf();
    }

    public void setPatient(PatientToFile patientToFile)
    {
        patient.changeSelf(patientToFile);
    }

    public DoctorToFile getDoctor()
    {
        return doctor.getSelf();
    }

    public void setDoctor(DoctorToFile doctorToFile)
    {
        doctor.changeSelf(doctorToFile);
    }

    public FacilityToFile getFacility()
    {
        return facility.getSelf();
    }

    public void setFacility(FacilityToFile facilityToFile)
    {
        facility.changeSelf(facilityToFile);
    }

    public MedicalRecordToFile getMedicalRecord()
    {
        return medicalRecord.getSelf();
    }

    public void setMedicalRecord(MedicalRecordToFile medicalRecordToFile)
    {
        medicalRecord.changeSelf(medicalRecordToFile);
    }

    public FeedbackToFile getFeedback()
    {
        return feedback.getSelf();
    }

    public void setFeedback(FeedbackToFile feedbackToFile)
    {
        feedback.changeSelf(feedbackToFile);
    }

    public Appointment(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);
        patient = new LazyEntity<PatientToFile>
        (
            linkerManagerHashMap.get(PatientToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(PatientToFile.PREFIX))
        );
        doctor = new LazyEntity<DoctorToFile>
        (
            linkerManagerHashMap.get(DoctorToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(DoctorToFile.PREFIX))
        );
        facility = new LazyEntity<FacilityToFile>
        (
            linkerManagerHashMap.get(FacilityToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(FacilityToFile.PREFIX))
        );
        medicalRecord = new LazyEntity<MedicalRecordToFile>
        (
            linkerManagerHashMap.get(MedicalRecordToFile.PREFIX).findBasedOnKeyOneResult(selfId, false),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRecordToFile.PREFIX))
        );
        feedback = new LazyEntity<FeedbackToFile>
        (
            linkerManagerHashMap.get(FeedbackToFile.PREFIX).findBasedOnKeyOneResult(selfId, false),
            new EntityHandler(fileDataHandlerHashMap.get(FeedbackToFile.PREFIX))
        );
    }
}
