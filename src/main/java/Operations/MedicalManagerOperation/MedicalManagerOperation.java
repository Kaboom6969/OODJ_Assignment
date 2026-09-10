package Operations.MedicalManagerOperation;

import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.MedicalManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MedicalManagerOperation {

    // Store allocator reference instead of direct FileDataHandler
    private final HospitalEntityAllocator allocator;
    private MedicalManager medicalManager;

    // Constructor to inject the allocator
    public MedicalManagerOperation(HospitalEntityAllocator allocator,MedicalManager medicalManager) {
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
        medicalManager.getSelf().setName(name);
        medicalManager.getSelf().setPassword(password);
        medicalManager.getSelf().setGender(gender);
        medicalManager.getSelf().setDateOfBirth(birthDate);
        medicalManager.getSelf().setPhoneNumber(phone);
        medicalManager.getSelf().setEmail(email);
        allocator.saveChanges(medicalManager);
    }

    // Load all department entities using allocator
    public List<Department> loadDepartmentEntities() {
        return allocator.getAllBusinessEntities(DepartmentToFile.PREFIX);
    }

    // Load departments formatted for UI table
    public ArrayList<String[]> loadDepartment() {
        ArrayList<String[]> list = new ArrayList<>();
        for (Department department : loadDepartmentEntities()) {
            list.add(new String[]{department.getId(),department.getSelf().getName()});
        }
        return list;
    }

    public String generateNextDeptId()
    {
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
            } catch (Exception ignored) {}
        }
        return String.format(prefix + "%04d", maxNum + 1);
    }
    // Add new department using allocator.addEntity
    public void addDepartment(String deptName) {
        if (deptName == null || deptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty.");
        }
        DepartmentToFile dept = new DepartmentToFile(null, deptName.trim());
        Department department = allocator.convertToBusinessEntity(dept,true);
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
        Department department = allocator.convertToBusinessEntity(dept,false);
        allocator.saveChanges(department);
    }

    // Delete department using allocator.removeEntity
    public void deleteDepartment(String deptId) {
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        DepartmentToFile dept = allocator.getEntity(deptId.trim());
        if (dept == null) {
            throw new IllegalArgumentException("Department ID '" + deptId + "' not found.");
        }
        allocator.deleteBusinessEntity(deptId);
    }
}