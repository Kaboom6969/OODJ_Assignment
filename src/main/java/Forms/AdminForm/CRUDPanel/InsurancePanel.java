/*
 * Created by JFormDesigner on Thu Sep 24 03:22:51 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDPanel;

import Forms.AdminForm.CRUDDialog.InsuranceDialog;
import Forms.AdminForm.CRUDDialog.UserDialog;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.Users.User;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Insurance;

import java.awt.*;import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;

/**
 * @author leezh
 */
public class InsurancePanel extends JPanel {
    private AdminOperation adminOperation;
    private Window frameWindow;
    public InsurancePanel(Window frameWindow,AdminOperation adminOperation)
    {
        this.frameWindow = frameWindow;
        this.adminOperation = adminOperation;
        initComponents();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);
        panelInit();
    }

    private void panelInit()
    {
        table.removeColumn(table.getColumn("Insurance Object"));
        clearTable(table);
        loadAllToTable();
        table.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting()) return;
            buttonDetectForSelectListInTable();
        });
        buttonDetectForSelectListInTable();
    }
    private void buttonDetectForSelectListInTable()
    {
        int selectedRow = table.getSelectedRow();
        updateButton.setEnabled(selectedRow != -1);
        deleteButton.setEnabled(selectedRow != -1);
    }
    private void clearTable(JTable table)
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }

    private void loadAllToTable()
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        var objects = adminOperation.getAllInsurances();
        for (var object  : objects)
        {
            Object[] row = new Object[5];
            row[0] = object.getId();
            row[1] = object.getSelf().getCompanyName();
            row[2] = object.getSelf().getCoveragePercentage();
            row[3] = object.getSelf().isAccepted();
            row[4] = object;
            model.addRow(row);
        }

    }

    private void reload(ActionEvent e)
    {
        clearTable(table);
        loadAllToTable();
    }

    private void add(ActionEvent e)
    {
        InsuranceDialog insuranceDialog = new InsuranceDialog(frameWindow,adminOperation);
        insuranceDialog.setVisible(true);
        reload(null);
    }

    private void update(ActionEvent e)
    {
        Insurance insurance = getObjectFromCurrentSelectedRow(table,4);
        if(insurance == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a insurance");
            return;
        }
       InsuranceDialog insuranceDialog = new InsuranceDialog(frameWindow,adminOperation,insurance);
        insuranceDialog.setVisible(true);
        reload(null);
    }
    private void delete(ActionEvent e)
    {
        Insurance insurance = getObjectFromCurrentSelectedRow(table, 4);
        if(insurance == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a Insurance");
            return;
        }
        AdminOperation.CRUDInformation crudInformation = adminOperation.delete(insurance);
        if (crudInformation.isSuccess())
        {
            reload(null);
            JOptionPane.showMessageDialog(this, "Insurance has been deleted");
        }
        else
        {
            JOptionPane.showMessageDialog(this, crudInformation.message());
        }
    }






    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        reloadButton = new JButton();
        addButton = new JButton();
        updateButton = new JButton();
        deleteButton = new JButton();
        allUserScrollPanel = new JScrollPane();
        table = new JTable();

        //======== this ========

        //---- reloadButton ----
        reloadButton.setText("Reload");
        reloadButton.addActionListener(e -> reload(e));

        //---- addButton ----
        addButton.setText("Add");
        addButton.addActionListener(e -> add(e));

        //---- updateButton ----
        updateButton.setText("Update");
        updateButton.addActionListener(e -> update(e));

        //---- deleteButton ----
        deleteButton.setText("Delete");
        deleteButton.addActionListener(e -> delete(e));

        //======== allUserScrollPanel ========
        {

            //---- table ----
            table.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, null, null},
                },
                new String[] {
                    "Id", "Company Name", "Coverage Percentage", "Is Accepted", "Insurance Object"
                }
            ));
            {
                TableColumnModel cm = table.getColumnModel();
                cm.getColumn(2).setPreferredWidth(150);
            }
            allUserScrollPanel.setViewportView(table);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(32, 32, 32)
                    .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(reloadButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                        .addComponent(addButton)
                        .addComponent(updateButton)
                        .addComponent(deleteButton))
                    .addContainerGap(57, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 425, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(92, 92, 92)
                            .addComponent(reloadButton)
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
    private JButton reloadButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JScrollPane allUserScrollPanel;
    private JTable table;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
