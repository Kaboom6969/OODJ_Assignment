/*
 * Created by JFormDesigner on Thu Sep 17 01:46:42 GMT+08:00 2026
 */

package Forms.LoginForm;

import java.awt.event.*;

import Forms.AdminForm.AdminForm;
import Forms.BaseFrame;
import Forms.DoctorForm.DoctorPanel;
import Forms.DoctorForm.DoctorPanelTest;
import Forms.MedicalManagerForm.MedicalManagerForm;
import Forms.PatientForm.PatientForm;
import Operations.LoginOperation.LoginOperation;
import Tools.EntityConvertManager;
import Tools.HospitalEntityAllocator;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.Users.*;
import entities.BusinessEntity.Admin;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.MedicalManager;
import entities.BusinessEntity.Patient;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author leezh
 */
public class LoginForm extends BaseFrame
{
    private final Map<String, Class<? extends User>> roleMap =
    Map.of
    (
            "Admin Staff", AdminToFile.class,
            "Medical Manager", MedicalManagerToFile.class,
            "Doctor", DoctorToFile.class,
            "Patient", PatientToFile.class
    );
    private HospitalEntityAllocator hospitalEntityAllocator;
    private LoginOperation loginOperation;
    public LoginForm(HospitalEntityAllocator hospitalEntityAllocator)
    {
        this.hospitalEntityAllocator = hospitalEntityAllocator;
        loginOperation = new LoginOperation(hospitalEntityAllocator);
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private void clear()
    {
        nameField.setText("");
        passwordField.setText("");
    }

    private void loginButtonMouseClicked(MouseEvent e)
    {

    }

    private void login(ActionEvent e)
    {
        if (nameField.getText().isEmpty() || passwordField.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please fill all the fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        var user = loginOperation.login(nameField.getText(),passwordField.getText(),roleMap.get((String)roleComboBox.getSelectedItem()));
        if (user == null)
        {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        switch (user)
        {
            case Admin admin:
                AdminForm adminForm = new AdminForm(admin,hospitalEntityAllocator);
                goToSon(adminForm);
                break;
            case MedicalManager medicalManager:
                MedicalManagerForm medicalManagerForm = new MedicalManagerForm(hospitalEntityAllocator,medicalManager);
                goToSon(medicalManagerForm);
                break;
            case Doctor doctor:
                DoctorPanelTest doctorPanel = new DoctorPanelTest(doctor,hospitalEntityAllocator);
                goToSon(doctorPanel);
                break;
            case Patient patient:
                PatientForm patientForm = new PatientForm(hospitalEntityAllocator,patient);
                goToSon(patientForm);
            default: break;
        }
        clear();
        this.setVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        nameLabel = new JLabel();
        passwordLabel = new JLabel();
        nameField = new JTextField();
        loginButton = new JButton();
        roleComboBox = new JComboBox<>();
        roleLabel = new JLabel();
        passwordField = new JPasswordField();

        //======== this ========
        var contentPane = getContentPane();

        //---- nameLabel ----
        nameLabel.setText("Name:");

        //---- passwordLabel ----
        passwordLabel.setText("Password:");

        //---- loginButton ----
        loginButton.setText("Login");
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loginButtonMouseClicked(e);
            }
        });
        loginButton.addActionListener(e -> login(e));

        //---- roleComboBox ----
        roleComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
            "Admin Staff",
            "Medical Manager",
            "Doctor",
            "Patient"
        }));

        //---- roleLabel ----
        roleLabel.setText("Role:");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap(68, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(25, 25, 25)
                                    .addComponent(nameLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                                .addComponent(passwordLabel, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                                .addComponent(roleLabel, GroupLayout.Alignment.TRAILING))
                            .addGap(58, 58, 58)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(passwordField, GroupLayout.Alignment.LEADING)
                                    .addComponent(nameField, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE))))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(65, 65, 65)
                            .addComponent(loginButton)))
                    .addGap(61, 61, 61))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(59, 59, 59)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(passwordLabel, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(21, 21, 21)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(roleLabel))
                    .addGap(18, 18, 18)
                    .addComponent(loginButton)
                    .addGap(43, 43, 43))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JTextField nameField;
    private JButton loginButton;
    private JComboBox<String> roleComboBox;
    private JLabel roleLabel;
    private JPasswordField passwordField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
