/*
 * Created by JFormDesigner on Sun Sep 20 23:01:21 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDDialog;

import java.awt.event.*;
import Forms.ComboBoxItem;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.Users.*;
import entities.BusinessEntity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author leezh
 */
public class UserDialog extends JDialog
{
    private final AdminOperation adminOperation;

    public enum Modes
    {
        ADD,MODIFY
    }
    private Modes mode;
    private String id = null;
    public UserDialog(Window owner,AdminOperation adminOperation)
    {
        super(owner, "Add User", ModalityType.APPLICATION_MODAL);
        initComponents();
        genderComboBox.setModel(new DefaultComboBoxModel<>(UserWithDetails.Gender.values()));
        initRoleComboItems(roleComboBox);
        actionButton.setText("Add");
        mode = Modes.ADD;
        this.adminOperation = adminOperation;
    }
    public UserDialog(Window owner,AdminOperation adminOperation, BusinessEntity<? extends User> user)
    {
        super(owner, "Modify User", ModalityType.APPLICATION_MODAL);
        Class<? extends User> userClass = (Class<? extends User>) ((ParameterizedType)user.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        initComponents();
        this.id = user.getId();
        genderComboBox.setModel(new DefaultComboBoxModel<>(UserWithDetails.Gender.values()));
        roleComboBox.addItem
        (
                new ComboBoxItem<Class<? extends User>>(userClass,"")
        );
        roleComboBox.setSelectedIndex(0);
        roleComboBox.setVisible(false);
        roleLabel.setVisible(false);
        updateFieldForRole(user.getSelf().getClass());
        actionButton.setText("Modify");
        updateAllFieldForUser(user.getSelf());
        mode = Modes.MODIFY;
        this.adminOperation = adminOperation;
    }

    private void updateAllFieldForUser(User user)
    {
        nameTextField.setText(user.getName());
        passwordTextField.setText(user.getPassword());
        emailTextField.setText(user.getEmail());
        if (user instanceof UserWithDetails userWithDetails)
        {
            genderComboBox.setSelectedItem(userWithDetails.getGender());
            dateOfBirthTextField.setText(userWithDetails.getDateOfBirth().toString());
            phoneNumberTextField.setText(userWithDetails.getPhoneNumber());
        }

    }
    private void initRoleComboItems(JComboBox<ComboBoxItem<?>> jComboBox)
    {
        jComboBox.addItem(new ComboBoxItem<Class<?>>(AdminToFile.class,"Admin"));
        jComboBox.addItem(new ComboBoxItem<Class<?>>(PatientToFile.class,"Patient"));
        jComboBox.addItem(new ComboBoxItem<Class<?>>(DoctorToFile.class,"Doctor"));
        jComboBox.addItem(new ComboBoxItem<Class<?>>(MedicalManagerToFile.class,"MedicalManager"));
    }

    private void updateFieldForRole(Class<? extends User> clazz)
    {
        if (clazz.getSuperclass().equals(User.class))
        {
            genderLabel.setVisible(false);
            genderComboBox.setVisible(false);
            dateOfBirthLabel.setVisible(false);
            dateOfBirthTextField.setVisible(false);
            dateOfBirthTextField.setText("");
            phoneNumberLabel.setVisible(false);
            phoneNumberTextField.setVisible(false);
            phoneNumberTextField.setText("");
        }
        else
        {
            genderLabel.setVisible(true);
            genderComboBox.setVisible(true);
            dateOfBirthLabel.setVisible(true);
            dateOfBirthTextField.setVisible(true);
            phoneNumberLabel.setVisible(true);
            phoneNumberTextField.setVisible(true);
        }
    }

    private void roleComboBoxItemStateChanged(ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            ComboBoxItem<Class<?>> ce = (ComboBoxItem<Class<?>>) e.getItem();
            updateFieldForRole((Class<? extends User>) ce.getItem());
        }
    }
    private List<String> packData(String id)
    {
        List<String> data = new ArrayList<>();
        data.add(id);
        data.add(nameTextField.getText());
        data.add(passwordTextField.getText());
        data.add(emailTextField.getText());
        ComboBoxItem<?> role = (ComboBoxItem<?>)roleComboBox.getSelectedItem();
        if (role == null) throw new IllegalStateException("Role is not Selected!");
        if (role.getItem() == AdminToFile.class) return data;
        data.add(String.valueOf((UserWithDetails.Gender)genderComboBox.getSelectedItem()));
        data.add(dateOfBirthTextField.getText());
        data.add(phoneNumberTextField.getText());
        return data;
    }

