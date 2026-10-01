/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Operations.DoctorOperation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import Operations.OtherOperation;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.AssessmentTypeToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.MedicalRequestToFile;
import entities.BaseEntity.PrescriptionToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.AssessmentType;
import entities.BusinessEntity.Bill;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.MedicalRequest;
import entities.BusinessEntity.Patient;

public class DoctorOperation {

    //Constructer
    private final HospitalEntityAllocator allocator;
    private final Doctor doctor;

    public DoctorOperation(HospitalEntityAllocator allocator, Doctor doctor) {
        this.allocator = allocator;
        this.doctor = doctor;
    }

    // Prevent characters that could break the pipe-separated text file format.
    private void validateSafeText(String value, String fieldName) {
        if (value != null
                && (value.contains("|")
                || value.contains("\n")
                || value.contains("\r"))) {

            throw new IllegalArgumentException(
                    fieldName
                    + " cannot contain '|' or line breaks."
            );
        }
    }

    // Update the current doctor's profile information after validating the input.
    public void updateProfile(
            String name,
            String password,
            UserWithDetails.Gender gender,
            String dob,
            String email,
            String phone) {
        LocalDate birthDate;    // Store the converted date of birth.
        
        // Validate and convert the date of birth into LocalDate format.
        try {
            birthDate = LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Date of Birth must be a real date in YYYY-MM-DD format.");
        }

        // Ensure a current doctor is available.
        if (doctor == null) {
            throw new IllegalStateException("Current doctor is unavailable.");
        }

        DoctorToFile self = doctor.getSelf();
        if (self == null) {
            throw new IllegalStateException("Current doctor data is unavailable.");
        }

        // Validate every value before changing the current in-memory doctor.
        DoctorToFile validatedProfile = new DoctorToFile(
            self.getId(), name, password, email, gender, birthDate, phone);
        
        self.setName(validatedProfile.getName());
        self.setPassword(validatedProfile.getPassword());
        self.setEmail(validatedProfile.getEmail());
        self.setGender(validatedProfile.getGender());
        self.setDateOfBirth(validatedProfile.getDateOfBirth());
        self.setPhoneNumber(validatedProfile.getPhoneNumber());

        // Save the updated doctor profile.
        allocator.saveChanges(doctor);
    }

    // Generate a bill for the selected appointment.
    public Bill generateBill(String appointmentId)
    {
        // The appointment must belong to the current doctor.
        Appointment appointment = allocator.getBusinessEntity(appointmentId);

        // Check that the appointment belongs to the current doctor.
        if (appointment.getDoctor() == null || !appointment.getDoctor().getId().equals(doctor.getId()))
        {
            throw new IllegalArgumentException("Appointment does not belong to this doctor.");
        }

        // Delegate the actual bill generation to OtherOperation.
        return new OtherOperation(allocator).generateBill(appointmentId);
    }

    // Update the status of an appointment assigned to the current doctor.
    public void updateAppointmentStatus(String appointmentId,AppointmentToFile.AppointmentStatus status) {

        // Validate that the appointment ID is not null or empty.
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID cannot be empty.");
        }

        // Validate that a valid appointment status is provided.
        if (status == null) {
            throw new IllegalArgumentException("Appointment status cannot be empty.");
        }

        AppointmentToFile appointment;

        // Retrieve the appointment from the current doctor's appointment list.
        try {
            appointment = doctor.getAppointments().get(appointmentId.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Appointment not found.");
        }

        // Apply the new appointment status selected by the doctor.
        appointment.setStatus(status);
        // Save the updated appointment information.
        allocator.saveChanges(doctor);
    }

    // Retrieve an appointment from the current doctor's appointment list.
    public AppointmentToFile getAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        try {
            return doctor.getAppointments().get(appointmentId.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Appointment not found.");
        }
    }

