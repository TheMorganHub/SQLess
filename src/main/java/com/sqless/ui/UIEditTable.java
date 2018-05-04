package com.sqless.ui;

import com.sqless.ui.seteditor.SQLSetCellEditor;
import com.mysql.jdbc.Blob;
import com.sqless.queries.*;
import com.sqless.sql.objects.*;
import com.sqless.ui.fkcelleditor.FKCellEditor;
import com.sqless.ui.listeners.TableCellListener;
import com.sqless.utils.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.*;
import javax.swing.table.*;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class UIEditTable extends FrontPanel {

    private SQLTable table;
    private TableCellListener cellListener;
    private SQLPrimaryKey tablePK;
    private List<SQLRow> rows;

    public UIEditTable(JTabbedPane parentPane, SQLTable table) {
        super(parentPane);
        this.table = new SQLTable(table);
        initComponents();
        GenericWaitingDialog waitingDialog = new GenericWaitingDialog("Cargando...");
        waitingDialog.display(() -> {
            this.table.loadColumns();
            if (this.table.getColumns().isEmpty()) {
                setIntegrity(Integrity.CORRUPT);
            } else {
                rows = new ArrayList<>();
                tablePK = this.table.getPrimaryKey();
                prepareTable();
            }
        });
    }

    /**
     * Evalúa si la tabla dada en el constructor de esta clase está capacitada
     * para ser modificada por esta UI. Si la tabla no tiene una PK, la tabla no
     * podrá ser modificada por esta UI.
     *
     * @return
     */
    public boolean tableAllowsModifications() {
        return !tablePK.isEmpty();
    }

    @Override
    public void onCreate() {
        splitPane.setDividerLocation((int) (parentPane.getHeight() * 0.75));
    }

    public void prepareTable() {
        uiTable.addPropertyChangeListener(tableEditorPropertyChangeListener);
        makeTableModel();
        cellListener = new TableCellListener(uiTable, actionEditTable);
        uiTable.getSelectionModel().addListSelectionListener(tableSelectionListener);
    }

    public void loadKeyBindings() {
        uiTable.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("TAB"), "ADD_ROW");
        uiTable.getActionMap().put("ADD_ROW", actionAddRowTab);

        uiTable.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F5"), "REFRESH_TABLE");
        uiTable.getActionMap().put("REFRESH_TABLE", actionRefreshTable);

        uiTable.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("DELETE"), "DELETE_ROW");
        uiTable.getActionMap().put("DELETE_ROW", actionDeleteRow);
    }

    public void makeTableModel() {
        List<SQLColumn> columns = table.getColumns();
        if (columns.isEmpty()) {
            return;
        }
        String[] columnNames = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            SQLColumn column = columns.get(i);
            columnNames[i] = column.getName();
        }
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return !columns.get(column).getDataType().equals("blob");
            }
        };
        uiTable.setModel(model);

        setUpCellEditors();

        SQLQuery querySelect = new SQLSelectQuery(table.getSelectStatement(200)) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    Vector row = new Vector();
                    for (int i = 1; i <= table.getColumnCount(); i++) {
                        SQLColumn column = table.getColumn(i - 1);
                        if (column.getDataType().equals("blob")) {
                            row.add(DataTypeUtils.parseBlob((Blob) rs.getBlob(i)));
                        } else if (column.getDataType().equals("year")) {
                            row.add(DataTypeUtils.parseSQLYear(rs.getString(i)));
                        } else if (column.isTimeBased()) {
                            row.add(SQLUtils.dateFromString(rs.getString(i), column));
                        } else {
                            row.add(rs.getString(i));
                        }
                    }
                    model.addRow(row);
                    rows.add(new SQLRow(row));
                }
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Editar filas", "Hubo un error al traer los datos de la tabla "
                        + table.getName() + " desde la base de datos.\nEl servidor respondió con el mensaje:\n" + errMessage, null);
            }
        };
        querySelect.exec();

        UIUtils.enhanceTableColumns(uiTable, table);
    }

    private void setUpCellEditors() {
        for (int i = 0; i < table.getColumnCount(); i++) {
            SQLColumn sqlColumn = table.getColumn(i);
            TableColumn uiColumn = uiTable.getColumn(i);
            if (sqlColumn.isTimeBased()) {
                uiTable.getColumn(i).setCellEditor(new SQLDatePickerCellEditor(sqlColumn));
                ((SQLDatePickerCellEditor) uiColumn.getCellEditor()).setClickCountToStart(1);
            } else if (sqlColumn.getDataType().equals("enum")) {
                uiColumn.setCellEditor(new DefaultCellEditor(UIUtils.makeComboBoxForEnumColumn(sqlColumn)));
            } else if (sqlColumn.getDataType().equals("set")) {
                uiColumn.setCellEditor(new SQLSetCellEditor(SQLUtils.getEnumLikeValuesAsArray(sqlColumn.getEnumLikeValues(false))));
            } else if (sqlColumn.isFK()) {
                SQLForeignKey fkFromColumn = table.getForeignKeyFromColumn(sqlColumn);
                uiColumn.setCellEditor(new FKCellEditor(fkFromColumn));
            } else {
                uiColumn.setCellEditor(new StringCellEditor(new JTextField()));
                ((DefaultCellEditor) uiTable.getDefaultEditor(String.class)).setClickCountToStart(1);
            }

            uiColumn.setCellRenderer(sqlCellRenderer);
        }
    }

    public boolean tableHasNewRows() {
        for (SQLRow row : rows) {
            if (row.isBrandNew()) {
                return true;
            }
        }
        return false;
    }

    public boolean tableHasChanges() {
        for (SQLRow row : rows) {
            if (row.isBrandNew() || row.hasUncommittedChanges()) {
                return true;
            }
        }
        return false;
    }

    private TableCellRenderer sqlCellRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            SQLColumn sqlColumn = table.getColumn(column);
            JLabel component = (JLabel) super.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
            if (value != null) {
                if (sqlColumn.isTimeBased()) {
                    component.setText(DataTypeUtils.convertDateToValidSQLDate(value, sqlColumn));
                }
            } else {
                component.setText("<html><span style=\"color:gray\">Null</span></html>");
            }

            SQLRow sqlRow = rows.get(row);
            if (sqlRow.isBrandNew() || sqlRow.columnHasChanged(column)) {
                component.setFont(UIUtils.SEGOE_UI_FONT_BOLD);
            }
            return component;
        }
    };

    private PropertyChangeListener tableEditorPropertyChangeListener = (evt) -> {
        JXTable source = (JXTable) evt.getSource();
        if ("tableCellEditor".equals(evt.getPropertyName())) {
            if (source.isEditing()) {
                if (source.getCellEditor() instanceof DefaultCellEditor) {
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
        }
    };

    private Action actionEditTable = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int column = cellListener.getColumn();
            int row = cellListener.getRow();
            Object oldValue = cellListener.getOldValue();
            Object newValue = cellListener.getNewValue();
            SQLColumn editingColumn = table.getColumn(column);

            if (editingColumn.isTimeBased()) { //resuelve un bug raro que hace que el DatePickerCellEditor registre un evento de edit cuando la fecha no cambió - solo para columnas de fechas
                if (newValue != null) {
                    String formattedOld = DataTypeUtils.convertDateToValidSQLDate(oldValue, editingColumn);
                    String formattedNew = DataTypeUtils.convertDateToValidSQLDate(newValue, editingColumn);
                    if (formattedOld.equals(formattedNew)) {
                        return;
                    }
                }
            }

            SQLRow sqlRow = rows.get(row);
            if (newValue != null && !oldValue.equals(newValue)) {
                if (sqlRow.isBrandNew()) {
                    sqlRow.setValue(editingColumn.getName(), newValue);
                } else {
                    sqlRow.setValueForUpdate(column, newValue);
                }
                sqlRow.setIsUntouched(false);
            }

        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popTable = new javax.swing.JPopupMenu();
        menuItemDeleteRow = new javax.swing.JMenuItem();
        menuItemSetNull = new javax.swing.JMenuItem();
        menuItemSetEmpty = new javax.swing.JMenuItem();
        popLog = new javax.swing.JPopupMenu();
        menuItemClearLog = new javax.swing.JMenuItem();
        splitPane = new javax.swing.JSplitPane();
        UIUtils.flattenPane(splitPane);
        scrTable = new javax.swing.JScrollPane();
        uiTable = new JXTable() {
            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                int condition, boolean pressed) {
                if (!ks.equals(KeyStroke.getKeyStroke("control S"))) {
                    return super.processKeyBinding(ks, e, condition, pressed);
                }
                return false;
            }

        };
        uiTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (uiTable.isEditing()) {
                    uiTable.getCellEditor().cancelCellEditing();
                }
            }
        });
        uiTable.addMouseListener(UIUtils.mouseListenerWithPopUpMenuForJTable(popTable, uiTable));
        loadKeyBindings();
        scrLog = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextPane();

        menuItemDeleteRow.setAction(actionDeleteRow);
        menuItemDeleteRow.setText("Eliminar fila(s)");
        popTable.add(menuItemDeleteRow);
        popTable.addSeparator();

        menuItemSetNull.setAction(actionSetToNull);
        menuItemSetNull.setText("Poner en NULL");
        popTable.add(menuItemSetNull);

        menuItemSetEmpty.setAction(actionSetToEmpty);
        menuItemSetEmpty.setText("Cadena de texto vacía");
        popTable.add(menuItemSetEmpty);

        menuItemClearLog.setAction(actionClearLog);
        menuItemClearLog.setText("Borrar");
        popLog.add(menuItemClearLog);

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);

        uiTable.setBackground(new java.awt.Color(240, 240, 240));
        uiTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        uiTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        uiTable.setColumnSelectionAllowed(true);
        uiTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        uiTable.setGridColor(new java.awt.Color(204, 204, 204));
        uiTable.setRowHeight(20);
        uiTable.setSelectionBackground(new java.awt.Color(233, 243, 253));
        uiTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        uiTable.setShowGrid(true);
        uiTable.getTableHeader().setReorderingAllowed(false);
        scrTable.setViewportView(uiTable);
        uiTable.setSortable(false);
        uiTable.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.WHITE));

        splitPane.setLeftComponent(scrTable);

        txtLog.setEditable(false);
        txtLog.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        txtLog.setComponentPopupMenu(popLog);
        scrLog.setViewportView(txtLog);

        splitPane.setRightComponent(scrLog);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1023, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem menuItemClearLog;
    private javax.swing.JMenuItem menuItemDeleteRow;
    private javax.swing.JMenuItem menuItemSetEmpty;
    private javax.swing.JMenuItem menuItemSetNull;
    private javax.swing.JPopupMenu popLog;
    private javax.swing.JPopupMenu popTable;
    private javax.swing.JScrollPane scrLog;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTextPane txtLog;
    private org.jdesktop.swingx.JXTable uiTable;
    // End of variables declaration//GEN-END:variables
    private JButton btnSave;
    private JButton btnAddRow;
    private JButton btnDeleteRows;
    private JButton btnRefresh;
    private JButton btnTruncate;
    private JMenuItem menuItemSave;

    @Override
    public Component[] getToolbarComponents() {
        if (toolbarComponents == null) {
            btnAddRow = UIUtils.newToolbarBtn(actionAddRow, "Agregar una fila nueva", UIUtils.icon(this, "ADD_ROW"));
            btnDeleteRows = UIUtils.newToolbarBtn(actionDeleteRow, "Eliminar la(s) fila(s) seleccionada(s)", UIUtils.icon(this, "DELETE_ROWS"));
            btnSave = UIUtils.newToolbarBtn(actionSaveChanges, "Confirmar todos los cambios", UIUtils.icon(this, "SAVE"));
            btnRefresh = UIUtils.newToolbarBtn(actionRefreshTable, "Cargar la tabla nuevamenete para reflejar cambios externos (F5)", UIUtils.icon(this, "REFRESH"));
            btnTruncate = UIUtils.newToolbarBtn(actionTruncateTable, "Eliminar todas las filas en esta tabla (TRUNCATE)", UIUtils.icon(this, "TRUNCATE"));
            toolbarComponents = new Component[]{btnSave, UIUtils.newSeparator(), btnAddRow, btnDeleteRows, UIUtils.newSeparator(), btnRefresh, UIUtils.newSeparator(), btnTruncate};
        }
        return toolbarComponents;
    }

    @Override
    public JMenuItem[] getMenuItems() {
        if (menuItems == null) {
            menuItemSave = new JMenuItem("Guardar");
            menuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
            menuItemSave.addActionListener(actionSaveChanges);
            menuItems = new JMenuItem[]{menuItemSave};
        }
        return menuItems;
    }

    private ActionListener actionSaveChanges = e -> {
        UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.STOP);

        for (int i = 0; i < rows.size(); i++) {
            SQLRow row = rows.get(i);
            boolean success;
            if (row.isBrandNew()) {
                success = row.addToDatabase(i);
            } else if (row.hasUncommittedChanges()) {
                success = row.update(i);
            } else {
                continue;
            }
            if (!success) {
                break;
            }
        }
        uiTable.repaint();
    };

    private ActionListener actionTruncateTable = e -> {
        if (!UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.CANCEL)) {
            return;
        }

        if (uiTable.getRowCount() == 0) {
            UIUtils.showErrorMessage("Truncar tabla", "La tabla no tiene filas.", UIClient.getInstance());
            return;
        }

        int opt = UIUtils.showConfirmationMessage("Truncar la tabla", "¿Estás seguro que deseas eliminar permanentemente las filas de esta tabla?", UIClient.getInstance());
        if (opt == 0) {
            SQLQuery truncateTable = new SQLUpdateQuery(table.getTruncateStatement()) {
                @Override
                public void onSuccess(int updateCount) {
                    ((DefaultTableModel) uiTable.getModel()).setRowCount(0);
                    rows.clear();
                }

                @Override
                public void onFailure(String errMessage) {
                    UIUtils.showErrorMessage("Error", "No se pudo truncar la tabla.\nEl servidor respondió con mensaje: " + errMessage, UIClient.getInstance());
                }
            };
            truncateTable.exec();
        }
    };

    private Action actionAddRowTab = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedColumn = uiTable.getSelectedColumn();
            int selectedRow = uiTable.getSelectedRow();

            if (selectedColumn == -1 || selectedRow == -1) {
                return;
            }

            if (selectedRow == uiTable.getRowCount() - 1 && selectedColumn == uiTable.getColumnCount() - 1) {
                actionAddRow.actionPerformed(e);
                return;
            }

            if (!UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.STOP)) {
                return;
            }
            int futureColumn = selectedColumn + 1 == uiTable.getColumnCount() ? 0 : selectedColumn + 1;
            int futureRow = futureColumn == 0 ? selectedRow + 1 : selectedRow;
            uiTable.setColumnSelectionInterval(futureColumn, futureColumn);
            uiTable.setRowSelectionInterval(futureRow, futureRow);
        }
    };

    /**
     * Agrega una fila a la tabla. Este método inicializa un {@code Vector}
     * vacío y lo agrega como fila al modelo de la tabla. También agrega un
     * nuevo objeto {@code SQLRow} a la lista que contiene todas las filas.
     * <br><br>
     * Nota: las filas agregadas mediante este método aún no son committeadas en
     * la base de datos.
     *
     * @see SQLRow#addToDatabase(int)
     */
    public void doAddRow() {
        Vector data = createVectorWithDefaults();
        if (data == null) {
            return;
        }
        ((DefaultTableModel) uiTable.getModel()).addRow(data);
        SQLRow newRow = new SQLRow(data);
        newRow.setBrandNew(true);
        newRow.setIsUntouched(true);
        rows.add(newRow);
        uiTable.setColumnSelectionInterval(0, 0);
        uiTable.setRowSelectionInterval(uiTable.getRowCount() - 1, uiTable.getRowCount() - 1);
        UIUtils.scrollToBottom(scrTable);
    }

    public Vector createVectorWithDefaults() {
        Vector vect = new Vector();
        int autoIncrement = SQLUtils.getTableAutoIncrement(table);
        if (autoIncrement == -1) {
            return null;
        }
        for (SQLColumn column : table.getColumns()) {
            Object defaultVal = column.getDefaultVal();
            Object value = defaultVal;
            if (defaultVal != null) {
                if (column.isTimeBased()) {
                    value = defaultVal.toString().startsWith("CURRENT") ? DataTypeUtils.convertDateToValidSQLDate(new Date(), column) : defaultVal;
                }
            }
            if (column.isAutoincrement()) {
                value = autoIncrement;
            }
            vect.add(value);
        }
        return vect;
    }

    private Action actionRefreshTable = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.STOP);
            if (tableHasChanges()) {
                int opt = UIUtils.showYesNoOptionDialog("Actualizar la tabla", "Se han encontrado cambios no guardados en la tabla.\nSi refrescas ahora esos cambios se perderán. ¿Deseas continuar?",
                        JOptionPane.QUESTION_MESSAGE, false, null);
                if (opt == 0) {
                    doRefresh();
                }
            } else {
                doRefresh();
            }
        }

        void doRefresh() {
            rows.clear();
            table = new SQLTable(table);
            table.loadColumns();
            tablePK = table.getPrimaryKey();
            makeTableModel();
        }
    };

    private Action actionSetToEmpty = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SQLUtils.updateGroup(uiTable, table, rows, "", column -> {
                return column.getDataType().equals("varchar");
            });
        }
    };

    private Action actionSetToNull = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SQLUtils.updateGroup(uiTable, table, rows, null, column -> {
                return column.isNullable();
            });
        }
    };

    private Action actionClearLog = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            txtLog.setText("");
        }
    };

    private Action actionAddRow = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.STOP)) {
                return;
            }

            if (!rows.isEmpty()) {
                SQLRow lastRow = rows.get(rows.size() - 1);
                if (lastRow.isBrandNew()) {
                    if (lastRow.addToDatabase(rows.size() - 1)) {
                        doAddRow();
                    }
                } else if (lastRow.hasUncommittedChanges()) {
                    if (lastRow.update(rows.size() - 1)) {
                        doAddRow();
                    }
                } else {
                    doAddRow();
                }
            } else {
                doAddRow();
            }
        }
    };

    private Action actionDeleteRow = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UIUtils.interruptCellEdit(uiTable, UIUtils.CellEdit.CANCEL);

            int[] selectedRows = uiTable.getSelectedRows();
            if (selectedRows.length == 0) {
                return;
            }

            if (!rows.get(selectedRows[0]).isBrandNew()) {
                int opt = UIUtils.showConfirmationMessage("Eliminar filas", "¿Estás seguro que deseas eliminar " + selectedRows.length + " fila(s) permanentemente?", null);
                if (opt == 1) {
                    return;
                }
            }

            int lastDeleted = -1;
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int rowToDelete = selectedRows[i];
                SQLRow row = rows.get(rowToDelete);
                if (row.delete(rowToDelete)) {
                    lastDeleted = rowToDelete;
                } else {
                    break;
                }
            }

            if (uiTable.getRowCount() > 0) {
                int newSelectedRow = lastDeleted >= uiTable.getRowCount() ? lastDeleted - 1 : lastDeleted;
                uiTable.setRowSelectionInterval(newSelectedRow, newSelectedRow);
            }
        }
    };

    @Override
    public String getTabTitle() {
        return "Editando " + table.getName();
    }

    @Override
    public String getIconsFolder() {
        return "ui_edittable";
    }

    public class SQLRow {

        private boolean isBrandNew;
        private Map<String, Object> columnsAndValues;
        private Map<String, Object> valuesForUpdate;
        private Object[] pkValues;
        private boolean queryResult;
        private boolean isUntouched;

        public SQLRow(Vector rowData) {
            columnsAndValues = new LinkedHashMap<>();
            pkValues = new Object[tablePK.getSize()];
            List<SQLColumn> columns = table.getColumns();
            int pkValueCount = 0;
            for (int i = 0; i < columns.size(); i++) {
                SQLColumn column = columns.get(i);
                Object value = rowData != null ? rowData.get(i) : null;
                columnsAndValues.put(column.getName(), value);
                if (column.isPK()) {
                    pkValues[pkValueCount++] = value;
                }
            }
            isBrandNew = rowData == null;
        }

        public SQLRow() {
            this(null);
        }

        public boolean isUntouched() {
            return isUntouched;
        }

        public void setIsUntouched(boolean flag) {
            this.isUntouched = flag;
        }

        /**
         * Assigns a value for this row at the given column. If the column is a
         * PK, this method updates the array that holds the PK values with the
         * new value. This is important, because PK values are used in WHERE
         * conditions to update the column.
         *
         * @param columnName the index of the column to update.
         * @param value the value to insert.
         */
        public void setValue(String columnName, Object value) {
            SQLColumn updatedColumn = table.getColumn(columnName);
            if (updatedColumn.isPK()) { //have to update PK values with new values
                pkValues[tablePK.getIndexOfColumn(updatedColumn.getName())] = value;
            }
            columnsAndValues.replace(columnName, value);
        }

        public void setValueForUpdate(int column, Object value) {
            if (valuesForUpdate == null) {
                valuesForUpdate = new LinkedHashMap<>();
            }
            SQLColumn sqlColumn = table.getColumn(column);
            String colName = sqlColumn.getName();
            if (sqlColumn.isTimeBased()) {
                value = DataTypeUtils.convertDateToValidSQLDate(value, sqlColumn);
            }
            if (valuesForUpdate.containsKey(colName)) {
                valuesForUpdate.replace(colName, value);
            } else {
                valuesForUpdate.put(colName, value);
            }
        }

        public void setValueForUpdate(SQLColumn column, Object value) {
            setValueForUpdate(column.getOrdinalPosition() - 1, value);
        }

        public Object getValue(String colName) {
            return columnsAndValues.get(colName);
        }

        public boolean isBrandNew() {
            return isBrandNew;
        }

        public void setBrandNew(boolean flag) {
            this.isBrandNew = flag;
        }

        /**
         * Evalúa si una fila tiene cambios pendientes. Para filas nuevas, este
         * método va a retornar {@code false} siempre.
         *
         * @return {@code true} si el {@code HashMap} de valores para update no
         * es nulo y no está vacío.
         */
        public boolean hasUncommittedChanges() {
            return !isBrandNew() && valuesForUpdate != null && !valuesForUpdate.isEmpty();
        }

        public boolean columnHasChanged(int column) {
            return valuesForUpdate != null && !valuesForUpdate.isEmpty() && valuesForUpdate.containsKey(table.getColumn(column).getName());
        }

        /**
         * Transfers new values from {@code valuesForUpdate} to
         * {@code columnsAndValues} by calling
         * {@link #setValue(java.lang.String, java.lang.Object)} and then
         * empties {@code valuesForUpdate}.
         */
        private void commitNewValues() {
            if (valuesForUpdate != null && !valuesForUpdate.isEmpty()) {
                for (Map.Entry<String, Object> entry : valuesForUpdate.entrySet()) {
                    String colName = entry.getKey();
                    Object value = entry.getValue();
                    setValue(colName, value);
                }
            }
            valuesForUpdate.clear();
        }

        /**
         * Hace una operación de UPDATE en la base de datos. Es decir, todos los
         * valores pendientes en {@code valuesForUpdate} serán puestos en la
         * base de datos.
         *
         * @param row el numero de fila en la tabla visual
         * @return {@code true} si el UPDATE fue exitoso. De lo contario, falso.
         */
        public boolean update(int row) {
            SQLQuery updateQuery = new SQLUpdateQuery(createUpdateStatement()) {
                @Override
                public void onSuccess(int updateCount) {
                    for (Map.Entry<String, Object> entry : valuesForUpdate.entrySet()) {
                        String col = entry.getKey();
                        SQLColumn sqlColumn = table.getColumn(col);
                        Object value = entry.getValue();
                        Object oldValue = sqlColumn.isTimeBased() ? DataTypeUtils.convertDateToValidSQLDate(columnsAndValues.get(col), sqlColumn) : columnsAndValues.get(col);
                        DocStyler.of(txtLog).append("[" + MiscUtils.TIME_FORMAT.format(new Date()) + "] ", DocStyler.FontStyle.BOLD)
                                .append("UPDATE: ", DocStyler.FontStyle.NORMAL)
                                .append("'" + oldValue + "'", Color.RED).append(" -> ", Color.BLACK).append("'" + value + "'", Color.RED)
                                .append(" en columna ", Color.BLACK).append(col + "\n", Color.RED);
                    }
                    queryResult = true;
                    commitNewValues();
                }

                @Override
                public void onFailure(String errMessage) {
                    queryResult = false;
                    UIUtils.showErrorMessage("Actualizar fila", "No se pudo actualizar fila.\nEl servidor respondió con mensaje: " + errMessage, null);
                    System.out.println(getSql());
                    if (uiTable.getCellEditor() != null) {
                        uiTable.getCellEditor().cancelCellEditing();
                    }
                    uiTable.setRowSelectionInterval(row, row);
                }
            };
            updateQuery.exec();
            return queryResult;
        }

        public String createUpdateStatement() {
            if (valuesForUpdate == null) {
                return null;
            }
            String stmt = "UPDATE `" + table.getName() + "` SET ";
            StringBuilder stmtBuilder = new StringBuilder();

            for (Map.Entry<String, Object> entry : valuesForUpdate.entrySet()) {
                String col = entry.getKey();
                Object value = entry.getValue();
                stmtBuilder.append("`").append(col).append("`").append("=").append(value != null ? "'" : "").append(value).append(value != null ? "'" : "").append(", ");
            }
            stmtBuilder.setLength(stmtBuilder.length() - 2); //remover coma
            stmtBuilder.append(" ").append(createWhereCondition());
            return stmt.concat(stmtBuilder.toString());
        }

        private String createWhereCondition() {
            StringBuilder sb = new StringBuilder("WHERE ");
            List<SQLColumn> pkColumns = tablePK.getPkColumns();
            for (int i = 0; i < pkColumns.size(); i++) {
                sb.append("(").append("`").append(pkColumns.get(i).getName()).append("`").append("=").append("'").append(pkValues[i]).append("'").append(")");
                if (i < pkColumns.size() - 1) {
                    sb.append(" AND ");
                }
            }
            return sb.toString() + " LIMIT 1";
        }

        private String createInsertStatement() {
            StringBuilder sbCols = new StringBuilder("INSERT INTO `").append(table.getName()).append("` ").append("(");
            StringBuilder sbValues = new StringBuilder("VALUES(");
            int colCount = 0;
            for (Map.Entry<String, Object> entry : columnsAndValues.entrySet()) {
                Object column = entry.getKey();
                Object value = entry.getValue();
                SQLColumn sqlColumn = table.getColumn(colCount++);
                Object formattedValue = sqlColumn.formatUserValue(value);
                setValue(column.toString(), formattedValue);
                sbCols.append("`").append(column).append("`").append(",");
                sbValues.append(formattedValue != null ? "'" : "").append(formattedValue).append(formattedValue != null ? "'" : "").append(",");
            }
            sbCols.setLength(sbCols.length() - 1);
            sbCols.append(")");
            sbValues.setLength(sbValues.length() - 1);
            sbValues.append(")");
            return sbCols.append(" ").append(sbValues).toString();
        }

        private String createDeleteStatement() {
            return "DELETE FROM `" + table.getName() + "` " + createWhereCondition();
        }

        public void refreshWithUi(int row) {
            int colCount = 0;
            for (Map.Entry<String, Object> entry : columnsAndValues.entrySet()) {
                SQLColumn column = table.getColumn(colCount);
                Object value = entry.getValue();
                if (column.isTimeBased()) {
                    if (value != null && value instanceof String) {
                        value = SQLUtils.dateFromString(value.toString(), column);
                    }
                } else if (DataTypeUtils.dataTypeIsInteger(column.getDataType())) {
                    value = value != null ? Integer.parseInt(value.toString()) : null;
                } else if (DataTypeUtils.dataTypeIsDecimal(column.getDataType())) {
                    value = value != null ? Double.parseDouble(value.toString()) : null;
                }

                uiTable.setValueAt(value, row, colCount++);
            }
        }

        public void discardUpdate() {
            if (valuesForUpdate != null) {
                valuesForUpdate.clear();
            }
        }

        public boolean addToDatabase(int row) {
            SQLQuery insertQuery = new SQLUpdateQuery(createInsertStatement()) {
                @Override
                public void onSuccess(int updateCount) {
                    isBrandNew = false;
                    queryResult = true;
                    refreshWithUi(row);
                    DocStyler.of(txtLog).append("[" + MiscUtils.TIME_FORMAT.format(new Date()) + "] ", DocStyler.FontStyle.BOLD)
                            .append("INSERT: ", DocStyler.FontStyle.NORMAL).append("PK -> ").append(Arrays.toString(pkValues) + "\n", Color.RED);
                }

                @Override
                public void onFailure(String errMessage) {
                    UIUtils.showErrorMessage("Insertar fila", "No se pudo insertar fila " + row + ".\nEl servidor respondió con mensaje: " + errMessage, null);
                    queryResult = false;
                    if (uiTable.getCellEditor() != null) {
                        uiTable.getCellEditor().cancelCellEditing();
                    }
                    System.out.println("Failure: " + getSql());
                    uiTable.setRowSelectionInterval(row, row);
                }
            };
            insertQuery.exec();
            return queryResult;
        }

        public boolean delete(int row) {
            if (isBrandNew()) {
                ((DefaultTableModel) uiTable.getModel()).removeRow(row);
                rows.remove(SQLRow.this);
                return true;
            }

            SQLQuery deleteQuery = new SQLUpdateQuery(createDeleteStatement()) {
                @Override
                public void onSuccess(int updateCount) {
                    rows.remove(SQLRow.this);
                    ((DefaultTableModel) uiTable.getModel()).removeRow(row);
                    queryResult = true;
                }

                @Override
                public void onFailure(String errMessage) {
                    System.out.println(getSql());
                    UIUtils.showErrorMessage("Eliminar fila(s)", "No se pudo eliminar fila " + row + ".\nEl servidor respondió con mensaje: " + errMessage, null);
                    queryResult = false;
                }
            };
            deleteQuery.exec();
            return queryResult;
        }

        @Override
        public String toString() {
            return columnsAndValues.toString();
        }
    }

    private ListSelectionListener tableSelectionListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            handleSelectionEvent(e);
        }

        protected void handleSelectionEvent(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            int[] selectedRows = uiTable.getSelectedRows();

            /*El comportamiento demostrado abajo varía dependiendo el tipo de selección, es decir, múltiple o simple. 
            Si la selección es múltiple, no se entrará en el comportamiento de selección simple y vice versa.*/
            if (selectedRows.length > 1) {
//            si hay una selección múltiple, se buscan filas nuevas o con cambios en toda esa selección. Al encontrarse
//            filas con cambios o nuevas, se las tratará de commitear a la base de datos.
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int selectedRow = selectedRows[i];
                    SQLRow sqlRow = rows.get(selectedRow);
                    if (sqlRow.isBrandNew()) {
                        if (sqlRow.isUntouched()) {
                            rows.remove(selectedRow);
                            ((DefaultTableModel) uiTable.getModel()).removeRow(selectedRow);
                            continue;
                        }
                        sqlRow.addToDatabase(selectedRow);
                    } else if (sqlRow.hasUncommittedChanges()) {
                        sqlRow.update(selectedRow);
                    }
                }
                return;
            }

            if (firstIndex != lastIndex) {
//            Si el usuario va cambiando la selección de filas de uno en uno, se va a chequear si la fila con la selección anterior tiene cambios pendientes o es nueva
//            en el caso de que tenga cambios pendientes o sea nueva, se hará el commit en la base de datos
                int previousSelected = uiTable.getSelectedRow() == firstIndex ? lastIndex : firstIndex;
                if (previousSelected < rows.size()) {
                    SQLRow row = rows.get(previousSelected);
                    if (row.isBrandNew()) {
                        if (row.isUntouched()) {
                            rows.remove(row);
                            ((DefaultTableModel) uiTable.getModel()).removeRow(previousSelected);
                        } else {
                            row.addToDatabase(previousSelected);
                        }
                    } else if (row.hasUncommittedChanges()) {
                        row.update(previousSelected);
                    }
                }

            }
        }
    };

}
