/*
 * Created by JFormDesigner on Thu Sep 24 03:19:04 GMT+08:00 2026
 */

package Forms.AdminForm;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

/**
 * @author leezh
 */
public class ConsultationRatePanel extends JPanel {
    public ConsultationRatePanel() {
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
        reloadButton = new JButton();
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

        //---- reloadButton ----
        reloadButton.setText("Reload");
        reloadButton.addActionListener(e -> reloadUser(e));

        //---- addButton ----
        addButton.setText("Add");
        addButton.addActionListener(e -> addUser(e));

        //---- updateButton ----
        updateButton.setText("Update");
        updateButton.addActionListener(e -> updateUser(e));

        //---- deleteButton ----
        deleteButton.setText("Delete");
        deleteButton.addActionListener(e -> deleteUser(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(49, Short.MAX_VALUE)
                    .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                    .addGap(35, 35, 35)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(deleteButton)
                        .addComponent(addButton)
                        .addComponent(reloadButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                        .addComponent(updateButton))
                    .addGap(138, 138, 138))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(175, 175, 175)
                    .addComponent(reloadButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(addButton)
                    .addGap(18, 18, 18)
                    .addComponent(updateButton)
                    .addGap(18, 18, 18)
                    .addComponent(deleteButton)
                    .addContainerGap(181, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(66, Short.MAX_VALUE)
                    .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 425, GroupLayout.PREFERRED_SIZE)
                    .addGap(49, 49, 49))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane allUserScrollPanel;
    private JTable userTable;
    private JButton reloadButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
