/*
 * Created by JFormDesigner on Thu Sep 24 03:16:13 GMT+08:00 2026
 */

package Forms.AdminForm;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

/**
 * @author leezh
 */
public class AllocateFacilityToDepartmentPanel extends JPanel {
    public AllocateFacilityToDepartmentPanel() {
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
        facilityTable = new JTable();
        unLinkButton = new JButton();
        linkButton = new JButton();
        scrollPane2 = new JScrollPane();
        departmentTable = new JTable();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- facilityTable ----
            facilityTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "facilityObject"
                }
            ));
            scrollPane1.setViewportView(facilityTable);
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
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(unLinkButton)
                        .addComponent(linkButton))
                    .addGap(43, 43, 43)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                    .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE)
                                .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(114, 114, 114)
                            .addComponent(linkButton)
                            .addGap(18, 18, 18)
                            .addComponent(unLinkButton)))
                    .addContainerGap(23, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane scrollPane1;
    private JTable facilityTable;
    private JButton unLinkButton;
    private JButton linkButton;
    private JScrollPane scrollPane2;
    private JTable departmentTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
