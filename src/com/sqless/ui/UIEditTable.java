package com.sqless.ui;

import com.sqless.ui.enumeditor.SQLSetCellEditor;
import com.mysql.jdbc.Blob;
import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLSelectQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLPrimaryKey;
import com.sqless.sql.objects.SQLTable;
import com.sqless.ui.listeners.TableCellListener;
import com.sqless.utils.SQLUtils;
import com.sqless.utils.UIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
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
        this.table.loadColumns();
        initComponents();
        rows = new ArrayList<>();
        tablePK = this.table.getPrimaryKey();
        prepareTable();
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

        uiTable.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("TAB"), "ADD_ROW");
        uiTable.getActionMap().put("ADD_ROW", actionAddRowTab);
    }

    public void makeTableModel() {
        List<SQLColumn> columns = table.getColumns();
        Class<?>[] types = new Class<?>[columns.size()];
        String[] columnNames = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            SQLColumn column = columns.get(i);
            columnNames[i] = column.getName();
            if (column.isTimeBased()) {
                types[i] = Date.class;
            } else if (SQLUtils.dataTypeIsInteger(column.getDataType())) {
                //si es Integer, los campos Null van a aparecer como "0", es por eso que si es nullable, le asignamos la clase String a la columna
                types[i] = column.isNullable() ? String.class : Integer.class;
            } else if (SQLUtils.dataTypeIsDecimal(column.getDataType())) {
                types[i] = Double.class;
            } else {
                types[i] = String.class;
            }
        }
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return !columns.get(column).getDataType().equals("blob");
            }
        };
        uiTable.setModel(model);

        setUpCellEditors(types);

        SQLQuery querySelect = new SQLSelectQuery(table.getSelectStatement(200)) {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    Vector row = new Vector();
                    for (int i = 1; i <= table.getColumnCount(); i++) {
                        SQLColumn column = table.getColumn(i - 1);
                        if (column.getDataType().equals("blob")) {
                            Blob blob = (Blob) rs.getBlob(i);
                            row.add(blob != null ? "BLOB (" + String.format("%.2f", (float) blob.length() / 1024) + " KB)" : null);
                        } else if (column.getDataType().equals("year")) {
                            row.add(SQLUtils.parseSQLYear(rs.getString(i)));
                        } else if (column.isTimeBased()) {
                            row.add(rs.getDate(i));
                        } else if (SQLUtils.dataTypeIsInteger(column.getDataType())) {
                            row.add(column.isNullable() ? rs.getString(i) : rs.getInt(i));
                        } else if (SQLUtils.dataTypeIsDecimal(column.getDataType())) {
                            row.add(rs.getDouble(i));
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
                UIUtils.showErrorMessage("Edit table", "Hubo un error al traer los datos de la tabla "
                        + table.getName() + " desde la base de datos.\n" + errMessage, null);
            }

        };
        querySelect.exec();

        uiTable.packAll();
        UIUtils.enhanceTableColumns(uiTable, table);
    }

    private void setUpCellEditors(Class<?>[] columnTypes) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            SQLColumn sqlColumn = table.getColumn(i);
            TableColumn uiColumn = uiTable.getColumn(i);
            if (sqlColumn.isTimeBased()) {
                uiTable.getColumn(i).setCellEditor(new SQLDatePickerCellEditor(sqlColumn));
                ((SQLDatePickerCellEditor) uiColumn.getCellEditor()).setClickCountToStart(1);
            } else if (sqlColumn.getDataType().equals("enum")) {
                uiColumn.setCellEditor(new DefaultCellEditor(UIUtils.makeComboBoxForEnumColumn(sqlColumn)));
            } else if (sqlColumn.getDataType().equals("set")) {
                uiColumn.setCellEditor(new SQLSetCellEditor(SQLUtils.getEnumLikeValuesAsArray(sqlColumn.getEnumLikeValues())));
            } else {
                ((DefaultCellEditor) uiTable.getDefaultEditor(columnTypes[i])).setClickCountToStart(1);
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

    private TableCellRenderer sqlCellRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            SQLColumn sqlColumn = table.getColumn(column);
            JLabel component = (JLabel) super.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
            if (value != null) {
                if (sqlColumn.isTimeBased()) {
                    component.setText(sqlColumn.getDataType().equals("date") ? SQLUtils.MYSQL_DATE_FORMAT.format(value) : SQLUtils.MYSQL_DATETIME_FORMAT.format(value));
                }
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
                    String formattedOld = SQLUtils.convertDateToValidSQLDate(oldValue, editingColumn);
                    String formattedNew = SQLUtils.convertDateToValidSQLDate(newValue, editingColumn);
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
            }

        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        scrLog = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextPane();

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

        txtLog.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
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
    private JMenuItem menuItemSave;

    @Override
    public Component[] getToolbarComponents() {
        if (toolbarComponents == null) {
            btnAddRow = UIUtils.newToolbarBtn(actionAddRow, "Add a new row", UIUtils.icon(this, "ADD_ROW"));
            btnDeleteRows = UIUtils.newToolbarBtn(actionDeleteRow, "Delete the selected row(s)", UIUtils.icon(this, "DELETE_ROWS"));
            btnSave = UIUtils.newToolbarBtn(actionSaveChanges, "Commit all changes", UIUtils.icon(this, "SAVE"));
            btnRefresh = UIUtils.newToolbarBtn(null, "Reload the table to reflect external changes (F5)", UIUtils.icon(this, "REFRESH"));
            toolbarComponents = new Component[]{btnSave, UIUtils.newSeparator(), btnAddRow, btnDeleteRows, UIUtils.newSeparator(), btnRefresh};
        }
        return toolbarComponents;
    }

    @Override
    public JMenuItem[] getMenuItems() {
        if (menuItems == null) {
            menuItemSave = new JMenuItem("Save");
            menuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
            menuItemSave.addActionListener(actionSaveChanges);
            menuItems = new JMenuItem[]{menuItemSave};
        }
        return menuItems;
    }

    private ActionListener actionSaveChanges = e -> {
        if (uiTable.getCellEditor() != null) {
            uiTable.getCellEditor().stopCellEditing();
        }
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

    private Action actionAddRowTab = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedColumn = uiTable.getSelectedColumn();
            int selectedRow = uiTable.getSelectedRow();

            if (selectedRow == uiTable.getRowCount() - 1 && selectedColumn == uiTable.getColumnCount() - 1) {
                actionAddRow.actionPerformed(e);
                return;
            }

            if (uiTable.getCellEditor() != null) {
                uiTable.getCellEditor().stopCellEditing();
            }
            int futureColumn = selectedColumn + 1 == uiTable.getColumnCount() ? 0 : selectedColumn + 1;
            int futureRow = futureColumn == 0 ? selectedRow + 1 : selectedRow;
            uiTable.setColumnSelectionInterval(futureColumn, futureColumn);
            uiTable.setRowSelectionInterval(futureRow, futureRow);
        }
    };

    public void doAddRow() {
        ((DefaultTableModel) uiTable.getModel()).addRow(new Vector<>());
        rows.add(new SQLRow());
        uiTable.setColumnSelectionInterval(0, 0);
        uiTable.setRowSelectionInterval(uiTable.getRowCount() - 1, uiTable.getRowCount() - 1);
    }

    private Action actionAddRow = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (uiTable.getCellEditor() != null) {
                uiTable.getCellEditor().stopCellEditing();
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

    private ActionListener actionDeleteRow = e -> {
        if (uiTable.getCellEditor() != null) {
            uiTable.getCellEditor().stopCellEditing();
        }
        int[] selectedRows = uiTable.getSelectedRows();
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
            int newSelectedRow = lastDeleted == 0 ? 0 : lastDeleted - 1;
            uiTable.setRowSelectionInterval(newSelectedRow, newSelectedRow);
        }
    };

    @Override
    public String getTabTitle() {
        return "Editing " + table.getName();
    }

    @Override
    public String getIconsFolder() {
        return "ui_edittable";
    }

    private class SQLRow {

        private boolean isBrandNew;
        private Map<String, Object> columnsAndValues;
        private Map<String, Object> valuesForUpdate;
        private Object[] pkValues;
        private boolean queryResult;

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

        /**
         * Assigns a value for this row at the given column. If the column is a
         * PK, this method updates the array that holds the PK values with the
         * new value. This is important, because PK values are used in WHERE
         * conditions to update the column.
         *
         * @param column the index of the column to update.
         * @param value the value to insert.
         */
        public void setValue(String name, Object value) {
            SQLColumn updatedColumn = table.getColumn(name);
            if (updatedColumn.isPK()) { //have to update PK values with new values
                pkValues[tablePK.getIndexOfColumn(updatedColumn.getName())] = value;
            }
            columnsAndValues.replace(name, value);
        }

        public void setValueForUpdate(int column, Object value) {
            if (valuesForUpdate == null) {
                valuesForUpdate = new LinkedHashMap<>();
            }
            SQLColumn sqlColumn = table.getColumn(column);
            String colName = sqlColumn.getName();
            if (sqlColumn.isTimeBased()) {
                value = SQLUtils.convertDateToValidSQLDate(value, sqlColumn);
            }
            if (valuesForUpdate.containsKey(colName)) {
                valuesForUpdate.replace(colName, value);
            } else {
                valuesForUpdate.put(colName, value);
            }
        }

        public boolean isBrandNew() {
            return isBrandNew;
        }

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
         * Hace una operación de UPDATE en la base de datos. Ingresa el valor
         * nuevo a la columna dada.
         *
         * @param column el índice de la columna a actualizar.
         * @param value el nuevo valor.
         * @return {@code true} si el UPDATE fue exitoso. De lo contario, falso.
         */
        public boolean update(int row) {
            SQLQuery updateQuery = new SQLUpdateQuery(createUpdateStatement()) {
                @Override
                public void onSuccess(int updateCount) {
                    System.out.println(true);
//                    SQLColumn sqlColumn = table.getColumn(column);
//                    TextUtils.appendBold(txtLog, "[" + MiscUtils.TIME_FORMAT.format(new Date()) + "] ");
//                    TextUtils.appendToDoc(txtLog, "UPDATE: ", false);
//                    TextUtils.appendRed(txtLog, "'" + (sqlColumn.isTimeBased() ? SQLUtils.convertDateToValidSQLDate((Date) cellListener.getOldValue(), sqlColumn) : cellListener.getOldValue()) + "'");
//                    TextUtils.appendToDoc(txtLog, " -> ", false);
//                    TextUtils.appendRed(txtLog, "'" + (sqlColumn.isTimeBased() ? SQLUtils.convertDateToValidSQLDate((Date) value, sqlColumn) : value) + "'");
//                    TextUtils.appendToDoc(txtLog, " at column ", false);
//                    TextUtils.appendRed(txtLog, table.getColumn(column).getName() + "\n");
                    queryResult = true;
                    commitNewValues();
                }

                @Override
                public void onFailure(String errMessage) {
                    queryResult = false;
                    UIUtils.showErrorMessage("Update row", "Could not update row.\n" + errMessage, null);
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
            String stmt = "UPDATE `" + table.getName() + "` SET ";
            StringBuilder stmtBuilder = new StringBuilder();

            for (Map.Entry<String, Object> entry : valuesForUpdate.entrySet()) {
                String col = entry.getKey();
                Object value = entry.getValue();
                stmtBuilder.append("`").append(col).append("`").append("=").append("'").append(value).append("'").append(", ");
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
                Object defaultVal = sqlColumn.getDefaultVal();
                boolean valueWasNull = value == null;
                if (valueWasNull) {
                    //si el valor es nulo y la columna no tiene un valor default, SQLess hace lo posible para asignar un valor válido a la columna
                    if (defaultVal == null) {
                        if (sqlColumn.isTimeBased()) {
                            value = SQLUtils.convertDateToValidSQLDate(new Date(), sqlColumn);
                        } else if (sqlColumn.isStringBased()) {
                            value = "";
                        } else {
                            value = "0";
                        }
                    } else {
                        //si el valor es nulo y la columna tiene un valor default, usamos el valor default.
                        //a tener en cuenta: si la columna es de tiempo y el defaultVal empieza con Current, asumimos que es un llamado a la función "CURRENT_TIMESTAMP", etc
                        //y pasamos la fecha actual en formato String, esto nos permitirá pasar el valor sin problemas a continuación envuelto en ''
                        value = sqlColumn.isTimeBased() && defaultVal.toString().startsWith("CURRENT") ? SQLUtils.convertDateToValidSQLDate(new Date(), sqlColumn) : defaultVal;
                    }
                } else {
                    value = (sqlColumn.isTimeBased() ? SQLUtils.convertDateToValidSQLDate(value, sqlColumn) : value);
                }
                setValue(column.toString(), value);
                sbCols.append("`").append(column).append("`").append(",");
                sbValues.append("'").append(value).append("'").append(",");
            }
            sbCols.setLength(sbCols.length() - 1);
            sbCols.append(")");
            sbValues.setLength(sbValues.length() - 1);
            sbValues.append(")");
            return sbCols.append(" ").append(sbValues).toString();
        }

        public void refreshWithUi(int row) {
            int colCount = 0;
            for (Map.Entry<String, Object> entry : columnsAndValues.entrySet()) {
                SQLColumn column = table.getColumn(colCount);
                Object value = entry.getValue();
                if (column.isTimeBased()) {
                    value = SQLUtils.dateFromString(value.toString(), column);
                } else if (SQLUtils.dataTypeIsInteger(column.getDataType())) {
                    value = Integer.parseInt(value.toString());
                } else if (SQLUtils.dataTypeIsDecimal(column.getDataType())) {
                    value = Double.parseDouble(value.toString());
                }
                uiTable.setValueAt(value, row, colCount++);
            }
        }

        private String createDeleteStatement() {
            return "DELETE FROM `" + table.getName() + "` " + createWhereCondition();
        }

        public boolean addToDatabase(int row) {
            SQLQuery insertQuery = new SQLUpdateQuery(createInsertStatement()) {
                @Override
                public void onSuccess(int updateCount) {
                    isBrandNew = false;
                    queryResult = true;
                    refreshWithUi(row);
                }

                @Override
                public void onFailure(String errMessage) {
                    UIUtils.showErrorMessage("Insert row", "Could not insert row " + row + ".\n" + errMessage, null);
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
                    UIUtils.showErrorMessage("Delete row(s)", "Could not remove row " + row + ".\n" + errMessage, null);
                    queryResult = false;
                }
            };
            deleteQuery.exec();
            return queryResult;
        }

        public boolean isAllNull() {
            for (Map.Entry<String, Object> entry : columnsAndValues.entrySet()) {
                if (entry.getValue() != null) {
                    return false;
                }
            }
            return true;
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
                        if (sqlRow.isAllNull()) {
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
                        if (row.isAllNull()) {
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
