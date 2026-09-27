/*
 * Created by JFormDesigner on Thu Sep 24 00:14:43 GMT+08:00 2026
 */

package Forms.AdminForm.LinkPanel;

import java.awt.event.*;

import Interfaces.RefreshablePanel;
import Operations.AdminOperation.AdminOperation;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BaseEntity.Users.User;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.MedicalManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;import static Forms.AdminForm.FrameHelper.getObjectFromRow;

/**
 * @author leezh
 */
public class AllocateDoctorToMedicalManagerPanel extends JPanel implements RefreshablePanel
{
    private AdminOperation adminOperation;
    @Override
    public void refreshData()
    {
        reload();
    }

    public AllocateDoctorToMedicalManagerPanel(AdminOperation adminOperation)
    {
        this.adminOperation = adminOperation;
        initComponents();
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicalManagerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setDefaultEditor(Object.class, null);
        medicalManagerTable.setDefaultEditor(Object.class, null);
        colorMapInit();
        banMapInit();
        allocateDoctorToManagerPanelInit();
    }

    public void reload()
    {
        setAdtmStatus(ADTMStatus.NONE_SELECTED);
        clearColor(doctorTable);
        clearColor(medicalManagerTable);
        clearBanned(doctorTable);
        clearBanned(medicalManagerTable);
        reloadTable();
    }

