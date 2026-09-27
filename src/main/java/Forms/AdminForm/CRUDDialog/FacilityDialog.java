/*
 * Created by JFormDesigner on Thu Sep 24 21:11:38 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDDialog;

import Forms.ComboBoxItem;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.Insurance;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author leezh
 */
public class FacilityDialog extends JDialog {
    private final AdminOperation adminOperation;
    public enum Modes
    {
        ADD,MODIFY
    }
    private Modes mode;
    private String id = null;
    public FacilityDialog(Window owner, AdminOperation adminOperation)
    {
        super(owner,"Add Facility", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        isAvailableComboBoxInit();
        actionButton.setText("Add");
        facilityTypeComboBox.setModel(new DefaultComboBoxModel(FacilityToFile.FacilityType.values()));
        mode = Modes.ADD;
        this.adminOperation = adminOperation;
    }
    public FacilityDialog(Window owner, AdminOperation adminOperation, Facility facility)
    {
        super(owner, "Modify Facility", Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        isAvailableComboBoxInit();
        facilityTypeComboBox.setModel(new DefaultComboBoxModel(FacilityToFile.FacilityType.values()));
        this.id = facility.getId();
        actionButton.setText("Modify");
        updateAllField(facility);
        mode = Modes.MODIFY;
        this.adminOperation = adminOperation;
    }
    private void updateAllField(Facility facility)
    {
        nameTextField.setText(facility.getSelf().getName());
        capacityTextField.setText(String.valueOf(facility.getSelf().getCapacity()));
        facilityTypeComboBox.setSelectedItem(facility.getSelf().getFacilityType());
        availableComboBox.setSelectedIndex(facility.getSelf().isAvailable()? 0 : 1);
    }
    private void isAvailableComboBoxInit()
    {
        availableComboBox.addItem(new ComboBoxItem<Boolean>(true,"Yes"));
        availableComboBox.addItem(new ComboBoxItem<Boolean>(false,"No"));
    }
    private java.util.List<String> packData(String id)
    {
        List<String> data = new ArrayList<>();
        data.add(id);
        data.add(nameTextField.getText());
        data.add(facilityTypeComboBox.getSelectedItem().toString());
        data.add(capacityTextField.getText());
        data.add(String.valueOf(((ComboBoxItem<?>)availableComboBox.getSelectedItem()).getItem()));
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
                            adminOperation.add(packData(null), FacilityToFile.class);
                } catch (IllegalArgumentException iae)
                {
                    crudInformation = new AdminOperation.CRUDInformation(false, iae.getMessage());
                }
                break;
            case MODIFY:
                try
                {
                    Facility facility = adminOperation.constructFull(packData(id),FacilityToFile.class);
                    crudInformation = adminOperation.update(facility);
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
        facilityTypeLabel = new JLabel();
        availableLabel = new JLabel();
        nameTextField = new JTextField();
        actionButton = new JButton();
        availableComboBox = new JComboBox();
        facilityTypeComboBox = new JComboBox();
        capacityLabel = new JLabel();
        capacityTextField = new JTextField();

        //======== this ========
        var contentPane = getContentPane();

        //---- nameLabel ----
        nameLabel.setText("Name:");

        //---- facilityTypeLabel ----
        facilityTypeLabel.setText("Facility Type:");

        //---- availableLabel ----
        availableLabel.setText("Is Available:");

        //---- actionButton ----
        actionButton.setText("Action");
        actionButton.addActionListener(e -> action(e));

        //---- capacityLabel ----
        capacityLabel.setText("Capacity:");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(nameLabel)
                                .addComponent(facilityTypeLabel))
                            .addGap(18, 18, 18)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                .addComponent(facilityTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(137, 137, 137)
                            .addComponent(actionButton))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(availableLabel)
                                .addComponent(capacityLabel))
                            .addGap(18, 18, 18)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(availableComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(97, 97, 97))
                                .addComponent(capacityTextField))))
                    .addContainerGap(99, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(51, 51, 51)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(nameLabel))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(facilityTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(facilityTypeLabel))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(capacityTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(capacityLabel))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(availableLabel)
                        .addComponent(availableComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(actionButton)
                    .addGap(28, 28, 28))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel nameLabel;
    private JLabel facilityTypeLabel;
    private JLabel availableLabel;
    private JTextField nameTextField;
    private JButton actionButton;
    private JComboBox availableComboBox;
    private JComboBox facilityTypeComboBox;
    private JLabel capacityLabel;
    private JTextField capacityTextField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
