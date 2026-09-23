/*
 * Created by JFormDesigner on Thu Sep 24 03:20:37 GMT+08:00 2026
 */

package Forms.AdminForm;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

/**
 * @author leezh
 */
public class AllocateConsultationRateToDepartmentPanel extends JPanel {
    public AllocateConsultationRateToDepartmentPanel() {
        initComponents();
    }

    private void unLink(ActionEvent e) {
        // TODO add your code here
    }

    private void link(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        scrollPane1 = new JScrollPane();
        consulatationRateTable = new JTable();
        unLinkButton = new JButton();
        linkButton = new JButton();
        scrollPane2 = new JScrollPane();
        departmentTable = new JTable();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- consulatationRateTable ----
            consulatationRateTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "consultationRateObject"
                }
            ));
            scrollPane1.setViewportView(consulatationRateTable);
        }

        //---- unLinkButton ----
        unLinkButton.setText("Unlink");
        unLinkButton.addActionListener(e -> unLink(e));

        //---- linkButton ----
        linkButton.setText("Link");
        linkButton.addActionListener(e -> link(e));

        //======== scrollPane2 ========
        {

            //---- departmentTable ----
            departmentTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "departmentObject"
                }
            ));
            scrollPane2.setViewportView(departmentTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                    .addGap(53, 53, 53)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(unLinkButton)
                        .addComponent(linkButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(121, 121, 121)
                    .addComponent(linkButton)
                    .addGap(18, 18, 18)
                    .addComponent(unLinkButton)
                    .addContainerGap(278, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 12, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane scrollPane1;
    private JTable consulatationRateTable;
    private JButton unLinkButton;
    private JButton linkButton;
    private JScrollPane scrollPane2;
    private JTable departmentTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
