/*
 * Created by JFormDesigner on Sat Sep 26 15:26:35 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDPanel;

import Forms.AdminForm.CRUDDialog.AssessmentResultDialog;
import Interfaces.RefreshablePanel;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.MedicalRequestToFile;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.MedicalRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;

/**
 * @author leezh
 */
public class MedicalRequestPanel extends JPanel implements RefreshablePanel
{
    private final AdminOperation adminOperation;
    private final Window frameWindow;
    @Override
    public void refreshData()
    {
        reload(null);
    }


    public MedicalRequestPanel(Window frameWindow, AdminOperation adminOperation)
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
        table.removeColumn(table.getColumn("object"));
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
        approveButton.setEnabled(false);
        rejectButton.setEnabled(false);
        MedicalRequestToFile.RequestStatus status  = getObjectFromCurrentSelectedRow(table, 5);
        if (status == null) return;
        if (status == MedicalRequestToFile.RequestStatus.PENDING)
        {
            approveButton.setEnabled(true);
            rejectButton.setEnabled(true);
        }
    }

    private void clearTable(JTable table)
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }

    private void loadAllToTable()
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        var objects = adminOperation.getAllMedicalRequests();
        for (var object : objects)
        {
            MedicalRecord medicalRecord = (MedicalRecord) adminOperation.convertToBusinessEntity(object.getMedicalRecord());
            Appointment appointment = (Appointment) adminOperation.convertToBusinessEntity(medicalRecord.getAppointment());
            var assessmentType = adminOperation.convertToBusinessEntity(object.getAssessmentType());
            Object[] row = new Object[8];
            row[0] = object.getId();
            row[1] = appointment.getPatient().getName();
            row[2] = appointment.getDoctor().getName();
            row[3] = assessmentType.getSelf().getCategory();
            row[4] = object.getSelf().getRequestTime();
            row[5] = object.getSelf().getStatus();
            row[6] = object.getAssessmentResults().size();
            row[7] = object;
            model.addRow(row);
        }

    }

    private void reload(ActionEvent e)
    {
        clearTable(table);
        loadAllToTable();
    }

    private void reject(ActionEvent e)
    {
        MedicalRequest medicalRequest = getObjectFromCurrentSelectedRow(table, 7);
        if (medicalRequest == null) return;
        medicalRequest.getSelf().setStatus(MedicalRequestToFile.RequestStatus.REJECTED);
        AdminOperation.CRUDInformation crudInformation = adminOperation.update(medicalRequest);
        if (crudInformation.isSuccess()) JOptionPane.showMessageDialog(frameWindow,"Successfully rejected the request");
        else JOptionPane.showMessageDialog(frameWindow,"Failed to reject the request\n" + crudInformation.message());
        reload(null);
    }

    private void approve(ActionEvent e)
    {
        MedicalRequest medicalRequest = getObjectFromCurrentSelectedRow(table, 7);
        AssessmentResultDialog assessmentResultDialog = new AssessmentResultDialog(frameWindow,adminOperation,medicalRequest);
        assessmentResultDialog.setVisible(true);
        reload(null);
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        allUserScrollPanel = new JScrollPane();
        table = new JTable();
        reloadUserButton = new JButton();
        approveButton = new JButton();
        rejectButton = new JButton();

        //======== this ========

        //======== allUserScrollPanel ========
        {

            //---- table ----
            table.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, "", null, null, null, null},
                },
                new String[] {
                    "Id", "Patient Name", "Doctor Name", "Assessment Type", "Requested At", "Status", "Result Count", "object"
                }
            ));
            allUserScrollPanel.setViewportView(table);
        }

        //---- reloadUserButton ----
        reloadUserButton.setText("Reload");
        reloadUserButton.addActionListener(e -> reload(e));

        //---- approveButton ----
        approveButton.setText("Approve");
        approveButton.addActionListener(e -> approve(e));

        //---- rejectButton ----
        rejectButton.setText("Reject");
        rejectButton.addActionListener(e -> reject(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                    .addGap(30, 30, 30)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(reloadUserButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                        .addComponent(approveButton)
                        .addComponent(rejectButton))
                    .addContainerGap(84, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(52, 52, 52)
                            .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 425, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(130, 130, 130)
                            .addComponent(reloadUserButton)
                            .addGap(18, 18, 18)
                            .addComponent(approveButton)
                            .addGap(18, 18, 18)
                            .addComponent(rejectButton)))
                    .addContainerGap(83, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane allUserScrollPanel;
    private JTable table;
    private JButton reloadUserButton;
    private JButton approveButton;
    private JButton rejectButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
