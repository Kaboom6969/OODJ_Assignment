/*
 * Created by JFormDesigner on Sat Sep 26 18:36:56 GMT+08:00 2026
 */

package Forms.AdminForm.LinkPanel;

import Interfaces.RefreshablePanel;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.InsuranceToFile;
import entities.BusinessEntity.Insurance;
import entities.BusinessEntity.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;
import static Forms.AdminForm.FrameHelper.getObjectFromRow;

/**
 * @author leezh
 */
public class AllocateInsuranceToPatientPanel extends JPanel implements RefreshablePanel
{
    private final AdminOperation adminOperation;

    @Override
    public void refreshData()
    {
        reloadTable();
    }

    public AllocateInsuranceToPatientPanel(AdminOperation adminOperation)
    {
        this.adminOperation = adminOperation;
        initComponents();
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        insuranceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setDefaultEditor(Object.class, null);
        insuranceTable.setDefaultEditor(Object.class, null);

        piGroup = new JButton[]{linkButton, unLinkButton};
        colorMapInit();
        banMapInit();
        allocatePanelInit();
    }

    private enum RelationType
    {
        NONE,
        PATIENT_INSURANCE,
        INSURANCE_PATIENT,
    }

    private enum CurrentTable
    {
        NONE,
        PATIENT,
        INSURANCE,
    }

    private enum CurrentState
    {
        NONE,
        LINKING,
        UNLINKING,
    }

    private enum TargetTable
    {
        NONE,
        PATIENT,
        INSURANCE,
    }

    private boolean isTargetTableSelected()
    {
        return switch (getCurrentRelationType())
        {
            case NONE -> false;
            case PATIENT_INSURANCE -> insuranceTable.getSelectedRow() != -1;

            case INSURANCE_PATIENT -> patientTable.getSelectedRow() != -1;

        };
    }

    private record ColorStatus(Integer rowShouldBeColored, Color color){}

    private CurrentState currentState = CurrentState.NONE;
    private CurrentTable currentTable = CurrentTable.NONE;
    private TargetTable targetTable = TargetTable.NONE;


    private final HashMap<JTable, HashSet<ColorStatus>> colorMap = new HashMap<>();
    private final HashMap<JTable, HashSet<Integer>> banMap = new HashMap<>();
    private final Color HIGHLIGHT_COLOR = Color.YELLOW;
    private final Color BAN_COLOR = Color.GRAY;
    private final JButton[] piGroup;
    private int lastSelectedPatient = -1;
    private int lastSelectedInsurance = -1;
    private boolean recursion_defenser = false;

    private void setCurrentTable(CurrentTable currentTable)
    {
        switch (currentState)
        {
            case LINKING, UNLINKING ->
            {
                throw new IllegalStateException("While LINKING or UNLINKING,cannot change currentTable");
            }
        }
        this.currentTable = currentTable;
        switch (currentTable)
        {
            case PATIENT ->
            {
                insuranceTable.clearSelection();
                setTargetTable(TargetTable.INSURANCE);
            }
            case INSURANCE ->
            {
                patientTable.clearSelection();
                setTargetTable(TargetTable.PATIENT);
            }
            case NONE ->
            {
                setTargetTable(TargetTable.NONE);
            }
        }
    }

    private void setTargetTable(TargetTable targetTable)
    {
        switch (targetTable)
        {
            case INSURANCE ->
            {
                if (currentTable == CurrentTable.INSURANCE)
                    throw new IllegalStateException("TargetTable is same as CurrentTable");
            }
            case PATIENT ->
            {
                if (currentTable == CurrentTable.PATIENT)
                    throw new IllegalStateException("TargetTable is same as CurrentTable");
            }
        }
        this.targetTable = targetTable;
    }

