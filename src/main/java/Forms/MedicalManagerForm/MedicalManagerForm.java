package Forms.MedicalManagerForm;

import Operations.MedicalManagerOperation.MedicalManagerOperation;

import javax.swing.*;
import java.awt.*;

public class MedicalManagerForm extends JFrame {

    private JTextField nameField;
    private JComboBox<String> genderBox;
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton saveBtn;
    private JTextField dobField;

    private MedicalManagerOperation operation = new MedicalManagerOperation();

    public MedicalManagerForm() {
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

        // Window settings
        setTitle("Medical Manager Main.Java.Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane jTabbedPane = new JTabbedPane();
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Row 0: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        profilePanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        profilePanel.add(nameField, gbc);

        // Row 1: Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        profilePanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        profilePanel.add(passwordField, gbc);


        // Row 2: Gender
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        profilePanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        profilePanel.add(genderBox, gbc);

        // Row 3: DOB
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        profilePanel.add(new JLabel("Day Of Birth:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        profilePanel.add(dobField, gbc);

        // Row 4: Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        profilePanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        profilePanel.add(emailField, gbc);

        // Row 5: PhoneNumber
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        profilePanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        profilePanel.add(phoneField, gbc);

        // Row 6 : Button
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        profilePanel.add(saveBtn, gbc);


        JPanel deptPanel = new JPanel();


        JPanel rosterPanel = new JPanel();
        JPanel reportPanel = new JPanel();

        jTabbedPane.addTab("Personal Profile", profilePanel);
        jTabbedPane.addTab("Department Management", deptPanel);
        jTabbedPane.addTab("Shift Rosters", rosterPanel);
        jTabbedPane.addTab("Metrics & Revenue", reportPanel);

        add(jTabbedPane);
        profilePanel.setBackground(Color.decode("#4EBC97"));
        deptPanel.setBackground(Color.decode("#6BBD9F"));
        rosterPanel.setBackground(Color.decode("#88BEA7"));
        reportPanel.setBackground(Color.decode("#A5BFAF"));

        initEvents();
    }


    private void initEvents() {
        saveBtn.addActionListener(e -> {

            String name = nameField.getText();
            String gender = (String) genderBox.getSelectedItem();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String dob = dobField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // 调用业务层：如果校验失败，下一行不会执行，直接跳到 catch
                operation.updateProfile(name, password, gender, dob, email, phone);

                // 走到这里说明没有任何异常，保存成功
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields(); // 清空输入框

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Validation Error: " + ex.getMessage(),
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "System Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

    }


    public void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        passwordField.setText("");
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        genderBox.setSelectedIndex(0);
    }
}