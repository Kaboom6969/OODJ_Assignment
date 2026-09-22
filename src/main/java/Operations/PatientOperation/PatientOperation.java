package Operations.PatientOperation;

import Exceptions.PatientExceptions.BookingValidationException;
import Exceptions.PatientExceptions.FeedbackValidationException;
import Exceptions.PatientExceptions.ProfileValidationException;
import Operations.PatientOperation.PatientOperation.DoctorAvailability;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.AppointmentToFile.AppointmentStatus;
import entities.BaseEntity.BillToFile;
import entities.BaseEntity.FeedbackToFile;
import entities.BaseEntity.FacilityToFile;
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
import entities.BusinessEntity.Department;
import entities.BaseEntity.DepartmentToFile;

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
public class PatientOperation implements PatientService
{
    // Assumed slot length because DoctorShiftToFile and ConsultationRateToFile have no duration field.
    private static final long SLOT_DURATION_MINUTES = 30;
    // allocator is a HospitalEntityAllocator object responsible for loading and saving hospital data.
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
        // Validate date of birth
        if (dob == null || dob.trim().isEmpty()) {
            throw new ProfileValidationException("Date of Birth cannot be empty.");
        }
        if (!dob.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new ProfileValidationException("DOB must follow the format YYYY-MM-DD (e.g., 2000-01-01).");
        }
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new ProfileValidationException("Invalid calendar date. Please enter a real date.");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new ProfileValidationException("Date of Birth cannot be in the future.");
        }

        if (email != null && (email.contains("|") || email.contains("\n") || email.contains("\r"))) {
            throw new ProfileValidationException("Email cannot contain '|' or line breaks.");
        }

        try {
            patient.getSelf().setName(name);
            patient.getSelf().setPassword(password);
            patient.getSelf().setGender(gender);
            patient.getSelf().setDateOfBirth(birthDate);
            patient.getSelf().setPhoneNumber(phone);
            patient.getSelf().setEmail(email);
        } catch (IllegalArgumentException e) {
            throw new ProfileValidationException(e.getMessage());
        }
        allocator.saveChanges(patient);
    }

    /** 16. Loads all departments. Department records. */
    public List<Department> loadAllDepartments()
    {
        //PREFIX is a constant (a public static final variable) in DepartmentToFile..
        return allocator.getAllBusinessEntities(DepartmentToFile.PREFIX);
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
        if (ratedAppointmentCount == 0) {
            return 0.0;
        }else{
            return (double) totalRating / ratedAppointmentCount;
        }
    }
    
    /** 17. Filter all doctors available to the patient. Doctor records. */
    public List<Doctor> loadDoctorsByDepartment(Department department)
    {
        List<Doctor> filtered = new ArrayList<>();
        for (Doctor doctor : loadAllDoctors())
        {
            if (doctor.getBelongsToDepartment().getId().equals(department.getSelf().getId()))
            {
                filtered.add(doctor);
            }
        }
        return filtered;
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

    /** 18. Returns date-filtered results per doctor */
    // generates a complete, immutable (read-only) class behind the scenes.
    // Its purpose here is to simply bundle two pieces of data together (a specific doctor and their list of times)
    // so they can be returned as a single unit from the loadDoctorAvailability method.
    public record DoctorAvailability(Doctor doctor, List<LocalDateTime> availableSlots) {}

    public List<DoctorAvailability> loadDoctorAvailability(Department department, LocalDate date)
    {
        List<DoctorAvailability> results = new ArrayList<>();
        for (Doctor doctor : loadDoctorsByDepartment(department))
        {
            List<LocalDateTime> slotsThatDay = new ArrayList<>();
            for (LocalDateTime slot : loadAvailableSlots(doctor))
            {
                if (slot.toLocalDate().equals(date)) {
                    slotsThatDay.add(slot);
                }
            }
            if (!slotsThatDay.isEmpty()){
                 results.add(new DoctorAvailability(doctor, slotsThatDay));
            }
        }
        return results;
    }

    /** 19. Returns facility */
    private Facility findAvailableFacility(LocalDateTime time, String excludeAppointmentId)
    {
        for (Facility candidate : allocator.<Facility>getAllBusinessEntities(FacilityToFile.PREFIX)) {
            if (!candidate.getSelf().isAvailable()
                    || candidate.getSelf().getFacilityType()
                    != FacilityToFile.FacilityType.CONSULTATION_ROOM) {
                continue;
            }

            boolean occupied = false;
            for (AppointmentToFile appointmentData : candidate.getAppointments()) {
                if (appointmentData.getId().equals(excludeAppointmentId)) continue;

                AppointmentStatus status = appointmentData.getStatus();
                if ((status == AppointmentStatus.BOOKED
                        || status == AppointmentStatus.RESCHEDULED)
                        && time.equals(appointmentData.getAppointmentTime())) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied) {
                return candidate;
            }
        }
        return null;
    }

    /** 6. Books an appointment for the patient with the selected doctor and facility. Patient -> Appointment -> Doctor/Facility. */
    public void bookAppointment(Doctor doctor, LocalDateTime time, String reason)
    {
        // Only active appointments consume the patient's booking limit;
        // cancelled and completed appointments no longer count toward it.
        int activeAppointmentCount = 0;
        for (Appointment appointment : loadMyAppointments()) {
            AppointmentStatus status = appointment.getSelf().getStatus();
            if (status == AppointmentStatus.BOOKED
                    || status == AppointmentStatus.RESCHEDULED) {
                activeAppointmentCount++;
            }
        }
        if (activeAppointmentCount >= 5) {
            throw new BookingValidationException(
                    "You already have 5 active appointments. Please cancel or complete one before booking another.");
        }

        if (time == null) {
            throw new BookingValidationException("Appointment time cannot be null.");
        }
        if (time.isBefore(LocalDateTime.now())) {
            throw new BookingValidationException("Appointment time cannot be in the past.");
        }
        // Reject blank
        if (reason == null || reason.trim().isEmpty()) {
            throw new BookingValidationException("Reason cannot be empty.");
        }
        // otherwise will crash the delimiter-based file format when saving to disk, as '|' is used as a field separator.
        if (reason.contains("|")) {
            throw new BookingValidationException("Reason cannot contain the '|' character.");
        }

        if (!loadAvailableSlots(doctor).contains(time)) {
            throw new BookingValidationException("Appointment time is not within an available doctor shift.");
        }

        Facility facility = findAvailableFacility(time, null);
        if (facility == null) {
            throw new BookingValidationException("No consultation rooms are available at this time.");
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
            throw new BookingValidationException("Appointment cannot be null.");
        }
        if (newTime == null) {
            throw new BookingValidationException("New appointment time cannot be null.");
        }
        if (newTime.isBefore(LocalDateTime.now())) {
            throw new BookingValidationException("Appointment time cannot be in the past.");
        }

        Doctor doctor = allocator.getBusinessEntity(appointment.getDoctor().getId());
        if (!loadAvailableSlots(doctor, appointment.getId()).contains(newTime)) {
            throw new BookingValidationException("New appointment time is not within an available doctor shift.");
        }

        Facility facility = findAvailableFacility(newTime, appointment.getId());
        if (facility == null) {
            throw new BookingValidationException("No consultation rooms are available at the new time.");
        }

        appointment.getSelf().setAppointmentTime(newTime);
        appointment.setFacility(facility.getSelf());
        allocator.saveChanges(appointment);
    }

    /** 8. Cancels an appointment while preserving its linked medical record and feedback. Appointment -> AppointmentToFile. */
    public void cancelAppointment(Appointment appointment)
    {
        if (appointment == null) {
            throw new BookingValidationException("Appointment cannot be null.");
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

        // If first is earlier than second, it returns a negative number.
        // If first is exactly the same time as second, it returns 0.
        // If first is later than second, it returns a positive number.
        // The .sort() method uses these negative/positive numbers to figure out the order
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
            throw new FeedbackValidationException("Appointment cannot be null.");
        }
        if (appointment.getSelf().getStatus() != AppointmentStatus.COMPLETED) {
            throw new FeedbackValidationException("Feedback can only be submitted for completed appointments.");
        }
        if (appointment.getFeedback() != null) {
            throw new FeedbackValidationException("Feedback already exists for this appointment.");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new FeedbackValidationException("Comment cannot be empty.");
        }
        if (comment.contains("|")) {
            throw new FeedbackValidationException("Comment cannot contain the '|' character.");
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

    // 19. Loads the patient's billing list.
    public List<BillToFile> loadBillingList()
    {
        List<BillToFile> billList = new ArrayList<>();
        for (AppointmentToFile appointmentData : patient.getAppointments())
        {
            Appointment appointment = allocator.getBusinessEntity(appointmentData.getId());
            MedicalRecordToFile medicalRecordData = appointment.getMedicalRecord();
            if (medicalRecordData != null) {
                MedicalRecord medicalRecord = allocator.getBusinessEntity(medicalRecordData.getId());
                if (medicalRecord != null) {
                    BillToFile appointmentBill = medicalRecord.getBill();
                    if (appointmentBill != null) {
                        billList.add(appointmentBill);
                    }
                }
            }
        }
        return billList;
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