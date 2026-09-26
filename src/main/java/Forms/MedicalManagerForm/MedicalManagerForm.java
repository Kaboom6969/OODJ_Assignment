package Forms.MedicalManagerForm;

import Operations.MedicalManagerOperation.MedicalManagerOperation;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.MedicalManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MedicalManagerForm extends JFrame {

    // --- Profile Form Components ---
    private JTextField nameField;
    private JComboBox<String> genderBox;
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton saveBtn;
    private JTextField dobField;

    // --- Department Form Components ---
    private JTable deptTable;
    private DefaultTableModel deptTableModel;
    private JTextField deptIdField;
    private JTextField deptNameField;
    private JButton addDeptBtn;
    private JButton updateDeptBtn;
    private JButton deleteDeptBtn;
    private JButton clearDeptBtn;

    // --- Shift Rosters Form Components ---
    private JTable rosterTable;
    private DefaultTableModel rosterTableModel;
    private JComboBox<String> doctorBox;
    private JTextField departmentField;
    private JTextField rosterDateField;
    private JComboBox<String> shiftTypeBox;
    private JButton assignShiftBtn;
    private JButton deleteShiftBtn;
    private JButton clearShiftBtn;

    // --- Metrics & Revenue Form Components ---
    private JTable metricsTable;
    private DefaultTableModel metricsTableModel;
    private JComboBox<String> monthFilterBox;
    private JButton refreshMetricsBtn;
    private JButton exportReportBtn;

    // KPI Value Labels for dynamic display update
    private JLabel totalRevenueVal;
    private JLabel appointmentsVal;
    private JLabel activeDoctorsVal;
    private JLabel bedOccupancyVal;

    // Hold medicalManager, allocator and operation references
    private final MedicalManager medicalManager;
    private final HospitalEntityAllocator allocator;
    private final MedicalManagerOperation operation;

    // Feedback table and model
    private JTable feedbackTable;
    private DefaultTableModel feedbackTableModel;

    // Shift update controls
    private JButton updateShiftBtn;
    private String selectedShiftId = null;

    // Default manager IDfor profile update
    private String currentManagerId = "MM0001";

    // Constructor: setup main window and tabs
    public MedicalManagerForm(HospitalEntityAllocator allocator, MedicalManager medicalManager) {
        UIManager.put("Button.disabledText", new Color(190, 190, 190));
        this.allocator = allocator;
        this.medicalManager = medicalManager;
        this.operation = new MedicalManagerOperation(allocator, medicalManager);

        // Initialize current manager ID dynamically from the passed entity
        if (medicalManager != null && medicalManager.getId() != null) {
            this.currentManagerId = medicalManager.getId();
        }

        setTitle("Medical Management System");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        // Initialize tabs
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab("Personal Profile", createProfilePanel());
        jTabbedPane.addTab("Department Management", createDepartmentPanel());
        jTabbedPane.addTab("Shift Rosters", createRosterPanel());
        jTabbedPane.addTab("Metrics & Revenue", createReportPanel());
        jTabbedPane.addTab("Doctor Feedback", createFeedbackPanel());
        add(jTabbedPane);

        // Bind events and load initial data
        initEvents();
        loadProfileData();
        refreshDoctorComboBox();
        refreshRosterTable();

        // Load initial real metrics data on startup
        refreshMetrics();
        refreshFeedbackTable();

        // Trigger refresh automatically when month selection changes
        monthFilterBox.addActionListener(e -> refreshMetrics());

        // Refresh Metrics Button action
        refreshMetricsBtn.addActionListener(e -> {
            refreshMetrics();
            JOptionPane.showMessageDialog(
                    this,
                    "Metrics refreshed for " + monthFilterBox.getSelectedItem() + ".",
                    "Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Export Report Button action
        exportReportBtn.addActionListener(e -> {
            String selectedMonth = (String) monthFilterBox.getSelectedItem();

            java.io.File reportDir = new java.io.File("data/Report");
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            String exportFileName = "data/Report/Revenue_Report.txt";
            try {
                operation.exportMetricsReport(exportFileName, selectedMonth);
                JOptionPane.showMessageDialog(
                        this,
                        "Report exported to '" + exportFileName + "' successfully.",
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to export report: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }


    // Create the Personal Profile panel
    private JPanel createProfilePanel() {
        String[] genders = {"Male", "Female"};
        genderBox = new JComboBox<>(genders);

        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        dobField = new JTextField(15);
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        saveBtn = new JButton("Save Changes");
        setButtonState(saveBtn, true);

        // Handle placeholder text for DOB field
        dobField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (dobField.getText().equals("YYYY-MM-DD")) {
                    dobField.setText("");
                    dobField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (dobField.getText().trim().isEmpty()) {
                    dobField.setText("YYYY-MM-DD");
                    dobField.setForeground(Color.GRAY);
                }
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Row 0: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameField, gbc);

        // Row 1: Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        // Row 2: Gender
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(genderBox, gbc);

        // Row 3: DOB
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Day Of Birth:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(dobField, gbc);

        // Row 4: Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);

        // Row 5: Phone Number
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(phoneField, gbc);

        // Row 6: Save Button
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        saveBtn.setFocusPainted(false);
        panel.add(saveBtn, gbc);

        panel.setBackground(Color.decode("#4EBC97"));
        return panel;
    }

    // Initialize event listeners and table selections
    private void initEvents() {
        // Load initial department records
        refreshDepartmentTable();

        // Row selection listener for Department Table
        deptTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = deptTable.getSelectedRow();
                if (selectedRow != -1) {
                    setButtonState(updateDeptBtn, true);
                    setButtonState(deleteDeptBtn, true);
                    try {
                        String id = deptTable.getValueAt(selectedRow, deptTable.getColumnModel().getColumnIndex("Department Id")).toString();
                        String name = deptTable.getValueAt(selectedRow, deptTable.getColumnModel().getColumnIndex("Department Name")).toString();
                        deptIdField.setText(id);
                        deptNameField.setText(name);
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Column name not found: " + ex.getMessage());
                    }
                } else {
                    resetToNewDeptMode();
                }
            }
        });

        // Clear Department Form Button
        clearDeptBtn.addActionListener(e -> deptTable.clearSelection());

        // Add Department Button
        addDeptBtn.addActionListener(e -> {
            if (deptTable.getSelectedRow() != -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "You currently have a department selected.\nPlease click 'Clear' first to add a new department, or click 'Update' to modify it.",
                        "Action Not Allowed",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String id = deptIdField.getText().trim();
            String name = deptNameField.getText().trim();

            try {
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Department Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                operation.addDepartment(id, name);
                refreshDepartmentTable();
                deptTable.clearSelection();
                JOptionPane.showMessageDialog(this, "Department added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Save Profile Button
        saveBtn.addActionListener(e -> {
            String name = nameField.getText();
            String gender = (String) genderBox.getSelectedItem();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String dob = dobField.getText();
            String password = new String(passwordField.getPassword());

            try {
                operation.updateProfile(name, password, UserWithDetails.Gender.valueOf(gender.toUpperCase()), dob, email, phone);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProfileData();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Validation Error: " + ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "System Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update Department Button
        updateDeptBtn.addActionListener(e -> {
            int selectedRow = deptTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a department from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String deptId = deptIdField.getText().trim();
            String deptNewDeptName = deptNameField.getText().trim();
            try {
                if (deptNewDeptName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Department Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                operation.updateDepartment(deptId, deptNewDeptName);
                refreshDepartmentTable();
                refreshRosterTable();
                deptTable.clearSelection();
                JOptionPane.showMessageDialog(this, "Department updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Delete Department Button
        deleteDeptBtn.addActionListener(e -> {
            int selectedRow = deptTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a department from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String deptId = deptIdField.getText().trim();
            String deptName = deptNameField.getText().trim();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete department '" + deptId + " - " + deptName + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    operation.deleteDepartment(deptId);
                    refreshDepartmentTable();
                    deptTable.clearSelection();
                    JOptionPane.showMessageDialog(this, "Department deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // --- Shift Rosters Button Actions ---

        // Assign Shift Button Action
        assignShiftBtn.addActionListener(e -> {
            String selectedDoc = (String) doctorBox.getSelectedItem();
            if (selectedDoc == null || !selectedDoc.contains(" - ")) {
                JOptionPane.showMessageDialog(this, "Please select a valid doctor.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String doctorId = selectedDoc.split(" - ")[0].trim();
            String date = rosterDateField.getText().trim();
            String shift = (String) shiftTypeBox.getSelectedItem();

            if (date.isEmpty() || "YYYY-MM-DD".equals(date)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid shift date.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                operation.assignShift(doctorId, date, shift);
                refreshRosterTable();
                rosterDateField.setText("YYYY-MM-DD");
                rosterDateField.setForeground(Color.GRAY);
                JOptionPane.showMessageDialog(this, "Shift assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Delete Shift Button Action
        deleteShiftBtn.addActionListener(e -> {
            int selectedRow = rosterTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a roster row to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String shiftId = rosterTable.getValueAt(selectedRow, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete shift '" + shiftId + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    operation.deleteShift(shiftId);
                    refreshRosterTable();
                    JOptionPane.showMessageDialog(this, "Shift deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Clear Shift Button Action
        clearShiftBtn.addActionListener(e -> {
            rosterDateField.setText("YYYY-MM-DD");
            rosterDateField.setForeground(Color.GRAY);
            rosterTable.clearSelection();
            if (doctorBox.getItemCount() > 0) {
                doctorBox.setSelectedIndex(0);
            }
        });

        // Listen to roster table row selection and populate input fields
        rosterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && rosterTable.getSelectedRow() != -1) {
                int row = rosterTable.getSelectedRow();
                selectedShiftId = (String) rosterTableModel.getValueAt(row, 0);
                String docName = (String) rosterTableModel.getValueAt(row, 1);
                String date = (String) rosterTableModel.getValueAt(row, 3);
                String shiftDetails = (String) rosterTableModel.getValueAt(row, 4);

                // Fill date text field
                rosterDateField.setText(date);
                rosterDateField.setForeground(Color.BLACK);

                // Match and select doctor in dropdown
                for (int i = 0; i < doctorBox.getItemCount(); i++) {
                    if (doctorBox.getItemAt(i).contains(docName)) {
                        doctorBox.setSelectedIndex(i);
                        break;
                    }
                }

                // Match shift type in dropdown
                if (shiftDetails.contains("Morning")) {
                    shiftTypeBox.setSelectedIndex(0);
                } else if (shiftDetails.contains("Evening")) {
                    shiftTypeBox.setSelectedIndex(1);
                } else if (shiftDetails.contains("Night")) {
                    shiftTypeBox.setSelectedIndex(2);
                }

                // Enable update button, disable assign button
                setButtonState(updateShiftBtn, true);
                setButtonState(assignShiftBtn, false);
            }
        });

        // Update Shift Button Action
        updateShiftBtn.addActionListener(e -> {
            if (selectedShiftId == null) {
                JOptionPane.showMessageDialog(this, "Please select a shift from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedDoc = (String) doctorBox.getSelectedItem();
            if (selectedDoc == null || !selectedDoc.contains(" - ")) {
                JOptionPane.showMessageDialog(this, "Please select a valid doctor.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String doctorId = selectedDoc.split(" - ")[0].trim();
            String dateStr = rosterDateField.getText().trim();
            String shiftType = (String) shiftTypeBox.getSelectedItem();

            try {
                operation.updateShift(selectedShiftId, doctorId, dateStr, shiftType);
                JOptionPane.showMessageDialog(this, "Shift updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Refresh table and reset form
                refreshRosterTable();
                selectedShiftId = null;
                rosterTable.clearSelection();
                setButtonState(updateShiftBtn, false);
                setButtonState(assignShiftBtn, true);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "System Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update clearShiftBtn to reset update button
        clearShiftBtn.addActionListener(e -> {
            selectedShiftId = null;
            rosterTable.clearSelection();
            rosterDateField.setText("YYYY-MM-DD");
            rosterDateField.setForeground(Color.GRAY);
            departmentField.setText("");
            if (doctorBox.getItemCount() > 0) {
                doctorBox.setSelectedIndex(0);
            }
            setButtonState(updateShiftBtn, false);
            setButtonState(assignShiftBtn, true);
        });

        // Automatically sync department display when doctor selection changes
        doctorBox.addActionListener(e -> {
            String selectedDoc = (String) doctorBox.getSelectedItem();
            if (selectedDoc != null && selectedDoc.contains(" - ")) {
                String docId = selectedDoc.split(" - ")[0].trim();
                try {
                    Doctor docBiz = allocator.getBusinessEntity(docId);
                    if (docBiz != null && docBiz.getBelongsToDepartment() != null) {
                        String deptId = docBiz.getBelongsToDepartment().getId();
                        Department freshDept = allocator.getBusinessEntity(deptId);
                        if (freshDept != null && freshDept.getSelf() != null) {
                            departmentField.setText(freshDept.getSelf().getName());
                        } else {
                            departmentField.setText(docBiz.getBelongsToDepartment().getName());
                        }
                    } else {
                        departmentField.setText("N/A");
                    }
                } catch (Exception ignored) {
                    departmentField.setText("N/A");
                }
            } else {
                departmentField.setText("");
            }
        });
    }

    private void setButtonState(JButton btn, boolean enabled) {
        btn.setEnabled(enabled);
        if (enabled) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        } else {
            btn.setBackground(new Color(224, 224, 224));
            btn.setForeground(new Color(166, 166, 166));
        }
    }

    // Create the Department Management panel
    public JPanel createDepartmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.decode("#6BBD9F"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Department Id", "Department Name"};
        deptTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        deptTable = new JTable(deptTableModel);
        deptTable.setRowHeight(24);
        deptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(deptTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Operations"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        deptIdField = new JTextField(12);
        deptIdField.setEditable(false);
        deptIdField.setBackground(new Color(240, 240, 240));
        deptNameField = new JTextField(16);

        addDeptBtn = new JButton("Add");
        updateDeptBtn = new JButton("Update");
        deleteDeptBtn = new JButton("Delete");
        clearDeptBtn = new JButton("Clear");

        setButtonState(addDeptBtn, true);
        setButtonState(clearDeptBtn, true);

        setButtonState(updateDeptBtn, false);
        setButtonState(deleteDeptBtn, false);

        // Row 0: Dept ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Dept ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(deptIdField, gbc);

        // Row 1: Dept Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Dept Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(deptNameField, gbc);

        // Row 2: Buttons
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(addDeptBtn);
        btnGroup.add(updateDeptBtn);
        btnGroup.add(deleteDeptBtn);
        btnGroup.add(clearDeptBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnGroup, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Create Shift Rosters panel
    public JPanel createRosterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.decode("#88BEA7"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Roster ID", "Doctor Name", "Department", "Date", "Shift"};
        rosterTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rosterTable = new JTable(rosterTableModel);
        rosterTable.setRowHeight(24);
        panel.add(new JScrollPane(rosterTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder("Shift Assignment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        doctorBox = new JComboBox<>();
        rosterDateField = new JTextField(14);
        rosterDateField.setText("YYYY-MM-DD");
        rosterDateField.setForeground(Color.GRAY);

        // Handle placeholder text for Roster Date field
        rosterDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (rosterDateField.getText().equals("YYYY-MM-DD")) {
                    rosterDateField.setText("");
                    rosterDateField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (rosterDateField.getText().trim().isEmpty()) {
                    rosterDateField.setText("YYYY-MM-DD");
                    rosterDateField.setForeground(Color.GRAY);
                }
            }
        });

        departmentField = new JTextField(14);
        departmentField.setEditable(false);

        String[] shiftTypes = {"Morning (08:00 - 16:00)", "Evening (16:00 - 00:00)", "Night (00:00 - 08:00)"};
        shiftTypeBox = new JComboBox<>(shiftTypes);

        assignShiftBtn = new JButton("Assign Shift");
        deleteShiftBtn = new JButton("Delete Shift");
        clearShiftBtn = new JButton("Clear");
        updateShiftBtn = new JButton("Update Shift");

        setButtonState(assignShiftBtn, true);
        setButtonState(deleteShiftBtn, true);
        setButtonState(clearShiftBtn, true);
        setButtonState(updateShiftBtn, false);

        // Row 0: Doctor Dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(doctorBox, gbc);

        // Row 1: Department Dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(departmentField, gbc);

        // Row 2: Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Shift Date:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(rosterDateField, gbc);

        // Row 3: Shift Type
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Shift Type:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(shiftTypeBox, gbc);

        // Row 4: Buttons
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(assignShiftBtn);
        btnGroup.add(updateShiftBtn);
        btnGroup.add(deleteShiftBtn);
        btnGroup.add(clearShiftBtn);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnGroup, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Create Metrics & Revenue panel
    public JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.decode("#A5BFAF"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        kpiPanel.setOpaque(false);
        kpiPanel.add(createKpiCard("Total Revenue", "$128,450.00"));
        kpiPanel.add(createKpiCard("Appointments", "1,240"));
        kpiPanel.add(createKpiCard("Active Doctors", "32"));
        kpiPanel.add(createKpiCard("Bed Occupancy", "87.5%"));
        panel.add(kpiPanel, BorderLayout.NORTH);

        String[] columns = {"Dept ID", "Department Name", "Patients Served", "Revenue"};
        metricsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        metricsTable = new JTable(metricsTableModel);
        metricsTable.setRowHeight(24);
        panel.add(new JScrollPane(metricsTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setOpaque(false);

        String[] months = {
                "All Months",
                "January 2026", "February 2026", "March 2026", "April 2026",
                "May 2026", "June 2026", "July 2026", "August 2026",
                "September 2026", "October 2026", "November 2026", "December 2026"
        };
        monthFilterBox = new JComboBox<>(months);

        refreshMetricsBtn = new JButton("Refresh");
        exportReportBtn = new JButton("Export Report");

        JButton[] buttons = {refreshMetricsBtn, exportReportBtn};
        for (JButton btn : buttons) {
            setButtonState(btn, true);
        }

        bottomPanel.add(new JLabel("Period:"));
        bottomPanel.add(monthFilterBox);
        bottomPanel.add(refreshMetricsBtn);
        bottomPanel.add(exportReportBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Generate styled KPI metric card
    private JPanel createKpiCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.DARK_GRAY);


        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.decode("#2E7D5E"));


        // Retain reference according to card title
        if ("Total Revenue".equalsIgnoreCase(title)) {
            totalRevenueVal = valueLabel;
        } else if ("Appointments".equalsIgnoreCase(title)) {
            appointmentsVal = valueLabel;
        } else if ("Active Doctors".equalsIgnoreCase(title)) {
            activeDoctorsVal = valueLabel;
        } else if ("Bed Occupancy".equalsIgnoreCase(title)) {
            bedOccupancyVal = valueLabel;
        }

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // Create clean and styled feedback UI panel
    private JPanel createFeedbackPanel() {
        Color themeBg = Color.decode("#C2D4C8"); // Consistent background color

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(themeBg);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Table model setup
        feedbackTableModel = new DefaultTableModel(
                new String[]{"Doctor Name", "Appointment ID", "Rating", "Comment", "Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        feedbackTable = new JTable(feedbackTableModel);
        feedbackTable.setRowHeight(26);
        feedbackTable.getTableHeader().setBackground(Color.decode("#EAECEE"));

        // ScrollPane setup: clean white background for entire table area
        JScrollPane scrollPane = new JScrollPane(feedbackTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#B0C4DE"), 1));

        // Top action bar: make it transparent so theme background shows through cleanly
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh Feedback");


        // Apply your own setButtonState method
        setButtonState(refreshBtn, true);

        refreshBtn.addActionListener(e -> refreshFeedbackTable());

        topPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Load and display feedback from operation
    private void refreshFeedbackTable() {
        if (feedbackTableModel == null) return;
        feedbackTableModel.setRowCount(0);

        try {
            List<MedicalManagerOperation.DoctorFeedbackInfo> list = operation.loadAssignedDoctorsFeedback();
            for (MedicalManagerOperation.DoctorFeedbackInfo item : list) {
                feedbackTableModel.addRow(new Object[]{
                        item.doctorName(),
                        item.appointmentId(),
                        item.rating(),
                        item.comment(),
                        item.date()
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to load feedback: " + e.getMessage());
        }
    }

    // Fetch live data via Operation and refresh both KPI cards and table rows
    private void refreshMetrics() {
        if (metricsTableModel == null) return;
        String selectedMonth = (String) monthFilterBox.getSelectedItem();
        MedicalManagerOperation.HospitalMetrics metrics = operation.calculateMetrics(selectedMonth);

        // 1. Update KPI value labels
        if (totalRevenueVal != null) {
            totalRevenueVal.setText(String.format("$%,.2f", metrics.totalRevenue()));
        }
        if (appointmentsVal != null) {
            appointmentsVal.setText(String.format("%,d", metrics.totalAppointments()));
        }
        if (activeDoctorsVal != null) {
            activeDoctorsVal.setText(String.valueOf(metrics.activeDoctors()));
        }
        if (bedOccupancyVal != null) {
            bedOccupancyVal.setText(String.format("%.1f%%", metrics.bedOccupancyRate()));
        }

        // 2. Refresh metrics table data
        metricsTableModel.setRowCount(0);
        for (String[] row : metrics.tableRows()) {
            metricsTableModel.addRow(row);
        }
    }

    // Reset personal profile input fields
    public void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        passwordField.setText("");
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        genderBox.setSelectedIndex(0);
    }

    // Load personal profile directly from allocator/file
    private void loadProfileData() {
        try {
            MedicalManager freshManager = allocator.getBusinessEntity(currentManagerId);
            var self = freshManager.getSelf();
            if (self != null) {
                nameField.setText(self.getName());
                emailField.setText(self.getEmail());
                phoneField.setText(self.getPhoneNumber());
                passwordField.setText(self.getPassword());
                if (self.getDateOfBirth() != null) {
                    dobField.setText(self.getDateOfBirth().toString());
                    dobField.setForeground(Color.BLACK);
                }
                if (self.getGender() != null) {
                    genderBox.setSelectedItem("MALE".equalsIgnoreCase(self.getGender().name()) ? "Male" : "Female");
                }
            }
        } catch (Exception e) {
            // Silently keep fields blank if no record exists yet
        }
    }

    // Reset department fields to Add Mode
    private void resetToNewDeptMode() {
        deptIdField.setText(operation.generateNextDeptId());
        deptNameField.setText("");
        setButtonState(updateDeptBtn, false);
        setButtonState(deleteDeptBtn, false);
    }

    // Refresh Department Table directly from data
    private void refreshDepartmentTable() {
        deptTableModel.setRowCount(0);
        ArrayList<String[]> depts = operation.loadDepartment();
        for (String[] d : depts) {
            deptTableModel.addRow(d);
        }
        resetToNewDeptMode();
    }

    // Populate doctor dropdown with real doctors from allocator
    private void refreshDoctorComboBox() {
        if (doctorBox == null) return;
        doctorBox.removeAllItems();
        try {
            List<Doctor> doctors = operation.loadDoctors();
            for (Doctor doc : doctors) {
                if (doc.getSelf() != null) {
                    doctorBox.addItem(doc.getId() + " - " + doc.getSelf().getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to refresh doctor dropdown: " + e.getMessage());
        }
    }

    // Refresh the roster table with real shift records from allocator
    private void refreshRosterTable() {
        if (rosterTableModel == null) return;
        rosterTableModel.setRowCount(0);
        try {
            ArrayList<String[]> rosters = operation.loadRosters();
            for (String[] row : rosters) {
                rosterTableModel.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Failed to load rosters: " + e.getMessage());
        }
    }

    // Application entry point
/*    public static void main(String[] args) {
        BaseEntity.setIdNumberWidth(4);

        HospitalEntityAllocator allocator = new HospitalEntityAllocator(
                java.nio.file.Path.of("data/Linker"),
                java.nio.file.Path.of("data/Entity")
        );

        SwingUtilities.invokeLater(() -> {
            MedicalManager manager = allocator.getBusinessEntity("MM0001");
            new MedicalManagerForm(allocator, manager).setVisible(true);
        });
    }*/
}