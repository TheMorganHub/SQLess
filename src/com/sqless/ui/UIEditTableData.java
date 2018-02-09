package com.sqless.ui;

import com.sqless.utils.UIUtils;
import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.*;
import javax.swing.table.*;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.ui.listeners.TableCellListener;
import com.sqless.utils.TextUtils;
import com.sqless.settings.UserPreferencesLoader;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.utils.MiscUtils;

public class UIEditTableData extends javax.swing.JDialog {

    private SQLTable sqlTable;
    /**
     * A List of Lists representing a table.
     */
    private List<List<Object>> rows;
    private TableCellListener cellListener;
    /**
     * The rows allowed to be editable.
     */
    private List<Integer> editableRows;
    private UserPreferencesLoader userPreferences;
    /**
     * The rows that haven't been committed to DB yet. These aren't classed as
     * editable yet.
     */
    private List<Integer> uncommittedRows;

    public UIEditTableData(SQLTable sqlTable) {
        super(UIClient.getInstance(), true);
        initComponents();
        this.sqlTable = sqlTable;
        this.userPreferences = UserPreferencesLoader.getInstance();
        setTitle("Editing table " + sqlTable.getName());
        setLocationRelativeTo(getParent());
        sqlTable.loadColumns();
        prepareUI();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popTable = new javax.swing.JPopupMenu();
        menuItemRemoveRow = new javax.swing.JMenuItem();
        popLogger = new javax.swing.JPopupMenu();
        menuClearLogger = new javax.swing.JMenuItem();
        pnlContainer = new javax.swing.JPanel();
        barTop = new javax.swing.JToolBar();
        btnAddRow = new javax.swing.JButton();
        btnCommit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        topSeparator = new javax.swing.JToolBar.Separator();
        btnTruncateTable = new javax.swing.JButton();
        pnlTop2 = new javax.swing.JPanel();
        iLblRowsToDisplay = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        iLblVisibleRows = new javax.swing.JLabel();
        txtAmountOfRowsToDisplay = new javax.swing.JTextField();
        lblVisibleRows = new javax.swing.JLabel();
        comboOrderBy = new javax.swing.JComboBox<>();
        iLblOrderBy = new javax.swing.JLabel();
        splitContainer = new javax.swing.JSplitPane();
        scrTable = new javax.swing.JScrollPane();
        uiTable = uiTable = new JXTable() {
            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new MorganTableHeader(columnModel);
            }
        };
        scrLog = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextPane();

