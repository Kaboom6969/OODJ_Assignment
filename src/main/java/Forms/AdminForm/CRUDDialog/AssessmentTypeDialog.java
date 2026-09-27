/*
 * Created by JFormDesigner on Sat Sep 26 06:03:32 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDDialog;

import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.AssessmentTypeToFile;
import entities.BusinessEntity.AssessmentType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leezh
 */
public class AssessmentTypeDialog extends JDialog {
    private final AdminOperation adminOperation;
    public enum Modes
    {
        ADD,MODIFY
    }
    private final Modes mode;
    private String id = null;
    public AssessmentTypeDialog(Window owner, AdminOperation adminOperation)
    {
        super(owner,"Add Assessment Type", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        actionButton.setText("Add");
        assessmentTypeComboBoxInit();
        mode = Modes.ADD;
        this.adminOperation = adminOperation;
    }
    public AssessmentTypeDialog(Window owner, AdminOperation adminOperation, AssessmentType assessmentType)
    {
        super(owner, "Modify Assessment Type", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        this.id = assessmentType.getId();
        actionButton.setText("Modify");
        assessmentTypeComboBoxInit();
        updateAllField(assessmentType);
        mode = Modes.MODIFY;
        this.adminOperation = adminOperation;
    }

    private void updateAllField(AssessmentType assessmentType)
    {
        nameTextField.setText(assessmentType.getSelf().getName());
        priceTextField.setText(String.valueOf(assessmentType.getSelf().getPrice()));
        categoryComboBox.setSelectedItem(assessmentType.getSelf().getCategory());

    }
    private void assessmentTypeComboBoxInit()
    {
        categoryComboBox.setModel(new DefaultComboBoxModel<>(AssessmentTypeToFile.AssessmentCategory.values()));
    }
    private java.util.List<String> packData(String id)
    {
        List<String> data = new ArrayList<>();
        data.add(id);
        data.add(nameTextField.getText());
        data.add(String.valueOf(categoryComboBox.getSelectedItem()));
        data.add(priceTextField.getText());
        return data;
    }

    private void action(ActionEvent e)
    {
        AdminOperation.CRUDInformation crudInformation = null;
        switch (mode)
        {
            case ADD:
                try
                {
                    crudInformation =
                            adminOperation.add(packData(null), AssessmentTypeToFile.class);
                } catch (IllegalArgumentException iae)
                {
                    crudInformation = new AdminOperation.CRUDInformation(false, iae.getMessage());
                }
                break;
            case MODIFY:
                try
                {
                    AssessmentType assessmentType = adminOperation.constructFull(packData(id),AssessmentTypeToFile.class);
                    crudInformation = adminOperation.update(assessmentType);
                } catch (RuntimeException re)
                {
                    crudInformation = new AdminOperation.CRUDInformation(false, re.getMessage());
                }
        }
        int icon = JOptionPane.INFORMATION_MESSAGE;
        if (!crudInformation.isSuccess()) icon = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(this, crudInformation.message(),"",icon);
        if (crudInformation.isSuccess()) dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        nameLabel = new JLabel();
        priceLabel = new JLabel();
        categoryLabel = new JLabel();
        nameTextField = new JTextField();
        priceTextField = new JTextField();
        actionButton = new JButton();
        categoryComboBox = new JComboBox();

        //======== this ========
        var contentPane = getContentPane();

        //---- nameLabel ----
        nameLabel.setText("Name:");

        //---- priceLabel ----
        priceLabel.setText("Price:");

        //---- categoryLabel ----
        categoryLabel.setText("Category:");

        //---- actionButton ----
        actionButton.setText("Done");
        actionButton.addActionListener(e -> action(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(80, 80, 80)
                            .addComponent(priceLabel)
                            .addGap(18, 18, 18)
                            .addComponent(priceTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(75, 75, 75)
                                    .addComponent(nameLabel)
                                    .addGap(18, 18, 18))
                                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(categoryLabel)
                                    .addGap(18, 18, 18)))
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                .addComponent(categoryComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(177, 177, 177)
                            .addComponent(actionButton)))
                    .addContainerGap(180, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(73, 73, 73)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addComponent(nameLabel))
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(categoryComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(categoryLabel))
                    .addGap(9, 9, 9)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(priceLabel)
                        .addComponent(priceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(11, 11, 11)
                    .addComponent(actionButton)
                    .addContainerGap(87, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel categoryLabel;
    private JTextField nameTextField;
    private JTextField priceTextField;
    private JButton actionButton;
    private JComboBox categoryComboBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
