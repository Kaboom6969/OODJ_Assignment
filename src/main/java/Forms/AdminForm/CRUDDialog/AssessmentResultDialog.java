/*
 * Created by JFormDesigner on Sat Sep 26 16:44:59 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDDialog;

import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.AssessmentResultToFile;
import entities.BaseEntity.MedicalRequestToFile;
import entities.BusinessEntity.AssessmentResult;
import entities.BusinessEntity.MedicalRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leezh
 */
public class AssessmentResultDialog extends JDialog {
    private final AdminOperation adminOperation;
    private final MedicalRequest medicalRequest;

    public AssessmentResultDialog(Window owner, AdminOperation adminOperation, MedicalRequest medicalRequest)
    {
        super(owner,"Fill result", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        actionButton.setText("Fill");
        this.adminOperation = adminOperation;
        this.medicalRequest = medicalRequest;
    }

    private java.util.List<String> packData(String id)
    {
        List<String> data = new ArrayList<>();
        data.add(id);
        data.add(resultTextField.getText());
        data.add(remarkTextField.getText());
        data.add(LocalDateTime.now().toString());
        return data;
    }

    private void action(ActionEvent e)
    {
        AdminOperation.CRUDInformation crudInformation;

        try
        {
            AssessmentResult assessmentResult = adminOperation.constructNewFull
            (
                packData(null),
                AssessmentResultToFile.class
            );
            assessmentResult.setMedicalRecord(medicalRequest.getMedicalRecord());
            assessmentResult.setAssessmentType(medicalRequest.getAssessmentType());
            assessmentResult.setMedicalRequest(medicalRequest.getSelf());

            crudInformation = adminOperation.update(assessmentResult);
            if (crudInformation.isSuccess())
            {
                medicalRequest.getAssessmentResults().add(assessmentResult.getSelf());
                medicalRequest.getSelf().setStatus(MedicalRequestToFile.RequestStatus.COMPLETED);
                crudInformation = adminOperation.update(medicalRequest);
            }
        } catch (RuntimeException re)
        {
            crudInformation = new AdminOperation.CRUDInformation(false, re.getMessage());
        }

        int icon = JOptionPane.INFORMATION_MESSAGE;
        if (!crudInformation.isSuccess()) icon = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(this, crudInformation.message(),"",icon);
        if (crudInformation.isSuccess()) dispose();
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        resultLabel = new JLabel();
        remarkLabel = new JLabel();
        resultTextField = new JTextField();
        remarkTextField = new JTextField();
        actionButton = new JButton();

        //======== this ========
        var contentPane = getContentPane();

        //---- resultLabel ----
        resultLabel.setText("Result:");

        //---- remarkLabel ----
        remarkLabel.setText("Remark:");

        //---- actionButton ----
        actionButton.setText("Action");
        actionButton.addActionListener(e -> action(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(175, 175, 175)
                            .addComponent(actionButton))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(73, 73, 73)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(remarkLabel)
                                .addComponent(resultLabel))
                            .addGap(18, 18, 18)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(resultTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                .addComponent(remarkTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(72, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(46, 46, 46)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addComponent(resultLabel))
                        .addComponent(resultTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(17, 17, 17)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(remarkLabel)
                        .addComponent(remarkTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(55, 55, 55)
                    .addComponent(actionButton)
                    .addContainerGap(49, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel resultLabel;
    private JLabel remarkLabel;
    private JTextField resultTextField;
    private JTextField remarkTextField;
    private JButton actionButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
