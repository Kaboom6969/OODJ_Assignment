/*
 * Created by JFormDesigner on Sat Sep 26 05:59:04 GMT+08:00 2026
 */

package Forms.AdminForm.CRUDPanel;

import Forms.AdminForm.CRUDDialog.AssessmentTypeDialog;
import Interfaces.RefreshablePanel;
import Operations.AdminOperation.AdminOperation;
import entities.BusinessEntity.AssessmentType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;

/**
 * @author leezh
 */
public class AssessmentTypePanel extends JPanel implements RefreshablePanel
{
    private final AdminOperation adminOperation;
    private final Window frameWindow;
    @Override
    public void refreshData()
    {
        reload(null);
    }

    public AssessmentTypePanel(Window frameWindow, AdminOperation adminOperation)
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
        table.removeColumn(table.getColumn("Object"));
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
        var objects = adminOperation.getAllAssessmentTypes();
        for (var object : objects)
        {
            Object[] row = new Object[5];
            row[0] = object.getId();
            row[1] = object.getSelf().getName();
            row[2] = object.getSelf().getCategory();
            row[3] = object.getSelf().getPrice();
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
        AssessmentTypeDialog dialog = new AssessmentTypeDialog(frameWindow, adminOperation);
        dialog.setVisible(true);
        reload(null);
    }

    private void update(ActionEvent e)
    {
        AssessmentType assessmentType = getObjectFromCurrentSelectedRow(table, 4);
        if (assessmentType == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a assessment type");
            return;
        }
        AssessmentTypeDialog dialog = new AssessmentTypeDialog(frameWindow, adminOperation, assessmentType);
        dialog.setVisible(true);
        reload(null);
    }

    private void delete(ActionEvent e)
    {
        AssessmentType assessmentType = getObjectFromCurrentSelectedRow(table, 4);
        if (assessmentType == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a assessment type");
            return;
        }
        if (!assessmentType.getAssessmentResults().isEmpty() || !assessmentType.getMedicalRequests().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "This assessment type is link to another entity,cannot delete it");
            return;
        }
        AdminOperation.CRUDInformation crudInformation = adminOperation.delete(assessmentType);
        if (crudInformation.isSuccess())
        {
            reload(null);
            JOptionPane.showMessageDialog(this, "assessment type has been deleted");
        } else
        {
            JOptionPane.showMessageDialog(this, crudInformation.message());
        }
    }

    private void initComponents() {
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
                    "Id", "Name", "Category", "Price", "Object"
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
                    .addGap(25, 25, 25)
                    .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 709, GroupLayout.PREFERRED_SIZE)
                    .addGap(46, 46, 46)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(addButton)
                        .addGroup(layout.createParallelGroup()
                            .addComponent(deleteButton)
                            .addComponent(updateButton))
                        .addComponent(reloadButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(allUserScrollPanel, GroupLayout.PREFERRED_SIZE, 425, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(59, 59, 59)
                            .addComponent(reloadButton)
                            .addGap(18, 18, 18)
                            .addComponent(addButton)
                            .addGap(18, 18, 18)
                            .addComponent(updateButton)
                            .addGap(26, 26, 26)
                            .addComponent(deleteButton)))
                    .addContainerGap(33, Short.MAX_VALUE))
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
