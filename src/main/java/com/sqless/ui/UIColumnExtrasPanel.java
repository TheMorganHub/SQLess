package com.sqless.ui;

import com.sqless.sql.objects.SQLColumn;
import com.sqless.ui.dateeditor.UISQLDatePanelInner;
import com.sqless.ui.enumeditor.UISQLEnumPanelInner;
import com.sqless.ui.seteditor.UISQLSetPanelInner;
import com.sqless.utils.SQLUtils;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.swingx.JXDatePicker;

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
        chkUnsigned = new JCheckBox("No signado");
        lblDefault = new JLabel("Default:");
        txtDefault = new JTextField();
        lblEnumSetValues = new JLabel("Valores:");
        txtEnumSetValues = new JTextField();
        btnEditValues = new JButton("...");
        btnEditValues.setMargin(new Insets(1, 4, 1, 4));
        btnEditValues.setFocusable(false);
        btnEditValues.addActionListener(actionEditValues);
        btnEditDefaultVals = new JButton("...");
        btnEditDefaultVals.setMargin(new Insets(1, 4, 1, 4));
        btnEditDefaultVals.setFocusable(false);
        btnEditDefaultVals.addActionListener(actionEditDefaultVals);

        setLayout(new java.awt.GridBagLayout());
    }

    public void addToPnl(Component comp, int y) {
        add(comp, getConstraintsForGridX(comp.getClass(), y));
        revalidate();
    }

    public void refresh(List<SQLColumn> columnList) {
        this.setVisible(uiTable.getRowCount() > 0);
        this.columnList = columnList;
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
                addToPnl(btnEditDefaultVals, 0);
                addToPnl(lblEnumSetValues, 1);
                addToPnl(txtEnumSetValues, 1);
                addToPnl(btnEditValues, 1);
                txtEnumSetValues.getDocument().removeDocumentListener(enumSetValueChangeListener);
                txtEnumSetValues.setText(selectedColumn.getEnumLikeValues(false));
                txtEnumSetValues.getDocument().addDocumentListener(enumSetValueChangeListener);
                break;
            case "datetime":
                addToPnl(btnEditDefaultVals, 0);
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
                break;
            case "JTextField":
                gridBagConstraints.weightx = 1;
                gridBagConstraints.insets = new java.awt.Insets(0, 100, 10, 10);
                gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "JCheckBox":
                gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 2);
                gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "JButton":
                gridBagConstraints.gridx = 2;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
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

    private Action actionEditValues = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow());
            UISetEnumValuesEditor editor = new UISetEnumValuesEditor(selectedColumn.getName(), SQLUtils.getEnumLikeValuesAsArray(selectedColumn.getEnumLikeValues(false)));
            String valuesFromEditor = editor.showDialog();
            if (editor.valuesChanged()) {
                selectedColumn.setEnumLikeValues(valuesFromEditor);
                selectedColumn.evaluateUncommittedChanges();
                txtEnumSetValues.setText(valuesFromEditor);
                frontPanel.boldTitleLabel();
            }
        }
    };
    private JButton btnEditDefaultVals;
    private JButton btnEditValues;
    private JTextField txtDefault;
    private JTextField txtEnumSetValues;
    private JLabel lblEnumSetValues;
    private JLabel lblDefault;
    private JCheckBox chkAutoIncrement;
    private JCheckBox chkUnsigned;

    private ActionListener actionEditDefaultVals = e -> {
        SQLColumn selectedColumn = columnList.get(uiTable.getSelectedRow() == -1 ? 0 : uiTable.getSelectedRow());
        switch (selectedColumn.getDataType()) {
            case "enum":
                JPopupMenu enumPop = new JPopupMenu();
                UISQLEnumPanelInner enumPanelInnerPopUp = new UISQLEnumPanelInner(enumPop, SQLUtils.getEnumLikeValuesAsArray(selectedColumn.getEnumLikeValues(false)), txtDefault.getText(), txtDefault);
                enumPop.add(enumPanelInnerPopUp);
                enumPop.show(this, btnEditDefaultVals.getLocation().x - 95, btnEditDefaultVals.getLocation().y + 20);
                break;
            case "set":
                JPopupMenu setPop = new JPopupMenu();
                UISQLSetPanelInner setPanelInnerPopUp = new UISQLSetPanelInner(setPop, SQLUtils.getEnumLikeValuesAsArray(selectedColumn.getEnumLikeValues(false)), txtDefault.getText(), txtDefault);
                setPop.add(setPanelInnerPopUp);
                setPop.show(this, btnEditDefaultVals.getLocation().x - 95, btnEditDefaultVals.getLocation().y + 20);
                break;
            case "datetime":
                UISQLDatePanelInner datePanelInnerPopUp = new UISQLDatePanelInner(selectedColumn, txtDefault.getText(), txtDefault);
                datePanelInnerPopUp.setVisible(true);
                break;
        }
    };
}