        menuItemRemoveRow.setText("Remove this row");
        menuItemRemoveRow.setToolTipText("");
        menuItemRemoveRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveRowActionPerformed(evt);
            }
        });
        popTable.add(menuItemRemoveRow);

        menuClearLogger.setText("Clear log");
        menuClearLogger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuClearLoggerActionPerformed(evt);
            }
        });
        popLogger.add(menuClearLogger);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        org.jdesktop.swingx.border.DropShadowBorder dropShadowBorder1 = new org.jdesktop.swingx.border.DropShadowBorder();
        dropShadowBorder1.setShadowSize(3);
        dropShadowBorder1.setShowRightShadow(false);
        barTop.setBorder(dropShadowBorder1);
        barTop.setFloatable(false);
        barTop.setRollover(true);

        btnAddRow.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnAddRow.setText("Add row");
        btnAddRow.setToolTipText("Adds a new row");
        btnAddRow.setEnabled(false);
        btnAddRow.setFocusable(false);
        btnAddRow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddRow.setMargin(new java.awt.Insets(2, 5, 2, 5));
        btnAddRow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRowActionPerformed(evt);
            }
        });
        barTop.add(btnAddRow);

        btnCommit.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnCommit.setText("Commit");
        btnCommit.setToolTipText("Commits all unsaved rows to the database");
        btnCommit.setEnabled(false);
        btnCommit.setFocusable(false);
        btnCommit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCommit.setMargin(new java.awt.Insets(2, 5, 2, 5));
        btnCommit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommitActionPerformed(evt);
            }
        });
        barTop.add(btnCommit);

        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnDelete.setText("Delete row");
        btnDelete.setToolTipText("Deletes a specific row");
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setMargin(new java.awt.Insets(2, 5, 2, 5));
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        barTop.add(btnDelete);
        barTop.add(topSeparator);

        btnTruncateTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnTruncateTable.setText("Truncate");
        btnTruncateTable.setToolTipText("Deletes all the data in this table");
        btnTruncateTable.setFocusable(false);
        btnTruncateTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTruncateTable.setMargin(new java.awt.Insets(2, 5, 2, 5));
        btnTruncateTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTruncateTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTruncateTableActionPerformed(evt);
            }
        });
        barTop.add(btnTruncateTable);

        org.jdesktop.swingx.border.DropShadowBorder dropShadowBorder2 = new org.jdesktop.swingx.border.DropShadowBorder();
        dropShadowBorder2.setShadowSize(3);
        dropShadowBorder2.setShowRightShadow(false);
        dropShadowBorder2.setShowTopShadow(true);
        pnlTop2.setBorder(dropShadowBorder2);

        iLblRowsToDisplay.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        iLblRowsToDisplay.setText("Rows to display:");

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        iLblVisibleRows.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        iLblVisibleRows.setText("Visible rows:");

        txtAmountOfRowsToDisplay.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        lblVisibleRows.setText("0");

        comboOrderBy.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        comboOrderBy.setFocusable(false);

        iLblOrderBy.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        iLblOrderBy.setText("Order by:");

        javax.swing.GroupLayout pnlTop2Layout = new javax.swing.GroupLayout(pnlTop2);
        pnlTop2.setLayout(pnlTop2Layout);
        pnlTop2Layout.setHorizontalGroup(
            pnlTop2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTop2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iLblVisibleRows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVisibleRows, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 308, Short.MAX_VALUE)
                .addComponent(iLblOrderBy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboOrderBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(iLblRowsToDisplay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAmountOfRowsToDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addContainerGap())
        );
        pnlTop2Layout.setVerticalGroup(
            pnlTop2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTop2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(pnlTop2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblVisibleRows)
                    .addComponent(iLblRowsToDisplay)
                    .addComponent(txtAmountOfRowsToDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh)
                    .addComponent(lblVisibleRows)
                    .addComponent(comboOrderBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iLblOrderBy))
                .addGap(1, 1, 1))
        );

        splitContainer.setDividerLocation(480);
        splitContainer.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitContainer.setContinuousLayout(true);

        uiTable.setBackground(new java.awt.Color(240, 240, 240));
        uiTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        uiTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        uiTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        uiTable.setGridColor(new java.awt.Color(204, 204, 204));
        uiTable.setRowHeight(20);
        uiTable.setSelectionBackground(new java.awt.Color(183, 219, 255));
        uiTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        uiTable.setShowGrid(true);
        uiTable.setSortable(false);
        uiTable.setSurrendersFocusOnKeystroke(true);
        uiTable.getTableHeader().setReorderingAllowed(false);
        uiTable.addHighlighter(HighlighterFactory.createAlternateStriping(new Color(255, 255, 255), new Color(0xEDEDED)));
        uiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        uiTable.setDefaultRenderer(Object.class, new CellRenderer());
        HighlightPredicate myPredicate = new HighlightPredicate() {
            @Override
            public boolean isHighlighted(Component component, org.jdesktop.swingx.decorator.ComponentAdapter adapter) {
                return rowIsUncommitted(adapter.row);
            }
        };
        ColorHighlighter highlighter = new ColorHighlighter(myPredicate, new Color(0xA82321), Color.WHITE);
        uiTable.addHighlighter(highlighter);
        scrTable.setViewportView(uiTable);

        splitContainer.setLeftComponent(scrTable);

        txtLog.setEditable(false);
        txtLog.setComponentPopupMenu(popLogger);
        scrLog.setViewportView(txtLog);

        splitContainer.setRightComponent(scrLog);

        UIUtils.flattenPane(splitContainer);

        javax.swing.GroupLayout pnlContainerLayout = new javax.swing.GroupLayout(pnlContainer);
        pnlContainer.setLayout(pnlContainerLayout);
        pnlContainerLayout.setHorizontalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTop2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitContainer)
                .addContainerGap())
        );
        pnlContainerLayout.setVerticalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addComponent(barTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(pnlTop2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void prepareUI() {
        int rowsToDisplay = Integer.parseInt(userPreferences.
                getProperty("Editor.DisplayRows"));
        createTable(rowsToDisplay, 1);
        cellListener = new TableCellListener(uiTable, new ActionEditCell());
        uiTable.addMouseListener(new TableMouseListener());
        txtAmountOfRowsToDisplay.setText("" + rowsToDisplay);
    }

    public void loadColumnSorter() {
        if (comboOrderBy.getItemCount() == 0) {
            DefaultTableModel model = (DefaultTableModel) uiTable.getModel();
            for (int i = 0; i < model.getColumnCount(); i++) {
                comboOrderBy.addItem(model.getColumnName(i));
            }
            comboOrderBy.addItemListener(new ColumnSortListener());
        }
    }

    public void createTable(int rowsToDisplay, int orderBy) {
        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<DefaultTableModel, Void>() {

            private int rowCount = 0;
            DefaultTableModel model;

            @Override
            protected DefaultTableModel doInBackground() {
                try (Statement stmt = SQLConnectionManager.getInstance().getConnection().createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM "
                            + sqlTable.getName(true) + " ORDER BY " + orderBy + " LIMIT " + rowsToDisplay);
                    ResultSetMetaData rsmd = rs.getMetaData();

                    Vector<Object> columnNames = new Vector<>();
                    for (int i = 0; i < rsmd.getColumnCount(); i++) {
                        columnNames.add(rsmd.getColumnName(i + 1));
                    }

                    Vector<Vector<Object>> data = new Vector<>();
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        for (int i = 0; i < rsmd.getColumnCount(); i++) {
                            row.add(rs.getObject(i + 1));
                        }
                        data.add(row);
                    }
                    model = new DefaultTableModel(data, columnNames);
                    model.addTableModelListener(new RowActionListener());

                    rows = new ArrayList<>();
                    editableRows = new ArrayList<>();
                    for (Vector<Object> row : data) {
                        List<Object> values = new ArrayList<>();
                        for (Object object : row) {
                            values.add(object);
                        }
                        editableRows.add(rowCount++);
                        rows.add(values);
                    }
                } catch (SQLException ex) {
                    UIUtils.showErrorMessage("Error", "Could not load table data.", getParent());
                    System.err.println(ex.getMessage());
                }

                return model;
            }

            @Override
            protected void done() {
                try {
                    uiTable.setModel(get());
                    UIUtils.packAndSetMinWidths(uiTable, 100);
                    uiTable.packAll();
                    btnAddRow.setEnabled(rowCount < 100);
                    lblVisibleRows.setText("" + rowCount);
                    loadColumnSorter();
                } catch (InterruptedException | java.util.concurrent.ExecutionException ex) {
                    UIUtils.showErrorMessage("Error", "Could not load table data.", getParent());
                    System.err.println(ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Checks whether a row has any duplicates within the sqlTable.
     * <p>
     * <b>Note:</b> This method will automatically return {@code true} if the
     * {@code SQLTable} being edited has a Primary key.</p>
     *
     * @param rowNum The row to check.
     * @return {@code true} if the row is unique. A row is unique if no other
     * row has its same values in the same order.
     */
    public boolean rowIsUnique(int rowNum) {
        if (sqlTable.hasPK()) {
            return true;
        }
        String sql = "SELECT COUNT(*) FROM " + sqlTable.getName(true) + "\nWHERE "
                + createWhereStatement(rowNum);
        int occurrences = 0;
        try (Statement stmt = SQLConnectionManager.getInstance().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            occurrences = rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            UIUtils.showErrorMessage("Error", "There has been an error whilst trying to find occurrences "
                    + "for row " + rowNum, getParent());
            System.err.println(ex.getMessage());
        }
        return occurrences == 1;
    }

    /**
     * Formats a specified value according to the data of the column that holds
     * it. E.g: a column that holds nvarchar values will be wrapped with ' ';
     *
     * @param column a {@code SQLColumn} that contains the data type of this
     * value.
     * @param value the value to format.
     * @return a {@code String} formatted according to its data type.
     */
    public String formatValue(SQLColumn column, Object value) {
        if (value == null) {
            return "";
        }
        return column.isStringBased() ? "'" + value + "'" : "" + value;
    }

    /**
     * Creates a SQL {@code WHERE} statement using the column names of the
     * sqlTable and values at {@code rowNum}.
     * <p>
     * Example: picture a sqlTable 'Person' that has two columns 'PersonID' of
     * type {@code int} and 'Name' of type {@code nvarchar}. The values for
     * those columns in the fifth row are, {@code 5} for 'PersonID' and
     * {@code 'David'} for 'Name'. Say we want to change the name from 'David'
     * to 'Thomas'. This method can then be used to identify all the other
     * values in the row that holds 'David'. The following {@code WHERE}
     * statement will be produced:
     * {@code WHERE PersonID = 5 AND Name = 'David'}. This {@code WHERE}
     * statement can then be appended to a SQL {@code UPDATE} statement like so:
     * <p>
     * <code>UPDATE Person<br>
     * SET Name = 'Thomas'<br>
     * WHERE PersonID = 5 AND Name = 'David'</code></p></p>
     *
     * @param rowNum The row that contains the value that is to be edited.
     * @return a SQL {@code WHERE} statement used to identify a certain cell in
     * a row.
     */
    public String createWhereStatement(int rowNum) {
        StringBuilder sb = new StringBuilder();
        List<Object> row = rows.get(rowNum);
        int columnCount = 0;
        for (Object cell : row) {
            System.out.println("CELL: " + cell);
            SQLColumn column = sqlTable.getColumn(columnCount++);
            sb.append(column.getName(true)).append(cell == null
                    ? " IS NULL "
                    : " = ")
                    .append(formatValue(column, cell)).append(' ');
            if (columnCount < sqlTable.getColumnCount()) {
                sb.append("AND ");
            }
        }
        return sb.toString();
    }

    /**
     * Returns the number of rows that haven't been committed to the database
     * yet.
     *
     * @return an {@code int} with rows that are yet to be saved.
     */
    public int getUncommittedRows() {
        return ((DefaultTableModel) uiTable.getModel()).getRowCount() - rows.size();
    }

    /**
     * Refreshes a value in a cell by removing the old value at
     * {@code columnNum} from the row and promptly adding {@code newValue} to
     * the row.
     *
     * @param rowNum The row that contains the modified value.
     * @param columnNum The cell number within the row that is to be refreshed.
     * @param newValue The value that will replace the old value.
     */
    public void refreshRow(int rowNum, int columnNum, Object newValue) {
        List<Object> newRow = rows.get(rowNum);
        newRow.set(columnNum, newValue);
    }

    public boolean rowIsEditable(int rowNum) {
        return rowNum < editableRows.size();
    }

    private class RowActionListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            DefaultTableModel model = (DefaultTableModel) uiTable.getModel();
            btnCommit.setEnabled(model.getRowCount() - rows.size() > 0);
            btnAddRow.setEnabled(model.getRowCount() < 100);
            lblVisibleRows.setText("" + model.getRowCount());
        }

    }

    private class ActionEditCell extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            String tableName = sqlTable.getName(true);
            int columnNum = cellListener.getColumn();
            int rowNum = cellListener.getRow();
            SQLColumn column = sqlTable.getColumn(columnNum);
            Object newValue = cellListener.getNewValue();
            Object oldValue = cellListener.getOldValue();

            if (!rowIsEditable(rowNum)) {
                return;
            }

            if (!rowIsUnique(cellListener.getRow())) {
                UIUtils.showErrorMessage("Cannot edit cell", "The cell you are trying to edit is "
                        + "in a row that is not unique and cannot be modified.",
                        getParent());
            } else {
                String sql = "UPDATE " + tableName
                        + "\nSET " + column.getName(true) + " = " + formatValue(column, newValue)
                        + "\nWHERE " + createWhereStatement(rowNum);

                SQLQuery editValueQuery = new SQLUpdateQuery(sql) {
                    @Override
                    public void onSuccess(int affectedRows) {
                        refreshRow(rowNum, columnNum, newValue);
                        uiTable.packAll();
                        log("The value at (column: " + columnNum + ", row: " + rowNum + ")"
                                + ", has changed from '" + oldValue + "' to '" + newValue + "'.", false);
                    }

                    @Override
                    public void onFailure(String err) {
                        uiTable.setValueAt(oldValue, rowNum, columnNum);
                        log("The value at (column: " + columnNum + ", row: " + rowNum + ") could not be updated"
                                + ". Check that the new value matches the data type of the column.", true);
                    }
                };
                editValueQuery.exec();
            }
        }
    }

    private void btnAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRowActionPerformed
        if (uncommittedRows == null) {
            uncommittedRows = new ArrayList<>();
        }
        DefaultTableModel model = (DefaultTableModel) uiTable.getModel();
        model.addRow(new Vector());
        uncommittedRows.add(model.getRowCount() - 1);
    }//GEN-LAST:event_btnAddRowActionPerformed

    private void btnCommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommitActionPerformed
        actionCommit();
    }//GEN-LAST:event_btnCommitActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        actionDeleteRow();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        if (txtAmountOfRowsToDisplay.getText().isEmpty()) {
            createTable(Integer.parseInt(lblVisibleRows.getText()), 1);
            return;
        }
        int rowsToDisplay;
        try {
            rowsToDisplay = Integer.parseInt(txtAmountOfRowsToDisplay.getText());
            if (rowsToDisplay <= 0) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            UIUtils.showErrorMessage("Error", "You must input a valid integer larger than 0.", getParent());
            return;
        }
        if (getUncommittedRows() > 0) {
            switch (showUncommittedRowsDialog()) {
                case 0:
                    if (actionCommit()) {
                        createTable(rowsToDisplay, 1);
                    }
                    break;
                case 1:
                    createTable(rowsToDisplay, 1);
                    break;
            }
        } else {
            createTable(rowsToDisplay, 1);
        }
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void menuItemRemoveRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRemoveRowActionPerformed
        actionDeleteRow();
    }//GEN-LAST:event_menuItemRemoveRowActionPerformed

    private void btnTruncateTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTruncateTableActionPerformed
        actionTruncateTable();
    }//GEN-LAST:event_btnTruncateTableActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        UserPreferencesLoader userPrefs = UserPreferencesLoader.getInstance();
        if (uncommittedRows != null && uncommittedRows.size() > 0
                && Boolean.valueOf(userPrefs.getProperty("Ask.UncommittedRows"))) {
            int confirmation = UIUtils.showOptionDialog("Uncommitted rows", "There are "
                    + uncommittedRows.size() + " rows that haven't been committed to the database yet."
                    + "\nIf you leave now, your changes will be lost. "
                    + "What would you like to do?", getParent(),
                    "Commit and exit", "Discard changes", "Exit and don't ask again");

            switch (confirmation) {
                case 0:
                    if (actionCommit()) {
                        dispose();
                    }
                    break;
                case 1:
                    dispose();
                    break;
                case 2:
                    userPrefs.set("Ask.UncommittedRows", "false");
                    userPrefs.flushFile();
                    dispose();
                    break;
            }
        } else {
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void menuClearLoggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuClearLoggerActionPerformed
        actionClearLog();
    }//GEN-LAST:event_menuClearLoggerActionPerformed

    public void actionClearLog() {
        TextUtils.emptyDoc(txtLog);
    }

    public void actionTruncateTable() {
        int confirmation = UIUtils.showOptionDialog("Truncate table", "Are you absolutely sure you"
                + " wish to TRUNCATE this table?\nThis will permanently erase the data in it.", getParent(),
                "No", "Yes");
        if (confirmation == 1) {
            SQLQuery truncateTableQuery = new SQLUpdateQuery("TRUNCATE TABLE " + sqlTable.getName(true)) {
                @Override
                public void onSuccess(int affectedRows) {
                    ((DefaultTableModel) uiTable.getModel()).setNumRows(0);
                    int numRemovedRows = rows.size();
                    int numUncommittedRows = 0;
                    rows.clear();
                    editableRows.clear();
                    if (uncommittedRows != null) {
                        numUncommittedRows = uncommittedRows.size();
                        uncommittedRows.clear();
                    }
                    log("The table has been truncated successfully. "
                            + "Removed (" + numRemovedRows + ") rows and "
                            + "(" + numUncommittedRows + ") uncommitted rows.", false);
                }

                @Override
                public void onFailure(String err) {
                    logError(err);
                }
            };
            truncateTableQuery.exec();
        }
    }

