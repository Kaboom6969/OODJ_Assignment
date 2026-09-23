package Operations.PatientOperation;

import entities.BaseEntity.PrescriptionToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.MedicalRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientService
{
    // Core services for patient operations
    // 1. Edit personal / individual profile. 
    void updateProfile(String name, String password, UserWithDetails.Gender gender,
                        String dob, String email, String phone);

    // 2. Browse available slots of doctors’ consultations and book/reschedule/cancel booking.
    void bookAppointment(Doctor doctor, LocalDateTime time, String reason);

    void rescheduleAppointment(Appointment appointment, LocalDateTime newTime);

    void cancelAppointment(Appointment appointment);

    // 3. View personal medical history and prescriptions.
    List<MedicalRecord> loadMedicalHistory();

    List<PrescriptionToFile> loadPrescriptions(MedicalRecord record);

    // 4. Submit ratings and comments to doctors 
    void submitFeedback(Appointment appointment, int rating, String comment);
}