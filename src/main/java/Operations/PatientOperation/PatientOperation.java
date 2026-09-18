package Operations.PatientOperation;

import Tools.HospitalEntityAllocator;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.AppointmentToFile.AppointmentStatus;
import entities.BaseEntity.FeedbackToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.PrescriptionToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient-facing operations. Implementations must use HospitalEntityAllocator
 * for persistence and business-entity loading.
 */
public class PatientOperation
{
    // Assumed slot length because DoctorShiftToFile and ConsultationRateToFile have no duration field.
    private static final long SLOT_DURATION_MINUTES = 30;
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
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException("Name can only contain letters and spaces.");
        }

        // Validate password
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (e.g., !@#$%^&*).");
        }

        // Validate date of birth
        if (dob == null || dob.trim().isEmpty()) {
            throw new IllegalArgumentException("Date of Birth cannot be empty.");
        }
        if (!dob.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("DOB must follow the format YYYY-MM-DD (e.g., 2000-01-01).");
        }
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid calendar date. Please enter a real date.");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of Birth cannot be in the future.");
        }

        // Validate email and phone
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format (missing '@').");
        }
        if (phone == null || !phone.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain digits only.");
        }
        patient.getSelf().setName(name);
        patient.getSelf().setPassword(password);
        patient.getSelf().setGender(gender);
        patient.getSelf().setDateOfBirth(birthDate);
        patient.getSelf().setPhoneNumber(phone);
        patient.getSelf().setEmail(email);
        allocator.saveChanges(patient);
    }

    /** 3. Loads all doctors available to the patient. Doctor records. */
    public List<Doctor> loadAllDoctors()
    {
        return allocator.getAllBusinessEntities(DoctorToFile.PREFIX);
    }

    /** 4.  Aggregates ratings from the doctor's completed appointments. Doctor -> Appointment -> Feedback. */
    public double loadDoctorAverageRating(Doctor doctor)
    {
        int totalRating = 0;
        int ratedAppointmentCount = 0;
        for (AppointmentToFile appointmentData : doctor.getAppointments())
        {
            if (appointmentData.getStatus() != AppointmentStatus.COMPLETED) continue;
            Appointment appointment = allocator.getBusinessEntity(appointmentData.getId());
            FeedbackToFile feedback = appointment.getFeedback();
            if (feedback == null) continue;
            totalRating += feedback.getRating();
            ratedAppointmentCount++;
        }
        if (ratedAppointmentCount == 0) return 0.0;
        return (double) totalRating / ratedAppointmentCount;
    }

    /** 5. Loads available appointment slots from doctor shifts. Doctor -> DoctorShift -> Appointment. */
    public List<LocalDateTime> loadAvailableSlots(Doctor doctor)
    {
        return loadAvailableSlots(doctor, null);
    }

    public List<LocalDateTime> loadAvailableSlots(Doctor doctor, String excludeAppointmentId)
    {
        List<LocalDateTime> availableSlots = new ArrayList<>();
        for (var shift : doctor.getDoctorShifts())
        {
            LocalDateTime shiftStart = LocalDateTime.of(shift.getShiftDate(), shift.getStartTime());
            LocalDateTime shiftEnd = LocalDateTime.of(shift.getShiftDate(), shift.getEndTime());
            LocalDateTime lastSlotStart = shiftEnd.minusMinutes(SLOT_DURATION_MINUTES);
            for (LocalDateTime slotStart = shiftStart;
                 !slotStart.isAfter(lastSlotStart);
                 slotStart = slotStart.plusMinutes(SLOT_DURATION_MINUTES))
            {
                boolean unavailable = false;
                for (AppointmentToFile appointment : doctor.getAppointments())
                {
                    if (appointment.getId().equals(excludeAppointmentId)) continue;
                    AppointmentStatus status = appointment.getStatus();
                    boolean blocksSlot = status == AppointmentStatus.BOOKED
                            || status == AppointmentStatus.RESCHEDULED;
                    if (blocksSlot && slotStart.equals(appointment.getAppointmentTime()))
                    {
                        unavailable = true;
                        break;
                    }
                }
                if (!unavailable) availableSlots.add(slotStart);
            }
        }
        return availableSlots;
    }
    
    /** 6. Books an appointment for the patient with the selected doctor and facility. Patient -> Appointment -> Doctor/Facility. */
    public void bookAppointment(Doctor doctor, Facility facility, LocalDateTime time, String reason)
    {
        if (time == null) {
            throw new IllegalArgumentException("Appointment time cannot be null.");
        }
        if (time.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time cannot be in the past.");
        }

        if (!loadAvailableSlots(doctor).contains(time)) {
            throw new IllegalArgumentException("Appointment time is not within an available doctor shift.");
        }

        AppointmentToFile appointmentData = new AppointmentToFile(
                null, time, reason, AppointmentStatus.BOOKED
        );
        Appointment appointment = allocator.convertToBusinessEntity(appointmentData, true);
        appointment.setPatient(patient.getSelf());
        appointment.setDoctor(doctor.getSelf());
        appointment.setFacility(facility.getSelf());
        allocator.saveChanges(appointment);
    }

    /** 7.  Reschedules an existing appointment to a new time. Appointment -> AppointmentToFile. */
    public void rescheduleAppointment(Appointment appointment, LocalDateTime newTime)
    {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        if (newTime == null) {
            throw new IllegalArgumentException("New appointment time cannot be null.");
        }
        if (newTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time cannot be in the past.");
        }

        Doctor doctor = allocator.getBusinessEntity(appointment.getDoctor().getId());
        if (!loadAvailableSlots(doctor, appointment.getId()).contains(newTime)) {
            throw new IllegalArgumentException("New appointment time is not within an available doctor shift.");
        }

        appointment.getSelf().setAppointmentTime(newTime);
        allocator.saveChanges(appointment);
    }

    /** 8. Cancels an appointment while preserving its linked medical record and feedback. Appointment -> AppointmentToFile. */
    public void cancelAppointment(Appointment appointment)
    {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        appointment.getSelf().setStatus(AppointmentStatus.CANCELLED);
        allocator.saveChanges(appointment);
    }

    /** 9. Loads the patient's upcoming and past appointments, including their statuses. Patient -> Appointment. */
    public List<Appointment> loadMyAppointments()
    {
        List<Appointment> appointments = new ArrayList<>();
        // Straight read of all linked appointments; no validation or status filtering is needed.
        for (AppointmentToFile appointmentData : patient.getAppointments())
        {
            appointments.add(allocator.getBusinessEntity(appointmentData.getId()));
        }
        appointments.sort((first, second) ->
                first.getSelf().getAppointmentTime().compareTo(second.getSelf().getAppointmentTime()));
        return appointments;
    }

    /** 10. Loads the patient's medical history. Patient -> Appointment -> MedicalRecord. */
    public List<MedicalRecord> loadMedicalHistory()
    {
        List<MedicalRecord> medicalHistory = new ArrayList<>();
        for (AppointmentToFile appointmentData : patient.getAppointments())
        {
            Appointment appointment = allocator.getBusinessEntity(appointmentData.getId());
            MedicalRecordToFile medicalRecordData = appointment.getMedicalRecord();
            if (medicalRecordData == null) continue;
            medicalHistory.add(allocator.getBusinessEntity(medicalRecordData.getId()));
        }
        return medicalHistory;
    }

    /** 11. Loads prescriptions belonging to a medical record. Patient -> MedicalRecord -> Prescription. */
    public List<PrescriptionToFile> loadPrescriptions(MedicalRecord record)
    {
        List<PrescriptionToFile> prescriptions = new ArrayList<>();
        for (PrescriptionToFile prescription : record.getPrescriptions())
        {
            prescriptions.add(prescription);
        }
        return prescriptions;
    }

    /** 12. Submits one feedback record for a completed appointment that has no feedback yet. Appointment -> Feedback. */
    public void submitFeedback(Appointment appointment, int rating, String comment)
    {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        if (appointment.getSelf().getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Feedback can only be submitted for completed appointments.");
        }
        if (appointment.getFeedback() != null) {
            throw new IllegalArgumentException("Feedback already exists for this appointment.");
        }

        FeedbackToFile feedbackData = new FeedbackToFile(
                null,
                rating,
                comment,
                LocalDateTime.now()
        );
        appointment.setFeedback(feedbackData);
        allocator.saveChanges(appointment);
    }

    /** 13. Loads feedback submitted by the patient. Patient -> Appointment -> Feedback. */
    public List<FeedbackToFile> loadMyFeedback()
    {
        List<FeedbackToFile> feedbackList = new ArrayList<>();
        for (AppointmentToFile appointmentData : patient.getAppointments())
        {
            Appointment appointment = allocator.getBusinessEntity(appointmentData.getId());
            FeedbackToFile feedback = appointment.getFeedback();
            if (feedback != null) {
                feedbackList.add(feedback);
            }
        }
        return feedbackList;
    }

    /** 14. Loads the patient's insurance status. Patient -> Insurance. */
    public InsuranceToFile loadInsuranceStatus()
    {
        return patient.getInsurance();
    }

    /** 15. Loads the next upcoming appointment, or null when none exists. Patient -> Appointment. */
    public Appointment loadNextUpcomingAppointment()
    {
        LocalDateTime now = LocalDateTime.now();
        Appointment nextAppointment = null;
        for (Appointment appointment : loadMyAppointments())
        {
            AppointmentStatus status = appointment.getSelf().getStatus();
            LocalDateTime appointmentTime = appointment.getSelf().getAppointmentTime();
            if ((status != AppointmentStatus.BOOKED && status != AppointmentStatus.RESCHEDULED)
                    || !appointmentTime.isAfter(now)) continue;
            if (nextAppointment == null
                    || appointmentTime.isBefore(nextAppointment.getSelf().getAppointmentTime()))
            {
                nextAppointment = appointment;
            }
        }
        return nextAppointment;
    }
}