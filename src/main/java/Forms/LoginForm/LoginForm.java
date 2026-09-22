/*
 * Created by JFormDesigner on Thu Sep 17 01:46:42 GMT+08:00 2026
 */

package Forms.LoginForm;

import java.awt.event.*;

import Forms.MedicalManagerForm.MedicalManagerForm;
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
public class LoginForm extends JFrame
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
    }

    private void loginButtonMouseClicked(MouseEvent e)
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
            case Admin ignored: break;
            case MedicalManager medicalManager:
                MedicalManagerForm mmf = new MedicalManagerForm(hospitalEntityAllocator,medicalManager);
                mmf.setVisible(true);
                break;
            case Doctor ignored: break;
            case Patient ignored: break;
            default: break;
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        nameLabel = new JLabel();
        passwordLabel = new JLabel();
        nameField = new JTextField();
        passwordField = new JTextField();
        loginButton = new JButton();
        roleComboBox = new JComboBox<>();
        roleLabel = new JLabel();

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
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(106, 106, 106)
                                    .addComponent(nameLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                                .addComponent(passwordLabel, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                                .addComponent(roleLabel, GroupLayout.Alignment.TRAILING))
                            .addGap(58, 58, 58)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(nameField, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                                .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                                .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(146, 146, 146)
                            .addComponent(loginButton)))
                    .addContainerGap(70, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(125, 125, 125)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(passwordLabel, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(roleLabel))
                    .addGap(18, 18, 18)
                    .addComponent(loginButton)
                    .addGap(57, 57, 57))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JTextField nameField;
    private JTextField passwordField;
    private JButton loginButton;
    private JComboBox<String> roleComboBox;
    private JLabel roleLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
