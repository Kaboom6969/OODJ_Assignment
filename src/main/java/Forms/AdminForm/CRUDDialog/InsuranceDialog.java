/*
 * Created by JFormDesigner on Thu Sep 24 19:20:35 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDDialog;

import Forms.ComboBoxItem;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.Users.AdminToFile;
import entities.BaseEntity.Users.User;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Insurance;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author leezh
 */
public class InsuranceDialog extends JDialog {
    private final AdminOperation adminOperation;
    public enum Modes
    {
        ADD,MODIFY
    }
    private Modes mode;
    private String id = null;
    public InsuranceDialog(Window owner,AdminOperation adminOperation)
    {
        super(owner,"Add Insurance", ModalityType.APPLICATION_MODAL);
        initComponents();
        isAcceptedComboBoxInit();
        actionButton.setText("Add");
        mode = Modes.ADD;
        this.adminOperation = adminOperation;
    }
    public InsuranceDialog(Window owner,AdminOperation adminOperation, Insurance insurance)
    {
        super(owner, "Modify Insurance", ModalityType.APPLICATION_MODAL);
        initComponents();
        isAcceptedComboBoxInit();
        this.id = insurance.getId();
        actionButton.setText("Modify");
        updateAllField(insurance);
        mode = Modes.MODIFY;
        this.adminOperation = adminOperation;
    }
    private void updateAllField(Insurance insurance)
    {
        companyNameTextField.setText(insurance.getSelf().getCompanyName());
        coveragePercentagesTextField.setText(String.valueOf(insurance.getSelf().getCoveragePercentage()));
        isAcceptedComboBox.setSelectedIndex(insurance.getSelf().isAccepted()? 0 : 1);

    }
    private void isAcceptedComboBoxInit()
    {
        isAcceptedComboBox.addItem(new ComboBoxItem<Boolean>(true,"Yes"));
        isAcceptedComboBox.addItem(new ComboBoxItem<Boolean>(false,"No"));
    }
    private java.util.List<String> packData(String id)
    {
        List<String> data = new ArrayList<>();
        data.add(id);
        data.add(companyNameTextField.getText());
        data.add(coveragePercentagesTextField.getText());
        data.add(String.valueOf(((ComboBoxItem<?>)isAcceptedComboBox.getSelectedItem()).getItem()));
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
                            adminOperation.add(packData(null), InsuranceToFile.class);
                } catch (IllegalArgumentException iae)
                {
                    crudInformation = new AdminOperation.CRUDInformation(false, iae.getMessage());
                }
                break;
            case MODIFY:
                try
                {
                    Insurance insurance = adminOperation.constructFull(packData(id),InsuranceToFile.class);
                    crudInformation = adminOperation.update(insurance);
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
        companyNameLabel = new JLabel();
        coveragePercentagesLabel = new JLabel();
        isAcceptedLabel = new JLabel();
        companyNameTextField = new JTextField();
        coveragePercentagesTextField = new JTextField();
        actionButton = new JButton();
        isAcceptedComboBox = new JComboBox();

        //======== this ========
        var contentPane = getContentPane();

        //---- companyNameLabel ----
        companyNameLabel.setText("Company Name:");

        //---- coveragePercentagesLabel ----
        coveragePercentagesLabel.setText("Coverage Percentages:");

        //---- isAcceptedLabel ----
        isAcceptedLabel.setText("Is Accepted:");

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
                            .addGap(37, 37, 37)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(isAcceptedLabel)
                                .addComponent(coveragePercentagesLabel)
                                .addComponent(companyNameLabel))
                            .addGap(18, 18, 18)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(companyNameTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                .addComponent(coveragePercentagesTextField, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                .addComponent(isAcceptedComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(163, 163, 163)
                            .addComponent(actionButton)))
                    .addContainerGap(56, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(61, 61, 61)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(companyNameLabel)
                        .addComponent(companyNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(coveragePercentagesLabel)
                        .addComponent(coveragePercentagesTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(12, 12, 12)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(isAcceptedLabel)
                        .addComponent(isAcceptedComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(actionButton)
                    .addContainerGap(51, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel companyNameLabel;
    private JLabel coveragePercentagesLabel;
    private JLabel isAcceptedLabel;
    private JTextField companyNameTextField;
    private JTextField coveragePercentagesTextField;
    private JButton actionButton;
    private JComboBox isAcceptedComboBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
