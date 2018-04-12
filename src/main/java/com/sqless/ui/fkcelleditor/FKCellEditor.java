package com.sqless.ui.fkcelleditor;

import com.sqless.sql.objects.SQLForeignKey;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class FKCellEditor extends AbstractCellEditor implements TableCellEditor {
    private UIFKCellEditor uiFkCellEditor;
    private boolean valueWasNull;
    private Object originalValue;

    public FKCellEditor(SQLForeignKey fk) {
        this.uiFkCellEditor = new UIFKCellEditor(fk);
    }

    @Override
    public Object getCellEditorValue() {
        String newValue = uiFkCellEditor.getValue();
        if (valueWasNull && newValue == null || valueWasNull && newValue.isEmpty()) {
            return originalValue;
        }
        return newValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        valueWasNull = value == null;
        originalValue = value;
        uiFkCellEditor.setValue(value == null ? "" : value.toString());
        return uiFkCellEditor;
    }
}
