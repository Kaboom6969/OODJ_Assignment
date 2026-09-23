/*
 * Created by JFormDesigner on Thu Sep 24 03:08:25 GMT+08:00 2026
 */

package Forms.AdminForm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

/**
 * @author leezh
 */
public class FacilityPanel extends JPanel {
    public FacilityPanel() {
        initComponents();
    }

    private void reloadUser(ActionEvent e) {
        // TODO add your code here
    }

    private void addUser(ActionEvent e) {
        // TODO add your code here
    }

    private void updateUser(ActionEvent e) {
        // TODO add your code here
    }

    private void deleteUser(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        allUserScrollPanel = new JScrollPane();
        userTable = new JTable();
        reloadUserButton = new JButton();
        addButton = new JButton();
        updateButton = new JButton();
        deleteButton = new JButton();

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

        //---- addButton ----
        addButton.setText("Add Facility");
        addButton.addActionListener(e -> addUser(e));

        //---- updateButton ----
        updateButton.setText("Update Facility");
        updateButton.addActionListener(e -> updateUser(e));

        //---- deleteButton ----
        deleteButton.setText("Delete Facility");
        deleteButton.addActionListener(e -> deleteUser(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                    .addGap(35, 35, 35)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(deleteButton)
                        .addComponent(updateButton)
                        .addComponent(addButton)
                        .addComponent(reloadUserButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(104, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 425, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(158, 158, 158)
                            .addComponent(reloadUserButton)
                            .addGap(18, 18, 18)
                            .addComponent(addButton)
                            .addGap(18, 18, 18)
                            .addComponent(updateButton)
                            .addGap(18, 18, 18)
                            .addComponent(deleteButton)))
                    .addContainerGap(43, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane allUserScrollPanel;
    private JTable userTable;
    private JButton reloadUserButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
