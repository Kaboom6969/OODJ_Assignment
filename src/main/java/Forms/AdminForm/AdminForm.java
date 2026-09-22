/*
 * Created by JFormDesigner on Sat Sep 19 00:36:11 GMT+08:00 2026
 */

package Forms.AdminForm;

import java.awt.event.*;
import Operations.AdminOperation.AdminOperation;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.Users.User;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Admin;
import entities.BusinessEntity.BusinessEntity;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

/**
 * @author leezh
 */
public class AdminForm extends JFrame {
    private AdminOperation adminOperation;

    public AdminForm(Admin admin, HospitalEntityAllocator hospitalEntityAllocator)
    {
        adminOperation = new AdminOperation(hospitalEntityAllocator,admin);
        initComponents();
        userTable.removeColumn(userTable.getColumn("User Object"));
        clearUserTable();
        loadAllUserToTable();
        userTable.getSelectionModel().addListSelectionListener(e -> {
            buttonDetectForSelectListInTable();});
        buttonDetectForSelectListInTable();
    }

    private void buttonDetectForSelectListInTable()
    {
        int selectedRow = userTable.getSelectedRow();
        updateUserButton.setEnabled(selectedRow != -1);
        deleteUserButton.setEnabled(selectedRow != -1);
    }

    private void clearUserTable()
    {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);
    }

    private void reloadUser(ActionEvent e)
    {
        clearUserTable();
        loadAllUserToTable();
    }

    private void addUser(ActionEvent e)
    {
        UserDialog userDialog = new UserDialog(this,adminOperation);
        userDialog.setVisible(true);
    }

    private void updateUser(ActionEvent e)
    {
        int selectedRow = userTable.getSelectedRow();
        if(selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        BusinessEntity<? extends User> user = (BusinessEntity<? extends User>)userTable.getModel().getValueAt(modelRow, 7);
        UserDialog userDialog = new UserDialog(this,adminOperation,user);
        userDialog.setVisible(true);
        reloadUser(null);
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
        adminOperation.deleteUser(user);
        reloadUser(null);
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
        scrollPane1 = new JScrollPane();
        doctorTable = new JTable();
        scrollPane2 = new JScrollPane();
        medicalManagerTable = new JTable();
        linkButton = new JButton();
        unLinkButton = new JButton();

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

                //======== scrollPane1 ========
                {

                    //---- doctorTable ----
                    doctorTable.setModel(new DefaultTableModel(
                        new Object[][] {
                            {null, null, null},
                            {null, null, null},
                        },
                        new String[] {
                            "Id", "Name", "doctorObject"
                        }
                    ));
                    scrollPane1.setViewportView(doctorTable);
                }

                //======== scrollPane2 ========
                {

                    //---- medicalManagerTable ----
                    medicalManagerTable.setModel(new DefaultTableModel(
                        new Object[][] {
                            {null, null, null},
                            {null, null, null},
                        },
                        new String[] {
                            "Id", "Name", "medicalManagerObject"
                        }
                    ));
                    scrollPane2.setViewportView(medicalManagerTable);
                }

                //---- linkButton ----
                linkButton.setText("Link");

                //---- unLinkButton ----
                unLinkButton.setText("Unlink");

                GroupLayout DTMMPanelLayout = new GroupLayout(DTMMPanel);
                DTMMPanel.setLayout(DTMMPanelLayout);
                DTMMPanelLayout.setHorizontalGroup(
                    DTMMPanelLayout.createParallelGroup()
                        .addGroup(DTMMPanelLayout.createSequentialGroup()
                            .addGap(46, 46, 46)
                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                            .addGroup(DTMMPanelLayout.createParallelGroup()
                                .addComponent(unLinkButton)
                                .addComponent(linkButton))
                            .addGap(44, 44, 44)
                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                            .addGap(22, 22, 22))
                );
                DTMMPanelLayout.setVerticalGroup(
                    DTMMPanelLayout.createParallelGroup()
                        .addGroup(DTMMPanelLayout.createSequentialGroup()
                            .addGap(56, 56, 56)
                            .addGroup(DTMMPanelLayout.createParallelGroup()
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                            .addContainerGap())
                        .addGroup(DTMMPanelLayout.createSequentialGroup()
                            .addGap(170, 170, 170)
                            .addComponent(linkButton)
                            .addGap(41, 41, 41)
                            .addComponent(unLinkButton)
                            .addContainerGap(158, Short.MAX_VALUE))
                );
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
    private JScrollPane scrollPane1;
    private JTable doctorTable;
    private JScrollPane scrollPane2;
    private JTable medicalManagerTable;
    private JButton linkButton;
    private JButton unLinkButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void loadAllUserToTable()
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
