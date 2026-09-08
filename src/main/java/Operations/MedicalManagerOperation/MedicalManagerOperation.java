package Operations.MedicalManagerOperation;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class MedicalManagerOperation {
    private final String profileFilePath = "data/MedicalManager.txt";
    private final String deptFilePath = "data/Department.txt";

    private static final String deptPrefix = "DP";
    private static final String idFormat = deptPrefix + "%04d";

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

    public ArrayList<String[]> loadDepartment() throws IOException {
        File file = new File(deptFilePath);
        String line;
        ArrayList<String[]> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            line = br.readLine(); //skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    list.add(Arrays.stream(parts).map(String::trim).toArray(String[]::new));
                }
            }
        }
        return list;
    }

    public void addDepartment(String deptId, String deptName) throws IOException {
        // 1. Validate empty inputs (prevent NullPointerException)
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        if (deptName == null || deptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty.");
        }

        // 2. Duplicate check: prevent writing an existing ID into the file
        ArrayList<String[]> existingList = loadDepartment();
        for (String[] row : existingList) {
            if (row.length > 0 && row[0].equalsIgnoreCase(deptId.trim())) {
                throw new IllegalArgumentException("Department ID '" + deptId + "' already exists.");
            }
        }

        // 3. Ensure parent directory exists
        File file = new File(deptFilePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        boolean needHeader = !file.exists() || file.length() <= 0;

        // 4. Append record to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            if (needHeader) {
                bw.write("DepartmentId|DepartmentName");
                bw.newLine();
            }
            bw.write(deptId.trim() + "|" + deptName.trim());
            bw.newLine();
        }
    }

    public String generateNextDeptId() throws IOException {
        ArrayList<String[]> list = loadDepartment();

        if (list.isEmpty()) {
            return String.format(idFormat, 1);
        }
        try {
            String lastId = list.get(list.size() - 1)[0];
            int lastNum = Integer.parseInt(lastId.substring(deptPrefix.length())) + 1;
            return String.format(idFormat, lastNum);
        } catch (Exception e) {
            return String.format(idFormat, list.size() + 1);
        }
    }

    public void updateDepartment(String deptId, String newDeptName) throws IOException {
        // 1. Validate inputs
        if (deptId == null || deptId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be empty.");
        }
        if (newDeptName == null || newDeptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department Name cannot be empty.");
        }
        File file = new File(deptFilePath);
        if (!file.exists() || file.length() == 0) {
            throw new IllegalArgumentException("Department file is empty or does not exist.");
        }

        String headerLine;
        try (BufferedReader br = new BufferedReader(new FileReader(deptFilePath))) {
            headerLine = br.readLine();
        }
        int idCol = getColumnIndex(headerLine, "DepartmentId");
        int nameCol = getColumnIndex(headerLine, "DepartmentName");

        // 2. Load all records into memory
        ArrayList<String[]> list = loadDepartment();
        boolean found = false;

        // 3. Find target ID and update the department name
        int requiredLength = Math.max(idCol, nameCol);
        for (String[] row : list) {
            if (row.length > requiredLength && row[idCol].equalsIgnoreCase(deptId.trim())) {
                row[nameCol] = newDeptName.trim();
                found = true;
                break;
            }
        }

        // 4. If not found, abort
        if (!found) {
            throw new IllegalArgumentException("Department ID '" + deptId + "' not found.");
        }

        // 5. Rewrite entire file (false = Overwrite mode)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write(headerLine);
            bw.newLine();
            for (String[] row : list) {
                bw.write(String.join("|", row));
                bw.newLine();
            }
        }
    }

    private int getColumnIndex(String headerLine, String targetColumnName) {
        String[] arrHeader = headerLine.trim().split("\\|");
        for (int i = 0; i < arrHeader.length; i++) {
            if (arrHeader[i].trim().equalsIgnoreCase(targetColumnName.trim())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column '" + targetColumnName + "' not found in header.");
    }

    public static void main(String[] args) throws IOException {
        MedicalManagerOperation op = new MedicalManagerOperation();
        ArrayList<String[]> list = op.loadDepartment();
        for (String[] row : list) {
            System.out.println(row[0]);
        }
        System.out.println(Arrays.toString(op.loadDepartment().get(0)));
    }
}
