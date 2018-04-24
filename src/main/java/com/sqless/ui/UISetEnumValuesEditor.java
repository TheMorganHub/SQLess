package com.sqless.ui;

import com.sqless.ui.listeners.TableCellListener;
import com.sqless.utils.UIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * Un editor de valores para columnas de tipo de dato {@code enum} o
 * {@code set}.
 *
 * @author Morgan
 */
public class UISetEnumValuesEditor extends javax.swing.JDialog {

    private boolean valuesChanged;
    private TableCellListener cellListener;

    public UISetEnumValuesEditor(String colName, String[] valuesArray) {
        super(UIClient.getInstance(), true);
        initComponents();
        loadTable(valuesArray);
        cellListener = new TableCellListener(uiTable, actionValueChange);
        setLocationRelativeTo(getParent());
        getRootPane().setDefaultButton(btnOk);
        setTitle("Editar valores para '" + colName + "'");
    }

    public String showDialog() {
        setVisible(true);
        if (!valuesChanged) {
            return null;
        }
        StringBuilder sbValues = new StringBuilder();
        if (uiTable.getRowCount() == 0) {
            return sbValues.toString();
        }
        for (int i = 0; i < uiTable.getRowCount(); i++) {
            String value = uiTable.getValueAt(i, 0).toString();
            sbValues.append("'").append(value).append("',");
        }

        return sbValues.substring(0, sbValues.length() - 1);
    }

    public void loadTable(String[] valuesArray) {
        uiTable.addPropertyChangeListener(tableEditorPropertyChangeListener);
        DefaultCellEditor defEditor = new DefaultCellEditor(new JTextField());
        defEditor.setClickCountToStart(1);
        uiTable.getColumnModel().getColumn(0).setCellEditor(defEditor);
        for (String value : valuesArray) {
            getModel().addRow(new String[]{value});
        }
    }

    public boolean valuesChanged() {
        return valuesChanged;
    }

    private boolean isInTable(String value, int ignoreRow) {
        if (value == null) {
            return false;
        }

        for (int i = 0; i < uiTable.getRowCount(); i++) {
            String tableValue = uiTable.getValueAt(i, 0).toString();
            if (ignoreRow == i) {
                continue;
            }
            if (tableValue.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public DefaultTableModel getModel() {
        return (DefaultTableModel) uiTable.getModel();
    }

    private Action actionAdd = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            getModel().addRow(new String[]{""});
            UIUtils.scrollToBottom(scrTable);
        }
    };

    private Action actionRemove = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (uiTable.getRowCount() > 0) {
                UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.CANCEL);
                int[] selectedRows = uiTable.getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        getModel().removeRow(selectedRows[i]);
                    }
                    valuesChanged = true;
                }
            }
        }
    };

    private Action actionOK = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.STOP);
            dispose();
        }
    };

    private Action actionCancelar = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.CANCEL);
            valuesChanged = false;
            dispose();
        }
    };

    private Action actionValueChange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String oldValue = cellListener.getOldValue().toString();
            String newValue = cellListener.getNewValue().toString();
            int row = cellListener.getRow();

            if (isInTable(newValue, row)) {
                UIUtils.showErrorMessage("Editor", "No puede haber valores repetidos", UISetEnumValuesEditor.this);
                uiTable.setValueAt(oldValue, row, 0);
            } else {
                valuesChanged = true;
            }
        }
    };

    private PropertyChangeListener tableEditorPropertyChangeListener = (evt) -> {
        JXTable source = (JXTable) evt.getSource();
        if ("tableCellEditor".equals(evt.getPropertyName())) {
            if (source.isEditing()) {
                Component component = ((DefaultCellEditor) source.getCellEditor()).getComponent();
                if (component instanceof JTextField) {
                    ((JTextField) component).setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
                    ((JTextField) component).setFont(UIUtils.SEGOE_UI_FONT);
                }
                if (component instanceof JComboBox) {
                    JComboBox combobox = (JComboBox) component;
                    combobox.setBorder(BorderFactory.createEmptyBorder());
                    combobox.setFont(UIUtils.SEGOE_UI_FONT);
                }
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tbButtons = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        scrTable = new javax.swing.JScrollPane();
        uiTable = new org.jdesktop.swingx.JXTable();
        btnOk = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editor de valores");
        setResizable(false);

        tbButtons.setFloatable(false);
        tbButtons.setRollover(true);

        btnAdd.setAction(actionAdd);
        btnAdd.setText("+");
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbButtons.add(btnAdd);

        btnDelete.setAction(actionRemove);
        btnDelete.setText("-");
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbButtons.add(btnDelete);

        uiTable.setBackground(new java.awt.Color(240, 240, 240));
        uiTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Valores"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        uiTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        uiTable.setGridColor(new java.awt.Color(204, 204, 204));
        uiTable.setRowHeight(20);
        uiTable.setSelectionBackground(new java.awt.Color(233, 243, 253));
        uiTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        uiTable.setShowGrid(true);
        scrTable.setViewportView(uiTable);
        uiTable.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.WHITE));
        uiTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (uiTable.isEditing()) {
                    uiTable.getCellEditor().cancelCellEditing();
                }
            }
        });

        btnOk.setAction(actionOK);
        btnOk.setText("OK");

        btnCancelar.setAction(actionCancelar);
        btnCancelar.setText("Cancelar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tbButtons, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
            .addComponent(scrTable)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOk)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tbButtons, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(scrTable, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancelar))
                .addGap(5, 5, 5))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnOk;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JToolBar tbButtons;
    private org.jdesktop.swingx.JXTable uiTable;
    // End of variables declaration//GEN-END:variables
}
