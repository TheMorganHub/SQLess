package com.sqless.ui;

import com.sqless.sql.objects.SQLColumn;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Un {@code JPanel} que sirve para representar, visualizar, y cambiar
 * características de columnas SQL, como por ejemplo valores default, auto
 * increment, etc. Este panel dependerá de un {@link FrontPanel} y tomará una
 * referencia de la {@code JTable} que tiene información sobre las columnas, y
 * una referencia a una {@code List} de {@link SQLColumn}. Este panel utilizará
 * eventos de cambio de selección en la {@code JTable} para actualizarse y así
 * cambiar sus componentes mostrados. Asimismo, los componentes visuales serán
 * llenados con información sobre la columna seleccionada utilizando la
 * referencia a la {@code List} de {@code SQLColumn}s.
 *
 * @author Morgan
 */
public class UIColumnExtrasPanel extends javax.swing.JPanel {

    private FrontPanel frontPanel;
    private JTable uiTable;
    private List<SQLColumn> columnList;

    public UIColumnExtrasPanel(FrontPanel frontPanel, JTable uiTable, List<SQLColumn> columnList) {
        init();
        this.frontPanel = frontPanel;
        this.uiTable = uiTable;
        this.columnList = columnList;
    }

    public void init() {
        chkAutoIncrement = new JCheckBox("Autoincrement");
        chkUnsigned = new JCheckBox("Unsigned");
        lblDefault = new JLabel("Default:");
        txtDefault = new JTextField();
        lblEnumSetValues = new JLabel("Values:");
        txtEnumSetValues = new JTextField();

        setLayout(new java.awt.GridBagLayout());
    }

    public void addToPnl(Component comp, int y) {
        add(comp, getConstraintsForGridX(comp.getClass(), y));
        revalidate();
    }

    public void refresh() {
        this.setVisible(uiTable.getRowCount() > 0);
        if (columnList.isEmpty()) {
            return;
        }
        removeAll(); //limpiamos el panel        

        SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow() == -1 ? 0 : uiTable.getSelectedRow());

        switch (selectedColumn.getDataType()) {
            case "tinyint":
            case "smallint":
            case "mediumint":
            case "int":
            case "bigint":
                addToPnl(chkAutoIncrement, 1);
                chkAutoIncrement.removeItemListener(autoIncrementValueChangeListener);
                chkAutoIncrement.setSelected(selectedColumn.isAutoincrement());
                chkAutoIncrement.addItemListener(autoIncrementValueChangeListener);

                addToPnl(chkUnsigned, 2);
                chkUnsigned.removeItemListener(unsignedChangeListener);
                chkUnsigned.setSelected(selectedColumn.isUnsigned());
                chkUnsigned.addItemListener(unsignedChangeListener);
                break;
            case "set":
            case "enum":
                addToPnl(lblEnumSetValues, 1);
                addToPnl(txtEnumSetValues, 1);
                txtEnumSetValues.getDocument().removeDocumentListener(enumSetValueChangeListener);
                txtEnumSetValues.setText(selectedColumn.getEnumLikeValues(false));
                txtEnumSetValues.getDocument().addDocumentListener(enumSetValueChangeListener);
                break;
        }
        //estos siempre van a estar
        addToPnl(lblDefault, 0);
        addToPnl(txtDefault, 0);
        txtDefault.getDocument().removeDocumentListener(defaultValueChangeListener);
        txtDefault.setText(selectedColumn.getDefaultVal());
        txtDefault.getDocument().addDocumentListener(defaultValueChangeListener);
    }

    private GridBagConstraints getConstraintsForGridX(Class<? extends Component> type, int y) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = y;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;

        switch (type.getSimpleName()) {
            case "JLabel":
                gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 0);
                gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "JTextField":
                gridBagConstraints.gridheight = 2;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(0, 150, 10, 10);
                gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "JCheckBox":
                gridBagConstraints.gridheight = 1;
                gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 2);
                break;
        }
        return gridBagConstraints;
    }

    private DocumentListener defaultValueChangeListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow());
            selectedColumn.setDefaultVal(txtDefault.getText(), true);
            frontPanel.boldTitleLabel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (uiTable.getSelectedRow() != -1) { //si la columna no está siendo eliminada
                SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow());
                selectedColumn.setDefaultVal(txtDefault.getText(), true);
                frontPanel.boldTitleLabel();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };
    
    private DocumentListener enumSetValueChangeListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow());
            selectedColumn.setEnumLikeValues(txtEnumSetValues.getText());
            selectedColumn.evaluateUncommittedChanges();
            frontPanel.boldTitleLabel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (uiTable.getSelectedRow() != -1) { //si la columna no está siendo eliminada
                SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow());
                selectedColumn.setEnumLikeValues(txtEnumSetValues.getText());
                selectedColumn.evaluateUncommittedChanges();
                frontPanel.boldTitleLabel();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };
    
    private ItemListener autoIncrementValueChangeListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow());
            selectedColumn.setAutoincrement(chkAutoIncrement.isSelected(), true);
            frontPanel.boldTitleLabel();
        }
    };
    private ItemListener unsignedChangeListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow());
            selectedColumn.setUnsigned(chkUnsigned.isSelected(), true);
            frontPanel.boldTitleLabel();
        }
    };

    private JTextField txtDefault;
    private JTextField txtEnumSetValues;
    private JLabel lblEnumSetValues;
    private JLabel lblDefault;
    private JCheckBox chkAutoIncrement;
    private JCheckBox chkUnsigned;
}
