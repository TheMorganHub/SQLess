package com.sqless.ui;

import com.sqless.file.FileManager;
import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLForeignKey;
import com.sqless.sql.objects.SQLPrimaryKey;
import com.sqless.sql.objects.SQLTable;
import com.sqless.ui.listeners.TableCellListener;
import com.sqless.utils.DataTypeUtils;
import com.sqless.utils.HintsManager;
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
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import jsyntaxpane.DefaultSyntaxKit;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class UICreateTableSQLess extends FrontPanel {

    private TableCellListener cellChangeListener;
    private TableCellListener cellFkChangeListener;
    private JComboBox comboFieldsFK = UIUtils.createEditableComboBox();
    private final ImageIcon PK_ICON = UIUtils.icon(getIconsFolder(), "PRIMARY_KEY");
    private SQLTable sqlTable;
    /**
     * Una referencia a las columnas de la tabla.
     */
    private List<SQLColumn> columnList;
    /**
     * Una referencia a las FKs de la tabla.
     */
    private List<SQLForeignKey> fkList;
    public static final int TABLE_CREATE = 1;
    public static final int TABLE_UPDATE = 2;
    private int task = -1;
    private boolean fromSuggestion;
    private GenericWaitingDialog waitDialog;

    /**
     * Crea un nuevo editor de tablas.
     *
     * @param parentPane El {@code JTabbedPane} que contendrá a este editor.
     * @param sqlTable La {@code SQLTable} a editar. Si es {@code null}, se
     * asume que el editor va a estar en modo creación de tabla.
     */
    public UICreateTableSQLess(JTabbedPane parentPane, SQLTable sqlTable) {
        super(parentPane);
        initComponents();

        if (sqlTable == null) {
            task = TABLE_CREATE;
            this.sqlTable = new SQLTable();
            waitDialog = new GenericWaitingDialog("Cargando...");
            waitDialog.display(() -> {
                SQLColumn emptyNewColumn = new SQLColumn("");
                if (emptyNewColumn.getCollation() != null && emptyNewColumn.getCharacterSet() != null) {
                    this.sqlTable.addColumn(emptyNewColumn);
                    SwingUtilities.invokeLater(() -> postColumnLoadTasks());
                } else {
                    setIntegrity(Integrity.CORRUPT);
                    waitDialog.dispose();
                    UIUtils.showErrorMessage("Error", "Hubo un error al cargar las columnas. Por favor, revisa que la conexión con la base de datos está activa.", UIClient.getInstance());                    
                }
            });
        } else {
            task = TABLE_UPDATE;
            this.sqlTable = new SQLTable(sqlTable);
            waitDialog = new GenericWaitingDialog("Cargando columnas...");
            waitDialog.display(() -> {
                this.sqlTable.loadColumnsForUI();
                if (!this.sqlTable.getColumns().isEmpty()) {
                    SwingUtilities.invokeLater(() -> postColumnLoadTasks());
                } else {
                    setIntegrity(Integrity.CORRUPT);
                    waitDialog.dispose();
                    UIUtils.showErrorMessage("Error", "Hubo un error al cargar las columnas. Por favor, revisa que la conexión con la base de datos está activa.", UIClient.getInstance());
                }                
            });
        }
    }

    /**
     * Crea un nuevo editor de tablas en modo creación. Usar este constructor
     * sólo si se está creando una tabla mediante una sugerencia de SQLess. Por
     * ejemplo, si creamos una base de datos y nos conectamos a ella y está
     * vacía. Para crear un editor normalmente, usar
     * {@link #UICreateTableSQLess(javax.swing.JTabbedPane, com.sqless.sql.objects.SQLTable)}
     *
     * @param parentPane El {@code JTabbedPane} que contendrá a este editor.
     * @param fromSuggestion Si {@code true}, el editor automáticamente agregará
     * una columna de nombre id que será PK. Si {@code false}, se tirará una
     * excepción.
     */
    public UICreateTableSQLess(JTabbedPane parentPane, boolean fromSuggestion) {
        super(parentPane);
        if (!fromSuggestion) {
            throw new IllegalArgumentException("Wrong constructor for UICreateTableSQLess. "
                    + "Use UICreateTableSQLess(JTabbedPane parentPane, SQLTable sqlTable) if the editor isn't created from a suggestion.");
        }
        initComponents();
        this.fromSuggestion = fromSuggestion;
        task = TABLE_CREATE;
        sqlTable = new SQLTable();
        fkList = this.sqlTable.getForeignKeys();
        columnList = this.sqlTable.getColumns();

        SQLColumn pkCol = new SQLColumn("id");
        pkCol.setAutoincrement(true, false);
        SQLColumn newExtraCol = new SQLColumn("");
        sqlTable.getPrimaryKey().addColumn(pkCol);
        sqlTable.addColumn(pkCol);
        sqlTable.addColumn(newExtraCol);
        getTableModel().addRow(new Object[5]);

        pnlExtraSettings.add(new UIColumnExtrasPanel(this, uiTable, columnList));
        syncRowWithList(0);
        syncRowWithList(1);
        prepararUI();
    }

    private void postColumnLoadTasks() {
        fkList = this.sqlTable.getForeignKeys();
        columnList = this.sqlTable.getColumns();

        pnlExtraSettings.add(new UIColumnExtrasPanel(this, uiTable, columnList));
        prepararUI();
    }

    public void prepararUI() {
        DefaultSyntaxKit.initKit();
        sqlPane.setContentType("text/sql");
        sqlPane.setComponentPopupMenu(null);
        prepararToolbar();
        prepararMainTable();
        prepararFKTable();
    }

    public void prepararMainTable() {
        uiTable.addPropertyChangeListener(tableEditorPropertyChangeListener);
        uiTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        uiTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox(PK_ICON)));
        uiTable.getColumnModel().getColumn(0).setCellRenderer(pkCellRenderer);
        DefaultCellEditor comboboxEditor = new DefaultCellEditor(UIUtils.createEditableComboBox(DataTypeUtils.DEFAULT_DATA_TYPES));
        uiTable.getColumnModel().getColumn(2).setCellEditor(comboboxEditor);
        for (int i = 0; i < uiTable.getColumnModel().getColumnCount(); i++) {
            ((DefaultCellEditor) uiTable.getDefaultEditor(uiTable.getColumnClass(i))).setClickCountToStart(1);
        }
        uiTable.getColumn(0).setPreferredWidth(20);
        uiTable.getColumn(1).setPreferredWidth(125);
        uiTable.getSelectionModel().addListSelectionListener(tableSelectionListener);

        cellChangeListener = new TableCellListener(uiTable, actionEditCell);

        if (task == TABLE_UPDATE) {
            getTableModel().setRowCount(columnList.size());

            for (int i = 0; i < columnList.size(); i++) {
                SQLColumn col = columnList.get(i);
                syncRowWithList(i);
            }
        }
    }

    @Override
    public void onCreate() {
        SwingUtilities.invokeLater(() -> {
            int row = fromSuggestion ? 1 : 0;
            uiTable.setRowSelectionInterval(row, row);
            if (task == TABLE_CREATE) {
                SwingUtilities.invokeLater(() -> {
                    uiTable.editCellAt(fromSuggestion ? 1 : 0, 1);
                });
            }
        });
    }

    public void prepararFKTable() {
        uiTableFKs.addPropertyChangeListener(tableEditorPropertyChangeListener);
        JComboBox comboReferencedField = UIUtils.createEditableComboBox();
        uiTableFKs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        uiTableFKs.getColumn(0).setPreferredWidth(125);
        uiTableFKs.getColumn(1).setPreferredWidth(125);
        uiTableFKs.getColumn(2).setPreferredWidth(125);
        uiTableFKs.getColumn(3).setPreferredWidth(125);
        uiTableFKs.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboFieldsFK));
        uiTableFKs.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(UIUtils.createEditableComboBox(SQLUtils.getTablesFromDBAsString().toArray(new String[0]))));
        uiTableFKs.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboReferencedField));

        for (int i = 0; i < uiTableFKs.getColumnModel().getColumnCount(); i++) {
            DefaultCellEditor defaultEditor = (DefaultCellEditor) uiTableFKs.getDefaultEditor(uiTableFKs.getColumnClass(i));
            defaultEditor.setClickCountToStart(1);
        }

        if (task == TABLE_UPDATE) {
            List<SQLForeignKey> tableFKs = sqlTable.getForeignKeys();
            getFKTableModel().setRowCount(0);
            for (int i = 0; i < tableFKs.size(); i++) {
                SQLForeignKey fk = tableFKs.get(i);
                getFKTableModel().addRow(new String[0]);
                uiTableFKs.setValueAt(fk.getName(), i, 0);
                uiTableFKs.setValueAt(fk.getField(), i, 1);
                uiTableFKs.setValueAt(fk.getReferencedTableName(), i, 2);
                DefaultComboBoxModel comboReferencedFieldModel = (DefaultComboBoxModel) comboReferencedField.getModel();
                List<String> columnNames = SQLUtils.getColumnsFromTableAsString(fk.getReferencedTableName());
                for (String columnName : columnNames) {
                    comboReferencedFieldModel.addElement(columnName);
                }
                uiTableFKs.setValueAt("", i, 3);
                uiTableFKs.setValueAt(fk.getReferencedColumnName(), i, 3);
            }
        }
        cellFkChangeListener = new TableCellListener(uiTableFKs, actionEditFKCell);
    }

    public void forceAddFKToUITable(SQLForeignKey fk) {
        getFKTableModel().addRow(new Object[]{fk.getName(), fk.getField(), fk.getReferencedTableName(), fk.getReferencedColumnName()});
    }

    public void prepararToolbar() {
        tabbedpane.addChangeListener(e -> {
            int selectedTab = tabbedpane.getSelectedIndex();

            switch (selectedTab) {
                case 1:
                    DefaultComboBoxModel model = (DefaultComboBoxModel) comboFieldsFK.getModel();
                    model.removeAllElements();
                    for (int i = 0; i < columnList.size(); i++) {
                        model.addElement(columnList.get(i).getUncommittedName());
                    }
                    if (uiTableFKs.getRowCount() > 0) {
                        SwingUtilities.invokeLater(() -> {
                            uiTableFKs.setRowSelectionInterval(0, 0);
                        });
                    }
                    break;
                case 2:
                    sqlPane.setText(task == TABLE_CREATE ? accionMakeCreateTableStmt() : accionCreateAlterTableStmt());
                    break;
            }
            UIClient.getInstance().replaceToolbarIcons();
        });
    }

    /**
     * Sincroniza los valores en la tabla en la fila {@code rowNum} con los
     * valores de la columna en {@code columnList} en la posición
     * {@code rowNum}. <br>
     * Luego de TODO cambio que ocurra en la lista de columnas se deberá llamar
     * a este método para que los cambios se vean reflejados en la tabla visual.
     *
     * @param rowNum El número de fila el cual sincronizar.
     */
    public void syncRowWithList(int rowNum) {
        SQLColumn col = columnList.get(rowNum);
        uiTable.setValueAt(sqlTable.columnIsPK(col), rowNum, 0);
        uiTable.setValueAt(col.getUncommittedName(), rowNum, 1);
        uiTable.setValueAt(col.getDataType(), rowNum, 2);

        String strLength = col.getLength();
        uiTable.setValueAt(strLength != null && !col.getDataType().equals("enum")
                && !col.getDataType().equals("set") ? Integer.parseInt(strLength) : null, rowNum, 3);

        String strNumScale = col.getNumericScale();
        uiTable.setValueAt(strNumScale != null ? (Integer.parseInt(col.getNumericScale()) > 0 ? Integer.parseInt(col.getNumericScale()) : null) : null, rowNum, 4);
        uiTable.setValueAt(col.isNullable(), rowNum, 5);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popMenu = new javax.swing.JPopupMenu();
        menuItemInsert = new javax.swing.JMenuItem();
        menuItemDelete = new javax.swing.JMenuItem();
        tabbedpane = new javax.swing.JTabbedPane();
        pnlCampos = new javax.swing.JPanel();
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
        uiTable.addMouseListener(UIUtils.mouseListenerWithPopUpMenuForJTable(popMenu, uiTable));
        pnlExtraSettings = new javax.swing.JPanel();
        pnlFKs = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        uiTableFKs = new JXTable() {
            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                int condition, boolean pressed) {
                if (!ks.equals(KeyStroke.getKeyStroke("control S"))) {
                    return super.processKeyBinding(ks, e, condition, pressed);
                }
                return false;
            }

        };
        pnlSQL = new javax.swing.JPanel();
        scrSQL = new javax.swing.JScrollPane();
        sqlPane = new javax.swing.JEditorPane();

        menuItemInsert.setAction(actionInsertCampo);
        menuItemInsert.setText("Insertar fila aquí");
        popMenu.add(menuItemInsert);

        menuItemDelete.setAction(actionDeleteCampo);
        menuItemDelete.setText("Eliminar fila(s)");
        popMenu.add(menuItemDelete);

        setMinimumSize(new java.awt.Dimension(590, 505));

        tabbedpane.setFocusable(false);

        uiTable.setBackground(new java.awt.Color(240, 240, 240));
        uiTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null,  new Boolean(true)}
            },
            new String [] {
                "", "Nombre", "Tipo", "Longitud", "Decimales", "Acepta null"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        uiTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        uiTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        uiTable.setGridColor(new java.awt.Color(204, 204, 204));
        uiTable.setRowHeight(20);
        uiTable.setSelectionBackground(new java.awt.Color(233, 243, 253));
        uiTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        uiTable.setShowGrid(true);
        scrTable.setViewportView(uiTable);
        uiTable.getTableHeader().setReorderingAllowed(false);
        uiTable.setSortable(false);
        uiTable.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.WHITE));
        uiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pnlExtraSettings.setLayout(new org.jdesktop.swingx.VerticalLayout());

        javax.swing.GroupLayout pnlCamposLayout = new javax.swing.GroupLayout(pnlCampos);
        pnlCampos.setLayout(pnlCamposLayout);
        pnlCamposLayout.setHorizontalGroup(
            pnlCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrTable, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
            .addComponent(pnlExtraSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlCamposLayout.setVerticalGroup(
            pnlCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCamposLayout.createSequentialGroup()
                .addComponent(scrTable, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlExtraSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedpane.addTab("Campos", pnlCampos);

        uiTableFKs.setBackground(new java.awt.Color(240, 240, 240));
        uiTableFKs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Campo", "Tabla referenciada", "Campo referenciado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        uiTableFKs.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        uiTableFKs.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        uiTableFKs.setGridColor(new java.awt.Color(204, 204, 204));
        uiTableFKs.setRowHeight(20);
        uiTableFKs.setSelectionBackground(new java.awt.Color(220, 235, 251));
        uiTableFKs.setSelectionForeground(new java.awt.Color(0, 0, 0));
        uiTableFKs.setShowGrid(true);
        jScrollPane1.setViewportView(uiTableFKs);
        uiTableFKs.getTableHeader().setReorderingAllowed(false);
        uiTableFKs.setSortable(false);
        uiTableFKs.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.WHITE));

        javax.swing.GroupLayout pnlFKsLayout = new javax.swing.GroupLayout(pnlFKs);
        pnlFKs.setLayout(pnlFKsLayout);
        pnlFKsLayout.setHorizontalGroup(
            pnlFKsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
        );
        pnlFKsLayout.setVerticalGroup(
            pnlFKsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        tabbedpane.addTab("Claves foráneas", pnlFKs);

        sqlPane.setEditable(false);
        scrSQL.setViewportView(sqlPane);

        javax.swing.GroupLayout pnlSQLLayout = new javax.swing.GroupLayout(pnlSQL);
        pnlSQL.setLayout(pnlSQLLayout);
        pnlSQLLayout.setHorizontalGroup(
            pnlSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrSQL, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
        );
        pnlSQLLayout.setVerticalGroup(
            pnlSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrSQL, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        tabbedpane.addTab("SQL", pnlSQL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(tabbedpane)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(tabbedpane)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    public String accionMakeCreateTableStmt() {
        return sqlTable.getCreateStatement() + ";";
    }

    public String accionCreateAlterTableStmt() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sqlTable.getDroppedFKs().size(); i++) {
            SQLForeignKey fk = sqlTable.getDroppedFKs().get(i);
            sb.append(fk.getDropStatement()).append("\n");
        }

        for (int i = 0; i < fkList.size(); i++) {
            SQLForeignKey fk = fkList.get(i);
            if (fk.hasUncommittedChanges()) {
                sb.append(fk.getDropStatement()).append("\n");
            }
        }
        sb.append("\n");

        boolean alterTableAdded = false;
        for (int i = 0; i < sqlTable.getDroppedColumns().size(); i++) {
            if (!alterTableAdded) {
                alterTableAdded = true;
                sb.append("ALTER TABLE ").append("`").append(sqlTable.getName()).append("`\n");
            }
            sb.append(sqlTable.getDroppedColumns().get(i).getDropStatement()).append(",\n");
        }

        for (int i = 0; i < columnList.size(); i++) {
            SQLColumn col = columnList.get(i);
            if (col.hasUncommittedChanges() || sqlTable.columnHasMoved(col)) {
                if (!alterTableAdded) {
                    alterTableAdded = true;
                    sb.append("ALTER TABLE ").append("`").append(sqlTable.getName()).append("`\n");
                }
                sb.append(col.getChangeColumnStatement()).append(",\n");
            }
        }

        for (int i = 0; i < columnList.size(); i++) {
            SQLColumn col = columnList.get(i);

            if (col.isBrandNew()) {
                if (!alterTableAdded) {
                    alterTableAdded = true;
                    sb.append("ALTER TABLE ").append("`").append(sqlTable.getName()).append("`\n");
                }
                sb.append(col.getCreateStatement(true)).append(",\n");
            }
        }

        if (sqlTable.getPrimaryKey().hasChanged()) {
            SQLPrimaryKey primaryKey = sqlTable.getPrimaryKey();
            if (!alterTableAdded) {
                alterTableAdded = true;
                sb.append("ALTER TABLE ").append("`").append(sqlTable.getName()).append("`\n");
            }
            if (!primaryKey.mustNotDrop()) {
                sb.append(primaryKey.getDropStatement()).append("\n");
            }
            sb.append(primaryKey.getAddPKsStatement(true)).append("\n");
        }

        if (alterTableAdded) {
            sb.setCharAt(sb.length() - 2, ';');
        }
        sb.append("\n");

        for (int i = 0; i < fkList.size(); i++) {
            SQLForeignKey fk = fkList.get(i);
            if (fk.hasUncommittedChanges() || fk.isBrandNew()) {
                sb.append(fk.getChangeConstraintStatement()).append("\n");
            }
        }
        String trimmed = sb.toString().trim();
        return trimmed;
    }

    public DefaultTableModel getTableModel() {
        return (DefaultTableModel) uiTable.getModel();
    }

    public DefaultTableModel getFKTableModel() {
        return (DefaultTableModel) uiTableFKs.getModel();
    }

    public Vector getRow(int rowNum) {
        return rowNum < 0 ? null : (Vector) getTableModel().getDataVector().get(rowNum);
    }

    public boolean columnNameExists(String name, int skipRow) {
        for (int i = 0; i < uiTable.getRowCount(); i++) {
            if (i != skipRow) {
                if (name.equalsIgnoreCase(uiTable.getValueAt(i, 1).toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void processPKColumnChange(SQLColumn colEditada, int row) {
        boolean newVal = (boolean) cellChangeListener.getNewValue();
        if (newVal) {
            sqlTable.getPrimaryKey().addColumn(colEditada);
        } else {
            sqlTable.getPrimaryKey().removeColumn(colEditada);
        }
        syncRowWithList(row);
    }

    public void processNameColumnChange(SQLColumn colEditada, int row) {
        if (columnNameExists(cellChangeListener.getNewValue().toString(), row)) {
            UIUtils.showErrorMessage("Editar nombre", "No puede haber columnas duplicadas en una tabla.", null);
            uiTable.setValueAt(cellChangeListener.getOldValue(), row, 1);
            return;
        }

        HintsManager hints = new HintsManager(this, colEditada, cellChangeListener);

        if (cellChangeListener.getOldValue().toString().isEmpty()) {
            hints.activate(HintsManager.GUESS_DATATYPE_BY_NAME);
        }
        colEditada.setUncommittedName(cellChangeListener.getNewValue().toString());

        hints.activate(HintsManager.COULD_BE_PK);
        hints.activate(HintsManager.COULD_BE_FK);
        syncRowWithList(row);
    }

    public void processDataTypeColumnChange(SQLColumn colEditada, int row) {
        colEditada.setDataType(cellChangeListener.getNewValue().toString());
        syncRowWithList(row);
        refreshPnlExtras();
    }

    public void processLengthColumnChange(SQLColumn colEditada, int row) {
        if (!colEditada.getDataType().startsWith("enum") && !colEditada.getDataType().startsWith("set")
                && !colEditada.getDataType().equals("text") && !colEditada.getDataType().equals("year")
                && !colEditada.isTimeBased()) {
            colEditada.setLength(cellChangeListener.getNewValue().toString());
            syncRowWithList(row);
        } else {
            uiTable.setValueAt(cellChangeListener.getOldValue(), row, 3);
        }
    }

    public void processDecimalColumnChange(SQLColumn colEditada, int row) {
        //decimales - si el tipo de dato del row no es decimal, el campo decimal no se va a poder setear
        if (!colEditada.getDataType().equals("decimal")) {
            uiTable.setValueAt(null, row, 4);
        } else {
            colEditada.setNumericScale(cellChangeListener.getNewValue().toString());
            syncRowWithList(row);
        }
    }

    public void processNullColumnChange(SQLColumn colEditada, int row) {
        if (colEditada.isPK()) { //una columna PK no puede ser nunca nullable
            uiTable.setValueAt(false, row, 5);
            UIUtils.showMessage("Convertir a nullable", "Una columna (PK) que identifica a esta tabla no puede ser nunca nullable", UIClient.getInstance());
        } else {
            colEditada.setNullable((boolean) cellChangeListener.getNewValue());
        }
    }

    private AbstractAction actionEditCell = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (cellChangeListener.getNewValue() == null) {
                return;
            }
            int row = cellChangeListener.getRow();
            SQLColumn colEditada = columnList.get(row);

            switch (cellChangeListener.getColumn()) {
                case 0:
                    processPKColumnChange(colEditada, row);
                    break;
                case 1:
                    processNameColumnChange(colEditada, row);
                    break;
                case 2:
                    processDataTypeColumnChange(colEditada, row);
                    break;
                case 3:
                    processLengthColumnChange(colEditada, row);
                    break;
                case 4:
                    processDecimalColumnChange(colEditada, row);
                    break;
                case 5:
                    processNullColumnChange(colEditada, row);
                    break;
            }
            if (colEditada.evaluateUncommittedChanges()) {
                boldTitleLabel();
            }
            if (sqlTable.getPrimaryKey().hasChanged()) {
                boldTitleLabel();
            }
        }
    };

    private AbstractAction actionEditFKCell = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (cellFkChangeListener.getNewValue() == null) {
                return;
            }
            int col = cellFkChangeListener.getColumn();
            int row = cellFkChangeListener.getRow();
            SQLForeignKey fkEditada = fkList.get(row);

            switch (col) {
                case 0:
                    fkEditada.setUncommittedName(cellFkChangeListener.getNewValue().toString());
                    break;
                case 1: //campo
                    String colName = cellFkChangeListener.getNewValue().toString();
                    for (int i = 0; i < columnList.size(); i++) {
                        if (columnList.get(i).getUncommittedName().equals(colName)) {
                            columnList.get(i).setNullable(false); //si la col es elegida como fk, no puede ser nullable
                            syncRowWithList(i);
                            break;
                        }
                    }
                    fkEditada.setField(colName);
                    break;
                case 2: //tabla referenciada
                    String tableName = cellFkChangeListener.getNewValue().toString();
                    DefaultCellEditor editor = (DefaultCellEditor) uiTableFKs.getColumn(3).getCellEditor();
                    DefaultComboBoxModel comboReferencedFieldModel = (DefaultComboBoxModel) ((JComboBox) editor.getComponent()).getModel();
                    comboReferencedFieldModel.removeAllElements();
                    List<String> columnNames = SQLUtils.getColumnsFromTableAsString(tableName);
                    for (String columnName : columnNames) {
                        comboReferencedFieldModel.addElement(columnName);
                    }
                    uiTableFKs.setValueAt("", uiTableFKs.getSelectedRow(), 3);
                    fkEditada.setReferencedTableName(tableName);
                    break;
                case 3:
                    fkEditada.setReferencedColumnName(cellFkChangeListener.getNewValue().toString());
                    break;
            }
            if (fkEditada.evaluateUncommittedChanges()) {
                boldTitleLabel();
            }
        }
    };

    private DefaultTableCellRenderer pkCellRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Boolean valor = (Boolean) value;
            lbl.setIcon(value != null && valor ? PK_ICON : null);
            lbl.setText("");
            return lbl;
        }
    };

    public void refreshPnlExtras() {
        UIColumnExtrasPanel extrasPnl = (UIColumnExtrasPanel) pnlExtraSettings.getComponent(0);
        extrasPnl.refresh(columnList);
    }

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemInsert;
    private javax.swing.JPanel pnlCampos;
    private javax.swing.JPanel pnlExtraSettings;
    private javax.swing.JPanel pnlFKs;
    private javax.swing.JPanel pnlSQL;
    private javax.swing.JPopupMenu popMenu;
    private javax.swing.JScrollPane scrSQL;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JEditorPane sqlPane;
    private javax.swing.JTabbedPane tabbedpane;
    private org.jdesktop.swingx.JXTable uiTable;
    private org.jdesktop.swingx.JXTable uiTableFKs;
    // End of variables declaration//GEN-END:variables
    private JButton btnAddCampo;
    private JButton btnInsertCampo;
    private JButton btnRemoveCampo;
    private JButton btnAddPK;
    private JButton btnAddFK;
    private JButton btnRemoveFK;
    private JButton btnSave;
    private JButton btnMoveColumns;
    private JButton btnMoveUp;
    private JButton btnMoveDown;
    private JButton btnSaveAsScript;
    private Component[] camposToolbarComponents;
    private Component[] fkToolbarComponents;
    private Component[] sqlStatementToolbarComponents;
    private JMenuItem menuSave;

    private ListSelectionListener tableSelectionListener = (e) -> {
        if (e.getValueIsAdjusting()) {
            return;
        }

        refreshPnlExtras();
    };

    @Override
    public Component[] getToolbarComponents() {
        if (camposToolbarComponents == null) {
            btnSave = UIUtils.newToolbarBtn(actionSave, "Commit all changes", UIUtils.icon("ui_general", "SAVE"));
            btnAddCampo = UIUtils.newToolbarBtn(actionAddCampo, "Agregar una columna a la tabla", UIUtils.icon(this, "AGREGAR_CAMPO"));
            btnInsertCampo = UIUtils.newToolbarBtn(actionInsertCampo, "Insertar un campo en la posición actual", UIUtils.icon(this, "INSERTAR_CAMPO"));
            btnRemoveCampo = UIUtils.newToolbarBtn(actionDeleteCampo, "Remover el campo en la posición actual", UIUtils.icon(this, "BORRAR_CAMPO"));
            btnAddPK = UIUtils.newToolbarBtn(actionAddPK, "Alternar PK en esta columna", UIUtils.icon(this, "ADD_PK"));
            btnMoveUp = UIUtils.newToolbarBtn(actionMoveUp, "Mover la columna seleccionada hacia arriba", UIUtils.icon(this, "MOVER_ARRIBA"));
            btnMoveDown = UIUtils.newToolbarBtn(actionMoveDown, "Mover la columna seleccionada hacia abajo", UIUtils.icon(this, "MOVER_ABAJO"));
            btnMoveColumns = UIUtils.newToolbarBtn(actionMoveColumns, "Opciones avanzadas de orden de columnas", UIUtils.icon(this, "MOVER_COLUMNAS"));
            camposToolbarComponents = new Component[]{btnSave, UIUtils.newSeparator(), btnAddCampo, btnInsertCampo, btnRemoveCampo, UIUtils.newSeparator(), btnAddPK, UIUtils.newSeparator(),
                btnMoveUp, btnMoveDown, btnMoveColumns};

        }

        if (fkToolbarComponents == null) {
            btnAddFK = UIUtils.newToolbarBtn(actionAddFK, "Crear una nueva FK", UIUtils.icon(getIconsFolder(), "ADD_FK"));
            btnRemoveFK = UIUtils.newToolbarBtn(actionRemoveFK, "Eliminar la FK seleccionada", UIUtils.icon(getIconsFolder(), "REMOVE_FK"));
            fkToolbarComponents = new Component[]{btnSave, UIUtils.newSeparator(), btnAddFK, btnRemoveFK};
        }

        if (sqlStatementToolbarComponents == null) {
            btnSaveAsScript = UIUtils.newToolbarBtn(actionSaveAsScript, "Guardar este script en una ubicación externa", UIUtils.icon(this, "SAVE_AS_SCRIPT"));
            sqlStatementToolbarComponents = new Component[]{btnSave, UIUtils.newSeparator(), btnSaveAsScript};
        }

        switch (tabbedpane.getSelectedIndex()) {
            case 0:
                toolbarComponents = camposToolbarComponents;
                break;
            case 1:
                toolbarComponents = fkToolbarComponents;
                break;
            case 2:
                toolbarComponents = sqlStatementToolbarComponents;
                break;
        }

        return toolbarComponents;
    }

    @Override
    public JMenuItem[] getMenuItems() {
        if (menuItems == null) {
            menuSave = new JMenuItem("Save");
            menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
            menuSave.addActionListener(actionSave);
            menuItems = new JMenuItem[]{menuSave};
        }
        return menuItems;
    }

    /**
     * Returns the title of this tab depending on the task being performed.
     * <br><br>
     * Note: if the task happens to change while this window is open, this
     * method may return a different value than when it first was executed.
     *
     * @return if {@code task == TABLE_CREATE} the title will be "New table". If
     * {@code task == TABLE_MODIFY}, the title will be "Modify table
     * [table_name]".
     */
    @Override
    public String getTabTitle() {
        return task == TABLE_CREATE ? "Nueva tabla" : "Modificar tabla " + sqlTable.getName();
    }

    @Override
    public String getIconsFolder() {
        return "ui_createtable";
    }

    private ActionListener actionMoveUp = e -> {
        if (uiTable.getCellEditor() != null) {
            uiTable.getCellEditor().stopCellEditing();
        }

        int selectStart = uiTable.getSelectedRow();

        if (selectStart > 0) {
            columnList.get(selectStart).moveUp();
            getTableModel().moveRow(selectStart, selectStart, selectStart - 1);
            uiTable.setRowSelectionInterval(selectStart - 1, selectStart - 1);
            columnList.get(selectStart - 1).evaluateUncommittedChanges();
            boldTitleLabel();
        }
    };

    private ActionListener actionMoveDown = e -> {
        if (uiTable.getCellEditor() != null) {
            uiTable.getCellEditor().stopCellEditing();
        }

        int selectStart = uiTable.getSelectedRow();

        if (selectStart < uiTable.getRowCount() - 1) {
            columnList.get(selectStart).moveDown();
            getTableModel().moveRow(selectStart, selectStart, selectStart + 1);
            uiTable.setRowSelectionInterval(selectStart + 1, selectStart + 1);
            columnList.get(selectStart + 1).evaluateUncommittedChanges();
            boldTitleLabel();
        }
    };

    private ActionListener actionAddCampo = e -> {
        if (columnList.isEmpty() || !columnList.get(uiTable.getSelectedRow()).getUncommittedName().isEmpty()) {
            if (uiTable.getCellEditor() != null) {
                uiTable.getCellEditor().stopCellEditing();
            }

            sqlTable.addColumn(new SQLColumn(""));
            getTableModel().addRow(new Object[5]);
            uiTable.setRowSelectionInterval(getTableModel().getRowCount() - 1, getTableModel().getRowCount() - 1);

            syncRowWithList(columnList.size() - 1);
            boldTitleLabel();
        }
    };

    private Action actionInsertCampo = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (columnList.isEmpty()) {
                return;
            }

            int rowSeleccionada = uiTable.getSelectedRow();

            if (!columnList.get(rowSeleccionada).getUncommittedName().isEmpty()) {
                if (uiTable.getCellEditor() != null) {
                    uiTable.getCellEditor().stopCellEditing();
                }

                sqlTable.insertColumn(rowSeleccionada, new SQLColumn(""));

                getTableModel().insertRow(rowSeleccionada, new Object[5]);
                uiTable.setRowSelectionInterval(rowSeleccionada, rowSeleccionada);

                syncRowWithList(rowSeleccionada);
                boldTitleLabel();
            }
        }
    };

    private Action actionDeleteCampo = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (columnList.isEmpty()) {
                return;
            }
            if (uiTable.getCellEditor() != null) {
                uiTable.getCellEditor().cancelCellEditing();
            }
            int[] selectedRowsIndexes = uiTable.getSelectedRows();
            int selRowStart = selectedRowsIndexes[0];
            for (int i = selectedRowsIndexes.length - 1; i >= 0; i--) {
                if (sqlTable.columnIsPK(columnList.get(selectedRowsIndexes[i]))) {
                    sqlTable.getPrimaryKey().removeColumn(columnList.get(selectedRowsIndexes[i]));
                }
                if (!columnList.get(selectedRowsIndexes[i]).isBrandNew()) {
                    sqlTable.getDroppedColumns().add(columnList.get(selectedRowsIndexes[i]));
                }
                sqlTable.removeColumn(selectedRowsIndexes[i]);
                getTableModel().removeRow(selectedRowsIndexes[i]);
            }
            if (uiTable.getRowCount() > 0) {
                //si la fila a remover fue la última en la tabla, seleccionamos la que ahora es última
                if (selRowStart == uiTable.getRowCount()) {
                    uiTable.setRowSelectionInterval(selRowStart - 1, selRowStart - 1);
                } else { //de lo contrario, seleccionamos la que nos cae de arriba, que ahora va a ocupar el lugar de la removida
                    uiTable.setRowSelectionInterval(selRowStart, selRowStart);
                }
            }
            boldTitleLabel();
        }
    };

    private ActionListener actionMoveColumns = e -> {
        if (task == TABLE_CREATE) {
            if (uiTable.getCellEditor() != null) {
                uiTable.getCellEditor().stopCellEditing();
            }
            UIMoveColumns uiMoveColumns = new UIMoveColumns(sqlTable, task);
            int result = uiMoveColumns.showDialog();
            if (result == UIMoveColumns.ORDER_CHANGED) {
                columnList = sqlTable.getColumns(); //refrescamos la referencia
                for (int i = 0; i < uiTable.getRowCount(); i++) {
                    syncRowWithList(i);
                }
            }
            return;
        }

        if (sqlTable.isReadyToMoveColumns()) {
            if (uiTable.getCellEditor() != null) {
                uiTable.getCellEditor().stopCellEditing();
            }
            UIMoveColumns uiMoveColumns = new UIMoveColumns(sqlTable, task);
            int result = uiMoveColumns.showDialog();
            if (result == UIMoveColumns.ORDER_CHANGED) {
                columnList = sqlTable.getColumns(); //refrescamos la referencia
                for (int i = 0; i < uiTable.getRowCount(); i++) {
                    syncRowWithList(i);
                }
                refreshPnlExtras();
            }
        } else {
            int opt = UIUtils.showOptionDialog("Mover columnas", "Se han detectado columnas con cambios pendientes. "
                    + "Para poder acceder a opciones avanzadas de orden de columnas es necesario hacer el commit en la base de datos.", null,
                    "Guardar cambios", "Cancelar");
            if (opt == 0) {
                commitUpdateTable();
            }
        }
    };

    private ActionListener actionAddFK = e -> {
        Object lastColName = null;
        if (uiTableFKs.getRowCount() > 0) {
            lastColName = uiTableFKs.getValueAt(uiTableFKs.getRowCount() - 1, 0);
        }

        if (uiTableFKs.getRowCount() == 0 || (lastColName != null && !lastColName.toString().isEmpty())) {
            getFKTableModel().addRow(new Object[4]);
            sqlTable.addFK(new SQLForeignKey(sqlTable.getName()));
            SwingUtilities.invokeLater(() -> {
                uiTableFKs.setRowSelectionInterval(getFKTableModel().getRowCount() - 1, getFKTableModel().getRowCount() - 1);
            });
            boldTitleLabel();
        }
    };

    private ActionListener actionRemoveFK = e -> {
        if (uiTableFKs.getCellEditor() != null) {
            uiTableFKs.getCellEditor().stopCellEditing();
        }
        int[] selectedRows = uiTableFKs.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            if (!fkList.get(selectedRows[i]).isBrandNew()) { //si la FK era nueva no hace falta droppearla ya que todavía no está agregada a la tabla
                sqlTable.getDroppedFKs().add(fkList.get(selectedRows[i]));
            }
            sqlTable.removeFK(selectedRows[i]);
            getFKTableModel().removeRow(selectedRows[i]);
            boldTitleLabel();
        }
        if (uiTableFKs.getRowCount() > 0) {
            //si la fila a remover fue la última en la tabla, seleccionamos la que ahora es última
            if (selectedRows[0] == uiTableFKs.getRowCount()) {
                uiTableFKs.setRowSelectionInterval(selectedRows[0] - 1, selectedRows[0] - 1);
            } else { //de lo contrario, seleccionamos la que nos cae de arriba, que ahora va a ocupar el lugar de la removida
                uiTableFKs.setRowSelectionInterval(selectedRows[0], selectedRows[0]);
            }
        }
    };

    private ActionListener actionAddPK = e -> {
        int row = uiTable.getSelectedRow();
        SQLColumn colSeleccionada = columnList.get(row);
        if (colSeleccionada.isPK()) {
            sqlTable.getPrimaryKey().removeColumn(colSeleccionada);
        } else {
            sqlTable.getPrimaryKey().addColumn(colSeleccionada);
        }
        syncRowWithList(row);
        boldTitleLabel();
    };

    private ActionListener actionSave = e -> {
        switch (task) {
            case TABLE_CREATE:
                commitNewTable();
                break;
            case TABLE_UPDATE:
                commitUpdateTable();
                break;
        }
    };

    private ActionListener actionSaveAsScript = e -> {
        FileManager.getInstance().saveFileAs("SQL", sqlPane.getText());
    };

    public void commitNewTable() {
        if (uiTable.getCellEditor() != null) {
            uiTable.getCellEditor().stopCellEditing();
        }
        String tableName = UIUtils.showInputDialog("Create new table", "Table name:", UIClient.getInstance());
        if (tableName == null) {
            return;
        }
        sqlTable.rename(tableName);
        String createTableStmt = accionMakeCreateTableStmt();

        SQLQuery createQuery = new SQLUpdateQuery(createTableStmt) {
            @Override
            public void onSuccess(int updateCount) {
                sqlTable.commit(tableName);
                task = TABLE_UPDATE;
                sqlPane.setText("");
                setTabTitle(getTabTitle());
                unboldTitleLabel();
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Create table", errMessage, null);
            }
        };
        createQuery.exec();
    }

    public void commitUpdateTable() {
        if (uiTable.getCellEditor() != null) {
            uiTable.getCellEditor().stopCellEditing();
        }
        String alterTableStmt = accionCreateAlterTableStmt();
        if (alterTableStmt.isEmpty()) {
            unboldTitleLabel();
            return;
        }

        SQLQuery updateQuery = new SQLUpdateQuery(alterTableStmt) {
            @Override
            public void onSuccess(int updateCount) {
                sqlTable.commit();
                sqlPane.setText("");
                unboldTitleLabel();
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Alter table", errMessage, null);
            }
        };
        updateQuery.exec();
    }

}