    private void link(ActionEvent e)
    {
        switch (adtmStatus)
        {
            case ADTMStatus.NONE_SELECTED:
                throw new IllegalStateException("NONE_SELECTED Should not press link button");
            case ADTMStatus.DOCTOR_SELECTED_BUT_NOT_LINKED:
                setAdtmStatus(ADTMStatus.DOCTOR_LINKING_BUT_NOT_CONNECTED);
                setTableToAllBanExceptSelectedRows(doctorTable);
                break;
            case ADTMStatus.MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED,ADTMStatus.MEDICAL_MANAGER_SELECTED_AND_LINKED:
                setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_LINKING_BUT_NOT_CONNECTED);
                setTableToAllBanExceptSelectedRows(medicalManagerTable);
                List<Integer> doctorRowToBanned = new ArrayList<>();
                for (int i = 0; i < doctorTable.getModel().getRowCount(); i++)
                {
                    if (((Doctor)getObjectFromRow(doctorTable,i,2)).getBelongsToMedicalManager() != null)
                        doctorRowToBanned.add(i);
                }
                setTableRowToBan(doctorTable, doctorRowToBanned);
                break;

            case ADTMStatus.MEDICAL_MANAGER_LINKING_AND_CONNECTED,ADTMStatus.DOCTOR_LINKING_AND_CONNECTED:
                Doctor doctorToLink = getObjectFromCurrentSelectedRow(doctorTable,2);
                MedicalManager medicalManagerToLink = getObjectFromCurrentSelectedRow(medicalManagerTable,2);
                if(doctorToLink == null || medicalManagerToLink == null)
                    throw new IllegalStateException("In LINKING_AND_CONNECTED status,both table should be selected!");
                AdminOperation.CRUDInformation linkInformation= adminOperation.allocateDoctorToMedicalManager(doctorToLink, medicalManagerToLink);
                if (linkInformation.isSuccess())
                    JOptionPane.showMessageDialog(this, "Doctor has been allocated successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                else
                    JOptionPane.showMessageDialog(this, linkInformation.message(),"Error",JOptionPane.ERROR_MESSAGE);
                setAdtmStatus(ADTMStatus.NONE_SELECTED);
                reload();
                break;
            case ADTMStatus.DOCTOR_UNLINKING_AND_CONNECTED, ADTMStatus.MEDICAL_MANAGER_UNLINKING_AND_CONNECTED:
                Doctor doctorToUnlink = getObjectFromCurrentSelectedRow(doctorTable,2);
                MedicalManager medicalManagerToUnlink = getObjectFromCurrentSelectedRow(medicalManagerTable,2);
                if(doctorToUnlink == null || medicalManagerToUnlink == null)
                    throw new IllegalStateException("In UNLINKING_AND_CONNECTED status,both table should be selected!");
                AdminOperation.CRUDInformation unLinkInformation= adminOperation.unallocatedDoctorToMedicalManager(doctorToUnlink, medicalManagerToUnlink);
                if (unLinkInformation.isSuccess())
                    JOptionPane.showMessageDialog(this, "Doctor has been unAllocated successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                else
                    JOptionPane.showMessageDialog(this, unLinkInformation.message(),"Error",JOptionPane.ERROR_MESSAGE);
                reload();
                break;

        }
    }

    private void unLink(ActionEvent e)
    {
        switch (adtmStatus)
        {
            case ADTMStatus.NONE_SELECTED:
                throw new IllegalStateException("NONE_SELECTED Should not press link button");
            case ADTMStatus.DOCTOR_SELECTED_AND_LINKED:
                setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_UNLINKING_AND_CONNECTED);
                setTableToAllBanExceptSelectedRows(doctorTable);
                boolean yellowAlrFind = false;
                for(ColorStatus colorStatus: colorMap.get(medicalManagerTable))
                {
                    if (yellowAlrFind) throw new IllegalStateException("Find two medical manager is linked(color result) to same doctor(should not happen)");
                    if (colorStatus.color == Color.YELLOW)
                    {
                        yellowAlrFind = true;
                        medicalManagerTable.setRowSelectionInterval
                        (
                            medicalManagerTable.convertRowIndexToView(colorStatus.rowShouldBeColored),
                            medicalManagerTable.convertRowIndexToView(colorStatus.rowShouldBeColored)
                        );
                    }
                }
                if (!yellowAlrFind) throw new IllegalStateException("No medical manager found");
                setTableToAllBanExceptSelectedRows(medicalManagerTable);
                break;
            case ADTMStatus.MEDICAL_MANAGER_SELECTED_AND_LINKED:
                setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_UNLINKING_BUT_NOT_CONNECTED);
                setTableToAllBanExceptSelectedRows(medicalManagerTable);
                List<Integer> doctorRowToExclude = new ArrayList<>();
                for(ColorStatus colorStatus: colorMap.get(doctorTable))
                {
                    if (colorStatus.color == Color.YELLOW)
                    {
                        doctorRowToExclude.add(colorStatus.rowShouldBeColored);
                    }
                }
                if (doctorRowToExclude.isEmpty()) throw new IllegalStateException("In MEDICAL_MANAGER_SELECTED_AND_LINKED, doctor must be linked");
                List<Integer> doctorRowToBanned = new ArrayList<>();
                for(int i = 0; i < doctorTable.getModel().getRowCount(); i++)
                {
                    if (doctorRowToExclude.contains(i)) continue;
                    doctorRowToBanned.add(i);
                }
                setTableRowToBan(doctorTable,doctorRowToBanned);
                break;
            case ADTMStatus.MEDICAL_MANAGER_LINKING_AND_CONNECTED,
                 ADTMStatus.DOCTOR_LINKING_AND_CONNECTED,
                 ADTMStatus.MEDICAL_MANAGER_UNLINKING_AND_CONNECTED,
                 ADTMStatus.DOCTOR_UNLINKING_AND_CONNECTED,
                 ADTMStatus.MEDICAL_MANAGER_LINKING_BUT_NOT_CONNECTED,
                 ADTMStatus.DOCTOR_LINKING_BUT_NOT_CONNECTED,
                 ADTMStatus.MEDICAL_MANAGER_UNLINKING_BUT_NOT_CONNECTED,
                 ADTMStatus.DOCTOR_UNLINKING_BUT_NOT_CONNECTED:
                reload();
                break;
        }
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        DTMMPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        doctorTable = new JTable();
        scrollPane2 = new JScrollPane();
        medicalManagerTable = new JTable();
        linkButton = new JButton();
        unLinkButton = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== DTMMPanel ========
        {

            //======== scrollPane1 ========
            {

                //---- doctorTable ----
                doctorTable.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null},
                        {null, null, null},
                    },
                    new String[] {
                        "Id", "Name", "doctorObject"
                    }
                ));
                scrollPane1.setViewportView(doctorTable);
            }

            //======== scrollPane2 ========
            {

                //---- medicalManagerTable ----
                medicalManagerTable.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null},
                        {null, null, null},
                    },
                    new String[] {
                        "Id", "Name", "medicalManagerObject"
                    }
                ));
                scrollPane2.setViewportView(medicalManagerTable);
            }

            //---- linkButton ----
            linkButton.setText("Link");
            linkButton.addActionListener(e -> link(e));

            //---- unLinkButton ----
            unLinkButton.setText("Unlink");
            unLinkButton.addActionListener(e -> unLink(e));

            GroupLayout DTMMPanelLayout = new GroupLayout(DTMMPanel);
            DTMMPanel.setLayout(DTMMPanelLayout);
            DTMMPanelLayout.setHorizontalGroup(
                DTMMPanelLayout.createParallelGroup()
                    .addGroup(DTMMPanelLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addGroup(DTMMPanelLayout.createParallelGroup()
                            .addComponent(linkButton)
                            .addComponent(unLinkButton))
                        .addGap(44, 44, 44)
                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
            );
            DTMMPanelLayout.setVerticalGroup(
                DTMMPanelLayout.createParallelGroup()
                    .addGroup(DTMMPanelLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(DTMMPanelLayout.createParallelGroup()
                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(DTMMPanelLayout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(linkButton)
                        .addGap(18, 18, 18)
                        .addComponent(unLinkButton)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }
        add(DTMMPanel, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    private enum ADTMStatus
    {
        NONE_SELECTED,
        DOCTOR_SELECTED_AND_LINKED,
        DOCTOR_SELECTED_BUT_NOT_LINKED,
        MEDICAL_MANAGER_SELECTED_AND_LINKED,
        MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED,

        DOCTOR_LINKING_BUT_NOT_CONNECTED,
        DOCTOR_LINKING_AND_CONNECTED,
        MEDICAL_MANAGER_LINKING_BUT_NOT_CONNECTED,
        MEDICAL_MANAGER_LINKING_AND_CONNECTED,

        DOCTOR_UNLINKING_BUT_NOT_CONNECTED,
        DOCTOR_UNLINKING_AND_CONNECTED,
        MEDICAL_MANAGER_UNLINKING_BUT_NOT_CONNECTED,
        MEDICAL_MANAGER_UNLINKING_AND_CONNECTED,
    }

    private record ColorStatus(Integer rowShouldBeColored, Color color){}

    private ADTMStatus adtmStatus;

    private HashMap<JTable, List<ColorStatus>> colorMap = new HashMap<>();
    private HashMap<JTable, HashSet<String>> banMap = new HashMap<>();
    private int lastSelectedDoctor = -1;
    private int lastSelectedMedicalManager = -1;
    private boolean recursion_defenser = false;

    private void setAdtmStatus(ADTMStatus adtmStatus)
    {
        switch (adtmStatus)
        {
            case NONE_SELECTED:
                linkButton.setText("Link");
                unLinkButton.setText("Unlink");
                linkButton.setEnabled(false);
                unLinkButton.setEnabled(false);
                break;
            case DOCTOR_SELECTED_BUT_NOT_LINKED:
                linkButton.setText("Link");
                unLinkButton.setText("Unlink");
                linkButton.setEnabled(true);
                unLinkButton.setEnabled(false);
                break;
            case DOCTOR_SELECTED_AND_LINKED:
                linkButton.setText("Link");
                unLinkButton.setText("Unlink");
                linkButton.setEnabled(false);
                unLinkButton.setEnabled(true);
                break;
            case MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED:
                linkButton.setText("Link");
                unLinkButton.setText("Unlink");
                linkButton.setEnabled(true);
                unLinkButton.setEnabled(false);
                break;
            case MEDICAL_MANAGER_SELECTED_AND_LINKED:
                linkButton.setText("Link");
                unLinkButton.setText("Unlink");
                linkButton.setEnabled(true);
                unLinkButton.setEnabled(true);
                break;
            case DOCTOR_LINKING_BUT_NOT_CONNECTED, MEDICAL_MANAGER_LINKING_BUT_NOT_CONNECTED,
                 DOCTOR_UNLINKING_BUT_NOT_CONNECTED, MEDICAL_MANAGER_UNLINKING_BUT_NOT_CONNECTED:
                linkButton.setText("Confirm");
                unLinkButton.setText("Cancel");
                linkButton.setEnabled(false);
                unLinkButton.setEnabled(true);
                break;
            case DOCTOR_LINKING_AND_CONNECTED, MEDICAL_MANAGER_LINKING_AND_CONNECTED,
                 DOCTOR_UNLINKING_AND_CONNECTED, MEDICAL_MANAGER_UNLINKING_AND_CONNECTED:
                linkButton.setText("Confirm");
                unLinkButton.setText("Cancel");
                linkButton.setEnabled(true);
                unLinkButton.setEnabled(true);
                break;
        }
        this.adtmStatus = adtmStatus;
    }

    private void colorMapInit()
    {
        colorMap.put(doctorTable, new ArrayList<>());
        colorMap.put(medicalManagerTable, new ArrayList<>());
    }

    private void banMapInit()
    {
        banMap.put(doctorTable, new HashSet<>());
        banMap.put(medicalManagerTable, new HashSet<>());
    }

    private void allocateDoctorToManagerPanelInit()
    {
        doctorTable.removeColumn(doctorTable.getColumn("doctorObject"));
        medicalManagerTable.removeColumn(medicalManagerTable.getColumn("medicalManagerObject"));
        reloadTable();
        doctorTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting() || recursion_defenser) return;
            if (rejectBannedStatus(doctorTable,lastSelectedDoctor))
            {
                return;
            }
            functionForSelectListInTableOfADTMPanel();
            refreshHighlight();
            changeLastSelected();
        });
        medicalManagerTable.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting() || recursion_defenser) return;
            if (rejectBannedStatus(medicalManagerTable,lastSelectedMedicalManager))
            {
                return;
            }
            functionForSelectListInTableOfADTMPanel();
            refreshHighlight();
            changeLastSelected();
        });

    }
    private void reloadTable()
    {
        clearTable(medicalManagerTable);
        clearTable(doctorTable);
        loadDataToTableADTMVer(doctorTable, adminOperation.getAllDoctors());
        loadDataToTableADTMVer(medicalManagerTable, adminOperation.getAllMedicalManagers());
        setAdtmStatus(ADTMStatus.NONE_SELECTED);
    }

    private void loadDataToTableADTMVer(JTable table, List<? extends BusinessEntity<? extends User>> users)
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (var user : users)
        {
            User userData = user.getSelf();
            Object[] row = new Object[3];
            row[0] = user.getId();
            row[1] = user.getSelf().getName();
            row[2] = user;
            model.addRow(row);
        }
    }

    private void refreshHighlight()
    {
        int selectedRow;
        Color highLightColor = Color.YELLOW;
        switch (adtmStatus)
        {
            case NONE_SELECTED:
                clearColor(doctorTable);
                clearColor(medicalManagerTable);
                break;
            case DOCTOR_SELECTED_AND_LINKED, DOCTOR_SELECTED_BUT_NOT_LINKED:
                clearColor(doctorTable);
                clearColor(medicalManagerTable);
                Doctor doctorSelected = getObjectFromCurrentSelectedRow(doctorTable, 2);
                if (doctorSelected == null)
                    throw new IllegalStateException("doctor selected status,doctor must be selected");
                MedicalManagerToFile manager = doctorSelected.getBelongsToMedicalManager();
                if (manager == null)
                {
                    return;
                }
                for (int i = 0; i < medicalManagerTable.getRowCount(); i++)
                {
                    if (((MedicalManager) getObjectFromRow(medicalManagerTable, i, 2)).getId().equals(manager.getId()))
                    {
                        colorMap.get(medicalManagerTable).add(new ColorStatus(i, highLightColor));
                    }
                }
                break;
            case MEDICAL_MANAGER_SELECTED_AND_LINKED, MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED:
                clearColor(doctorTable);
                clearColor(medicalManagerTable);
                MedicalManager managerSelected = getObjectFromCurrentSelectedRow(medicalManagerTable, 2);
                if (managerSelected == null)
                    throw new IllegalStateException("medical manager selected status,medical manager must be selected");
                var doctors = managerSelected.getDoctors();
                if (doctors.isEmpty())
                {
                    return;
                }
                List<String> doctorIds = doctors.getIds();
                for (String doctorId : doctorIds)
                {
                    for (int j = 0; j < doctorTable.getRowCount(); j++)
                    {
                        if (((Doctor) getObjectFromRow(doctorTable, j, 2)).getId().equals(doctorId))
                        {
                            colorMap.get(doctorTable).add(new ColorStatus(j, highLightColor));
                            break;
                        }
                    }
                }
                break;
        }
        highlightRow(doctorTable, colorMap.get(doctorTable));
        highlightRow(medicalManagerTable, colorMap.get(medicalManagerTable));
    }

    private void highlightRow(JTable table, List<ColorStatus> rowInformation)
    {
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
        highlightRow(table, colorMap.get(table));
    }

    private void clearBanned(JTable table)
    {
        banMap.get(table).clear();
    }

    private void changeLastSelected()
    {
        lastSelectedDoctor = doctorTable.convertRowIndexToModel(doctorTable.getSelectedRow());
        lastSelectedMedicalManager = medicalManagerTable.convertRowIndexToModel(medicalManagerTable.getSelectedRow());
    }

    private void functionForSelectListInTableOfADTMPanel()
    {
        try
        {
            recursion_defenser = true;
            Doctor selectedRowDoctor = getObjectFromCurrentSelectedRow(doctorTable, 2);
            MedicalManager selectedRowMedicalManager = getObjectFromCurrentSelectedRow(medicalManagerTable, 2);
            switch (adtmStatus)
            {
                case NONE_SELECTED:

                    if (selectedRowDoctor == null && selectedRowMedicalManager == null)
                        setAdtmStatus(ADTMStatus.NONE_SELECTED);
                    if (selectedRowDoctor != null && selectedRowMedicalManager != null)
                        throw new IllegalStateException("Doctor and MedicalManager can't be both selected In NONE_SELECTED state");
                    if (selectedRowDoctor != null)
                    {
                        medicalManagerTable.clearSelection();
                        if (selectedRowDoctor.getBelongsToMedicalManager() == null)
                            setAdtmStatus(ADTMStatus.DOCTOR_SELECTED_BUT_NOT_LINKED);
                        else setAdtmStatus(ADTMStatus.DOCTOR_SELECTED_AND_LINKED);
                        break;
                    }
                    if (selectedRowMedicalManager != null)
                    {
                        doctorTable.clearSelection();
                        if (selectedRowMedicalManager.getDoctors().isEmpty())
                            setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED);
                        else setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_SELECTED_AND_LINKED);
                        break;
                    }
                    break;
                case DOCTOR_SELECTED_AND_LINKED, DOCTOR_SELECTED_BUT_NOT_LINKED:
                    if (selectedRowDoctor == null && selectedRowMedicalManager == null)
                        setAdtmStatus(ADTMStatus.NONE_SELECTED);
                    if (selectedRowMedicalManager != null)
                    {
                        doctorTable.clearSelection();
                        if (selectedRowMedicalManager.getDoctors().isEmpty())
                            setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED);
                        else setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_SELECTED_AND_LINKED);
                        break;
                    }
                    if (selectedRowDoctor != null)
                    {
                        medicalManagerTable.clearSelection();
                        if (selectedRowDoctor.getBelongsToMedicalManager() == null)
                            setAdtmStatus(ADTMStatus.DOCTOR_SELECTED_BUT_NOT_LINKED);
                        else setAdtmStatus(ADTMStatus.DOCTOR_SELECTED_AND_LINKED);
                        break;
                    }
                    break;
                case MEDICAL_MANAGER_SELECTED_AND_LINKED, MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED:
                    if (selectedRowDoctor == null && selectedRowMedicalManager == null)
                        setAdtmStatus(ADTMStatus.NONE_SELECTED);
                    if (selectedRowDoctor != null)
                    {
                        medicalManagerTable.clearSelection();
                        if (selectedRowDoctor.getBelongsToMedicalManager() == null)
                            setAdtmStatus(ADTMStatus.DOCTOR_SELECTED_BUT_NOT_LINKED);
                        else setAdtmStatus(ADTMStatus.DOCTOR_SELECTED_AND_LINKED);
                        break;
                    }
                    if (selectedRowMedicalManager != null)
                    {
                        doctorTable.clearSelection();
                        if (selectedRowMedicalManager.getDoctors().isEmpty())
                            setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_SELECTED_BUT_NOT_LINKED);
                        else setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_SELECTED_AND_LINKED);
                        break;
                    }
                    break;
                case DOCTOR_LINKING_BUT_NOT_CONNECTED:
                    if (selectedRowDoctor == null)
                        throw new IllegalStateException("In DOCTOR_LINKING_BUT_NOT_CONNECTED, doctor table must be selected");
                    if (selectedRowMedicalManager == null) break;
                    setAdtmStatus(ADTMStatus.DOCTOR_LINKING_AND_CONNECTED);
                    break;
                case MEDICAL_MANAGER_LINKING_BUT_NOT_CONNECTED:
                    if (selectedRowMedicalManager == null)
                        throw new IllegalStateException("In MEDICAL_MANAGER_LINKING_BUT_NOT_CONNECTED,medical manager must be selected");
                    if (selectedRowDoctor == null) break;
                    setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_LINKING_AND_CONNECTED);
                    break;
                case DOCTOR_UNLINKING_BUT_NOT_CONNECTED:
                    if (selectedRowDoctor == null)
                        throw new IllegalStateException("In DOCTOR_UNLINKING_BUT_NOT_CONNECTED,doctor table must be selected");
                    if (selectedRowMedicalManager == null) break;
                    setAdtmStatus(ADTMStatus.DOCTOR_UNLINKING_AND_CONNECTED);
                    break;
                case MEDICAL_MANAGER_UNLINKING_BUT_NOT_CONNECTED:
                    if (selectedRowMedicalManager == null)
                        throw new IllegalStateException("In MEDICAL_MANAGER_UNLINKING_BUT_NOT_CONNECTED,medical manager must be selected");
                    if (selectedRowDoctor == null) break;
                    setAdtmStatus(ADTMStatus.MEDICAL_MANAGER_UNLINKING_AND_CONNECTED);
                    break;
            }
        } finally
        {
            recursion_defenser = false;
        }
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
        colorMap.get(table).clear();
        for (int i = 0; i < table.getModel().getRowCount(); i++)
        {
            if (i == selectedRow) continue;
            banMap.get(table).add(getObjectFromRow(table, i, 0));
            colorMap.get(table).add(new ColorStatus(i, Color.GRAY));
        }
        highlightRow(table, colorMap.get(table));
    }

    private void setTableRowToBan(JTable table, List<Integer> bannedRow)
    {
        colorMap.get(table).clear();
        for (int row : bannedRow)
        {
            banMap.get(table).add(getObjectFromRow(table, row, 0));
            colorMap.get(table).add(new ColorStatus(row, Color.GRAY));
        }
        highlightRow(table, colorMap.get(table));
    }

    private boolean rejectBannedStatus(JTable table,int lastSelectedModelRow)
    {
        String selectedId = getObjectFromCurrentSelectedRow(table, 0);
        if (selectedId == null)
        {
            return false;
        }
        if (!banMap.get(table).contains(selectedId))
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
            String previousId = getObjectFromRow(table, lastSelectedModelRow, 0);
            if (banMap.get(table).contains(previousId))
            {
                table.clearSelection();
                return true;
            }
            int previousViewRow = table.convertRowIndexToView(lastSelectedModelRow);
            table.setRowSelectionInterval(previousViewRow, previousViewRow);
            return true;
        }
        finally
        {
            recursion_defenser = false;
        }

    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel DTMMPanel;
    private JScrollPane scrollPane1;
    private JTable doctorTable;
    private JScrollPane scrollPane2;
    private JTable medicalManagerTable;
    private JButton linkButton;
    private JButton unLinkButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
