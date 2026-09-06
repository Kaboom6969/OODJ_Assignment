package Operations.MedicalManagerOperation;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MedicalManagerOperation {
    public void updateProfile(String name, String password, String gender,String dob, String email, String phone) {
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

        if (dob == null || dob.trim().isEmpty()){
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

        // 第三层：业务逻辑检查（出生日期不能在今天之后）
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of Birth cannot be in the future.");
        }

        if (!email.contains("@")){
            throw new IllegalArgumentException("Invalid email format (missing '@').");
        }

        if (!phone.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain digits only.");
        }


        try {
            FileInputStream fis = new FileInputStream("MedicalManager.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
