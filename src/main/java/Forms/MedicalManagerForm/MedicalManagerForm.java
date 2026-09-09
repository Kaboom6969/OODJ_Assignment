package Forms.MedicalManagerForm;

import Operations.MedicalManagerOperation.MedicalManagerOperation;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

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
    private JTextField doctorNameField;
    private JComboBox<String> departmentBox;
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

    // Hold allocator and operation references
    private final HospitalEntityAllocator allocator;
    private final MedicalManagerOperation operation;

    // Default manager ID for profile update
    private String currentManagerId = "MM0001";

    // Constructor: setup main window and tabs
    public MedicalManagerForm(HospitalEntityAllocator allocator) throws IOException {
        this.allocator = allocator;
        this.operation = new MedicalManagerOperation(allocator);

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
        add(jTabbedPane);

        // Bind events and load initial data
        initEvents();

        // 1. Preload sample roster records
        rosterTableModel.addRow(new Object[]{"RS1001", "Dr. Tan", "Emergency Surgery", "2026-09-10", "Morning"});
        rosterTableModel.addRow(new Object[]{"RS1002", "Dr. Lee", "Mental Health", "2026-09-11", "Evening"});

        // 2. Assign Shift Button action
        assignShiftBtn.addActionListener(e -> {
            String doc = doctorNameField.getText().trim();
            String dept = (String) departmentBox.getSelectedItem();
            String date = rosterDateField.getText().trim();
            String shift = (String) shiftTypeBox.getSelectedItem();

            if (doc.isEmpty() || date.isEmpty() || "YYYY-MM-DD".equals(date)) {
                JOptionPane.showMessageDialog(this, "Please fill in Doctor Name and valid Date.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (dept == null || dept.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a valid department.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String rosterId = "RS" + (1001 + rosterTableModel.getRowCount());
            rosterTableModel.addRow(new Object[]{rosterId, doc, dept, date, shift});

            doctorNameField.setText("");
            rosterDateField.setText("YYYY-MM-DD");
            rosterDateField.setForeground(Color.GRAY);
        });

        // 3. Delete Shift Button action
        deleteShiftBtn.addActionListener(e -> {
            int selectedRow = rosterTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a roster row to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            rosterTableModel.removeRow(selectedRow);
        });

        // 4. Clear Shift Button action
        clearShiftBtn.addActionListener(e -> {
            doctorNameField.setText("");
            rosterDateField.setText("YYYY-MM-DD");
            rosterDateField.setForeground(Color.GRAY);
            rosterTable.clearSelection();
        });

        // 5. Load mock data for Metrics Table
        metricsTableModel.addRow(new Object[]{"DP0001", "Mental Health", "310", "34,100.00", "4.2"});
        metricsTableModel.addRow(new Object[]{"DP0002", "Emergency Surgery", "520", "68,750.00", "2.8"});
        metricsTableModel.addRow(new Object[]{"DP0003", "Pediatrics", "410", "25,600.00", "1.5"});

        // 6. Refresh Metrics Button action
        refreshMetricsBtn.addActionListener(e -> {
            String selectedMonth = (String) monthFilterBox.getSelectedItem();
            JOptionPane.showMessageDialog(this, "Metrics refreshed for " + selectedMonth + ".", "Updated", JOptionPane.INFORMATION_MESSAGE);
        });

        // 7. Export Report Button action
        exportReportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Report exported to 'Revenue_Report.csv' successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // Refresh department names in Shift Rosters dropdown
    private void refreshDepartmentComboBox() {
        if (departmentBox == null) return;
        departmentBox.removeAllItems();
        try {
            ArrayList<String[]> depts = operation.loadDepartment();
            for (String[] d : depts) {
                if (d.length >= 2) {
                    departmentBox.addItem(d[1]); // Column 1: Department Name
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to refresh department dropdown: " + e.getMessage());
        }
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
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameField, gbc);

        // Row 1: Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        // Row 2: Gender
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        panel.add(genderBox, gbc);

        // Row 3: DOB
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Day Of Birth:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        panel.add(dobField, gbc);

        // Row 4: Email
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);

        // Row 5: Phone Number
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.WEST;
        panel.add(phoneField, gbc);

        // Row 6: Save Button
        gbc.gridx = 1; gbc.gridy = 6; gbc.anchor = GridBagConstraints.CENTER;
        saveBtn.setFocusPainted(false);
        panel.add(saveBtn, gbc);

        panel.setBackground(Color.decode("#4EBC97"));
        return panel;
    }

    // Initialize event listeners and table selections
    private void initEvents() {
        // Load initial department records into table & dropdown
        deptTableModel.setRowCount(0);
        ArrayList<String[]> dept = operation.loadDepartment();
        for (String[] s : dept) {
            deptTableModel.addRow(s);
        }
        refreshDepartmentComboBox();

        // Set initial ID in Add Mode
        resetToNewDeptMode();

        // Row selection listener: populate fields or reset to next ID
        deptTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = deptTable.getSelectedRow();
                if (selectedRow != -1) {
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
                deptTableModel.addRow(new Object[]{id, name});

                // Sync department dropdown
                refreshDepartmentComboBox();

                deptTable.clearSelection();
                resetToNewDeptMode();

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
                operation.updateProfile(currentManagerId, name, password, gender, dob, email, phone);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
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
                deptTable.setValueAt(deptNewDeptName, selectedRow, deptTable.getColumnModel().getColumnIndex("Department Name"));

                // Sync department dropdown
                refreshDepartmentComboBox();

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
                    deptTableModel.removeRow(selectedRow);

                    // Sync department dropdown
                    refreshDepartmentComboBox();

                    deptTable.clearSelection();

                    JOptionPane.showMessageDialog(this, "Department deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
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
        JButton[] buttons = {addDeptBtn, updateDeptBtn, deleteDeptBtn, clearDeptBtn};
        for (JButton btn : buttons) {
            btn.setBackground(Color.decode("#4EBC97"));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
        }

        // Row 0: Dept ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Dept ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(deptIdField, gbc);

        // Row 1: Dept Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Dept Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(deptNameField, gbc);

        // Row 2: Buttons
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(addDeptBtn);
        btnGroup.add(updateDeptBtn);
        btnGroup.add(deleteDeptBtn);
        btnGroup.add(clearDeptBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
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

        doctorNameField = new JTextField(14);
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

        // Initialize empty dropdown; populated dynamically via refreshDepartmentComboBox()
        departmentBox = new JComboBox<>();

        String[] shiftTypes = {"Morning (08:00 - 16:00)", "Evening (16:00 - 00:00)", "Night (00:00 - 08:00)"};
        shiftTypeBox = new JComboBox<>(shiftTypes);

        assignShiftBtn = new JButton("Assign Shift");
        deleteShiftBtn = new JButton("Delete Shift");
        clearShiftBtn = new JButton("Clear");

        JButton[] buttons = {assignShiftBtn, deleteShiftBtn, clearShiftBtn};
        for (JButton btn : buttons) {
            btn.setBackground(Color.decode("#4EBC97"));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
        }

        // Row 0: Doctor Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Doctor Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(doctorNameField, gbc);

        // Row 1: Department Dropdown
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(departmentBox, gbc);

        // Row 2: Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Shift Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(rosterDateField, gbc);

        // Row 3: Shift Type
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Shift Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(shiftTypeBox, gbc);

        // Row 4: Buttons
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(assignShiftBtn);
        btnGroup.add(deleteShiftBtn);
        btnGroup.add(clearShiftBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
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

        String[] columns = {"Dept ID", "Department Name", "Patients Served", "Revenue", "Avg Stay (Days)"};
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
            btn.setBackground(Color.decode("#4EBC97"));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
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
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.decode("#2E7D5E"));
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
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

    // Reset department fields to Add Mode
    private void resetToNewDeptMode() {
        deptIdField.setText(operation.generateNextDeptId());
        deptNameField.setText("");
    }

    // Application entry point
    public static void main(String[] args) {
        // Set ID number width to 4 digits
        BaseEntity.setIdNumberWidth(4);

        // Create allocator instance with data paths
        HospitalEntityAllocator allocator = new HospitalEntityAllocator(
                java.nio.file.Path.of("data/Linker"),
                java.nio.file.Path.of("data/Entity")
        );

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            try {
                new MedicalManagerForm(allocator).setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}