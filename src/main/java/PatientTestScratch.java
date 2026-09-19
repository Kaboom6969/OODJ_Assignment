import Operations.PatientOperation.PatientOperation;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.AppointmentToFile.AppointmentStatus;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorShiftToFile;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.FeedbackToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.PrescriptionToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.DoctorShift;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.MedicalManager;
import entities.BusinessEntity.Patient;
import entities.BusinessEntity.Prescription;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PatientTestScratch
{
    private interface CheckedStep
    {
        void run() throws Exception;
    }

    private static int passed;
    private static int failed;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) throws Exception
    {
        BaseEntity.setIdNumberWidth(4);
        Path scratchRoot = Path.of("target", "patient-scratch-data");
        Path entityDir = scratchRoot.resolve("Entity");
        Path linkerDir = scratchRoot.resolve("Linker");
        deleteDirectory(scratchRoot);
        Files.createDirectories(entityDir);
        Files.createDirectories(linkerDir);

        HospitalEntityAllocator allocator = new HospitalEntityAllocator(linkerDir, entityDir);
        Patient[] patientRef = new Patient[1];
        Doctor[] doctorRef = new Doctor[1];
        Department[] departmentRef = new Department[1];
        MedicalManager[] managerRef = new MedicalManager[1];
        Facility[] facilityRef = new Facility[1];
        DoctorShift[] primaryShiftRef = new DoctorShift[1];
        DoctorShift[] targetShiftRef = new DoctorShift[1];
        PatientOperation[] operationRef = new PatientOperation[1];
        Appointment[] appointmentRef = new Appointment[1];
        String[] medicalRecordIdRef = new String[1];
        String[] prescriptionIdRef = new String[1];
        String[] secondAppointmentIdRef = new String[1];

        LocalDate primaryDate = LocalDate.now().plusDays(1);
        LocalDate targetDate = primaryDate.plusDays(1);
        LocalTime shiftStart = LocalTime.of(9, 0);
        LocalTime shiftEnd = LocalTime.of(17, 0);
        LocalDateTime firstTime = LocalDateTime.of(primaryDate, LocalTime.of(10, 0));
        LocalDateTime rescheduledTime = LocalDateTime.of(targetDate, LocalTime.of(11, 0));

        step(1, "bootstrap patient, doctor, facility, and two shifts", () -> {
            PatientToFile patientData = new PatientToFile(
                    null, "Alice Tan", "OldPass1!", "alice@old.com",
                    UserWithDetails.Gender.FEMALE, "1995-05-20", "0111234567");
            patientRef[0] = allocator.convertToBusinessEntity(patientData, true);
            allocator.saveChanges(patientRef[0]);

                    departmentRef[0] = allocator.convertToBusinessEntity(
                    new DepartmentToFile(null, "General Medicine"), true);
                    allocator.saveChanges(departmentRef[0]);

                    managerRef[0] = allocator.convertToBusinessEntity(
                    new MedicalManagerToFile(null, "Manager One", "Manager1!",
                        "manager@hospital.com", UserWithDetails.Gender.FEMALE,
                        LocalDate.of(1975, 2, 2), "0115556666"), true);
                    allocator.saveChanges(managerRef[0]);

            DoctorToFile doctorData = new DoctorToFile(
                    null, "Dr Bob Lim", "Doctor1!", "bob@hospital.com",
                    UserWithDetails.Gender.MALE, LocalDate.of(1980, 3, 10), "0112223333");
            doctorRef[0] = allocator.convertToBusinessEntity(doctorData, true);
                doctorRef[0].setBelongsToDepartment(departmentRef[0].getSelf());
                doctorRef[0].setBelongsToMedicalManager(managerRef[0].getSelf());
            allocator.saveChanges(doctorRef[0]);

            FacilityToFile facilityData = new FacilityToFile(
                    null, "Consultation Room 1", FacilityToFile.FacilityType.CONSULTATION_ROOM,
                    1, true);
            facilityRef[0] = allocator.convertToBusinessEntity(facilityData, true);
                facilityRef[0].setBelongsToDepartment(departmentRef[0].getSelf());
            allocator.saveChanges(facilityRef[0]);

            primaryShiftRef[0] = createShift(allocator, doctorRef[0], primaryDate, shiftStart, shiftEnd);
            targetShiftRef[0] = createShift(allocator, doctorRef[0], targetDate, shiftStart, shiftEnd);
            refreshDoctor(allocator, doctorRef);
            operationRef[0] = new PatientOperation(allocator, patientRef[0]);

            printRow("Patient", entityDir, PatientToFile.class, patientRef[0].getId());
            printRow("Doctor", entityDir, DoctorToFile.class, doctorRef[0].getId());
            printRow("Facility", entityDir, FacilityToFile.class, facilityRef[0].getId());
            printRow("Primary shift", entityDir, DoctorShiftToFile.class, primaryShiftRef[0].getId());
            printRow("Target shift", entityDir, DoctorShiftToFile.class, targetShiftRef[0].getId());
        });

        step(2, "updateProfile valid and invalid email", () -> {
            operationRef[0].updateProfile(
                    "Alice Tan", "NewPass1!", UserWithDetails.Gender.FEMALE,
                    "2000-01-01", "alice@new.com", "0123456789");
            Path patientFile = entityFile(entityDir, PatientToFile.class);
            String savedRow = readRow(patientFile, patientRef[0].getId());
            require(savedRow.contains("Alice Tan|NewPass1!|alice@new.com"),
                    "valid profile row was not persisted: " + savedRow);
            String beforeInvalid = savedRow;
            try {
                operationRef[0].updateProfile(
                        "Alice Tan", "NewPass1!", UserWithDetails.Gender.FEMALE,
                        "2000-01-01", "bad-email", "0123456789");
                throw new AssertionError("invalid email was accepted");
            } catch (IllegalArgumentException expected) {
                String afterInvalid = readRow(patientFile, patientRef[0].getId());
                require(beforeInvalid.equals(afterInvalid),
                        "invalid email changed PatientToFile.txt: " + afterInvalid);
                System.out.println("    invalid-email exception: " + expected.getMessage());
            }
            printRow("Patient after profile tests", entityDir, PatientToFile.class, patientRef[0].getId());
        });

        step(3, "loadAllDoctors finds the new doctor", () -> {
            List<Doctor> doctors = operationRef[0].loadAllDoctors();
            require(doctors.stream().anyMatch(item -> item.getId().equals(doctorRef[0].getId())),
                    "new doctor was not returned");
            System.out.println("    doctors returned: " + doctors.stream().map(Doctor::getId).toList());
        });

        step(4, "loadAvailableShifts before booking", () -> {
            List<DoctorShiftToFile> shifts = operationRef[0].loadAvailableSlots(doctorRef[0]);
            require(shifts.stream().anyMatch(item -> item.getId().equals(primaryShiftRef[0].getId())),
                    "primary shift was not available before booking");
            System.out.println("    available shift ids: " + shifts.stream().map(DoctorShiftToFile::getId).toList());
        });

        step(5, "book appointment and verify persisted links", () -> {
            operationRef[0].bookAppointment(
                    doctorRef[0], facilityRef[0], firstTime, "Initial consultation");
            refreshPatientAndOperation(allocator, patientRef, operationRef);
            refreshDoctor(allocator, doctorRef);
            appointmentRef[0] = findAppointment(operationRef[0].loadMyAppointments(), firstTime);
            require(appointmentRef[0] != null, "booked appointment was not returned");
            printRow("Appointment", entityDir, AppointmentToFile.class, appointmentRef[0].getId());
            printLinkRows("Appointment links", linkerDir, appointmentRef[0].getId());
            require(hasLink(linkerDir, appointmentRef[0].getId(), patientRef[0].getId()),
                    "appointment is not linked to patient");
            require(hasLink(linkerDir, appointmentRef[0].getId(), doctorRef[0].getId()),
                    "appointment is not linked to doctor");
            require(hasLink(linkerDir, appointmentRef[0].getId(), facilityRef[0].getId()),
                    "appointment is not linked to facility");
        });

        step(6, "observe booked shift availability", () -> {
            List<DoctorShiftToFile> shifts = operationRef[0].loadAvailableSlots(doctorRef[0]);
            boolean bookedShiftStillAvailable = shifts.stream()
                    .anyMatch(item -> item.getId().equals(primaryShiftRef[0].getId()));
            System.out.println("    booked shift " + primaryShiftRef[0].getId()
                    + (bookedShiftStillAvailable ? " stays available" : " disappears entirely"));
        });

        step(7, "loadMyAppointments returns the booked appointment", () -> {
            List<Appointment> appointments = operationRef[0].loadMyAppointments();
            require(appointments.stream().anyMatch(item -> item.getId().equals(appointmentRef[0].getId())),
                    "booked appointment was not returned");
            printRow("Appointment from loadMyAppointments", entityDir,
                    AppointmentToFile.class, appointmentRef[0].getId());
        });

        step(8, "loadNextUpcomingAppointment returns the booked appointment", () -> {
            Appointment next = operationRef[0].loadNextUpcomingAppointment();
            require(next != null && next.getId().equals(appointmentRef[0].getId()),
                    "next appointment was not the booked appointment");
            System.out.println("    next appointment id: " + next.getId());
        });

        step(9, "reschedule and verify original shift reopens", () -> {
            operationRef[0].rescheduleAppointment(appointmentRef[0], rescheduledTime);
            refreshPatientAndOperation(allocator, patientRef, operationRef);
            refreshDoctor(allocator, doctorRef);
            appointmentRef[0] = allocator.getBusinessEntity(appointmentRef[0].getId());
            String row = readRow(entityFile(entityDir, AppointmentToFile.class), appointmentRef[0].getId());
            System.out.println("    rescheduled AppointmentToFile row: " + row);
            require(row.contains(rescheduledTime + "|Initial consultation|RESCHEDULED"),
                    "rescheduled row did not contain the new time and status: " + row);
            List<DoctorShiftToFile> shifts = operationRef[0].loadAvailableSlots(doctorRef[0]);
            require(shifts.stream().anyMatch(item -> item.getId().equals(primaryShiftRef[0].getId())),
                    "original shift did not become available again");
            require(!shifts.stream().anyMatch(item -> item.getId().equals(targetShiftRef[0].getId())),
                    "target shift remained available after reschedule");
            printRow("Rescheduled appointment", entityDir, AppointmentToFile.class, appointmentRef[0].getId());
        });

        step(10, "complete appointment and create medical record and prescription", () -> {
            appointmentRef[0].getSelf().setStatus(AppointmentStatus.COMPLETED);
            allocator.saveChanges(appointmentRef[0]);
            refreshDoctor(allocator, doctorRef);

            MedicalRecordToFile recordData = new MedicalRecordToFile(
                    null, 36.8, 72, 120, 80, "Routine check", "Stable");
            MedicalRecord record = allocator.convertToBusinessEntity(recordData, true);
            record.setAppointment(appointmentRef[0].getSelf());
            allocator.saveChanges(record);
            medicalRecordIdRef[0] = record.getId();

            PrescriptionToFile prescriptionData = new PrescriptionToFile(
                    null, "Paracetamol", "500mg", "Twice daily", 5,
                    "After meals", LocalDateTime.now());
            Prescription prescription = allocator.convertToBusinessEntity(prescriptionData, true);
            prescription.setMedicalRecord(record.getSelf());
            allocator.saveChanges(prescription);
            prescriptionIdRef[0] = prescription.getId();

            printRow("Completed appointment", entityDir, AppointmentToFile.class, appointmentRef[0].getId());
            printRow("Medical record", entityDir, MedicalRecordToFile.class, medicalRecordIdRef[0]);
            printRow("Prescription", entityDir, PrescriptionToFile.class, prescriptionIdRef[0]);
            printLinkRows("Medical record links", linkerDir, medicalRecordIdRef[0]);
            printLinkRows("Prescription links", linkerDir, prescriptionIdRef[0]);
        });

        step(11, "loadMedicalHistory and loadPrescriptions", () -> {
            List<MedicalRecord> history = operationRef[0].loadMedicalHistory();
            require(history.stream().anyMatch(item -> item.getId().equals(medicalRecordIdRef[0])),
                    "created medical record was not returned");
            MedicalRecord record = history.stream()
                    .filter(item -> item.getId().equals(medicalRecordIdRef[0]))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("created medical record was not loaded"));
            List<PrescriptionToFile> prescriptions = operationRef[0].loadPrescriptions(record);
            require(prescriptions.stream().anyMatch(item -> item.getId().equals(prescriptionIdRef[0])),
                    "created prescription was not returned");
            System.out.println("    history ids: " + history.stream().map(MedicalRecord::getId).toList());
            System.out.println("    prescription ids: " + prescriptions.stream().map(PrescriptionToFile::getId).toList());
        });

        step(12, "submit feedback and reject duplicate/non-completed feedback", () -> {
            operationRef[0].submitFeedback(appointmentRef[0], 5, "Excellent care");
            refreshPatientAndOperation(allocator, patientRef, operationRef);
            appointmentRef[0] = allocator.getBusinessEntity(appointmentRef[0].getId());
            FeedbackToFile feedback = appointmentRef[0].getFeedback();
            require(feedback != null, "feedback was not linked to completed appointment");
            printRow("Feedback", entityDir, FeedbackToFile.class, feedback.getId());
            printLinkRows("Feedback links", linkerDir, feedback.getId());
            require(hasLink(linkerDir, feedback.getId(), appointmentRef[0].getId()),
                    "feedback is not linked to appointment");

            expectIllegalArgument("duplicate feedback", () ->
                    operationRef[0].submitFeedback(appointmentRef[0], 4, "Duplicate"));
            Appointment nonCompleted = allocator.convertToBusinessEntity(
                    new AppointmentToFile(null, targetDate.plusDays(1).atTime(10, 0),
                            "Not completed", AppointmentStatus.BOOKED), true);
            expectIllegalArgument("non-completed feedback", () ->
                    operationRef[0].submitFeedback(nonCompleted, 4, "Not allowed"));
        });

        step(13, "loadMyFeedback returns submitted feedback", () -> {
            List<FeedbackToFile> feedback = operationRef[0].loadMyFeedback();
            require(feedback.stream().anyMatch(item -> item.getRating() == 5
                            && "Excellent care".equals(item.getComment())),
                    "submitted feedback was not returned");
            System.out.println("    feedback ids: " + feedback.stream().map(FeedbackToFile::getId).toList());
        });

        step(14, "loadDoctorAverageRating returns 5.0 and zero for unrated doctor", () -> {
            double average = operationRef[0].loadDoctorAverageRating(doctorRef[0]);
            require(Double.compare(average, 5.0) == 0, "expected doctor average 5.0 but got " + average);

            DoctorToFile secondDoctorData = new DoctorToFile(
                    null, "Dr Zero Rate", "Doctor2!", "zero@hospital.com",
                    UserWithDetails.Gender.MALE, LocalDate.of(1985, 6, 15), "0119998888");
            Doctor secondDoctor = allocator.convertToBusinessEntity(secondDoctorData, true);
                secondDoctor.setBelongsToDepartment(departmentRef[0].getSelf());
                secondDoctor.setBelongsToMedicalManager(managerRef[0].getSelf());
            allocator.saveChanges(secondDoctor);
            double zeroAverage = operationRef[0].loadDoctorAverageRating(secondDoctor);
            require(Double.compare(zeroAverage, 0.0) == 0,
                    "expected unrated doctor average 0.0 but got " + zeroAverage);
            System.out.println("    doctor averages: rated=" + average + ", unrated=" + zeroAverage);
        });

        step(15, "loadInsuranceStatus returns null", () -> {
            require(operationRef[0].loadInsuranceStatus() == null,
                    "patient unexpectedly has insurance on file");
            System.out.println("    insurance row: null (no insurance on record)");
        });

        step(16, "cancel second appointment without deleting it", () -> {
            refreshPatientAndOperation(allocator, patientRef, operationRef);
            refreshDoctor(allocator, doctorRef);
            operationRef[0].bookAppointment(
                    doctorRef[0], facilityRef[0],
                    primaryDate.atTime(14, 0), "Second appointment");
            refreshPatientAndOperation(allocator, patientRef, operationRef);
            Appointment secondAppointment = operationRef[0].loadMyAppointments().stream()
                    .filter(item -> item.getSelf().getStatus() == AppointmentStatus.BOOKED)
                    .filter(item -> !item.getId().equals(appointmentRef[0].getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("second appointment was not created"));
            secondAppointmentIdRef[0] = secondAppointment.getId();
            operationRef[0].cancelAppointment(secondAppointment);
            refreshPatientAndOperation(allocator, patientRef, operationRef);
            String row = readRow(entityFile(entityDir, AppointmentToFile.class), secondAppointmentIdRef[0]);
            System.out.println("    cancelled AppointmentToFile row: " + row);
            require(row.endsWith("|CANCELLED"), "cancelled row did not contain CANCELLED: " + row);
            require(operationRef[0].loadMyAppointments().stream()
                            .anyMatch(item -> item.getId().equals(secondAppointmentIdRef[0])),
                    "cancelled appointment disappeared from appointment history");
            require(operationRef[0].loadNextUpcomingAppointment() == null,
                    "cancelled appointment was returned as next upcoming appointment");
            refreshDoctor(allocator, doctorRef);
            require(operationRef[0].loadAvailableShifts(doctorRef[0]).stream()
                            .anyMatch(item -> item.getId().equals(primaryShiftRef[0].getId())),
                    "cancelled appointment still blocked its shift");
            printLinkRows("Cancelled appointment links remain", linkerDir, secondAppointmentIdRef[0]);
        });

        System.out.println();
        System.out.println("FINAL SUMMARY: " + passed + " passed, " + failed + " failed");
        if (!failures.isEmpty()) {
            System.out.println("Failures:");
            failures.forEach(item -> System.out.println("  " + item));
            throw new AssertionError("Patient flow had " + failed + " failed step(s)");
        }
    }

    private static DoctorShift createShift(HospitalEntityAllocator allocator, Doctor doctor,
                                           LocalDate date, LocalTime start, LocalTime end)
    {
        DoctorShiftToFile shiftData = new DoctorShiftToFile(null, date, start, end);
        DoctorShift shift = allocator.convertToBusinessEntity(shiftData, true);
        shift.setBelongsToDoctor(doctor.getSelf());
        allocator.saveChanges(shift);
        return shift;
    }

    private static void refreshPatientAndOperation(HospitalEntityAllocator allocator,
                                                   Patient[] patientRef,
                                                   PatientOperation[] operationRef)
    {
        patientRef[0] = allocator.getBusinessEntity(patientRef[0].getId());
        operationRef[0] = new PatientOperation(allocator, patientRef[0]);
    }

    private static void refreshDoctor(HospitalEntityAllocator allocator, Doctor[] doctorRef)
    {
        doctorRef[0] = allocator.getBusinessEntity(doctorRef[0].getId());
    }

    private static Appointment findAppointment(List<Appointment> appointments, LocalDateTime time)
    {
        return appointments.stream()
                .filter(item -> time.equals(item.getSelf().getAppointmentTime()))
                .findFirst()
                .orElse(null);
    }

    private static void step(int number, String name, CheckedStep action)
    {
        try {
            action.run();
            passed++;
            System.out.println("PASS " + number + ": " + name);
        } catch (Throwable error) {
            failed++;
            String message = error.getMessage() == null ? error.toString() : error.getMessage();
            failures.add("step " + number + " (" + name + "): " + message);
            System.out.println("FAIL " + number + ": " + name + " -> " + message);
        }
    }

    private static void expectIllegalArgument(String label, CheckedStep action) throws Exception
    {
        try {
            action.run();
            throw new AssertionError(label + " was accepted");
        } catch (IllegalArgumentException expected) {
            System.out.println("    " + label + " rejected: " + expected.getMessage());
        }
    }

    private static void require(boolean condition, String message)
    {
        if (!condition) throw new AssertionError(message);
    }

    private static Path entityFile(Path entityDir, Class<?> entityClass)
    {
        return entityDir.resolve(entityClass.getSimpleName() + ".txt");
    }

    private static String readRow(Path file, String id) throws IOException
    {
        return Files.readAllLines(file).stream()
                .filter(line -> line.startsWith(id + "|"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No row for " + id + " in " + file));
    }

    private static void printRow(String label, Path entityDir, Class<?> entityClass, String id) throws IOException
    {
        Path file = entityFile(entityDir, entityClass);
        System.out.println("    " + label + " [" + file + "]: " + readRow(file, id));
    }

    private static boolean hasLink(Path linkerDir, String firstId, String secondId) throws IOException
    {
        try (var files = Files.list(linkerDir)) {
            return files.anyMatch(file -> {
                try {
                    return Files.readAllLines(file).stream()
                            .anyMatch(line -> line.contains(firstId) && line.contains(secondId));
                } catch (IOException error) {
                    throw new RuntimeException(error);
                }
            });
        }
    }

    private static void printLinkRows(String label, Path linkerDir, String id) throws IOException
    {
        System.out.println("    " + label + ":");
        try (var files = Files.list(linkerDir).sorted(Comparator.comparing(Path::toString))) {
            files.forEach(file -> {
                try {
                    Files.readAllLines(file).stream()
                            .filter(line -> line.contains(id))
                            .forEach(line -> System.out.println("      " + file.getFileName() + ": " + line));
                } catch (IOException error) {
                    throw new RuntimeException(error);
                }
            });
        }
    }

    private static void deleteDirectory(Path directory) throws IOException
    {
        if (Files.notExists(directory)) return;
        try (var files = Files.walk(directory)) {
            files.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException error) {
                    throw new RuntimeException(error);
                }
            });
        }
    }
}
