/*
 * Created by JFormDesigner on Thu Sep 24 22:11:25 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDDialog;

import java.awt.event.*;

import Forms.ComboBoxItem;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.ConsultationRateToFile;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BusinessEntity.ConsultationRate;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.Insurance;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author leezh
 */
public class ConsultationRateDialog extends JDialog {
    private final AdminOperation adminOperation;
    public enum Modes
    {
        ADD,MODIFY
    }
    private Modes mode;
    private String id = null;
    public ConsultationRateDialog(Window owner, AdminOperation adminOperation)
    {
        super(owner,"Add Consultation Rate", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        actionButton.setText("Add");
        isActiveComboBoxInit();
        mode = Modes.ADD;
        this.adminOperation = adminOperation;
    }
    public ConsultationRateDialog(Window owner, AdminOperation adminOperation, ConsultationRate consultationRate)
    {
        super(owner, "Modify Consultation Rate", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        this.id = consultationRate.getId();
        actionButton.setText("Modify");
        isActiveComboBoxInit();
        updateAllField(consultationRate);
        mode = Modes.MODIFY;
        this.adminOperation = adminOperation;
    }

    private void updateAllField(ConsultationRate consultationRate)
    {
        nameTextField.setText(consultationRate.getSelf().getName());
        priceTextField.setText(String.valueOf(consultationRate.getSelf().getPrice()));
        isActiveComboBox.setSelectedIndex(consultationRate.getSelf().isActive()? 0 : 1);

    }
    private void isActiveComboBoxInit()
    {
        isActiveComboBox.addItem(new ComboBoxItem<Boolean>(true,"Yes"));
        isActiveComboBox.addItem(new ComboBoxItem<Boolean>(false,"No"));
    }
    private java.util.List<String> packData(String id)
    {
        List<String> data = new ArrayList<>();
        data.add(id);
        data.add(nameTextField.getText());
        data.add(priceTextField.getText());
        data.add(String.valueOf(((ComboBoxItem<?>)isActiveComboBox.getSelectedItem()).getItem()));
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
                            adminOperation.add(packData(null), ConsultationRateToFile.class);
                } catch (IllegalArgumentException iae)
                {
                    crudInformation = new AdminOperation.CRUDInformation(false, iae.getMessage());
                }
                break;
            case MODIFY:
                try
                {
                    ConsultationRate consultationRate = adminOperation.constructFull(packData(id),ConsultationRateToFile.class);
                    crudInformation = adminOperation.update(consultationRate);
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
        isActiveLabel = new JLabel();
        nameTextField = new JTextField();
        priceTextField = new JTextField();
        actionButton = new JButton();
        isActiveComboBox = new JComboBox();

        //======== this ========
        var contentPane = getContentPane();

        //---- nameLabel ----
        nameLabel.setText("Name:");

        //---- priceLabel ----
        priceLabel.setText("Price:");

        //---- isActiveLabel ----
        isActiveLabel.setText("Is Active:");

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
                            .addGap(105, 105, 105)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(isActiveLabel)
                                .addComponent(nameLabel)
                                .addComponent(priceLabel))
                            .addGap(18, 18, 18)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                .addComponent(priceTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                .addComponent(isActiveComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(224, 224, 224)
                            .addComponent(actionButton)))
                    .addContainerGap(218, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(113, 113, 113)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(nameLabel))
                    .addGap(15, 15, 15)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(priceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(priceLabel))
                    .addGap(11, 11, 11)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(isActiveLabel)
                        .addComponent(isActiveComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(actionButton)
                    .addContainerGap(92, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel isActiveLabel;
    private JTextField nameTextField;
    private JTextField priceTextField;
    private JButton actionButton;
    private JComboBox isActiveComboBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