//    /**
//     * When called, triggers an almost equivalent action to double clicking a
//     * cell, which in turns makes the cell editable. This method also selects
//     * the contents of the cell for easier editing.
//     */
//    public void actionStartEditCell() {
//        int selectedColumn = tbTable.getSelectedColumn();
//        int selectedRow = tbTable.getSelectedRow();
//        tbTable.editCellAt(selectedRow, selectedColumn);
//        JTextField editorComponent = (JTextField) tbTable.getEditorComponent();
//        editorComponent.selectAll();
//        editorComponent.requestFocus();
//
//    }
    /**
     * Displays an option dialog suited for when the user attempts to perform an
     * action but there are still uncommitted rows.
     *
     * @return <ul>
     * <li>0: Commit changes and refresh.</li>
     * <li>1: Discard changes and refresh.</li>
     * <li>2: Cancel.</li>
     * </ul>
     */
    public int showUncommittedRowsDialog() {
        return UIUtils.showOptionDialog("Uncommitted rows", "There "
                + "are still rows that haven't been committed to the database. If you "
                + "alter the number of visible rows or the sorting, your changes will be lost."
                + "\nWhat do you wish to do?", getParent(),
                "Commit changes and refresh",
                "Discard changes and refresh",
                "Cancel");
    }

    /**
     * The method that represents the action of removing a row from a SQL
     * sqlTable, and consequently the {@code JTable} that displays that
     * sqlTable.
     * <p>
     * This method prioritises removing the row from the database first, and if
     * that action is successful, it will proceed to remove the row from the
     * {@code JTable}. If the first action fails, this method will simply exit
     * cleanly and display an error message. However, if it succeeds, and for
     * some reason the second action fails, the {@code JTable} will have to be
     * manually refreshed to display the changes.</p>
     */
    public void actionDeleteRow() {
        DefaultTableModel model = (DefaultTableModel) uiTable.getModel();
        int selectedRow = uiTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        if (selectedRow > editableRows.size() - 1) {
            int max = MiscUtils.maxFromList(uncommittedRows);
            if (max > selectedRow) {
                int selectedIndex = 0;
                for (int i = 0; i < uncommittedRows.size(); i++) {
                    if (uncommittedRows.get(i) == selectedRow) {
                        selectedIndex = i;
                        break;
                    }
                }
                for (int j = selectedIndex + 1; j < uncommittedRows.size(); j++) {
                    uncommittedRows.set(j, uncommittedRows.get(j) - 1);
                }
                uncommittedRows.remove(selectedIndex);
            } else {
                uncommittedRows.remove(uncommittedRows.size() - 1);
            }
            model.removeRow(selectedRow);
            System.out.println("UNCOMMITTED AFTER DELETE: " + uncommittedRows);
            return;
        }
        int confirmation = UIUtils.showConfirmationMessage("Delete row",
                "Are you sure you wish to permanently delete this row?", getParent());
        if (confirmation != 0) {
            return;
        }

        if (rowIsUnique(selectedRow)) {
            deleteSQLRow(selectedRow);
        } else {
            int confirmationDuplicate = UIUtils.showOptionDialog("Row is not unique",
                    "The row you are trying to remove "
                    + "is not unique and thus cannot be removed. You can, however, remove ALL instances "
                    + "of this row from the table.\nWhat would you like to do?", getParent(),
                    "Leave alone", "Remove all instances of this row");
            if (confirmationDuplicate == 1) {
                deleteSQLRows(selectedRow);
            }
        }
    }

    /**
     * Compares two rows (represented by a {@code List<CellValue>} and checks if
     * each cell is equal to the same cell at that index.
     *
     * @param row1 the first row.
     * @param row2 the second row.
     * @return {@code true} if the amount of matching values is equal to the
     * amount of cells of both rows.
     */
    public boolean rowEqualsRow(List<Object> row1, List<Object> row2) {
        int matches = 0;
        for (int i = 0; i < row1.size(); i++) {
            if (row1.get(i).equals(row2.get(i))) {
                matches++;
            }
        }
        return matches == row1.size();
    }

    /**
     * Attempts to delete the given row from the database. If the operation is
     * successful, it then proceeds to remove said row from the UI sqlTable.
     *
     * @param rowNum the index of the row to remove.
     */
    public void deleteSQLRow(int rowNum) {
        SQLQuery deleteQuery = new SQLUpdateQuery("DELETE FROM " + sqlTable.getName(true) + "\n"
                + "WHERE " + createWhereStatement(rowNum)) {
            @Override
            public void onSuccess(int affectedRows) {
                ((DefaultTableModel) uiTable.getModel()).removeRow(rowNum);
                rows.remove(rowNum);
                editableRows.remove(rowNum);
                log("Successfully deleted (row: " + rowNum + ").", false);
            }

            @Override
            public void onFailure(String err) {
                log("Could not delete (row: " + rowNum + "). The following error was produced: "
                        + err, true);
            }
        };
        deleteQuery.exec();
    }

    /**
     * Attempts to delete ALL the rows that equal to the row {@code rowNum} from
     * the database. If the operation is successful, it then proceeds to remove
     * those rows from the UI sqlTable one by one.
     *
     * @param rowNum the index that contains an instance of the row to remove.
     */
    public void deleteSQLRows(int rowNum) {
        SQLQuery deleteRowQuery = new SQLUpdateQuery("DELETE FROM " + sqlTable.getName(true)
                + "\n WHERE " + createWhereStatement(rowNum)) {
            @Override
            public void onSuccess(int affectedRows) {
                if (affectedRows > 0) {
                    List<Object> row = rows.get(rowNum);
                    for (int i = rows.size() - 1; i >= 0; i--) {
                        if (rowEqualsRow(row, rows.get(i))) {
                            editableRows.remove(i);
                            rows.remove(i);
                            ((DefaultTableModel) uiTable.getModel()).removeRow(i);
                        }
                    }
                }
            }
        };
        deleteRowQuery.exec();
    }

    /**
     * Attempts to commit any new rows to the database.
     *
     * @return {@code true} if the operation was successful.
     */
    public boolean actionCommit() {
        int numUncommittedRows = getUncommittedRows();
        int editableRowsSize = editableRows.size(); //the size before committing
        List<Integer> rowsToCommit = new ArrayList<>();
        List<List<Object>> newRows = new ArrayList<>();
        for (int i = editableRowsSize; i < editableRowsSize + numUncommittedRows; i++) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < uiTable.getColumnCount(); j++) {
                row.add(uiTable.getValueAt(i, j));
            }
            newRows.add(row);
            rowsToCommit.add(i);
        }

        String sql = "INSERT INTO " + sqlTable.getName(true) + "\nVALUES";
        StringBuilder sb = new StringBuilder(sql);
        int columnCount;
        int rowsAdded = 0;
        for (List<Object> newRow : newRows) {
            sb.append("(");
            columnCount = 0;
            for (Object cellValue : newRow) {
                SQLColumn column = sqlTable.getColumn(columnCount++);
                sb.append(formatValue(column, cellValue == null ? "" : cellValue));
                sb.append(column.isLast() ? ")" : ",");
            }
            rowsAdded++;
            sb.append(rowsAdded < newRows.size() ? ", " : "");
        }

        boolean successfulInsert = insertIntoSQLTable(sb.toString());
        if (successfulInsert) {
            editableRows.addAll(rowsToCommit);
            rows.addAll(newRows);
            btnCommit.setEnabled(false);
            logSuccessfulCommit();
            uncommittedRows.clear();
            createTable(Integer.parseInt(lblVisibleRows.getText()), comboOrderBy.getSelectedIndex() + 1);
        }
        return successfulInsert;
    }

    /**
     * Attempts to insert values into a sqlTable using the given SQL INSERT
     * statement. The caller of this method is responsible for the correctness
     * of the SQL string passed on to this method to execute.
     *
     * @param insertStmt a SQL INSERT statement.
     * @return {@code true} if the insertion was successful.
     */
    public boolean insertIntoSQLTable(String insertStmt) {
        boolean success = false;
        try (Statement stmt = SQLConnectionManager.getInstance().getConnection().createStatement()) {
            stmt.executeUpdate(insertStmt);
            success = true;
        } catch (SQLException ex) {
            UIUtils.showErrorMessage("Error", "Could not insert new values into table.\n"
                    + "Check that the data in each of the rows matches the data "
                    + "type of the column or whether the column allows null values.", getParent());
            System.err.println(ex.getMessage());
        }
        return success;
    }

    public boolean rowIsUncommitted(int row) {
        if (uncommittedRows == null) {
            return false;
        }
        for (Integer uncommittedRow : uncommittedRows) {
            if (row == uncommittedRow) {
                return true;
            }
        }
        return false;
    }

    public void logSuccessfulCommit() {
        for (Integer uncommittedRow : uncommittedRows) {
            log("Successfully committed value at (row: " + uncommittedRow + ").", false);
        }
    }

    /**
     * Displays a message in the {@code JTextPane} that logs events that occur
     * within the operation.
     *
     * @param message a string to be displayed.
     * @param error whether to format the string as an error or a standard
     * message. Errors will be in red and messages in black.
     */
    public void log(String message, boolean error) {
        if (error) {
            TextUtils.appendAsError(txtLog, "Segoe UI", 12, "[" + MiscUtils.timeStamp() + "] "
                    + message + "\n");
        } else {
            TextUtils.appendAsMessage(txtLog, "Segoe UI", 12, "[" + MiscUtils.timeStamp() + "] "
                    + message + "\n");
        }
        UIUtils.scrollToBottom(scrLog);
    }

    /**
     * Logs an error taken directly from the query engine. This is a convenience
     * method equivalent to {@code log(queryEngine.getErrorLog(), true);}
     * @param err
     */
    public void logError(String err) {
        log(err, true);
    }

    private class TableMouseListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            int columnIndex = uiTable.columnAtPoint(e.getPoint());
            int rowIndex = uiTable.rowAtPoint(e.getPoint());
            if (columnIndex != -1 && rowIndex != -1) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    uiTable.setColumnSelectionInterval(columnIndex, columnIndex);
                    uiTable.setRowSelectionInterval(rowIndex, rowIndex);
                    popTable.show(uiTable, e.getX(), e.getY());
                }
            }
        }
    }

    private class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);

            if (table.isRowSelected(row)) {
                if (rowIsUncommitted(row)) {
                    setBackground(new Color(0xA82321));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(UIUtils.COOL_GREEN);
                }
            }

            if (hasFocus) {
                if (rowIsUncommitted(row)) {
                    setBackground(new Color(0x9E5B62));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(0x357F3C));
                    setForeground(Color.WHITE);
                }
            }

            return c;
        }

    }

    private class ColumnSortListener implements ItemListener {

        private String previousVal;
        private boolean failed;

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED && !failed) {
                JComboBox<String> src = (JComboBox<String>) e.getSource();
                sortBy(src.getSelectedItem().toString());
            } else {
                previousVal = (String) e.getItem();
            }
        }

        public void sortBy(String column) {
            int columnIndex = sqlTable.getColumn(column).getOrdinalPosition();
            int rowsToDisplay = Integer.parseInt(lblVisibleRows.getText());
            if (getUncommittedRows() > 0) {
                switch (showUncommittedRowsDialog()) {
                    case 0:
                        if (actionCommit()) {
                            createTable(rowsToDisplay, columnIndex);
                        } else {
                            failed = true; //prevents infinite loop
                            comboOrderBy.setSelectedItem(previousVal); //this will trigger itemStateChanged event
                            failed = false;
                        }
                        break;
                    case 1:
                        createTable(rowsToDisplay, columnIndex);
                        break;
                }
            } else {
                createTable(rowsToDisplay, columnIndex);
            }
        }
    }

    /**
     * A custom implementation of {@code JTableHeader} that provides support for
     * custom table header tooltips based on each {@code SQLColumn} in the
     * table. Apart from that, objects from this class behave exactly the same
     * as a {@code JTableHeader}.
     */
    private class MorganTableHeader extends JTableHeader {

        private int column = -1;
        private String currentToolTip;

        public MorganTableHeader(TableColumnModel columnModel) {
            super(columnModel);
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            int columnNum = uiTable.columnAtPoint(event.getPoint());
            if (columnNum != -1 && column != columnNum) { //prevents constant remaking of tooltip
                column = columnNum;
                currentToolTip = makeToolTip(sqlTable.getColumn(columnNum));
            }
            return currentToolTip;
        }

        public String makeToolTip(SQLColumn column) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><i><b>Ordinal position: </i></b>").append(column.getOrdinalPosition()).append("<br>");
            sb.append("<i><b>PK?: </b></i>").append(column.isPK() ? "Yes" : "No").append("<br>");
            sb.append("<i><b>FK?: </b></i>").append(column.isFK() ? "Yes" : "No").append("<br>");
            sb.append("<i><b>Data type: </b></i>").append(column.getDataType()).append(column.getDataPrecision());
            return sb.toString() + "<html>";
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar barTop;
    private javax.swing.JButton btnAddRow;
    private javax.swing.JButton btnCommit;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTruncateTable;
    private javax.swing.JComboBox<String> comboOrderBy;
    private javax.swing.JLabel iLblOrderBy;
    private javax.swing.JLabel iLblRowsToDisplay;
    private javax.swing.JLabel iLblVisibleRows;
    private javax.swing.JLabel lblVisibleRows;
    private javax.swing.JMenuItem menuClearLogger;
    private javax.swing.JMenuItem menuItemRemoveRow;
    private javax.swing.JPanel pnlContainer;
    private javax.swing.JPanel pnlTop2;
    private javax.swing.JPopupMenu popLogger;
    private javax.swing.JPopupMenu popTable;
    private javax.swing.JScrollPane scrLog;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JSplitPane splitContainer;
    private javax.swing.JToolBar.Separator topSeparator;
    private javax.swing.JTextField txtAmountOfRowsToDisplay;
    private javax.swing.JTextPane txtLog;
    private org.jdesktop.swingx.JXTable uiTable;
    // End of variables declaration//GEN-END:variables
}
