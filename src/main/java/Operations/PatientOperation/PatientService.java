package Operations.PatientOperation;

import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Doctor;

import java.time.LocalDateTime;

public interface PatientService
{
    void updateProfile(String name, String password, UserWithDetails.Gender gender,
                        String dob, String email, String phone);

    void bookAppointment(Doctor doctor, LocalDateTime time, String reason);

    void rescheduleAppointment(Appointment appointment, LocalDateTime newTime);

    void cancelAppointment(Appointment appointment);

    void submitFeedback(Appointment appointment, int rating, String comment);
}