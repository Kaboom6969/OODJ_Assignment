package Forms.MedicalManagerForm;

import Operations.MedicalManagerOperation.MedicalManagerOperation;

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

    // --- Business Logic Object ---
    private MedicalManagerOperation operation = new MedicalManagerOperation();

    // Constructor: setup the main window and tabs
    public MedicalManagerForm() throws IOException {
        // Set window title, size, and close behavior
        setTitle("Medical Management System");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen

        // Create tabbed pane and add tabs
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab("Personal Profile", createProfilePanel());
        jTabbedPane.addTab("Department Management", createDepartmentPanel());
        jTabbedPane.addTab("Shift Rosters", createRosterPanel());
        jTabbedPane.addTab("Metrics & Revenue", createReportPanel());

        add(jTabbedPane);

        // Bind event listeners
        initEvents();

        // --- 1. Preload sample roster records ---
        rosterTableModel.addRow(new Object[]{"RS1001", "Dr. Tan", "Emergency Surgery", "2026-09-10", "Morning"});
        rosterTableModel.addRow(new Object[]{"RS1002", "Dr. Lee", "Mental Health", "2026-09-11", "Evening"});

        // --- 2. Assign Shift Button action ---
        assignShiftBtn.addActionListener(e -> {
            String doc = doctorNameField.getText().trim();
            String dept = (String) departmentBox.getSelectedItem();
            String date = rosterDateField.getText().trim();
            String shift = (String) shiftTypeBox.getSelectedItem();

            if (doc.isEmpty() || date.isEmpty() || "YYYY-MM-DD".equals(date)) {
                JOptionPane.showMessageDialog(this, "Please fill in Doctor Name and valid Date.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Auto-generate simple ID for demo
            String rosterId = "RS" + (1001 + rosterTableModel.getRowCount());
            rosterTableModel.addRow(new Object[]{rosterId, doc, dept, date, shift});

            // Clear inputs
            doctorNameField.setText("");
            rosterDateField.setText("YYYY-MM-DD");
            rosterDateField.setForeground(Color.GRAY);
        });

        // --- 3. Delete Shift Button action ---
        deleteShiftBtn.addActionListener(e -> {
            int selectedRow = rosterTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a roster row to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            rosterTableModel.removeRow(selectedRow);
        });

        // --- 4. Clear Button action ---
        clearShiftBtn.addActionListener(e -> {
            doctorNameField.setText("");
            rosterDateField.setText("YYYY-MM-DD");
            rosterDateField.setForeground(Color.GRAY);
            rosterTable.clearSelection();
        });

        // --- 1. Load mock data for Metrics Table ---
        metricsTableModel.addRow(new Object[]{"DP0001", "Mental Health", "310", "34,100.00", "4.2"});
        metricsTableModel.addRow(new Object[]{"DP0002", "Emergency Surgery", "520", "68,750.00", "2.8"});
        metricsTableModel.addRow(new Object[]{"DP0003", "Pediatrics", "410", "25,600.00", "1.5"});

        // --- 2. Refresh Button Action ---
        refreshMetricsBtn.addActionListener(e -> {
            String selectedMonth = (String) monthFilterBox.getSelectedItem();
            JOptionPane.showMessageDialog(this, "Metrics refreshed for " + selectedMonth + ".", "Updated", JOptionPane.INFORMATION_MESSAGE);
        });

        // --- 3. Export Report Button Action ---
        exportReportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Report exported to 'Revenue_Report.csv' successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // Create the Personal Profile panel
    private JPanel createProfilePanel() {
        // Setup gender dropdown options
        String[] genders = {"Male", "Female"};
        genderBox = new JComboBox<>(genders);

        // Initialize input fields and button
        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        dobField = new JTextField(15);
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        saveBtn = new JButton("Save Changes");

        // Focus listener: manage placeholder text for DOB field
        dobField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Clear placeholder when user clicks inside
                if (dobField.getText().equals("YYYY-MM-DD")) {
                    dobField.setText("");
                    dobField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Restore placeholder if field is empty
                if (dobField.getText().trim().isEmpty()) {
                    dobField.setText("YYYY-MM-DD");
                    dobField.setForeground(Color.GRAY);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Space between components

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

        // Row 5: PhoneNumber
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(phoneField, gbc);

        // Row 6 : Button
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        saveBtn.setFocusPainted(false);
        panel.add(saveBtn, gbc);

        // Background color
        panel.setBackground(Color.decode("#4EBC97"));

        return panel;
    }

    // Initialize all button click actions
    private void initEvents() {
        // Add department data into table
        try {
            deptTableModel.setRowCount(0);
            ArrayList<String[]> dept = operation.loadDepartment();
            for (String[] s : dept) {
                deptTableModel.addRow(s);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load departments: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }

        // Initial state: auto-generate next ID
        resetToNewDeptMode();

        // Auto-fill fields on row click, or reset to next ID when deselected
        deptTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = deptTable.getSelectedRow();
                if (selectedRow != -1) {
                    // [Edit Mode]: Show selected row data
                    try {
                        String id = deptTable.getValueAt(selectedRow, deptTable.getColumnModel().getColumnIndex("Department Id")).toString();
                        String name = deptTable.getValueAt(selectedRow, deptTable.getColumnModel().getColumnIndex("Department Name")).toString();
                        deptIdField.setText(id);
                        deptNameField.setText(name);
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Column name not found: " + ex.getMessage());
                    }
                } else {
                    // [Add Mode]: Reset to next auto-incremented ID when deselected
                    resetToNewDeptMode();
                }
            }
        });

        // Clear Department Form Button
        clearDeptBtn.addActionListener(e -> {
            deptTable.clearSelection(); // Deselects row, automatically triggers resetToNewDeptMode()
        });

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
                // Validation: department name cannot be empty
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Department Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Call backend operation to write record into file
                operation.addDepartment(id, name);

                // Add newly created row to JTable immediately
                deptTableModel.addRow(new Object[]{id, name});

                // Reset table selection and prepare next auto-incremented ID
                deptTable.clearSelection();
                resetToNewDeptMode();

                JOptionPane.showMessageDialog(this, "Department added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Save button action listener (Profile)
        saveBtn.addActionListener(e -> {
            String name = nameField.getText();
            String gender = (String) genderBox.getSelectedItem();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String dob = dobField.getText();
            String password = new String(passwordField.getPassword());

            try {
                operation.updateProfile(name, password, gender, dob, email, phone);
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
                // Validation: department name cannot be empty
                if (deptNewDeptName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Department Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update text file
                operation.updateDepartment(deptId,deptNewDeptName);

                //Synchronize JTable cell
                deptTable.setValueAt(deptNewDeptName,selectedRow,deptTable.getColumnModel().getColumnIndex("Department Name"));

                // Reset table selection and prepare next auto-incremented ID
                deptTable.clearSelection();

                // Success feedback popup
                JOptionPane.showMessageDialog(this, "Department updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);;

            } catch (IllegalArgumentException | IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }

        });

    }

    // Create the Department Management panel
    public JPanel createDepartmentPanel() {
        // Main panel setup with border layout and padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.decode("#6BBD9F"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Setup table headers and make cells non-editable
        String[] columns = {"Department Id", "Department Name"};
        deptTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct editing inside cells
            }
        };

        // Create table and add scroll pane to center area
        deptTable = new JTable(deptTableModel);
        deptTable.setRowHeight(24);
        panel.add(new JScrollPane(deptTable), BorderLayout.CENTER);

        // Bottom form panel setup
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Transparent background
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Operations"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        // Initialize text fields
        deptIdField = new JTextField(12);
        // Prevent user to edit the dept id
        deptIdField.setEditable(false);
        //prompt user it cannot be modified
        deptIdField.setBackground(new Color(240, 240, 240));
        deptNameField = new JTextField(16);

        // Initialize and style buttons
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

        // Row 2: Button group placed in one line
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

        // Add form panel to bottom area
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel createRosterPanel() {
        // Main panel setup with border layout and padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.decode("#88BEA7"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Setup table headers and make cells non-editable
        String[] columns = {"Roster ID", "Doctor Name", "Department", "Date", "Shift"};
        rosterTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct editing inside cells
            }
        };

        // Create table and add scroll pane to center area
        rosterTable = new JTable(rosterTableModel);
        rosterTable.setRowHeight(24);
        panel.add(new JScrollPane(rosterTable), BorderLayout.CENTER);

        // Bottom form panel setup
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Transparent background
        formPanel.setBorder(BorderFactory.createTitledBorder("Shift Assignment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialize form fields
        doctorNameField = new JTextField(14);
        rosterDateField = new JTextField(14);
        rosterDateField.setText("YYYY-MM-DD");
        rosterDateField.setForeground(Color.GRAY);

        // Placeholder logic for Date field
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

        // Dropdown boxes
        String[] sampleDepts = {"Mental Health", "Emergency Surgery", "Pediatrics"};
        departmentBox = new JComboBox<>(sampleDepts);

        String[] shiftTypes = {"Morning (08:00 - 16:00)", "Evening (16:00 - 00:00)", "Night (00:00 - 08:00)"};
        shiftTypeBox = new JComboBox<>(shiftTypes);

        // Initialize and style buttons
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Doctor Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(doctorNameField, gbc);

        // Row 1: Department
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(departmentBox, gbc);

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

    public JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.decode("#A5BFAF"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ================= 1. Top Section: KPI Summary Cards =================
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        kpiPanel.setOpaque(false);

        // Add 4 stat cards (Title + Number)
        kpiPanel.add(createKpiCard("Total Revenue", "$128,450.00"));
        kpiPanel.add(createKpiCard("Appointments", "1,240"));
        kpiPanel.add(createKpiCard("Active Doctors", "32"));
        kpiPanel.add(createKpiCard("Bed Occupancy", "87.5%"));

        panel.add(kpiPanel, BorderLayout.NORTH);

        // ================= 2. Center Section: Department Metrics Table =================
        String[] columns = {"Dept ID", "Department Name", "Patients Served", "Revenue", "Avg Stay (Days)"};
        metricsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent manual editing in table
            }
        };

        metricsTable = new JTable(metricsTableModel);
        metricsTable.setRowHeight(24);
        panel.add(new JScrollPane(metricsTable), BorderLayout.CENTER);

        // ================= 3. Bottom Section: Filter and Action Buttons =================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setOpaque(false);

        // Month filter dropdown
        String[] months = {
                "All Months",
                "January 2026", "February 2026", "March 2026", "April 2026",
                "May 2026", "June 2026", "July 2026", "August 2026",
                "September 2026", "October 2026", "November 2026", "December 2026"
        };
        monthFilterBox = new JComboBox<>(months);

        // Action buttons
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

    // Helper method: generate a styled metric card
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
        valueLabel.setForeground(Color.decode("#2E7D5E")); // Deep green
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Clear personal profile form fields
    public void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        passwordField.setText("");
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        genderBox.setSelectedIndex(0);
    }

    // Reset to "Add Mode" with the next auto-generated ID
    private void resetToNewDeptMode() {
        try {
            deptIdField.setText(operation.generateNextDeptId());
        } catch (IOException e) {
            deptIdField.setText("DP0001");
        }
        deptNameField.setText("");
    }

    // Main entry point to launch the UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MedicalManagerForm().setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}