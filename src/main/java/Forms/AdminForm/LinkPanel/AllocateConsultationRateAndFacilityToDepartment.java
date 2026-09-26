/*
 * Created by JFormDesigner on Thu Sep 24 22:55:16 GMT+08:00 2026
 */

package Forms.AdminForm.LinkPanel;

import Interfaces.RefreshablePanel;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.DepartmentToFile;
import entities.BusinessEntity.ConsultationRate;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Facility;

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
public class AllocateConsultationRateAndFacilityToDepartment extends JPanel implements RefreshablePanel
{
    private final AdminOperation adminOperation;
    @Override
    public void refreshData()
    {
        reloadTable();
    }

    public AllocateConsultationRateAndFacilityToDepartment(AdminOperation adminOperation)
    {
        this.adminOperation = adminOperation;
        initComponents();
        consultationRateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        facilityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consultationRateTable.setDefaultEditor(Object.class, null);
        departmentTable.setDefaultEditor(Object.class, null);
        facilityTable.setDefaultEditor(Object.class, null);
        cdGroup = new JButton[]{cdLinkButton, cdUnLinkButton};
        fdGroup = new JButton[]{fdLinkButton, fdUnLinkButton};
        colorMapInit();
        banMapInit();
        allocatePanelInit();
    }

    private enum RelationType
    {
        NONE,
        DEPARTMENT_TO_BOTH,
        CONSULTATION_DEPARTMENT,
        FACILITY_DEPARTMENT,
        DEPARTMENT_CONSULTATION,
        DEPARTMENT_FACILITY,
    }

    private enum CurrentTable
    {
        NONE,
        CONSULTATION_RATE,
        DEPARTMENT,
        FACILITY,
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
        CONSULTATION_RATE,
        DEPARTMENT,
        FACILITY,
    }

    private boolean isTargetTableSelected()
    {
        return switch (getCurrentRelationType())
        {
            case NONE -> false;
            case CONSULTATION_DEPARTMENT, FACILITY_DEPARTMENT -> departmentTable.getSelectedRow() != -1;

            case DEPARTMENT_CONSULTATION -> consultationRateTable.getSelectedRow() != -1;

            case DEPARTMENT_FACILITY -> facilityTable.getSelectedRow() != -1;

            case DEPARTMENT_TO_BOTH -> false;
        };
    }

    private record ColorStatus(Integer rowShouldBeColored, Color color)
    {
    }

    private CurrentState currentState = CurrentState.NONE;
    private CurrentTable currentTable = CurrentTable.NONE;
    private TargetTable targetTable = TargetTable.NONE;


