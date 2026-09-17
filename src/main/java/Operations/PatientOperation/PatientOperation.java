package Operations.PatientOperation;

import Tools.HospitalEntityAllocator;
import entities.BaseEntity.DoctorShiftToFile;
import entities.BaseEntity.FeedbackToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.PrescriptionToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Patient-facing operations. Implementations must use HospitalEntityAllocator
 * for persistence and business-entity loading.
 */
public class PatientOperation
{
    private final HospitalEntityAllocator allocator;
    private Patient patient;

    /** 1. Creates patient operations for the supplied allocator and logged-in patient. */
    public PatientOperation(HospitalEntityAllocator allocator, Patient patient)
    {
        this.allocator = allocator;
        this.patient = patient;
    }

    /**
     * 2. Updates and validates the patient's profile details, then saves the patient.
     * Patient -> PatientToFile
     */
    public void updateProfile(String name, String password, UserWithDetails.Gender gender,
                              String dob, String email, String phone)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 3. Loads all doctors available to the patient. Doctor records. */
    public List<Doctor> loadAllDoctors()
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 4.  Aggregates ratings from the doctor's completed appointments. Doctor -> Appointment -> Feedback. */
    public double loadDoctorAverageRating(Doctor doctor)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 5. Loads shifts that do not clash with booked or rescheduled appointments. Doctor -> DoctorShift -> Appointment. */
    public List<DoctorShiftToFile> loadAvailableShifts(Doctor doctor)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 6. Books an appointment for the patient with the selected doctor and facility. Patient -> Appointment -> Doctor/Facility. */
    public void bookAppointment(Doctor doctor, Facility facility, LocalDateTime time, String reason)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 7.  Reschedules an existing appointment to a new time. Appointment -> AppointmentToFile. */
    public void rescheduleAppointment(Appointment appointment, LocalDateTime newTime)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 8. Cancels an appointment while preserving its linked medical record and feedback. Appointment -> AppointmentToFile. */
    public void cancelAppointment(Appointment appointment)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 9. Loads the patient's upcoming and past appointments, including their statuses. Patient -> Appointment. */
    public List<Appointment> loadMyAppointments()
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 10. Loads the patient's medical history. Patient -> Appointment -> MedicalRecord. */
    public List<MedicalRecord> loadMedicalHistory()
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 11. Loads prescriptions belonging to a medical record. Patient -> MedicalRecord -> Prescription. */
    public List<PrescriptionToFile> loadPrescriptions(MedicalRecord record)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 12. Submits one feedback record for a completed appointment that has no feedback yet. Appointment -> Feedback. */
    public void submitFeedback(Appointment appointment, int rating, String comment)
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 13. Loads feedback submitted by the patient. Patient -> Appointment -> Feedback. */
    public List<FeedbackToFile> loadMyFeedback()
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 14. Loads the patient's insurance status. Patient -> Insurance. */
    public InsuranceToFile loadInsuranceStatus()
    {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /** 15. Loads the next upcoming appointment, or null when none exists. Patient -> Appointment. */
    public Appointment loadNextUpcomingAppointment()
    {
        throw new UnsupportedOperationException("not yet implemented");
    }
}