    private void refreshAllButton()
    {
        disableAllButtons();
        switch (getCurrentRelationType())
        {
            case NONE ->
            {
                turnToLinkUnLink(piGroup);
            }
            case PATIENT_INSURANCE ->
            {
                if (currentState == CurrentState.NONE)
                {
                    turnToLinkUnLink(piGroup);
                    Patient patient = getObjectFromCurrentSelectedRow(patientTable, 2);
                    if (patient.getInsurance() == null) linkButton.setEnabled(true);
                    else unLinkButton.setEnabled(true);
                } else
                {
                    turnToConfirmCancel(piGroup);
                    unLinkButton.setEnabled(true);
                    if (isTargetTableSelected()) linkButton.setEnabled(true);
                }
            }
            case INSURANCE_PATIENT ->
            {
                if (currentState == CurrentState.NONE)
                {
                    turnToLinkUnLink(piGroup);
                    linkButton.setEnabled(true);
                    Insurance insurance = getObjectFromCurrentSelectedRow(insuranceTable, 2);
                    unLinkButton.setEnabled(!insurance.getPatients().isEmpty());
                } else
                {
                    turnToConfirmCancel(piGroup);
                    unLinkButton.setEnabled(true);
                    if (isTargetTableSelected()) linkButton.setEnabled(true);
                }
            }

        }
    }

    private void resetAllStatus()
    {
        try
        {
            recursion_defenser = true;
            setCurrentState(CurrentState.NONE);
            setCurrentTable(CurrentTable.NONE);
            insuranceTable.clearSelection();
            patientTable.clearSelection();
        } finally
        {
            recursion_defenser = false;
        }
    }

    private void setCurrentState(CurrentState currentState)
    {
        switch (currentState)
        {
            case LINKING, UNLINKING ->
            {
                if (currentTable == CurrentTable.NONE)
                    throw new IllegalStateException("Current state cannot be none");
            }
        }
        this.currentState = currentState;
    }

    private RelationType getCurrentRelationType()
    {
        switch (currentTable)
        {
            case NONE:
                return RelationType.NONE;
            case PATIENT:
                return RelationType.PATIENT_INSURANCE;
            case INSURANCE:
                return  RelationType.INSURANCE_PATIENT;
        }
        return RelationType.NONE;
    }


    private void disableAllButtons()
    {
        linkButton.setEnabled(false);
        unLinkButton.setEnabled(false);
    }

    private void turnToConfirmCancel(JButton[] buttonGroup)
    {
        buttonGroup[0].setText("Confirm");
        buttonGroup[1].setText("Cancel");
    }

    private void turnToLinkUnLink(JButton[] buttonGroup)
    {
        buttonGroup[0].setText("Link");
        buttonGroup[1].setText("Unlink");
    }

    private void colorMapInit()
    {
        colorMap.put(patientTable, new HashSet<>());
        colorMap.put(insuranceTable, new HashSet<>());
    }

    private void banMapInit()
    {
        banMap.put(patientTable, new HashSet<>());
        banMap.put(insuranceTable, new HashSet<>());
    }

    private void refreshPanelState()
    {
        refreshAllButton();
        refreshHighlight();
        refreshBan();
    }