    private final HashMap<JTable, HashSet<ColorStatus>> colorMap = new HashMap<>();
    private final HashMap<JTable, HashSet<Integer>> banMap = new HashMap<>();
    private final Color HIGHLIGHT_COLOR = Color.YELLOW;
    private final Color BAN_COLOR = Color.GRAY;
    private final JButton[] cdGroup;
    private final JButton[] fdGroup;
    private int lastSelectedConsultationRate = -1;
    private int lastSelectedDepartment = -1;
    private int lastSelectedFacility = -1;
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
            case CONSULTATION_RATE ->
            {
                facilityTable.clearSelection();
                departmentTable.clearSelection();
                setTargetTable(TargetTable.DEPARTMENT);
            }
            case FACILITY ->
            {
                consultationRateTable.clearSelection();
                departmentTable.clearSelection();
                setTargetTable(TargetTable.DEPARTMENT);
            }
            case DEPARTMENT ->
            {
                facilityTable.clearSelection();
                consultationRateTable.clearSelection();
                setTargetTable(TargetTable.NONE);
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
            case DEPARTMENT ->
            {
                if (currentTable == CurrentTable.DEPARTMENT)
                    throw new IllegalStateException("TargetTable is same as CurrentTable");
            }
            case CONSULTATION_RATE ->
            {
                if (currentTable == CurrentTable.CONSULTATION_RATE)
                    throw new IllegalStateException("TargetTable is same as CurrentTable");
            }
            case FACILITY ->
            {
                if (currentTable == CurrentTable.FACILITY)
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
                turnToLinkUnLink(cdGroup);
                turnToLinkUnLink(fdGroup);
            }
            case CONSULTATION_DEPARTMENT ->
            {
                turnToLinkUnLink(fdGroup);
                if (currentState == CurrentState.NONE)
                {
                    turnToLinkUnLink(cdGroup);
                    ConsultationRate consultationRate = getObjectFromCurrentSelectedRow(consultationRateTable, 2);
                    if (consultationRate.getBelongsToDepartment() == null) cdLinkButton.setEnabled(true);
                    else cdUnLinkButton.setEnabled(true);
                } else
                {
                    turnToConfirmCancel(cdGroup);
                    cdUnLinkButton.setEnabled(true);
                    if (isTargetTableSelected()) cdLinkButton.setEnabled(true);
                }
            }
            case FACILITY_DEPARTMENT ->
            {
                turnToLinkUnLink(cdGroup);
                if (currentState == CurrentState.NONE)
                {
                    turnToLinkUnLink(fdGroup);
                    Facility facility = getObjectFromCurrentSelectedRow(facilityTable, 2);
                    if (facility.getBelongsToDepartment() == null) fdLinkButton.setEnabled(true);
                    else fdUnLinkButton.setEnabled(true);
                } else
                {
                    turnToConfirmCancel(fdGroup);
                    fdUnLinkButton.setEnabled(true);
                    if (isTargetTableSelected()) fdLinkButton.setEnabled(true);
                }
            }
            case DEPARTMENT_TO_BOTH ->
            {
                if (currentState != CurrentState.NONE)
                    throw new IllegalStateException("In DEPARTMENT_TO_BOTH,state only can be none");
                turnToLinkUnLink(cdGroup);
                turnToLinkUnLink(fdGroup);
                cdLinkButton.setEnabled(true);
                fdLinkButton.setEnabled(true);
                Department department = getObjectFromCurrentSelectedRow(departmentTable, 2);
                if (!department.getFacilities().isEmpty()) fdUnLinkButton.setEnabled(true);
                if (!department.getConsultations().isEmpty()) cdUnLinkButton.setEnabled(true);
            }
            case DEPARTMENT_CONSULTATION ->
            {
                if (currentState == CurrentState.NONE)
                    throw new IllegalStateException("In DEPARTMENT_CONSULTATION,state cannot be none");
                turnToConfirmCancel(cdGroup);
                turnToLinkUnLink(fdGroup);
                cdUnLinkButton.setEnabled(true);
                if (isTargetTableSelected()) cdLinkButton.setEnabled(true);
            }
            case DEPARTMENT_FACILITY ->
            {
                if (currentState == CurrentState.NONE)
                    throw new IllegalStateException("In DEPARTMENT_FACILITY,state cannot be none");
                turnToConfirmCancel(fdGroup);
                turnToLinkUnLink(cdGroup);
                fdUnLinkButton.setEnabled(true);
                if (isTargetTableSelected()) fdLinkButton.setEnabled(true);
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
            departmentTable.clearSelection();
            facilityTable.clearSelection();
            consultationRateTable.clearSelection();
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
                if (currentTable == CurrentTable.NONE) throw new IllegalStateException("Current state cannot be none");
                switch (getCurrentRelationType())
                {

                }
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
            case CONSULTATION_RATE:
                return RelationType.CONSULTATION_DEPARTMENT;
            case FACILITY:
                return RelationType.FACILITY_DEPARTMENT;
            case DEPARTMENT:
                switch (targetTable)
                {
                    case NONE:
                        return RelationType.DEPARTMENT_TO_BOTH;
                    case CONSULTATION_RATE:
                        return RelationType.DEPARTMENT_CONSULTATION;
                    case FACILITY:
                        return RelationType.DEPARTMENT_FACILITY;
                    case DEPARTMENT:
                        throw new IllegalStateException("currentTable cannot be same as targetTable");
                }
        }
        return RelationType.NONE;
    }


    private void disableAllButtons()
    {
        cdLinkButton.setEnabled(false);
        cdUnLinkButton.setEnabled(false);
        fdLinkButton.setEnabled(false);
        fdUnLinkButton.setEnabled(false);
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
        colorMap.put(consultationRateTable, new HashSet<>());
        colorMap.put(departmentTable, new HashSet<>());
        colorMap.put(facilityTable, new HashSet<>());
    }

    private void banMapInit()
    {
        banMap.put(consultationRateTable, new HashSet<>());
        banMap.put(departmentTable, new HashSet<>());
        banMap.put(facilityTable, new HashSet<>());
    }

    private void refreshPanelState()
    {
        refreshAllButton();
        refreshHighlight();
        refreshBan();
    }

