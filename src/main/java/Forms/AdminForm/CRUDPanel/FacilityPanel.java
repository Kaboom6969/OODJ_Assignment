/*
 * Created by JFormDesigner on Thu Sep 24 03:08:25 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDPanel;

import Forms.AdminForm.CRUDDialog.FacilityDialog;import Forms.AdminForm.CRUDDialog.InsuranceDialog;
import Interfaces.RefreshablePanel;
import Operations.AdminOperation.AdminOperation;
import entities.BusinessEntity.Facility;import entities.BusinessEntity.Insurance;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;

/**
 * @author leezh
 */
public class FacilityPanel extends JPanel implements RefreshablePanel
{
    private AdminOperation adminOperation;
    private Window frameWindow;
    @Override
    public void refreshData()
    {
        reload(null);
    }


    public FacilityPanel(Window frameWindow, AdminOperation adminOperation)
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
        table.removeColumn(table.getColumn("Facility Object"));
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
        var objects = adminOperation.getAllFacilities();
        for (var object : objects)
        {
            Object[] row = new Object[6];
            row[0] = object.getId();
            row[1] = object.getSelf().getName();
            row[2] = object.getSelf().getFacilityType();
            row[3] = object.getSelf().getCapacity();
            row[4] = object.getSelf().isAvailable();
            row[5] = object;
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
        FacilityDialog facilityDialog = new FacilityDialog(frameWindow, adminOperation);
        facilityDialog.setVisible(true);
        reload(null);
    }

    private void update(ActionEvent e)
    {
        Facility facility = getObjectFromCurrentSelectedRow(table, 5);
        if (facility == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a facility");
            return;
        }
        FacilityDialog facilityDialog = new FacilityDialog(frameWindow, adminOperation, facility);
        facilityDialog.setVisible(true);
        reload(null);
    }

    private void delete(ActionEvent e)
    {
        Facility facility = getObjectFromCurrentSelectedRow(table, 5);
        if (facility == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a facility");
            return;
        }
        AdminOperation.CRUDInformation crudInformation = adminOperation.delete(facility);
        if (crudInformation.isSuccess())
        {
            reload(null);
            JOptionPane.showMessageDialog(this, "Facility has been deleted");
        } else
        {
            JOptionPane.showMessageDialog(this, crudInformation.message());
        }
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        allUserScrollPanel = new JScrollPane();
        table = new JTable();
        reloadUserButton = new JButton();
        addButton = new JButton();
        updateButton = new JButton();
        deleteButton = new JButton();

        //======== this ========

        //======== allUserScrollPanel ========
        {

            //---- table ----
            table.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, "", null, null},
                },
                new String[] {
                    "Id", "Name", "FacilityType", "Capacity", "Available Status", "Facility Object"
                }
            ));
            allUserScrollPanel.setViewportView(table);
        }

        //---- reloadUserButton ----
        reloadUserButton.setText("Reload");
        reloadUserButton.addActionListener(e -> reload(e));

        //---- addButton ----
        addButton.setText("Add");
        addButton.addActionListener(e -> add(e));

        //---- updateButton ----
        updateButton.setText("Update");
        updateButton.addActionListener(e -> update(e));

        //---- deleteButton ----
        deleteButton.setText("Delete");
        deleteButton.addActionListener(e -> delete(e));

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
                    .addContainerGap(67, Short.MAX_VALUE))
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
    private JTable table;
    private JButton reloadUserButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