    private void allocatePanelInit()
    {
        patientTable.removeColumn(patientTable.getColumn("patientObject"));
        insuranceTable.removeColumn(insuranceTable.getColumn("insuranceObject"));
        reloadTable();
        patientTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting() || recursion_defenser) return;
            if (rejectBannedStatus(patientTable, lastSelectedPatient))
            {
                return;
            }
            functionForSelectListInTableOfADTMPanel();
            refreshPanelState();
            changeLastSelected();
        });
        insuranceTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting() || recursion_defenser) return;
            if (rejectBannedStatus(insuranceTable, lastSelectedInsurance))
            {
                return;
            }
            functionForSelectListInTableOfADTMPanel();
            refreshPanelState();
            changeLastSelected();
        });

    }

    private void reloadTable()
    {
        try
        {
            recursion_defenser = true;
            resetAllStatus();
            clearTable(patientTable);
            clearTable(insuranceTable);
            loadDataToTableInsuranceVer();
            loadDataToTablePatientVer();
            refreshPanelState();
        } finally
        {
            recursion_defenser = false;
        }
    }

    private void loadDataToTableInsuranceVer()
    {
        DefaultTableModel model = (DefaultTableModel) insuranceTable.getModel();
        for (var insurance : adminOperation.getAllInsurances())
        {
            Object[] row = new Object[3];
            row[0] = insurance.getId();
            row[1] = insurance.getSelf().getCompanyName();
            row[2] = insurance;
            model.addRow(row);
        }
    }

    private void loadDataToTablePatientVer()
    {
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        for (var patient : adminOperation.getAllPatients())
        {
            Object[] row = new Object[3];
            row[0] = patient.getId();
            row[1] = patient.getSelf().getName();
            row[2] = patient;
            model.addRow(row);
        }
    }


    private void addColor(JTable table, ColorStatus colorStatus)
    {
        colorMap.get(table).add(colorStatus);
    }

    private void addHighLightColor(JTable table, int row)
    {
        addColor(table, new ColorStatus(row, HIGHLIGHT_COLOR));
    }

    private void addBanned(JTable table, int rowBeBanned)
    {
        addColor(table, new ColorStatus(rowBeBanned, BAN_COLOR));
        banMap.get(table).add(rowBeBanned);
    }

    private void refreshHighlight()
    {
        clearColor(patientTable);
        clearColor(insuranceTable);
        switch (getCurrentRelationType())
        {
            case PATIENT_INSURANCE ->
            {
                piHighLight();
            }

            case INSURANCE_PATIENT ->
            {
                ipHighLight();
            }
        }
        recolor(patientTable);
        recolor(insuranceTable);
    }

    private void refreshBan()
    {
        clearBanned(patientTable);
        clearBanned(insuranceTable);
        if (currentState == CurrentState.NONE) return;
        switch (getCurrentRelationType())
        {
            case PATIENT_INSURANCE ->
            {
                setTableToAllBanExceptSelectedRows(patientTable);
                if (currentState == CurrentState.LINKING) break;
                else convertExceptHighLightToBan(insuranceTable);
            }
            case INSURANCE_PATIENT ->
            {
                setTableToAllBanExceptSelectedRows(insuranceTable);
                if (currentState == CurrentState.LINKING)
                {
                    for (int i = 0; i < patientTable.getModel().getRowCount(); i++)
                    {
                        Patient patient = getObjectFromRow(patientTable, i, 2);
                        if (patient.getInsurance() != null) addBanned(patientTable, i);
                    }
                } else convertExceptHighLightToBan(patientTable);
            }

        }
        clearHighLightColor(insuranceTable);
        clearHighLightColor(patientTable);
        recolor(patientTable);
        recolor(insuranceTable);
    }


    private void piHighLight()
    {
        Patient patient = getObjectFromCurrentSelectedRow(patientTable, 2);
        if (patient == null)
            throw new IllegalStateException("In PATIENT_INSURANCE relation,patient must be selected!");
        InsuranceToFile insurance = patient.getInsurance();
        if (insurance == null) return;
        for (int i = 0; i < insuranceTable.getModel().getRowCount(); i++)
        {
            if (insuranceTable.getModel().getValueAt(i, 0).equals(insurance.getId()))
            {
                addHighLightColor(insuranceTable, i);
            }
        }
    }

    private void ipHighLight()
    {
        Insurance insurance = getObjectFromCurrentSelectedRow(insuranceTable, 2);
        if (insurance == null)
            throw new IllegalStateException("In INSURANCE_PATIENT relation,insurance must be selected!");
        var patient = insurance.getPatients();
        var patientIds = patient.getIds();
        if (patient.isEmpty()) return;
        for (int i = 0; i < patientTable.getRowCount(); i++)
        {
            if (patientIds.contains((String) patientTable.getModel().getValueAt(i, 0)))
            {
                addHighLightColor(patientTable, i);
            }
        }
    }


    private void recolor(JTable table)
    {
        HashSet<ColorStatus> rowInformation = colorMap.get(table);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int column)
            {
                Component cell = super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                int modelRow = t.convertRowIndexToModel(row);
                if (!selected)
                {
                    Color color = t.getBackground();
                    for (ColorStatus colorStatus : rowInformation)
                    {
                        if (colorStatus.rowShouldBeColored == modelRow)
                        {
                            color = colorStatus.color;
                        }
                    }
                    cell.setBackground(color);
                }
                return cell;
            }
        });
        table.repaint();
    }

    private void clearColor(JTable table)
    {
        colorMap.get(table).clear();
        recolor(table);
    }

    private void clearHighLightColor(JTable table)
    {
        colorMap.get(table).removeIf(colorStatus -> colorStatus.color == HIGHLIGHT_COLOR);
        recolor(table);
    }

    private void clearBanned(JTable table)
    {
        banMap.get(table).clear();
        colorMap.get(table).removeIf(colorStatus -> colorStatus.color == BAN_COLOR);
        recolor(table);
    }

    private void changeLastSelected()
    {
        lastSelectedPatient = patientTable.convertRowIndexToModel(patientTable.getSelectedRow());
        lastSelectedInsurance = insuranceTable.convertRowIndexToModel(insuranceTable.getSelectedRow());
    }

    private void functionForSelectListInTableOfADTMPanel()
    {
        try
        {
            recursion_defenser = true;
            Patient patient = getObjectFromCurrentSelectedRow(patientTable, 2);
            Insurance insurance = getObjectFromCurrentSelectedRow(insuranceTable, 2);
            if (currentState == CurrentState.LINKING || currentState == CurrentState.UNLINKING)
            {
                return;
            }
            switch (currentTable)
            {
                case NONE, INSURANCE ->
                {
                    if (patient != null)
                        setCurrentTable(CurrentTable.PATIENT);
                    else if (insurance != null)
                        setCurrentTable(CurrentTable.INSURANCE);
                    else setCurrentTable(CurrentTable.NONE);
                }
                case PATIENT ->
                {
                    if (insurance != null)
                        setCurrentTable(CurrentTable.INSURANCE);
                    else if (patient != null)
                        setCurrentTable(CurrentTable.PATIENT);
                    else setCurrentTable(CurrentTable.NONE);
                }

            }
        } finally
        {
            recursion_defenser = false;
        }
    }

    private java.util.List<Integer> getHighLightRows(JTable table)
    {
        java.util.List<Integer> highLightRows = new ArrayList<>();
        for (ColorStatus colorStatus : colorMap.get(table))
        {
            if (colorStatus.color == HIGHLIGHT_COLOR) highLightRows.add(colorStatus.rowShouldBeColored());
        }
        return highLightRows;
    }

    private void clearTable(JTable table)
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }


    private void setTableToAllBanExceptSelectedRows(JTable table)
    {
        int selectedRow = table.getSelectedRow();
        selectedRow = table.convertRowIndexToModel(selectedRow);
        if (selectedRow == -1) throw new IllegalStateException("Source table must have a selected row before banning");
        clearBanned(table);
        clearColor(table);
        for (int i = 0; i < table.getModel().getRowCount(); i++)
        {
            if (i == selectedRow) continue;
            addBanned(table, i);
        }
        recolor(table);
    }

    private void setTableRowToBan(JTable table, java.util.List<Integer> bannedRow)
    {
        clearBanned(table);
        clearColor(table);
        for (int row : bannedRow)
        {
            addBanned(table, row);
        }
        recolor(table);
    }

    private void convertHighLightToBan(JTable table)
    {
        clearBanned(table);
        java.util.List<Integer> highLightRows = getHighLightRows(table);
        for (int row : highLightRows)
        {
            addBanned(table, row);
        }
    }

    private void convertExceptHighLightToBan(JTable table)
    {
        clearBanned(table);
        List<Integer> highLightRows = getHighLightRows(table);
        for (int i = 0; i < table.getModel().getRowCount(); i++)
        {
            if (highLightRows.contains(i)) continue;
            addBanned(table, i);
        }
    }

    private boolean rejectBannedStatus(JTable table, int lastSelectedModelRow)
    {
        int selectedRow = table.getSelectedRow();
        selectedRow = table.convertRowIndexToModel(selectedRow);
        if (selectedRow == -1)
        {
            return false;
        }
        if (!banMap.get(table).contains(selectedRow))
        {
            return false;
        }
        try
        {
            recursion_defenser = true;
            if (lastSelectedModelRow == -1)
            {
                table.clearSelection();
                return true;
            }
            if (banMap.get(table).contains(lastSelectedModelRow))
            {
                table.clearSelection();
                return true;
            }
            int previousViewRow = table.convertRowIndexToView(lastSelectedModelRow);
            table.setRowSelectionInterval(previousViewRow, previousViewRow);
            return true;
        } finally
        {
            recursion_defenser = false;
        }

    }

    private void link(ActionEvent e)
    {
        switch (currentState)
        {
            case NONE ->
            {
                if (currentTable == CurrentTable.INSURANCE)
                {
                    setTargetTable(TargetTable.PATIENT);
                }
                setCurrentState(CurrentState.LINKING);
            }
            case LINKING ->
            {
                AdminOperation.CRUDInformation crudInformation =
                        adminOperation.allocateInsuranceToPatient
                        (
                            getObjectFromCurrentSelectedRow(insuranceTable, 2),
                            getObjectFromCurrentSelectedRow(patientTable, 2)
                        );
                if (crudInformation.isSuccess())
                {
                    JOptionPane.showMessageDialog(this, "Successfully allocated Insurance to patient");
                    reloadTable();
                    return;
                } else
                    JOptionPane.showMessageDialog(this, "Failed to allocate Insurance to patient\n" + crudInformation.message());
                resetAllStatus();
            }
            case UNLINKING ->
            {
                AdminOperation.CRUDInformation crudInformation =
                        adminOperation.unallocatedInsuranceToPatient
                        (
                                getObjectFromCurrentSelectedRow(insuranceTable, 2),
                                getObjectFromCurrentSelectedRow(patientTable, 2)
                        );
                if (crudInformation.isSuccess())
                {
                    JOptionPane.showMessageDialog(this, "Successfully unallocated Insurance to patient");
                    reloadTable();
                    return;
                } else
                    JOptionPane.showMessageDialog(this, "Failed to unallocated Insurance\n" + crudInformation.message());
                resetAllStatus();
            }
        }
        refreshPanelState();
    }

    private void unLink(ActionEvent e)
    {
        switch (currentState)
        {
            case NONE ->
            {
                if (currentTable == CurrentTable.INSURANCE)
                {
                    setTargetTable(TargetTable.PATIENT);
                }
                setCurrentState(CurrentState.UNLINKING);
            }
            case LINKING, UNLINKING -> resetAllStatus();
        }
        refreshPanelState();
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane scrollPane1;
    private JTable patientTable;
    private JScrollPane scrollPane2;
    private JTable insuranceTable;
    private JButton linkButton;
    private JButton unLinkButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        scrollPane1 = new JScrollPane();
        patientTable = new JTable();
        scrollPane2 = new JScrollPane();
        insuranceTable = new JTable();
        linkButton = new JButton();
        unLinkButton = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- patientTable ----
            patientTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "patientObject"
                }
            ));
            scrollPane1.setViewportView(patientTable);
        }

        //======== scrollPane2 ========
        {

            //---- insuranceTable ----
            insuranceTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "insuranceObject"
                }
            ));
            scrollPane2.setViewportView(insuranceTable);
        }

        //---- linkButton ----
        linkButton.setText("Link");
        linkButton.addActionListener(e -> link(e));

        //---- unLinkButton ----
        unLinkButton.setText("Unlink");
        unLinkButton.addActionListener(e -> unLink(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                    .addGap(61, 61, 61)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(linkButton)
                        .addComponent(unLinkButton))
                    .addGap(40, 40, 40)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(46, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(114, 114, 114)
                            .addComponent(linkButton)
                            .addGap(18, 18, 18)
                            .addComponent(unLinkButton))
                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(11, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }
}
