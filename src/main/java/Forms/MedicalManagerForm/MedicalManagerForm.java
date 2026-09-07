package Forms.MedicalManagerForm;

import Operations.MedicalManagerOperation.MedicalManagerOperation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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

    // --- Business Logic Object ---
    private MedicalManagerOperation operation = new MedicalManagerOperation();

    // Constructor: setup the main window and tabs
    public MedicalManagerForm() {
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
        //jTabbedPane.addTab("Metrics & Revenue", createReportPanel());

        add(jTabbedPane);

        // Bind event listeners
        initEvents();
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

        // Window settings
        setTitle("Medical Manager Main.Java.Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane jTabbedPane = new JTabbedPane();
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
        // Save button action listener
        saveBtn.addActionListener(e -> {

            // Get input values from text fields
            String name = nameField.getText();
            String gender = (String) genderBox.getSelectedItem();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String dob = dobField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Call operation to update profile and validate inputs
                operation.updateProfile(name, password, gender, dob, email, phone);

                // Show success popup message
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields(); // Clear all text fields

            } catch (IllegalArgumentException ex) {
                // Show validation error message (e.g. invalid format)
                JOptionPane.showMessageDialog(
                        this,
                        "Validation Error: " + ex.getMessage(),
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE
                );
            } catch (Exception ex) {
                // Show unexpected system error message
                JOptionPane.showMessageDialog(
                        this,
                        "System Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
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
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Doctor Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(doctorNameField, gbc);

        // Row 1: Department
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

    public void createReportPanel() {
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

    // Main entry point to launch the UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MedicalManagerForm().setVisible(true);
        });
    }
}