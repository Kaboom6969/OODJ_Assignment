package Operations.MedicalManagerOperation;

import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MedicalManagerOperation {
    // Data file path and framework handler
    private final String profileFilePath = "data/MedicalManager.txt";
    private final String deptFilePath = "data/Department.txt";
    private final FileDataHandler deptFileHandler = new FileDataHandler(deptFilePath);

    // Profile input validation
    public void updateProfile(String name, String password, String gender, String dob, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException("Name can only contain letters and spaces.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (e.g., !@#$%^&*).");
        }
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
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format (missing '@').");
        }
        if (!phone.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain digits only.");
        }
    }

    // Load departments with defensive validation against invalid rows
    public List<DepartmentToFile> loadDepartmentEntities() {
        List<DepartmentToFile> list = new ArrayList<>();
        String[][] allData = deptFileHandler.getAllData();
        if (allData == null) return list;

        for (String[] row : allData) {
            if (row != null && row.length >= 2) {
                String id = row[0] != null ? row[0].trim() : "";
                String name = row[1] != null ? row[1].trim() : "";

                // Ignore empty rows, header rows, or rows not starting with "DP"
                if (!id.startsWith(DepartmentToFile.PREFIX) || name.isEmpty()) {
                    continue;
                }

                try {
                    list.add(new DepartmentToFile(id, name));
                } catch (Exception e) {
                    System.err.println("Skipping malformed row: " + String.join("|", row));
                }
            }
        }
        return list;
    }

    // Load departments for UI table display
    public ArrayList<String[]> loadDepartment() throws IOException {
        ArrayList<String[]> list = new ArrayList<>();
        for (DepartmentToFile dept : loadDepartmentEntities()) {
            list.add(new String[]{dept.getId(), dept.getName()});
        }
        return list;
    }

    // Generate the next department ID safely
    public String generateNextDeptId() throws IOException {
        List<DepartmentToFile> list = loadDepartmentEntities();
        String prefix = DepartmentToFile.PREFIX;

        if (list.isEmpty()) {
            return String.format(prefix + "%04d", 1);
        }

        int maxNum = 0;
        for (DepartmentToFile dept : list) {
            try {
                String idStr = dept.getId();
                if (idStr.startsWith(prefix)) {
                    int num = Integer.parseInt(idStr.substring(prefix.length()));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                }
            } catch (Exception ignored) {}
        }
        return String.format(prefix + "%04d", maxNum + 1);
    }

    // Add a new department
    public void addDepartment(String deptId, String deptName) throws IOException {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        if (deptName == null || deptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty.");
        }

        String cleanId = deptId.trim();
        String cleanName = deptName.trim();

        if (!cleanId.startsWith(DepartmentToFile.PREFIX)) {
            throw new IllegalArgumentException("Department ID must start with '" + DepartmentToFile.PREFIX + "'.");
        }

        // Prevent duplicate ID
        FileDataHandler.DataInformation info = deptFileHandler.getDataInformationFromSpecificId(cleanId);
        if (!info.isEmpty()) {
            throw new IllegalArgumentException("Department ID '" + cleanId + "' already exists.");
        }

        // Save entity to file
        DepartmentToFile dept = new DepartmentToFile(cleanId, cleanName);
        deptFileHandler.addData(dept.toFileData());
    }

    // Update an existing department
    public void updateDepartment(String deptId, String newDeptName) throws IOException {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        if (newDeptName == null || newDeptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty.");
        }

        String cleanId = deptId.trim();
        String cleanName = newDeptName.trim();

        // Find target row by ID
        FileDataHandler.DataInformation info = deptFileHandler.getDataInformationFromSpecificId(cleanId);
        if (info.isEmpty() || info.row() == null) {
            throw new IllegalArgumentException("Department ID '" + cleanId + "' not found.");
        }

        // Overwrite row with updated entity
        DepartmentToFile updatedDept = new DepartmentToFile(cleanId, cleanName);
        deptFileHandler.updateData(updatedDept.toFileData(), info.row());
    }

    // Delete a department by ID
    public void deleteDepartment(String deptId) throws IOException {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }

        String cleanId = deptId.trim();

        // Locate target row by ID
        FileDataHandler.DataInformation info = deptFileHandler.getDataInformationFromSpecificId(cleanId);
        if (info.isEmpty() || info.row() == null) {
            throw new IllegalArgumentException("Department ID '" + cleanId + "' not found.");
        }

        // Remove row from file
        deptFileHandler.deleteRow(info.row());
    }
}