    private void action(ActionEvent e)
    {
        AdminOperation.CRUDInformation crudInformation = null;
        switch (mode)
        {
            case ADD:
                try
                {
                    crudInformation =
                    adminOperation.add(packData(null), (((ComboBoxItem<Class<?>>) Objects.requireNonNull(roleComboBox.getSelectedItem())).getItem()));
                } catch (RuntimeException re)
                {
                    crudInformation = new AdminOperation.CRUDInformation(false, re.getMessage());
                }
                break;
            case MODIFY:
                try
                {
                    BusinessEntity<?> user = adminOperation.constructFull(packData(id), (Class<? extends BaseEntity>) ((ComboBoxItem<Class<?>>) Objects.requireNonNull(roleComboBox.getSelectedItem())).getItem());
                    crudInformation = adminOperation.update(user);
                } catch (RuntimeException re)
                {
                    crudInformation = new AdminOperation.CRUDInformation(false, re.getMessage());
                }
        }
        int icon = JOptionPane.INFORMATION_MESSAGE;
        if (!crudInformation.isSuccess()) icon = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(this, crudInformation.message(),"",icon);
        if (crudInformation.isSuccess()) dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        nameLabel = new JLabel();
        passwordLabel = new JLabel();
        emailLabel = new JLabel();
        genderLabel = new JLabel();
        dateOfBirthLabel = new JLabel();
        phoneNumberLabel = new JLabel();
        roleLabel = new JLabel();
        nameTextField = new JTextField();
        emailTextField = new JTextField();
        phoneNumberTextField = new JTextField();
        genderComboBox = new JComboBox();
        roleComboBox = new JComboBox();
        passwordTextField = new JTextField();
        actionButton = new JButton();
        dateOfBirthTextField = new JTextField();

        //======== this ========
        var contentPane = getContentPane();

        //---- nameLabel ----
        nameLabel.setText("Name:");

        //---- passwordLabel ----
        passwordLabel.setText("Password:");

        //---- emailLabel ----
        emailLabel.setText("Email:");

        //---- genderLabel ----
        genderLabel.setText("Gender:");

        //---- dateOfBirthLabel ----
        dateOfBirthLabel.setText("Date Of Birth:");

        //---- phoneNumberLabel ----
        phoneNumberLabel.setText("Phone Number:");

        //---- roleLabel ----
        roleLabel.setText("Role:");

        //---- roleComboBox ----
        roleComboBox.addItemListener(e -> roleComboBoxItemStateChanged(e));

        //---- actionButton ----
        actionButton.setText("Action");
        actionButton.addActionListener(e -> action(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(nameLabel)
                        .addComponent(passwordLabel)
                        .addComponent(emailLabel)
                        .addComponent(dateOfBirthLabel)
                        .addComponent(phoneNumberLabel)
                        .addComponent(genderLabel)
                        .addComponent(roleLabel))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(passwordTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(phoneNumberTextField, GroupLayout.Alignment.LEADING)
                                .addComponent(emailTextField, GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(genderComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(dateOfBirthTextField, GroupLayout.Alignment.LEADING))
                            .addGap(69, 69, 69))))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(114, 114, 114)
                    .addComponent(actionButton)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(23, 23, 23)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameLabel)
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(emailLabel)
                        .addComponent(emailTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genderComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(genderLabel))
                    .addGap(9, 9, 9)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(dateOfBirthLabel)
                        .addComponent(dateOfBirthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(21, 21, 21)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(phoneNumberLabel)
                        .addComponent(phoneNumberTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(20, 20, 20)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(roleLabel))
                    .addGap(18, 18, 18)
                    .addComponent(actionButton)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JLabel emailLabel;
    private JLabel genderLabel;
    private JLabel dateOfBirthLabel;
    private JLabel phoneNumberLabel;
    private JLabel roleLabel;
    private JTextField nameTextField;
    private JTextField emailTextField;
    private JTextField phoneNumberTextField;
    private JComboBox genderComboBox;
    private JComboBox roleComboBox;
    private JTextField passwordTextField;
    private JButton actionButton;
    private JTextField dateOfBirthTextField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