    private void allocatePanelInit()
    {
        consultationRateTable.removeColumn(consultationRateTable.getColumn("consultationRateObject"));
        departmentTable.removeColumn(departmentTable.getColumn("departmentObject"));
        facilityTable.removeColumn(facilityTable.getColumn("facilityObject"));
        reloadTable();
        consultationRateTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting() || recursion_defenser) return;
            if (rejectBannedStatus(consultationRateTable, lastSelectedConsultationRate))
            {
                return;
            }
            functionForSelectListInTableOfADTMPanel();
            refreshPanelState();
            changeLastSelected();
        });
        departmentTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting() || recursion_defenser) return;
            if (rejectBannedStatus(departmentTable, lastSelectedDepartment))
            {
                return;
            }
            functionForSelectListInTableOfADTMPanel();
            refreshPanelState();
            changeLastSelected();
        });
        facilityTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting() || recursion_defenser) return;
            if (rejectBannedStatus(facilityTable, lastSelectedFacility)) return;
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
            clearTable(consultationRateTable);
            clearTable(departmentTable);
            clearTable(facilityTable);
            loadDataToTableDepartmentVer();
            loadDataToTableConsultationRateVer();
            loadDataToTableFacilityVer();
            refreshPanelState();
        } finally
        {
            recursion_defenser = false;
        }
    }

    private void loadDataToTableDepartmentVer()
    {
        DefaultTableModel model = (DefaultTableModel) departmentTable.getModel();
        for (var department : adminOperation.getAllDepartments())
        {
            Object[] row = new Object[3];
            row[0] = department.getId();
            row[1] = department.getSelf().getName();
            row[2] = department;
            model.addRow(row);
        }
    }

    private void loadDataToTableConsultationRateVer()
    {
        DefaultTableModel model = (DefaultTableModel) consultationRateTable.getModel();
        for (var consultationRate : adminOperation.getAllConsultationRates())
        {
            Object[] row = new Object[3];
            row[0] = consultationRate.getId();
            row[1] = consultationRate.getSelf().getName();
            row[2] = consultationRate;
            model.addRow(row);
        }
    }

    private void loadDataToTableFacilityVer()
    {
        DefaultTableModel model = (DefaultTableModel) facilityTable.getModel();
        for (var facility : adminOperation.getAllFacilities())
        {
            Object[] row = new Object[3];
            row[0] = facility.getId();
            row[1] = facility.getSelf().getName();
            row[2] = facility;
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
        clearColor(consultationRateTable);
        clearColor(departmentTable);
        clearColor(facilityTable);
        switch (getCurrentRelationType())
        {
            case CONSULTATION_DEPARTMENT ->
            {
                cdHighLight();
            }
            case FACILITY_DEPARTMENT ->
            {
                fdHighLight();
            }
            case DEPARTMENT_CONSULTATION ->
            {
                dcHighLight();
            }
            case DEPARTMENT_FACILITY ->
            {
                dfHighlight();
            }
            case DEPARTMENT_TO_BOTH ->
            {
                dcHighLight();
                dfHighlight();
            }
        }
        recolor(consultationRateTable);
        recolor(departmentTable);
        recolor(facilityTable);
    }

    private void refreshBan()
    {
        clearBanned(consultationRateTable);
        clearBanned(departmentTable);
        clearBanned(facilityTable);
        if (currentState == CurrentState.NONE) return;
        switch (getCurrentRelationType())
        {
            case CONSULTATION_DEPARTMENT ->
            {
                setTableToAllBanExceptSelectedRows(consultationRateTable);
                if (currentState == CurrentState.LINKING) break;
                else convertExceptHighLightToBan(departmentTable);
            }
            case FACILITY_DEPARTMENT ->
            {
                setTableToAllBanExceptSelectedRows(facilityTable);
                if (currentState == CurrentState.LINKING) break;
                else convertExceptHighLightToBan(departmentTable);
            }
            case DEPARTMENT_TO_BOTH ->
            {
                throw new IllegalStateException("In LINKING or UNLINKING,relation cannot be DEPARTMENT_TO_BOTH");
            }
            case DEPARTMENT_CONSULTATION ->
            {
                setTableToAllBanExceptSelectedRows(departmentTable);
                if (currentState == CurrentState.LINKING)
                {
                    for (int i = 0; i < consultationRateTable.getModel().getRowCount(); i++)
                    {
                        ConsultationRate consultationRate = getObjectFromRow(consultationRateTable, i, 2);
                        if (consultationRate.getBelongsToDepartment() != null) addBanned(consultationRateTable, i);
                    }
                } else convertExceptHighLightToBan(consultationRateTable);
            }
            case DEPARTMENT_FACILITY ->
            {
                setTableToAllBanExceptSelectedRows(departmentTable);
                if (currentState == CurrentState.LINKING)
                {
                    for (int i = 0; i < facilityTable.getModel().getRowCount(); i++)
                    {
                        Facility facility = getObjectFromRow(facilityTable, i, 2);
                        if (facility.getBelongsToDepartment() != null) addBanned(facilityTable, i);
                    }
                } else convertExceptHighLightToBan(facilityTable);
            }
        }
        clearHighLightColor(departmentTable);
        clearHighLightColor(facilityTable);
        clearHighLightColor(consultationRateTable);
        recolor(consultationRateTable);
        recolor(departmentTable);
        recolor(facilityTable);
    }

    private void fdHighLight()
    {
        Facility facility = getObjectFromCurrentSelectedRow(facilityTable, 2);
        if (facility == null)
            throw new IllegalStateException("In FACILITY_DEPARTMENT relation,facility must be selected!");
        DepartmentToFile departmentFromFacility = facility.getBelongsToDepartment();
        if (departmentFromFacility == null) return;
        for (int i = 0; i < departmentTable.getModel().getRowCount(); i++)
        {
            if (departmentTable.getModel().getValueAt(i, 0).equals(departmentFromFacility.getId()))
            {
                addHighLightColor(departmentTable, i);
            }
        }
    }

    private void cdHighLight()
    {
        ConsultationRate consultationRate = getObjectFromCurrentSelectedRow(consultationRateTable, 2);
        if (consultationRate == null)
            throw new IllegalStateException("In CONSULTATION_DEPARTMENT relation,consultationRate must be selected!");
        DepartmentToFile departmentFromConsultationRate = consultationRate.getBelongsToDepartment();
        if (departmentFromConsultationRate == null) return;
        for (int i = 0; i < departmentTable.getModel().getRowCount(); i++)
        {
            if (departmentTable.getModel().getValueAt(i, 0).equals(departmentFromConsultationRate.getId()))
            {
                addHighLightColor(departmentTable, i);
            }
        }
    }

    private void dcHighLight()
    {
        Department department = getObjectFromCurrentSelectedRow(departmentTable, 2);
        if (department == null)
            throw new IllegalStateException("In DEPARTMENT_CONSULTATION_RATE relation,department must be selected!");
        var consultationFromDepartments = department.getConsultations();
        var consultationToDepartmentIds = consultationFromDepartments.getIds();
        if (consultationFromDepartments.isEmpty()) return;
        for (int i = 0; i < consultationRateTable.getRowCount(); i++)
        {
            if (consultationToDepartmentIds.contains((String) consultationRateTable.getModel().getValueAt(i, 0)))
            {
                addHighLightColor(consultationRateTable, i);
            }
        }
    }

    private void dfHighlight()
    {
        Department department = getObjectFromCurrentSelectedRow(departmentTable, 2);
        if (department == null)
            throw new IllegalStateException("In DEPARTMENT_FACILITY relation,department must be selected!");
        var facilities = department.getFacilities();
        var facilitiesIds = facilities.getIds();
        if (facilities.isEmpty()) return;
        for (int i = 0; i < facilityTable.getRowCount(); i++)
        {
            if (facilitiesIds.contains((String) facilityTable.getModel().getValueAt(i, 0)))
            {
                addHighLightColor(facilityTable, i);
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
        lastSelectedConsultationRate = consultationRateTable.convertRowIndexToModel(consultationRateTable.getSelectedRow());
        lastSelectedDepartment = departmentTable.convertRowIndexToModel(departmentTable.getSelectedRow());
        lastSelectedFacility = facilityTable.convertRowIndexToModel(facilityTable.getSelectedRow());
    }

    private void functionForSelectListInTableOfADTMPanel()
    {
        try
        {
            recursion_defenser = true;
            ConsultationRate selectedRowConsultationRate = getObjectFromCurrentSelectedRow(consultationRateTable, 2);
            Department selectedRowDepartment = getObjectFromCurrentSelectedRow(departmentTable, 2);
            Facility selectedRowFacility = getObjectFromCurrentSelectedRow(facilityTable, 2);
            if (currentState == CurrentState.LINKING || currentState == CurrentState.UNLINKING)
            {
                return;
            }
            switch (currentTable)
            {
                case NONE ->
                {
                    if (selectedRowConsultationRate != null) setCurrentTable(CurrentTable.CONSULTATION_RATE);
                    else if (selectedRowDepartment != null) setCurrentTable(CurrentTable.DEPARTMENT);
                    else if (selectedRowFacility != null) setCurrentTable(CurrentTable.FACILITY);
                    else setCurrentTable(CurrentTable.NONE);
                }
                case CONSULTATION_RATE ->
                {
                    if (selectedRowDepartment != null) setCurrentTable(CurrentTable.DEPARTMENT);
                    else if (selectedRowFacility != null) setCurrentTable(CurrentTable.FACILITY);
                    else if (selectedRowConsultationRate != null) setCurrentTable(CurrentTable.CONSULTATION_RATE);
                    else setCurrentTable(CurrentTable.NONE);
                }
                case DEPARTMENT ->
                {
                    if (selectedRowFacility != null) setCurrentTable(CurrentTable.FACILITY);
                    else if (selectedRowConsultationRate != null) setCurrentTable(CurrentTable.CONSULTATION_RATE);
                    else if (selectedRowDepartment != null) setCurrentTable(CurrentTable.DEPARTMENT);
                    else setCurrentTable(CurrentTable.NONE);
                }
                case FACILITY ->
                {
                    if (selectedRowConsultationRate != null) setCurrentTable(CurrentTable.CONSULTATION_RATE);
                    else if (selectedRowDepartment != null) setCurrentTable(CurrentTable.DEPARTMENT);
                    else if (selectedRowFacility != null) setCurrentTable(CurrentTable.FACILITY);
                    else setCurrentTable(CurrentTable.NONE);
                }
            }
        } finally
        {
            recursion_defenser = false;
        }
    }

    private List<Integer> getHighLightRows(JTable table)
    {
        List<Integer> highLightRows = new ArrayList<>();
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

    private void setTableRowToBan(JTable table, List<Integer> bannedRow)
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
        List<Integer> highLightRows = getHighLightRows(table);
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

    private void cdLink(ActionEvent e)
    {
        switch (currentState)
        {
            case NONE ->
            {
                if (currentTable == CurrentTable.DEPARTMENT)
                {
                    setTargetTable(TargetTable.CONSULTATION_RATE);
                }
                setCurrentState(CurrentState.LINKING);
            }
            case LINKING ->
            {
                AdminOperation.CRUDInformation crudInformation =
                        adminOperation.allocateConsultationRateToDepartment
                                (
                                        getObjectFromCurrentSelectedRow(consultationRateTable, 2),
                                        getObjectFromCurrentSelectedRow(departmentTable, 2)
                                );
                if (crudInformation.isSuccess())
                {
                    JOptionPane.showMessageDialog(this, "Successfully allocated Consultation Rate to Department");
                    reloadTable();
                    return;
                } else
                    JOptionPane.showMessageDialog(this, "Failed to allocate Consultation Rate to Department\n" + crudInformation.message());
                resetAllStatus();
            }
            case UNLINKING ->
            {
                AdminOperation.CRUDInformation crudInformation =
                        adminOperation.unallocatedConsultationRateToDepartment
                                (
                                        getObjectFromCurrentSelectedRow(consultationRateTable, 2),
                                        getObjectFromCurrentSelectedRow(departmentTable, 2)
                                );
                if (crudInformation.isSuccess())
                {
                    JOptionPane.showMessageDialog(this, "Successfully unallocated Consultation Rate to Department");
                    reloadTable();
                    return;
                } else
                    JOptionPane.showMessageDialog(this, "Failed to unallocated Consultation Rate to Department\n" + crudInformation.message());
                resetAllStatus();
            }
        }
        refreshPanelState();
    }

    private void cdUnLink(ActionEvent e)
    {
        switch (currentState)
        {
            case NONE ->
            {
                if (currentTable == CurrentTable.DEPARTMENT)
                {
                    setTargetTable(TargetTable.CONSULTATION_RATE);
                }
                setCurrentState(CurrentState.UNLINKING);
            }
            case LINKING, UNLINKING -> resetAllStatus();
        }
        refreshPanelState();
    }

    private void fdLink(ActionEvent e)
    {
        switch (currentState)
        {
            case NONE ->
            {
                if (currentTable == CurrentTable.DEPARTMENT)
                {
                    setTargetTable(TargetTable.FACILITY);
                }
                setCurrentState(CurrentState.LINKING);
            }
            case LINKING ->
            {
                AdminOperation.CRUDInformation crudInformation =
                        adminOperation.allocateFacilityToDepartment
                                (
                                        getObjectFromCurrentSelectedRow(facilityTable, 2),
                                        getObjectFromCurrentSelectedRow(departmentTable, 2)
                                );
                if (crudInformation.isSuccess())
                {
                    JOptionPane.showMessageDialog(this, "Successfully allocated Facility to Department");
                    reloadTable();
                    return;
                } else
                    JOptionPane.showMessageDialog(this, "Failed to allocate Facility to Department\n" + crudInformation.message());
                resetAllStatus();
            }
            case UNLINKING ->
            {
                AdminOperation.CRUDInformation crudInformation =
                        adminOperation.unallocatedFacilityToDepartment
                                (
                                        getObjectFromCurrentSelectedRow(facilityTable, 2),
                                        getObjectFromCurrentSelectedRow(departmentTable, 2)
                                );
                if (crudInformation.isSuccess())
                {
                    JOptionPane.showMessageDialog(this, "Successfully unallocated Facility to Department");
                    reloadTable();
                    return;
                } else
                    JOptionPane.showMessageDialog(this, "Failed to unallocated Facility to Department\n" + crudInformation.message());
                resetAllStatus();
            }
        }
        refreshPanelState();
    }

    private void fdUnLink(ActionEvent e)
    {
        switch (currentState)
        {
            case NONE ->
            {
                if (currentTable == CurrentTable.DEPARTMENT)
                {
                    setTargetTable(TargetTable.FACILITY);
                }
                setCurrentState(CurrentState.UNLINKING);
            }
            case LINKING, UNLINKING -> resetAllStatus();
        }
        refreshPanelState();
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        scrollPane1 = new JScrollPane();
        consultationRateTable = new JTable();
        scrollPane2 = new JScrollPane();
        departmentTable = new JTable();
        cdLinkButton = new JButton();
        cdUnLinkButton = new JButton();
        scrollPane3 = new JScrollPane();
        facilityTable = new JTable();
        fdLinkButton = new JButton();
        fdUnLinkButton = new JButton();

        //======== this ========

        //======== scrollPane1 ========
        {

            //---- consultationRateTable ----
            consultationRateTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "consultationRateObject"
                }
            ));
            scrollPane1.setViewportView(consultationRateTable);
        }

        //======== scrollPane2 ========
        {

            //---- departmentTable ----
            departmentTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "departmentObject"
                }
            ));
            scrollPane2.setViewportView(departmentTable);
        }

        //---- cdLinkButton ----
        cdLinkButton.setText("Link");
        cdLinkButton.addActionListener(e -> cdLink(e));

        //---- cdUnLinkButton ----
        cdUnLinkButton.setText("Unlink");
        cdUnLinkButton.addActionListener(e -> cdUnLink(e));

        //======== scrollPane3 ========
        {

            //---- facilityTable ----
            facilityTable.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                    {null, null, null},
                },
                new String[] {
                    "Id", "Name", "facilityObject"
                }
            ));
            scrollPane3.setViewportView(facilityTable);
        }

        //---- fdLinkButton ----
        fdLinkButton.setText("Link");
        fdLinkButton.addActionListener(e -> fdLink(e));

        //---- fdUnLinkButton ----
        fdUnLinkButton.setText("Unlink");
        fdUnLinkButton.addActionListener(e -> fdUnLink(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(33, 33, 33)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(cdLinkButton)
                        .addComponent(cdUnLinkButton))
                    .addGap(18, 18, 18)
                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(fdLinkButton)
                        .addComponent(fdUnLinkButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(111, 111, 111)
                            .addComponent(cdLinkButton)
                            .addGap(18, 18, 18)
                            .addComponent(cdUnLinkButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(110, 110, 110)
                            .addComponent(fdLinkButton)
                            .addGap(18, 18, 18)
                            .addComponent(fdUnLinkButton)))
                    .addContainerGap(328, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 27, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE)
                                .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE))
                            .addGap(16, 16, 16))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE)
                            .addGap(25, 25, 25))))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane scrollPane1;
    private JTable consultationRateTable;
    private JScrollPane scrollPane2;
    private JTable departmentTable;
    private JButton cdLinkButton;
    private JButton cdUnLinkButton;
    private JScrollPane scrollPane3;
    private JTable facilityTable;
    private JButton fdLinkButton;
    private JButton fdUnLinkButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
