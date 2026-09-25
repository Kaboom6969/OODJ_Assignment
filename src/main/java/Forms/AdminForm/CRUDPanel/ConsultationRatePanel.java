/*
 * Created by JFormDesigner on Thu Sep 24 03:19:04 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDPanel;

import Forms.AdminForm.CRUDDialog.ConsultationRateDialog;
import Forms.AdminForm.CRUDDialog.FacilityDialog;
import Interfaces.RefreshablePanel;
import Operations.AdminOperation.AdminOperation;
import entities.BusinessEntity.ConsultationRate;
import entities.BusinessEntity.Facility;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;

/**
 * @author leezh
 */
public class ConsultationRatePanel extends JPanel implements RefreshablePanel
{
    private AdminOperation adminOperation;
    private Window frameWindow;
    @Override
    public void refreshData()
    {
        reload(null);
    }

    public ConsultationRatePanel(Window frameWindow, AdminOperation adminOperation)
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
        table.removeColumn(table.getColumn("ConsultationRate Object"));
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
        var objects = adminOperation.getAllConsultationRates();
        for (var object : objects)
        {
            Object[] row = new Object[5];
            row[0] = object.getId();
            row[1] = object.getSelf().getName();
            row[2] = object.getSelf().getPrice();
            row[3] = object.getSelf().isActive();
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
        ConsultationRateDialog consultationRateDialog = new ConsultationRateDialog(frameWindow, adminOperation);
        consultationRateDialog.setVisible(true);
        reload(null);
    }

    private void update(ActionEvent e)
    {
        ConsultationRate consultationRate = getObjectFromCurrentSelectedRow(table, 4);
        if (consultationRate == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a consultation rate");
            return;
        }
        ConsultationRateDialog consultationRateDialog = new ConsultationRateDialog(frameWindow, adminOperation, consultationRate);
        consultationRateDialog.setVisible(true);
        reload(null);
    }

    private void delete(ActionEvent e)
    {
        ConsultationRate consultationRate = getObjectFromCurrentSelectedRow(table, 4);
        if (consultationRate == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a consultation rate");
            return;
        }
        AdminOperation.CRUDInformation crudInformation = adminOperation.delete(consultationRate);
        if (crudInformation.isSuccess())
        {
            reload(null);
            JOptionPane.showMessageDialog(this, "Consultation rate has been deleted");
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
        reloadButton = new JButton();
        addButton = new JButton();
        updateButton = new JButton();
        deleteButton = new JButton();

        //======== this ========

        //======== allUserScrollPanel ========
        {

            //---- table ----
            table.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, null, null},
                },
                new String[] {
                    "Id", "Name", "Price", "Is Active", "ConsultationRate Object"
                }
            ));
            allUserScrollPanel.setViewportView(table);
        }

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
    private JTable table;
    private JButton reloadButton;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
