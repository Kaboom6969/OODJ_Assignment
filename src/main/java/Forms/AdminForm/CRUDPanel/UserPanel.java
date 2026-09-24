/*
 * Created by JFormDesigner on Thu Sep 24 20:57:40 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDPanel;

import Forms.AdminForm.CRUDDialog.UserDialog;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.Users.User;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.BusinessEntity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;

/**
 * @author leezh
 */
public class UserPanel extends JPanel {
    private AdminOperation adminOperation;
    private Window parentWindow;
    public UserPanel(Window FrameWindow, AdminOperation adminOperation)
    {
        this.parentWindow = FrameWindow;
        this.adminOperation = adminOperation;
        initComponents();
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setDefaultEditor(Object.class, null);
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
        UserDialog userDialog = new UserDialog(parentWindow,adminOperation);
        userDialog.setVisible(true);
        reloadUser(null);
    }

    private void updateUser(ActionEvent e)
    {
        BusinessEntity<? extends User> user = getObjectFromCurrentSelectedRow(userTable,7);
        if(user == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        UserDialog userDialog = new UserDialog(parentWindow,adminOperation,user);
        userDialog.setVisible(true);
        reloadUser(null);
    }

    private void deleteUser(ActionEvent e)
    {
        BusinessEntity<? extends User> user = getObjectFromCurrentSelectedRow(userTable,7);
        if(user == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        AdminOperation.CRUDInformation crudInformation = adminOperation.delete(user);
        if (crudInformation.isSuccess())
        {
            reloadUser(null);
            JOptionPane.showMessageDialog(this, "User has been deleted");
        }
        else
        {
            JOptionPane.showMessageDialog(this, crudInformation.message());
        }
    }
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        allUserScrollPanel = new JScrollPane();
        userTable = new JTable();
        reloadUserButton = new JButton();
        addUserButton = new JButton();
        updateUserButton = new JButton();
        deleteUserButton = new JButton();

        //======== this ========

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

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(deleteUserButton)
                        .addComponent(updateUserButton)
                        .addComponent(addUserButton)
                        .addComponent(reloadUserButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
                    .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(57, 57, 57)
                            .addComponent(reloadUserButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(addUserButton)
                            .addGap(18, 18, 18)
                            .addComponent(updateUserButton)
                            .addGap(18, 18, 18)
                            .addComponent(deleteUserButton))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 425, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(34, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane allUserScrollPanel;
    private JTable userTable;
    private JButton reloadUserButton;
    private JButton addUserButton;
    private JButton updateUserButton;
    private JButton deleteUserButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
