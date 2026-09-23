/*
 * Created by JFormDesigner on Sat Sep 19 00:36:11 GMT+08:00 2026
 */

package Forms.AdminForm;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import Operations.AdminOperation.AdminOperation;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BaseEntity.Users.User;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Admin;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.MedicalManager;
import entities.LazyEntity.LazyEntityList;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

/**
 * @author leezh
 */
public class AdminForm extends JFrame {
    private AdminOperation adminOperation;
    private AllocateDoctorToMedicalManagerPanel allocateDoctorToMedicalManagerPanel;



    public AdminForm(Admin admin, HospitalEntityAllocator hospitalEntityAllocator)
    {
        adminOperation = new AdminOperation(hospitalEntityAllocator,admin);
        initComponents();
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setDefaultEditor(Object.class, null);
        allocateDoctorToMedicalManagerPanel = new AllocateDoctorToMedicalManagerPanel(adminOperation);
        DTMMPanel.add(allocateDoctorToMedicalManagerPanel,BorderLayout.CENTER);
        userPanelInit();

    }



    private void userPanelInit()
    {
        userTable.removeColumn(userTable.getColumn("User Object"));
        clearTable(userTable);
        loadAllUserToUserTable();
        userTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting()) return;
            buttonDetectForSelectListInAllUserTable();
        });
        buttonDetectForSelectListInAllUserTable();
    }
    private void buttonDetectForSelectListInAllUserTable()
    {
        int selectedRow = userTable.getSelectedRow();
        updateUserButton.setEnabled(selectedRow != -1);
        deleteUserButton.setEnabled(selectedRow != -1);
    }



    private void clearTable(JTable table)
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }

    private void reloadUser(ActionEvent e)
    {
        clearTable(userTable);
        loadAllUserToUserTable();
    }

    private void addUser(ActionEvent e)
    {
        UserDialog userDialog = new UserDialog(this,adminOperation);
        userDialog.setVisible(true);
        reloadUser(null);
        allocateDoctorToMedicalManagerPanel.reload();
    }

    private void updateUser(ActionEvent e)
    {
        BusinessEntity<? extends User> user = getObjectFromCurrentSelectedRow(userTable,7);
        if(user == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        UserDialog userDialog = new UserDialog(this,adminOperation,user);
        userDialog.setVisible(true);
        reloadUser(null);
        allocateDoctorToMedicalManagerPanel.reload();
    }

    private void deleteUser(ActionEvent e)
    {
        int selectedRow = userTable.getSelectedRow();
        if(selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        BusinessEntity<? extends User> user = (BusinessEntity<? extends User>)userTable.getModel().getValueAt(modelRow, 7);
        AdminOperation.CRUDInformation crudInformation = adminOperation.deleteUser(user);
        if (crudInformation.isSuccess())
        {
            reloadUser(null);
            allocateDoctorToMedicalManagerPanel.reload();
            JOptionPane.showMessageDialog(this, "User has been deleted");
        }
        else
        {
            JOptionPane.showMessageDialog(this, crudInformation.message());
        }
    }

    private <T> T getObjectFromCurrentSelectedRow(JTable table, int column)
    {
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1)
        {
           return null;
        }
        selectedRow = table.convertRowIndexToModel(selectedRow);
        return getObjectFromRow(table,selectedRow,column);
    }
    private <T> T getObjectFromRow(JTable table, int modelRow, int column)
    {
        return (T)table.getModel().getValueAt(modelRow, column);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        adminTab = new JTabbedPane();
        allUserPanel = new JPanel();
        allUserScrollPanel = new JScrollPane();
        userTable = new JTable();
        reloadUserButton = new JButton();
        addUserButton = new JButton();
        updateUserButton = new JButton();
        deleteUserButton = new JButton();
        DTMMPanel = new JPanel();

        //======== this ========
        var contentPane = getContentPane();

        //======== adminTab ========
        {

            //======== allUserPanel ========
            {

                //======== allUserScrollPanel ========
                {

                    //---- userTable ----
                    userTable.setModel(new DefaultTableModel(
                        new Object[][] {
                        },
                        new String[] {
                            "Role", "Id", "Name", "Email", "Gender", "Date Of Birth", "Phone Number", "User Object"
                        }
                    ));
                    {
                        TableColumnModel cm = userTable.getColumnModel();
                        cm.getColumn(3).setPreferredWidth(150);
                    }
                    allUserScrollPanel.setViewportView(userTable);
                }

                //---- reloadUserButton ----
                reloadUserButton.setText("Reload");
                reloadUserButton.addActionListener(e -> reloadUser(e));

                //---- addUserButton ----
                addUserButton.setText("Add User");
                addUserButton.addActionListener(e -> addUser(e));

                //---- updateUserButton ----
                updateUserButton.setText("Update User");
                updateUserButton.addActionListener(e -> updateUser(e));

                //---- deleteUserButton ----
                deleteUserButton.setText("Delete User");
                deleteUserButton.addActionListener(e -> deleteUser(e));

                GroupLayout allUserPanelLayout = new GroupLayout(allUserPanel);
                allUserPanel.setLayout(allUserPanelLayout);
                allUserPanelLayout.setHorizontalGroup(
                    allUserPanelLayout.createParallelGroup()
                        .addGroup(allUserPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(allUserPanelLayout.createParallelGroup()
                                .addComponent(reloadUserButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                                .addComponent(addUserButton)
                                .addComponent(updateUserButton)
                                .addComponent(deleteUserButton))
                            .addContainerGap(31, Short.MAX_VALUE))
                );
                allUserPanelLayout.setVerticalGroup(
                    allUserPanelLayout.createParallelGroup()
                        .addGroup(allUserPanelLayout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addComponent(reloadUserButton)
                            .addGap(18, 18, 18)
                            .addComponent(addUserButton)
                            .addGap(18, 18, 18)
                            .addComponent(updateUserButton)
                            .addGap(18, 18, 18)
                            .addComponent(deleteUserButton)
                            .addContainerGap(207, Short.MAX_VALUE))
                        .addGroup(allUserPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(allUserScrollPanel, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            adminTab.addTab("All User", allUserPanel);

            //======== DTMMPanel ========
            {
                DTMMPanel.setLayout(new BorderLayout());
            }
            adminTab.addTab("Allocate Doctor to Manger", DTMMPanel);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(adminTab)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(adminTab)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JTabbedPane adminTab;
    private JPanel allUserPanel;
    private JScrollPane allUserScrollPanel;
    private JTable userTable;
    private JButton reloadUserButton;
    private JButton addUserButton;
    private JButton updateUserButton;
    private JButton deleteUserButton;
    private JPanel DTMMPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void loadAllUserToUserTable()
    {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        var users = adminOperation.getAllUsers();
        for (var user : users)
        {
            User userData = user.getSelf();
            Object[] row = new Object[8];
            row[0] = userData.getClass()
                    .getSimpleName()
                    .replace("ToFile", "");
            row[1] = user.getId();
            row[2] = user.getSelf().getName();
            row[3] = user.getSelf().getEmail();
            if (userData instanceof UserWithDetails userWithDetailsData)
            {
                row[4] = userWithDetailsData.getGender();
                row[5] = userWithDetailsData.getDateOfBirth();
                row[6] = userWithDetailsData.getPhoneNumber();
            }
            row[7] = user;
            model.addRow(row);
        }

    }
}
