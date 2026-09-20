/*
 * Created by JFormDesigner on Sat Sep 19 00:36:11 GMT+08:00 2026
 */

package Forms.AdminForm;

import Operations.AdminOperation.AdminOperation;
import Tools.EntityConvertManager;
import Tools.HospitalEntityAllocator;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.Users.User;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Admin;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;
import java.util.List;

/**
 * @author leezh
 */
public class AdminForm extends JFrame {
    private AdminOperation adminOperation;

    public AdminForm(Admin admin, HospitalEntityAllocator hospitalEntityAllocator)
    {
        adminOperation = new AdminOperation(hospitalEntityAllocator,admin);
        initComponents();
        loadAllUserToTable();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        adminTab = new JTabbedPane();
        allUserPanel = new JPanel();
        allUserScrollPanel = new JScrollPane();
        userTable = new JTable();
        reloadUserButton = new JButton();

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
                            "Role", "Id", "Name", "Email", "Gender", "Date Of Birth", "Phone Number"
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

                GroupLayout allUserPanelLayout = new GroupLayout(allUserPanel);
                allUserPanel.setLayout(allUserPanelLayout);
                allUserPanelLayout.setHorizontalGroup(
                    allUserPanelLayout.createParallelGroup()
                        .addGroup(allUserPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(reloadUserButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                allUserPanelLayout.setVerticalGroup(
                    allUserPanelLayout.createParallelGroup()
                        .addGroup(allUserPanelLayout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addComponent(reloadUserButton)
                            .addContainerGap(363, Short.MAX_VALUE))
                        .addGroup(allUserPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(allUserScrollPanel, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            adminTab.addTab("All User", allUserPanel);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(adminTab, GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void loadAllUserToTable()
    {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        var users = adminOperation.getAllUsers();
        for (var user : users)
        {
            User userData = user.getSelf();
            Object[] row = new Object[7];
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
            model.addRow(row);
        }

    }
}
