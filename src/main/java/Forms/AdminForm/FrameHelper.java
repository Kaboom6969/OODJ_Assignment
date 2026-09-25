package Forms.AdminForm;

import javax.swing.*;

public class FrameHelper
{
    public static <T> T getObjectFromCurrentSelectedRow(JTable table, int column)
    {
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1)
        {
            return null;
        }
        selectedRow = table.convertRowIndexToModel(selectedRow);
        return getObjectFromRow(table,selectedRow,column);
    }
    public static <T> T getObjectFromRow(JTable table, int modelRow, int column)
    {
        return (T)table.getModel().getValueAt(modelRow, column);
    }
}
