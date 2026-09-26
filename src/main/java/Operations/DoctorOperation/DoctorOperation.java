/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Operations.DoctorOperation;

import Tools.HospitalEntityAllocator;
import entities.BaseEntity.*;
import entities.BaseEntity.Users.PatientToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DoctorOperation {

    //Constructer
    private final HospitalEntityAllocator allocator;
    private final Doctor doctor;

    public DoctorOperation(HospitalEntityAllocator allocator, Doctor doctor) {
        this.allocator = allocator;
        this.doctor = doctor;
    }

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

    public void updateProfile(
            String name,
            String password,
            UserWithDetails.Gender gender,
            String dob,
            String email,
            String phone) {

        validateSafeText(name, "Name");
        validateSafeText(password, "Password");
        validateSafeText(dob, "Date of Birth");
        validateSafeText(email, "Email");
        validateSafeText(phone, "Phone number");

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        if (!name.matches("^[a-zA-Z0-9\\s]+$")) {
            throw new IllegalArgumentException(
                    "Name can only contain letters, number and spaces."
            );
        }

        // Validate password
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters."
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter."
            );
        }

        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one special character."
            );
        }

        // Validate date of birth
        if (dob == null || dob.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Date of Birth cannot be empty."
            );
        }

        if (!dob.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException(
                    "DOB must follow the format YYYY-MM-DD."
            );
        }

        LocalDate birthDate;

        try {
            birthDate = LocalDate.parse(
                    dob,
                    DateTimeFormatter.ISO_LOCAL_DATE
            );
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid calendar date. Please enter a real date."
            );
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Date of Birth cannot be in the future."
            );
        }

        // Validate email
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException(
                    "Invalid email format."
            );
        }

        // Validate phone
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Phone number cannot be empty."
            );
        }

        // Validate phone number format and allow digits, +, brackets, hyphens, and spaces
        if (!phone.matches("^[0-9+()\\-\\s]+$")) {
            throw new IllegalArgumentException(
                    "Phone number contains invalid characters."
            );
        }

        // Update Doctor profile
        doctor.getSelf().setName(name.trim());
        doctor.getSelf().setPassword(password);
        doctor.getSelf().setGender(gender);
        doctor.getSelf().setDateOfBirth(birthDate);
        doctor.getSelf().setEmail(email.trim());
        doctor.getSelf().setPhoneNumber(phone);

        // Use existing allocator to save changes
        allocator.saveChanges(doctor);
    }

    public void updateAppointmentStatus(
            String appointmentId,
            AppointmentToFile.AppointmentStatus status) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Appointment status cannot be empty."
            );
        }

        AppointmentToFile appointment;

        try {
            appointment = doctor.getAppointments().get(appointmentId.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Appointment not found."
            );
        }

        appointment.setStatus(status);

        allocator.saveChanges(doctor);
    }

    public AppointmentToFile getAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        try {
            return doctor.getAppointments()
                    .get(appointmentId.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Appointment not found."
            );
        }
    }

    public PatientToFile getAppointmentPatient(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        try {
            Appointment appointment
                    = allocator.getBusinessEntity(appointmentId.trim());

            if (appointment.getDoctor() == null
                    || !appointment.getDoctor()
                            .getId()
                            .equals(doctor.getId())) {

                throw new IllegalArgumentException(
                        "Appointment does not belong to this doctor."
                );
            }

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

        //validation
        validateSafeText(patientId, "Patient ID");
        validateSafeText(diagnosis, "Diagnosis");
        validateSafeText(consultationNote, "Consultation note");
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

        if (temperature <= 0) {
            throw new IllegalArgumentException(
                    "Temperature must be greater than 0."
            );
        }

        if (heartRate <= 0) {
            throw new IllegalArgumentException(
                    "Heart rate must be greater than 0."
            );
        }

        if (systolicPressure <= 0 || diastolicPressure <= 0) {
            throw new IllegalArgumentException(
                    "Blood pressure must be greater than 0."
            );
        }

        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Diagnosis cannot be empty."
            );
        }

        if (consultationNote == null || consultationNote.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Consultation note cannot be empty."
            );
        }

        Patient patient;

        try {
            patient
                    = allocator.getBusinessEntity(
                            patientId.trim()
                    );
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
                    "Appointment must be completed before consultation."
            );
        }


        if (selectedAppointment.getMedicalRecord() != null) {
            throw new IllegalArgumentException(
                    "A medical record already exists for this appointment."
            );
        }

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

        //validation
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

    public List<PatientToFile> getMyPatients() {
        Set<String> patientIds = new LinkedHashSet<>();

        for (AppointmentToFile appointmentFile : doctor.getAppointments()) {
            Appointment appointment
                    = allocator.getBusinessEntity(appointmentFile.getId());

            PatientToFile patient = appointment.getPatient();

            if (patient != null) {
                patientIds.add(patient.getId());
            }
        }

        List<PatientToFile> patients = new ArrayList<>();

        for (String patientId : patientIds) {
            Patient patient
                    = allocator.getBusinessEntity(patientId);

            patients.add(patient.getSelf());
        }

        return patients;
    }

    public List<AppointmentToFile> getMyAppointments() {
        List<AppointmentToFile> appointments = new ArrayList<>();

        for (AppointmentToFile appointment : doctor.getAppointments()) {
            appointments.add(appointment);
        }

        return appointments;
    }
}
