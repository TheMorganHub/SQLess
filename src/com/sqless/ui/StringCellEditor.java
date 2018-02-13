package com.sqless.ui;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Un {@code CellEditor} utilizado para columnas que posean valores
 * {@code String}. Es necesario hacer uso de esta clase ya que en la
 * implementación original de {@code DefaultCellEditor}, si al comenzar el edit
 * el valor es nulo y durante el edit no se modificó, al terminar el edit ese
 * valor se transforma en un {@code String} vacío. Lo que hace esta clase es
 * arreglar eso, manteniendo el valor nulo original si el texto no se editó.
 *
 * @author Morgan
 */
public class StringCellEditor extends DefaultCellEditor {

    private Object originalValue;

    public StringCellEditor(JTextField textField) {
        super(textField);
        setClickCountToStart(1);
    }

    /**
     * Retorna el valor del editor. Preserva los valores que originalmente
     * fueron nulos si es que no fueron cambiados.
     *
     * @return
     */
    @Override
    public Object getCellEditorValue() {
        Object newValue = super.getCellEditorValue();
        return newValue != null && newValue.toString().isEmpty() && originalValue == null ? null : newValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        originalValue = value;
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

}
