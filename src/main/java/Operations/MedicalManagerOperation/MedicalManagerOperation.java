package Operations.MedicalManagerOperation;

import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.*;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalManagerOperation {

    // Store allocator reference instead of direct FileDataHandler
    private final HospitalEntityAllocator allocator;
    private MedicalManager medicalManager;

    // Constructor to inject the allocator
    public MedicalManagerOperation(HospitalEntityAllocator allocator, MedicalManager medicalManager) {
        this.medicalManager = medicalManager;
        this.allocator = allocator;
    }

    // Validate inputs and update profile via allocator
    public void updateProfile(String name, String password, UserWithDetails.Gender gender, String dob, String email, String phone) {
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

        // Determine whether this is a first-time creation or an update to an existing record
        boolean isFirstTime = false;
        MedicalManagerToFile self = null;
        try {
            self = (medicalManager != null) ? medicalManager.getSelf() : null;
        } catch (Exception e) {
            // Record not found in file; treat as first-time creation
            isFirstTime = true;
        }

        if (isFirstTime || self == null) {
            // First-time creation: instantiate a new MedicalManagerToFile and save via allocator
            String id = (medicalManager != null && medicalManager.getId() != null) ? medicalManager.getId() : "MM0001";
            MedicalManagerToFile newManager = new MedicalManagerToFile(id, name, password, email, gender, birthDate, phone);

            // Convert to business entity and persist to file
            this.medicalManager = allocator.convertToBusinessEntity(newManager, true);
            allocator.saveChanges(this.medicalManager);
        } else {
            // Update existing entity in file
            self.setName(name);
            self.setPassword(password);
            self.setGender(gender);
            self.setDateOfBirth(birthDate);
            self.setPhoneNumber(phone);
            self.setEmail(email);
            allocator.saveChanges(medicalManager);
        }
    }

    // Getter for the current medical manager business entity
    public MedicalManager getMedicalManager() {
        return this.medicalManager;
    }

    // Load all department entities using allocator
    public List<Department> loadDepartmentEntities() {
        return allocator.getAllBusinessEntities(DepartmentToFile.PREFIX);
    }

    // Load departments formatted for UI table
    public ArrayList<String[]> loadDepartment() {
        ArrayList<String[]> list = new ArrayList<>();
        for (Department department : loadDepartmentEntities()) {
            list.add(new String[]{department.getId(), department.getSelf().getName()});
        }
        return list;
    }

    public String generateNextDeptId() {
        List<Department> list = loadDepartmentEntities();
        String prefix = DepartmentToFile.PREFIX;

        if (list.isEmpty()) {
            return String.format(prefix + "%04d", 1);
        }

        int maxNum = 0;
        for (Department dept : list) {
            try {
                String idStr = dept.getId();
                if (idStr.startsWith(prefix)) {
                    int num = Integer.parseInt(idStr.substring(prefix.length()));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return String.format(prefix + "%04d", maxNum + 1);
    }

    // Add new department using allocator.addEntity
    public void addDepartment(String deptId, String deptName) {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        if (deptName == null || deptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty.");
        }
        DepartmentToFile dept = new DepartmentToFile(deptId.trim(), deptName.trim());
        Department department = allocator.convertToBusinessEntity(dept, true);
        allocator.saveChanges(department);
    }

    // Update existing department using allocator.updateEntity
    public void updateDepartment(String deptId, String newDeptName) {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        if (newDeptName == null || newDeptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty.");
        }
        DepartmentToFile dept = new DepartmentToFile(deptId.trim(), newDeptName.trim());
        Department department = allocator.convertToBusinessEntity(dept, false);
        allocator.saveChanges(department);
    }

    // Delete department using allocator.removeEntity
    public void deleteDepartment(String deptId)
    {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        Department dept = allocator.getBusinessEntity(deptId.trim());
        if (dept == null) {
            throw new IllegalArgumentException("Department ID '" + deptId + "' not found.");
        }
        if (!dept.getDoctors().isEmpty() || !dept.getFacilities().isEmpty() || !dept.getConsultations().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete: This department has assigned doctors, facilities, or consultation rates.");
        }
        try
        {
            allocator.deleteBusinessEntity(dept);
        } catch (EntityNotMatchException| EntityNotFoundException e)
        {
            throw new RuntimeException("Department ID '" + deptId + "' does not exist.");
        }
    }

    // Load all doctors in the hospital
    public List<Doctor> loadDoctors() {
        List<Doctor> assigned = new ArrayList<>();
        if (this.medicalManager == null) return assigned;
        for (DoctorToFile docFile : this.medicalManager.getDoctors()) {
            try {
                Doctor docBiz = allocator.getBusinessEntity(docFile.getId());
                if (docBiz != null) {
                    assigned.add(docBiz);
                }
            } catch (Exception ignored) {}
        }
        return assigned;
    }

    // Load all doctor shifts formatted for the UI roster table
    public ArrayList<String[]> loadRosters() {
        ArrayList<String[]> rows = new ArrayList<>();
        List<DoctorShift> shifts;
        try {
            shifts = allocator.getAllBusinessEntities(DoctorShiftToFile.PREFIX);
        } catch (Exception e) {
            return rows;
        }

        // Get assigned doctor IDs
        List<String> myDoctorIds = new ArrayList<>();
        for (Doctor doc : loadDoctors()) {
            myDoctorIds.add(doc.getId());
        }

        for (DoctorShift shift : shifts) {
            try {
                String shiftId = shift.getId();
                DoctorToFile docToFile = shift.getBelongsToDoctor();

                // Filter out doctors not assigned to this manager
                if (docToFile == null || !myDoctorIds.contains(docToFile.getId())) {
                    continue;
                }

                String docName = docToFile.getName();
                String deptName = "N/A";
                // Fetch the latest department name directly to avoid stale cache
                try {
                    Doctor doctorBiz = allocator.getBusinessEntity(docToFile.getId());
                    if (doctorBiz != null && doctorBiz.getBelongsToDepartment() != null) {
                        String deptId = doctorBiz.getBelongsToDepartment().getId();
                        Department freshDept = allocator.getBusinessEntity(deptId);
                        if (freshDept != null && freshDept.getSelf() != null) {
                            deptName = freshDept.getSelf().getName();
                        } else {
                            deptName = doctorBiz.getBelongsToDepartment().getName();
                        }
                    }
                } catch (Exception ignored) {}

                LocalDate date = (shift.getSelf() != null) ? shift.getSelf().getShiftDate() : null;
                LocalTime start = (shift.getSelf() != null) ? shift.getSelf().getStartTime() : null;
                LocalTime end = (shift.getSelf() != null) ? shift.getSelf().getEndTime() : null;

                String shiftLabel = "N/A";
                if (start != null) {
                    int hour = start.getHour();
                    if (hour >= 8 && hour < 16) {
                        shiftLabel = "Morning";
                    } else if (hour >= 16) {
                        shiftLabel = "Evening";
                    } else {
                        shiftLabel = "Night";
                    }
                }
                String shiftDetails = String.format("%s (%s - %s)", shiftLabel, start, end);
                rows.add(new String[]{
                        shiftId,
                        docName,
                        deptName,
                        date != null ? date.toString() : "",
                        shiftDetails
                });
            } catch (Exception ignored) {}
        }
        rows.sort((a, b) -> a[0].compareTo(b[0]));
        return rows;
    }

    // Assign a new shift to a doctor and persist via allocator
    public void assignShift(String doctorId, String dateStr, String shiftType) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a doctor.");
        }
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Shift date cannot be empty.");
        }

        LocalDate shiftDate;
        try {
            shiftDate = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
        }

        // Fetch target doctor entity
        Doctor doctor = allocator.getBusinessEntity(doctorId);
        if (doctor == null || doctor.getSelf() == null) {
            throw new IllegalArgumentException("Doctor not found: " + doctorId);
        }

        // Check for shift conflicts (now both doctor and shiftDate are valid and available)
        if (hasShiftConflict(doctorId, shiftDate)) {
            throw new IllegalArgumentException("Dr. " + doctor.getSelf().getName() + " already has a shift scheduled on " + dateStr + ".");
        }

        // Determine start and end time based on selected shift
        LocalTime startTime;
        LocalTime endTime;
        if (shiftType.contains("Morning")) {
            startTime = LocalTime.of(8, 0);
            endTime = LocalTime.of(16, 0);
        } else if (shiftType.contains("Evening")) {
            startTime = LocalTime.of(16, 0);
            endTime = LocalTime.of(23, 59);
        } else { // Night
            startTime = LocalTime.of(0, 0);
            endTime = LocalTime.of(8, 0);
        }

        // Create new DoctorShift entity (ID is null for auto-generation)
        DoctorShiftToFile shiftToFile = new DoctorShiftToFile(null, shiftDate, startTime, endTime);
        DoctorShift doctorShift = allocator.convertToBusinessEntity(shiftToFile, true);

        // Associate shift with the doctor and save
        doctorShift.setBelongsToDoctor(doctor.getSelf());
        allocator.saveChanges(doctorShift);
    }

    // Update existing shift schedule and linked doctor
    public void updateShift(String shiftId, String doctorId, String dateStr, String shiftType) {
        if (shiftId == null || shiftId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shift ID cannot be empty.");
        }
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a doctor.");
        }
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Shift date cannot be empty.");
        }

        LocalDate shiftDate;
        try {
            shiftDate = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
        }

        Doctor doctor = allocator.getBusinessEntity(doctorId);
        if (doctor == null || doctor.getSelf() == null) {
            throw new IllegalArgumentException("Doctor not found: " + doctorId);
        }

        // Determine shift start and end times
        LocalTime startTime;
        LocalTime endTime;
        if (shiftType.contains("Morning")) {
            startTime = LocalTime.of(8, 0);
            endTime = LocalTime.of(16, 0);
        } else if (shiftType.contains("Evening")) {
            startTime = LocalTime.of(16, 0);
            endTime = LocalTime.of(23, 59);
        } else {
            startTime = LocalTime.of(0, 0);
            endTime = LocalTime.of(8, 0);
        }

        // 1. Create updated shift record keeping the same shift ID
        DoctorShiftToFile updatedShiftData = new DoctorShiftToFile(shiftId.trim(), shiftDate, startTime, endTime);

        // 2. Convert to business entity with isEntityNew = false
        DoctorShift doctorShift = allocator.convertToBusinessEntity(updatedShiftData, false);

        // 3. Update the doctor relationship
        doctorShift.setBelongsToDoctor(doctor.getSelf());

        // 4. Persist changes to disk
        allocator.saveChanges(doctorShift);
    }

    // Delete an existing shift by ID
    public void deleteShift(String shiftId) {
        if (shiftId == null || shiftId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shift ID cannot be empty.");
        }
        try {
            allocator.deleteBusinessEntity(shiftId.trim());
        } catch (EntityNotMatchException | EntityNotFoundException e) {
            throw new IllegalArgumentException("Shift ID '" + shiftId + "' not found or cannot be deleted: " + e.getMessage());
        }
    }

    // Data container for aggregated hospital KPIs and tabular department metrics
    public record HospitalMetrics (double totalRevenue, int totalAppointments, int activeDoctors, double bedOccupancyRate, List<String[]>tableRows){}

    // Calculate metrics safely and ignore invalid data rows
    public HospitalMetrics calculateMetrics(String selectedMonth) {
        Integer targetYear = null;
        Integer targetMonth = null;
        double totalRevenue = 0;
        int totalAppointments = 0;
        int activeDoctors = 0;
        double bedOccupancyRate = 0;
        List<String[]> tableRows = new ArrayList<>();

        if (selectedMonth != null && !selectedMonth.equalsIgnoreCase("All Months")) {
            String[] parts = selectedMonth.split(" ");
            if (parts.length == 2) {
                try {
                    targetMonth = Month.valueOf(parts[0].toUpperCase()).getValue();
                    targetYear = Integer.parseInt(parts[1]);
                } catch (Exception ignored) {}
            }
        }

        // Safe entity fetching
        List<Department> departments = new ArrayList<>();
        List<Doctor> doctors = new ArrayList<>();
        List<Appointment> appointments = new ArrayList<>();
        List<Bill> bills = new ArrayList<>();
        List<Facility> facilities = new ArrayList<>();

        try { departments = allocator.getAllBusinessEntities(DepartmentToFile.PREFIX); } catch (Exception ignored) {}
        try { doctors = allocator.getAllBusinessEntities(DoctorToFile.PREFIX); } catch (Exception ignored) {}
        try { appointments = allocator.getAllBusinessEntities(AppointmentToFile.PREFIX); } catch (Exception ignored) {}
        try { bills = allocator.getAllBusinessEntities(BillToFile.PREFIX); } catch (Exception ignored) {}
        try { facilities = allocator.getAllBusinessEntities(FacilityToFile.PREFIX); } catch (Exception ignored) {}

        Map<String, Integer> deptPatientCount = new HashMap<>();
        Map<String, Double> deptRevenue = new HashMap<>();
        for (Department dept : departments) {
            deptPatientCount.put(dept.getId(), 0);
            deptRevenue.put(dept.getId(), 0.0);
        }

        for (Appointment appt : appointments) {
            AppointmentToFile self = appt.getSelf();
            if (self == null || self.getAppointmentTime() == null) continue;

            if (targetYear != null && targetMonth != null) {
                if (self.getAppointmentTime().getYear() != targetYear ||
                        self.getAppointmentTime().getMonthValue() != targetMonth) {
                    continue;
                }
            }
            totalAppointments++;

            DoctorToFile docToFile = appt.getDoctor();
            if (docToFile != null) {
                try {
                    Doctor docBiz = allocator.getBusinessEntity(docToFile.getId());
                    if (docBiz != null) {
                        DepartmentToFile deptToFile = docBiz.getBelongsToDepartment();
                        if (deptToFile != null && deptPatientCount.containsKey(deptToFile.getId())) {
                            deptPatientCount.put(deptToFile.getId(), deptPatientCount.get(deptToFile.getId()) + 1);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        for (Bill bill : bills) {
            BillToFile self = bill.getSelf();
            if (self == null || self.getIssuedAt() == null) continue;

            // Only count paid bills
            if (self.getStatus() != BillToFile.BillStatus.PAID) continue;

            if (targetYear != null && targetMonth != null) {
                if (self.getIssuedAt().getYear() != targetYear ||
                        self.getIssuedAt().getMonthValue() != targetMonth) {
                    continue;
                }
            }
            double amount = self.getMoney();
            totalRevenue += amount;

            ConsultationRateToFile rateToFile = bill.getConsultationRate();
            if (rateToFile != null) {
                try {
                    ConsultationRate rateBiz = allocator.getBusinessEntity(rateToFile.getId());
                    if (rateBiz != null) {
                        DepartmentToFile deptToFile = rateBiz.getBelongsToDepartment();
                        if (deptToFile != null && deptRevenue.containsKey(deptToFile.getId())) {
                            deptRevenue.put(deptToFile.getId(), deptRevenue.get(deptToFile.getId()) + amount);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        activeDoctors = doctors.size();

        int totalWardCapacity = 0;
        int occupiedWardBeds = 0;
        for (Facility fac : facilities) {
            FacilityToFile self = fac.getSelf();
            if (self != null && self.getFacilityType() == FacilityToFile.FacilityType.WARD) {
                totalWardCapacity += self.getCapacity();
                if (!self.isAvailable()) {
                    occupiedWardBeds += self.getCapacity();
                }
            }
        }
        if (totalWardCapacity > 0) {
            bedOccupancyRate = ((double) occupiedWardBeds / totalWardCapacity) * 100.0;
        }

        for (Department dept : departments) {
            String deptId = dept.getId();
            String deptName = (dept.getSelf() != null) ? dept.getSelf().getName() : "Unknown";
            int patientsServed = deptPatientCount.getOrDefault(deptId, 0);
            double revenue = deptRevenue.getOrDefault(deptId, 0.0);
            tableRows.add(new String[]{
                    deptId,
                    deptName,
                    String.valueOf(patientsServed),
                    String.format("$%,.2f", revenue)
            });
        }
        return new HospitalMetrics(totalRevenue, totalAppointments, activeDoctors, bedOccupancyRate, tableRows);
    }

    // Checks if the doctor already has an active shift on the given date.
    private boolean hasShiftConflict(String doctorId, LocalDate targetDate) {
        List<DoctorShift> allShifts = allocator.getAllBusinessEntities(DoctorShiftToFile.PREFIX);
        for (DoctorShift shift : allShifts) {
            DoctorToFile assignedDoc = shift.getBelongsToDoctor();
            if (assignedDoc != null && assignedDoc.getId().equals(doctorId)) {
                if (shift.getSelf() != null && targetDate.equals(shift.getSelf().getShiftDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void exportMetricsReport(String filePath, String selectedMonth) throws IOException {
        HospitalMetrics metrics = calculateMetrics(selectedMonth);
/*        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write executive summary
            writer.println("=== Hospital Revenue and Metrics Report ===");
            writer.println("Period," + (selectedMonth != null ? selectedMonth : "All Months"));
            writer.println(String.format("Total Revenue,$%,.2f", metrics.totalRevenue));
            writer.println("Total Appointments," + metrics.totalAppointments);
            writer.println("Active Doctors," + metrics.activeDoctors);
            writer.println(String.format("Bed Occupancy Rate,%.1f%%", metrics.bedOccupancyRate));
            writer.println();

            // Write tabular department breakdown
            writer.println("Dept ID,Department Name,Patients Served,Revenue,Avg Stay (Days)");
            for (String[] row : metrics.tableRows) {
                // Remove commas and dollar signs from numeric values to preserve CSV column integrity
                String cleanedRevenue = row[3].replace("$", "").replace(",", "");
                writer.println(String.format("%s,%s,%s,%s,%s", row[0], row[1], row[2], cleanedRevenue, row[4]));
            }
        }*/
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("================================================================================");
            writer.println("                     Hospital Revenue and Metrics Report                        ");
            writer.println("================================================================================");
            writer.println("Period:             " + (selectedMonth != null ? selectedMonth : "All Months"));
            writer.println(String.format("Total Revenue:      $%,.2f", metrics.totalRevenue()));
            writer.println(String.format("Total Appointments: %,d", metrics.totalAppointments()));
            writer.println("Active Doctors:     " + metrics.activeDoctors());
            writer.println(String.format("Bed Occupancy Rate: %.1f%%", metrics.bedOccupancyRate()));
            writer.println();

            writer.println("--------------------------------------------------------------------------------");
            writer.printf("%-10s %-25s %-18s %-15s%n",
                    "Dept ID", "Department Name", "Patients Served", "Revenue");
            writer.println("--------------------------------------------------------------------------------");

            for (String[] row : metrics.tableRows()) {
                writer.printf("%-10s %-25s %-18s %-15s%n",
                        row[0], row[1], row[2], row[3]);
            }
            writer.println("================================================================================");
        }
    }

    // Data record for displaying doctor feedback in UI table
    public record DoctorFeedbackInfo(String doctorId, String doctorName, String appointmentId, int rating, String comment, String date) {}

    public List<DoctorFeedbackInfo> loadAssignedDoctorsFeedback() {
        List<DoctorFeedbackInfo> feedbackList = new ArrayList<>();
        if (this.medicalManager == null) return feedbackList;

        // 1. Get doctors assigned to this medical manager
        for (DoctorToFile docFile : this.medicalManager.getDoctors()) {
            try {
                Doctor docBiz = allocator.getBusinessEntity(docFile.getId());
                if (docBiz == null) continue;

                // 2. Loop through appointments of each doctor
                for (AppointmentToFile apptFile : docBiz.getAppointments()) {
                    try {
                        Appointment appt = allocator.getBusinessEntity(apptFile.getId());
                        if (appt == null) continue;

                        // 3. Get feedback linked to the appointment
                        FeedbackToFile fb = appt.getFeedback();
                        if (fb != null) {
                            feedbackList.add(new DoctorFeedbackInfo(
                                    docBiz.getId(),
                                    docBiz.getSelf().getName(),
                                    appt.getId(),
                                    fb.getRating(),
                                    fb.getComment(),
                                    fb.getCreatedTime() != null ? fb.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A"
                            ));
                        }
                    } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        }
        return feedbackList;
    }
}