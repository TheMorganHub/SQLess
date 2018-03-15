package com.sqless.ui.enumeditor;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class SQLSetCellEditor extends AbstractCellEditor implements TableCellEditor {
    
    private UISQLSetEditor uiEnumEditor;

    public SQLSetCellEditor(String[] columnDefaultVals) {
        this.uiEnumEditor = new UISQLSetEditor(columnDefaultVals);
    }

    @Override
    public Object getCellEditorValue() {
        return uiEnumEditor.getValues();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        uiEnumEditor.setValues(value == null ? "" : value.toString());
        return uiEnumEditor;
    }

    
}
