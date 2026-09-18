package Operations.MedicalManagerOperation;

import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.*;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.*;

import javax.print.Doc;
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
    public void deleteDepartment(String deptId) {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        allocator.deleteBusinessEntity(deptId);
    }

    // Load all doctors in the hospital
    public List<Doctor> loadDoctors() {
        return allocator.getAllBusinessEntities(DoctorToFile.PREFIX);
    }

    // Load all doctor shifts formatted for the UI roster table
    public ArrayList<String[]> loadRosters() {
        ArrayList<String[]> rows = new ArrayList<>();
        List<DoctorShift> shifts = allocator.getAllBusinessEntities(DoctorShiftToFile.PREFIX);
        for (DoctorShift shift : shifts) {
            String shiftId = shift.getId();
            DoctorToFile docToFile = shift.getBelongsToDoctor();

            String docName = "Unassigned";
            String deptName = "N/A";

            if (docToFile != null) {
                docName = docToFile.getName();
                try {
                    // Fetch doctor business entity to get associated department
                    Doctor doctorBiz = allocator.getBusinessEntity(docToFile.getId());
                    if (doctorBiz.getBelongsToDepartment() != null) {
                        deptName = doctorBiz.getBelongsToDepartment().getName();
                    }
                } catch (Exception ignored) {
                }
            }

            LocalDate date = shift.getSelf().getShiftDate();
            LocalTime start = shift.getSelf().getStartTime();
            LocalTime end = shift.getSelf().getEndTime();

            // Format shift label based on start time
            String shiftLabel;
            int hour = start.getHour();
            if (hour >= 8 && hour < 16) {
                shiftLabel = "Morning";
            } else if (hour >= 16) {
                shiftLabel = "Evening";
            } else {
                shiftLabel = "Night"; // 00:00 到 08:00 为夜班
            }
            String shiftDetails = String.format("%s (%s - %s)", shiftLabel, start, end);

            // Row matches: {"Roster ID", "Doctor Name", "Department", "Date", "Shift"}
            rows.add(new String[]{
                    shiftId,
                    docName,
                    deptName,
                    date != null ? date.toString() : "",
                    shiftDetails
            });
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

    // Delete an existing shift by ID
    public void deleteShift(String shiftId) {
        if (shiftId == null || shiftId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shift ID cannot be empty.");
        }
        allocator.deleteBusinessEntity(shiftId.trim());
    }

    // Data container for aggregated hospital KPIs and tabular department metrics
    public static class HospitalMetrics {
        public double totalRevenue = 0.0;
        public int totalAppointments = 0;
        public int activeDoctors = 0;
        public double bedOccupancyRate = 0.0;
        public List<String[]> tableRows = new ArrayList<>();
    }

    public HospitalMetrics calculateMetrics(String selectedMonth) {
        HospitalMetrics metrics = new HospitalMetrics();

        // 1. Parse target year and month if a specific period is selected
        Integer targetYear = null;
        Integer targetMonth = null;
        if (selectedMonth != null && !selectedMonth.equalsIgnoreCase("All Months")) {
            String[] parts = selectedMonth.split(" ");
            if (parts.length == 2) {
                try {
                    targetMonth = Month.valueOf(parts[0].toUpperCase()).getValue();
                    targetYear = Integer.parseInt(parts[1]);
                } catch (Exception ignored) {
                    // Fall back to all months if parsing fails
                }
            }
        }

        // 2. Fetch all necessary business entities from allocator
        List<Department> departments = allocator.getAllBusinessEntities(DepartmentToFile.PREFIX);
        List<Doctor> doctors = allocator.getAllBusinessEntities(DoctorToFile.PREFIX);
        List<Appointment> appointments = allocator.getAllBusinessEntities(AppointmentToFile.PREFIX);
        List<Bill> bills = allocator.getAllBusinessEntities(BillToFile.PREFIX);
        List<Facility> facilities = allocator.getAllBusinessEntities(FacilityToFile.PREFIX);

        // Map department IDs to patient count and revenue counters
        Map<String, Integer> deptPatientCount = new HashMap<>();
        Map<String, Double> deptRevenue = new HashMap<>();
        for (Department dept : departments) {
            deptPatientCount.put(dept.getId(), 0);
            deptRevenue.put(dept.getId(), 0.0);
        }

        // 3. Calculate appointment counts and patients served per department
        for (Appointment appt : appointments) {
            AppointmentToFile self = appt.getSelf();
            if (self == null || self.getAppointmentTime() == null) continue;

            // Apply date filtering
            if (targetYear != null && targetMonth != null) {
                if (self.getAppointmentTime().getYear() != targetYear ||
                        self.getAppointmentTime().getMonthValue() != targetMonth) {
                    continue;
                }
            }

            metrics.totalAppointments++;

            // Map appointment to department via doctor linkage
            DoctorToFile docToFile = appt.getDoctor();
            if (docToFile != null) {
                try {
                    Doctor docBiz = allocator.getBusinessEntity(docToFile.getId());
                    DepartmentToFile deptToFile = docBiz.getBelongsToDepartment();
                    if (deptToFile != null && deptPatientCount.containsKey(deptToFile.getId())) {
                        deptPatientCount.put(deptToFile.getId(), deptPatientCount.get(deptToFile.getId()) + 1);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        // 4. Calculate total revenue and department-specific earnings from bills
        for (Bill bill : bills) {
            BillToFile self = bill.getSelf();
            if (self == null || self.getIssuedAt() == null) continue;

            // Apply date filtering on bill issuance date
            if (targetYear != null && targetMonth != null) {
                if (self.getIssuedAt().getYear() != targetYear ||
                        self.getIssuedAt().getMonthValue() != targetMonth) {
                    continue;
                }
            }

            double amount = self.getMoney();
            metrics.totalRevenue += amount;

            // Map bill to department via consultation rate linkage
            ConsultationRateToFile rateToFile = bill.getConsultationRate();
            if (rateToFile != null) {
                try {
                    ConsultationRate rateBiz = allocator.getBusinessEntity(rateToFile.getId());
                    DepartmentToFile deptToFile = rateBiz.getBelongsToDepartment();
                    if (deptToFile != null && deptRevenue.containsKey(deptToFile.getId())) {
                        deptRevenue.put(deptToFile.getId(), deptRevenue.get(deptToFile.getId()) + amount);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        // 5. Total active doctors on system
        metrics.activeDoctors = doctors.size();

        // 6. Calculate bed occupancy rate from ward facilities
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
            metrics.bedOccupancyRate = ((double) occupiedWardBeds / totalWardCapacity) * 100.0;
        }

        // 7. Format rows for the Metrics & Revenue UI table
        for (Department dept : departments) {
            String deptId = dept.getId();
            String deptName = (dept.getSelf() != null) ? dept.getSelf().getName() : "Unknown";
            int patientsServed = deptPatientCount.getOrDefault(deptId, 0);
            double revenue = deptRevenue.getOrDefault(deptId, 0.0);
            String avgStay = "N/A"; // Outpatient departments typically default to N/A

            metrics.tableRows.add(new String[]{
                    deptId,
                    deptName,
                    String.valueOf(patientsServed),
                    String.format("$%,.2f", revenue),
                    avgStay
            });
        }

        return metrics;
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
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
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
                writer.println(String.format("%s,%s,%s,%s,%s", row[0], row, row, cleanedRevenue, row[4]));
            }
        }
    }
}