    // Retrieve the patient associated with an appointment.
    // The appointment must belong to the current doctor.
    public PatientToFile getAppointmentPatient(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        try {
            Appointment appointment = allocator.getBusinessEntity(appointmentId.trim());

            if (appointment.getDoctor() == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
                throw new IllegalArgumentException("Appointment does not belong to this doctor.");
            }

            // Return the patient linked to the selected appointment.
            return appointment.getPatient();

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Patient not found for this appointment."
            );
        }
    }

    public void saveConsultation(
            String patientId,
            String appointmentId,
            double temperature,
            int heartRate,
            int systolicPressure,
            int diastolicPressure,
            String diagnosis,
            String consultationNote) {

        // Validate the consultation input before processing the request.
        validateSafeText(patientId, "Patient ID");
        validateSafeText(diagnosis, "Diagnosis");
        validateSafeText(consultationNote, "Consultation note");
        validateSafeText(appointmentId, "Appointment ID");

        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID cannot be empty.");
        }

        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        }

        if (temperature <= 0) {
            throw new IllegalArgumentException("Temperature must be greater than 0.");
        }

        if (heartRate <= 0) {
            throw new IllegalArgumentException("Heart rate must be greater than 0.");
        }

        if (systolicPressure <= 0 || diastolicPressure <= 0) {
            throw new IllegalArgumentException("Blood pressure must be greater than 0.");
        }

        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty.");
        }

        if (consultationNote == null || consultationNote.trim().isEmpty()) {
            throw new IllegalArgumentException("Consultation note cannot be empty.");
        }

        Patient patient;

        // Retrieve the selected patient from the data layer.
        try {
            patient = allocator.getBusinessEntity(patientId.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Patient not found.");
        }

        Appointment selectedAppointment;

        // Retrieve the appointment selected by the doctor.
        try {
            selectedAppointment = allocator.getBusinessEntity(appointmentId.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Appointment not found.");
        }

        // Make sure the appointment belongs to the current doctor.
        if (selectedAppointment.getDoctor() == null || !selectedAppointment.getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalArgumentException("Appointment does not belong to this doctor.");
        }

        // Make sure the appointment belongs to the selected patient.
        if (selectedAppointment.getPatient() == null || !selectedAppointment.getPatient().getId().equals(patientId.trim())) {
            throw new IllegalArgumentException("Appointment does not belong to this patient.");
        }

        // A consultation can only be recorded after the appointment is completed.
        if (selectedAppointment.getSelf().getStatus() != AppointmentToFile.AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Appointment must be completed before consultation.");
        }

        // Prevent duplicate medical records for the same appointment.
        if (selectedAppointment.getMedicalRecord() != null) {
            throw new IllegalArgumentException("A medical record already exists for this appointment.");
        }

        // Create a new medical record using the consultation information.
        MedicalRecordToFile medicalRecord
                = new MedicalRecordToFile(
                        null,
                        temperature,
                        heartRate,
                        systolicPressure,
                        diastolicPressure,
                        diagnosis.trim(),
                        consultationNote.trim()
                );

        allocator.assignNewId(medicalRecord);
        // Link the new medical record to the selected appointment.
        selectedAppointment.setMedicalRecord(medicalRecord);
        allocator.saveChanges(selectedAppointment);
    }


    public void savePrescription(
            String patientId,
            String appointmentId,
            String medicationName,
            String dosage,
            String frequency,
            int durationDays,
            String instructions) {

        // Validate all prescription input before processing the request.
        validateSafeText(patientId, "Patient ID");
        validateSafeText(medicationName, "Medication name");
        validateSafeText(dosage, "Dosage");
        validateSafeText(frequency, "Frequency");
        validateSafeText(instructions, "Instructions");
        validateSafeText(appointmentId, "Appointment ID");

        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient ID cannot be empty."
            );
        }

        if (medicationName == null || medicationName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Medication name cannot be empty."
            );
        }

        if (dosage == null || dosage.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Dosage cannot be empty."
            );
        }

        if (frequency == null || frequency.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Frequency cannot be empty."
            );
        }

        if (durationDays < 1) {
            throw new IllegalArgumentException(
                    "Duration must be at least 1 day."
            );
        }

        if (instructions == null || instructions.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Instructions cannot be empty."
            );
        }

        Patient patient;

        try {
            patient = allocator.getBusinessEntity(patientId.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Patient not found."
            );
        }

        Appointment selectedAppointment;

        try {
            selectedAppointment
                    = allocator.getBusinessEntity(
                            appointmentId.trim()
                    );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Appointment not found."
            );
        }

        if (selectedAppointment.getDoctor() == null
                || !selectedAppointment.getDoctor()
                        .getId()
                        .equals(doctor.getId())) {

            throw new IllegalArgumentException(
                    "Appointment does not belong to this doctor."
            );
        }

        if (selectedAppointment.getPatient() == null
                || !selectedAppointment.getPatient()
                        .getId()
                        .equals(patientId.trim())) {

            throw new IllegalArgumentException(
                    "Appointment does not belong to this patient."
            );
        }

        if (selectedAppointment.getSelf().getStatus()
                != AppointmentToFile.AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Appointment must be completed before prescription."
            );
        }

        if (selectedAppointment.getMedicalRecord() == null) {
            throw new IllegalArgumentException(
                    "No medical record found for this appointment."
            );
        }

        MedicalRecordToFile record = selectedAppointment.getMedicalRecord();
        MedicalRecord medicalRecord = allocator.getBusinessEntity(record.getId());

        PrescriptionToFile prescription
                = new PrescriptionToFile(
                        null,
                        medicationName.trim(),
                        dosage.trim(),
                        frequency.trim(),
                        durationDays,
                        instructions.trim(),
                        java.time.LocalDateTime.now()
                );

        allocator.assignNewId(prescription);
        medicalRecord.getPrescriptions().add(prescription);
        allocator.saveChanges(medicalRecord);
    }

    
    public void sendMedicalRequest(
            String patientId,
            String appointmentId,
            String assessmentTypeId,
            String remark) {

        //validation
        validateSafeText(patientId, "Patient ID");
        validateSafeText(appointmentId, "Appointment ID");
        validateSafeText(assessmentTypeId, "Assessment Type ID");
        validateSafeText(remark, "Remark");

        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient ID cannot be empty."
            );
        }

        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        if (assessmentTypeId == null
                || assessmentTypeId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Assessment Type cannot be empty."
            );
        }

        if (remark == null || remark.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Remark cannot be empty."
            );
        }

        // Find patient
        Patient patient;

        try {
            patient = allocator.getBusinessEntity(
                    patientId.trim()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Patient not found."
            );
        }

        // Find the selected appointment
        Appointment selectedAppointment;

        try {
            selectedAppointment
                    = allocator.getBusinessEntity(
                            appointmentId.trim()
                    );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Appointment not found."
            );
        }

        // Make sure the appointment belongs to this doctor
        if (selectedAppointment.getDoctor() == null
                || !selectedAppointment.getDoctor()
                        .getId()
                        .equals(doctor.getId())) {

            throw new IllegalArgumentException(
                    "Appointment does not belong to this doctor."
            );
        }

        // Make sure the appointment belongs to the selected patient
        if (selectedAppointment.getPatient() == null
                || !selectedAppointment.getPatient()
                        .getId()
                        .equals(patientId.trim())) {

            throw new IllegalArgumentException(
                    "Appointment does not belong to this patient."
            );
        }

        // Only completed appointments can be used
        if (selectedAppointment.getSelf().getStatus()
                != AppointmentToFile.AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Appointment must be completed before medical request."
            );
        }

        // A medical record is required
        if (selectedAppointment.getMedicalRecord() == null) {
            throw new IllegalArgumentException(
                    "No medical record found for this appointment."
            );
        }

        MedicalRecordToFile record
                = selectedAppointment.getMedicalRecord();

        MedicalRecord medicalRecord
                = allocator.getBusinessEntity(
                        record.getId()
                );
        
        // Prevent a medical request from being created after the bill has been generated.
        if (medicalRecord.getBill() != null)
        {
            throw new IllegalStateException(
                    "Cannot create a medical request after the bill " +
                            "has been generated."
            );
        }

        // Find the exact assessment type selected by the doctor
        AssessmentType selectedAssessmentType;

        try {
            selectedAssessmentType
                    = allocator.getBusinessEntity(
                            assessmentTypeId.trim()
                    );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Assessment type not found."
            );
        }

        // Ensure a specific medical request assessment type is selected.
        if (selectedAssessmentType.getSelf().getCategory()
                == AssessmentTypeToFile.AssessmentCategory.GENERAL_CHECKUP) {

            throw new IllegalArgumentException(
                    "Please select a specific medical request type."
            );
        }

        // Create medical request
        MedicalRequestToFile request
                = new MedicalRequestToFile(
                        null,
                        java.time.LocalDateTime.now(),
                        remark.trim(),
                        MedicalRequestToFile.RequestStatus.PENDING
                );

        allocator.assignNewId(request);

        MedicalRequest medicalRequest
                = allocator.convertToBusinessEntity(
                        request,
                        true
                );

        // Set Medical Record relationship
        medicalRequest.setMedicalRecord(record);

        // Set exact Assessment Type selected by doctor
        medicalRequest.setAssessmentType(
                selectedAssessmentType.getSelf()
        );

        // Add request to Medical Record
        medicalRecord.getMedicalRequests().add(request);

        // Save Medical Request and its relationships
        allocator.saveChanges(medicalRequest);

        // Save Medical Record -> Medical Request relationship
        allocator.saveChanges(medicalRecord);
    }

    // Retrieve all unique patients who have appointments with the current doctor.
    public List<PatientToFile> getMyPatients() {
        
        // LinkedHashSet prevents the same patient from appearing multiple times.
        Set<String> patientIds = new LinkedHashSet<>();

        // Go through all appointments assigned to the current doctor.
        for (AppointmentToFile appointmentFile : doctor.getAppointments()) {

            // allocate appointment object according the ids
            Appointment appointment = allocator.getBusinessEntity(appointmentFile.getId());
            // get patient from appoinment
            PatientToFile patient = appointment.getPatient();

            // Add the patient ID if a patient is associated with the appointment.
            if (patient != null) {
                patientIds.add(patient.getId());
            }
        }

        // Create a list to store the complete patient information.
        List<PatientToFile> patients = new ArrayList<>();

        // Retrieve each patient using the collected patient IDs.
        for (String patientId : patientIds) {
            Patient patient = allocator.getBusinessEntity(patientId);
            patients.add(patient.getSelf());
        }

         // Sort the patients by their patient ID.
        patients.sort(Comparator.comparing(PatientToFile::getId));

        // Return the sorted list of unique patients.
        return patients;
    }

    // Return all appointments assigned to the current doctor.
    public List<AppointmentToFile> getMyAppointments() {
        List<AppointmentToFile> appointments = new ArrayList<>();

        for (AppointmentToFile appointment : doctor.getAppointments()) {
            appointments.add(appointment);
        }

        return appointments;
    }

    // Retrieve medical records belonging to the selected patient
    // from appointments handled by the current doctor. 
    public List<Appointment> getMedicalRecordsForPatient(String patientId) {
        List<Appointment> records = new ArrayList<>();

        if (patientId == null || patientId.trim().isEmpty()) {
            return records;
        }

        for (AppointmentToFile appointmentFile : doctor.getAppointments()) {

            Appointment appointment
                    = allocator.getBusinessEntity(appointmentFile.getId());

            PatientToFile patient = appointment.getPatient();

            if (patient == null
                    || !patient.getId().equals(patientId.trim())) {
                continue;
            }

            if (appointment.getMedicalRecord() != null) {
                records.add(appointment);
            }
        }

        return records;
    }
}
