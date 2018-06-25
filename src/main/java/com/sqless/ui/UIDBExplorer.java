package com.sqless.ui;

import com.sqless.file.FileManager;
import com.sqless.utils.UIUtils;
import com.sqless.utils.SQLUtils;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.settings.SessionSettings;
import com.sqless.utils.TextUtils;
import com.sqless.settings.UserPreferencesLoader;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;

public class UIDBExplorer extends javax.swing.JDialog {

    private UIClient client;
    private SQLConnectionManager conManager;
    private final ImageIcon FAVOURITE_ICON = new ImageIcon(getClass().
            getResource("/icons/ui_dbexplorer/FAVOURITE_ICON.png"));
    private final ImageIcon NOT_FAVOURITE_ICON = new ImageIcon(getClass().
            getResource("/icons/ui_dbexplorer/NOT_FAVOURITE_ICON.png"));
    private SessionSettings sessionSettings;
    private List<String> newDatabases;

    public UIDBExplorer() {
        super(UIClient.getInstance(), true);
        this.client = UIClient.getInstance();
        this.conManager = SQLConnectionManager.getInstance();
        this.sessionSettings = SessionSettings.getINSTANCE();
        this.newDatabases = new ArrayList<>();
        initComponents();

        prepareUI();
        setLocationRelativeTo(client);
        getRootPane().setDefaultButton(btnConnect);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String currentDb = SQLConnectionManager.getInstance().getConnectedDB().getName();
                if (currentDb.equals("mysql")) {
                    UIClient.getInstance().createJTree();
                }
            }
        });
    }

    public void prepareUI() {
        lblHost.setText(conManager.getHostName() + ":" + conManager.getPort());
        loadTable();
        comboCharset.addItemListener(new CharsetChangeListener());
        showMasterDB((Boolean) sessionSettings.get(SessionSettings.Keys.SHOW_MASTER_DB));
        tabPaneMain.addChangeListener(c -> {
            int tabNum = tabPaneMain.getSelectedIndex();
            switch (tabNum) {
                case 0:
                    getRootPane().setDefaultButton(btnConnect);
                    break;
                case 1:
                    getRootPane().setDefaultButton(btnCreateDb);
                    break;
            }
        });
        chkIncludeCreate.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            lblNombreCreate.setEnabled(selected);
            txtNombreCreate.setEnabled(selected);
            lblNoticeCreate.setVisible(!selected);
        });

        pack();
    }

    public boolean dbIsNew(String dbName) {
        for (String newDatabase : newDatabases) {
            if (newDatabase.equalsIgnoreCase(dbName)) {
                return true;
            }
        }
        return false;
    }

    public void loadTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("");
        model.addColumn("Bases de datos");
        for (String dbName : SQLUtils.retrieveDBNamesFromServer()) {
            Object[] row = {dbIsDefault(dbName) ? FAVOURITE_ICON : NOT_FAVOURITE_ICON, dbName};
            model.addRow(row);
        }

        tableDb.setModel(model);
        //si no hay ninguna DB, ir a creation tab
        if (tableDb.getRowCount() == 0) {
            tabPaneMain.setSelectedIndex(1);
            txtNewDbName.requestFocus();
            return;
        }
        tableDb.setCellEditor(new UIButtonColumn(tableDb, new ActionAddFavourite(), 0, true));
        int rowOnStart = getDefaultDbRow() == -1 ? 0 : getDefaultDbRow();
        tableDb.setRowSelectionInterval(rowOnStart, rowOnStart);
        tableDb.packAll();
        tableDb.addMouseListener(UIUtils.mouseListenerWithPopUpMenuForJTable(popTable, tableDb));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popTable = new javax.swing.JPopupMenu();
        menuItemDropDb = new javax.swing.JMenuItem();
        tabPaneMain = new javax.swing.JTabbedPane();
        pnlManager = new javax.swing.JPanel();
        btnDrop = new javax.swing.JButton();
        btnConnect = new javax.swing.JButton();
        scrMain = new javax.swing.JScrollPane();
        tableDb = new JXTable() {
            public boolean isCellEditable(int row, int column) {
                return column == 0 && !tableDb.getValueAt(row, 1).toString().equals("mysql");
            }
        };
        iLblHost = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        chkShowMaster = new javax.swing.JCheckBox();
        btnRefresh = new javax.swing.JButton();
        pnlCreation = new javax.swing.JPanel();
        pnlManualCreation = new javax.swing.JPanel();
        iLblName = new javax.swing.JLabel();
        txtNewDbName = new javax.swing.JTextField();
        btnRestoreDefaults = new javax.swing.JButton();
        btnCreateDb = new javax.swing.JButton();
        iLblCharset = new javax.swing.JLabel();
        iLblCollation = new javax.swing.JLabel();
        comboCharset = new javax.swing.JComboBox<>();
        comboCollation = new javax.swing.JComboBox<>();
        pnlScriptCreate = new javax.swing.JPanel();
        iLblUbicacion = new javax.swing.JLabel();
        txtRutaScript = new javax.swing.JTextField();
        btnExplorar = new javax.swing.JButton();
        chkIncludeCreate = new javax.swing.JCheckBox();
        btnEjecutarScript = new javax.swing.JButton();
        txtNombreCreate = new javax.swing.JTextField();
        lblNombreCreate = new javax.swing.JLabel();
        lblNoticeCreate = new javax.swing.JLabel();

        menuItemDropDb.setAction(actionDropDatabase);
        menuItemDropDb.setText("Eliminar base de datos");
        popTable.add(menuItemDropDb);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrador de bases de datos");

        tabPaneMain.setFocusable(false);

        btnDrop.setText("Eliminar");
        btnDrop.setFocusable(false);
        btnDrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDropActionPerformed(evt);
            }
        });

        btnConnect.setText("Conectar");
        btnConnect.setFocusable(false);
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });
        btnConnect.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnConnectKeyPressed(evt);
            }
        });

        tableDb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableDb.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tableDb.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableDb.setGridColor(new java.awt.Color(204, 204, 204));
        tableDb.setRowHeight(20);
        tableDb.setSelectionBackground(new java.awt.Color(183, 219, 255));
        tableDb.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tableDb.setShowGrid(true);
        tableDb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tableDbMousePressed(evt);
            }
        });
        tableDb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableDbKeyPressed(evt);
            }
        });
        scrMain.setViewportView(tableDb);
        tableDb.getTableHeader().setReorderingAllowed(false);
        tableDb.getTableHeader().setResizingAllowed(false);
        tableDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        iLblHost.setText("Host:");

        lblHost.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHost.setText("###");

        chkShowMaster.setText("Mostrar base de datos 'mysql'");
        chkShowMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowMasterActionPerformed(evt);
            }
        });

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_dbexplorer/REFRESH_ICON.png"))); // NOI18N
        btnRefresh.setToolTipText("Refrescar lista de bases de datos");
        btnRefresh.setFocusable(false);
        btnRefresh.setPreferredSize(new java.awt.Dimension(49, 23));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlManagerLayout = new javax.swing.GroupLayout(pnlManager);
        pnlManager.setLayout(pnlManagerLayout);
        pnlManagerLayout.setHorizontalGroup(
            pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManagerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrMain, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlManagerLayout.createSequentialGroup()
                        .addComponent(chkShowMaster)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDrop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConnect))
                    .addGroup(pnlManagerLayout.createSequentialGroup()
                        .addComponent(iLblHost)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlManagerLayout.setVerticalGroup(
            pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManagerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblHost)
                    .addComponent(lblHost))
                .addGap(9, 9, 9)
                .addComponent(scrMain, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(chkShowMaster, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDrop)
                            .addComponent(btnConnect)))
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabPaneMain.addTab("Administrar", pnlManager);

        pnlCreation.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        pnlManualCreation.setBorder(javax.swing.BorderFactory.createTitledBorder("Crear base de datos vacía"));

        iLblName.setText("Nombre:");

        btnRestoreDefaults.setText("Restaurar predeterminados");
        btnRestoreDefaults.setFocusable(false);
        btnRestoreDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestoreDefaultsActionPerformed(evt);
            }
        });

        btnCreateDb.setText("Crear");
        btnCreateDb.setFocusable(false);
        btnCreateDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateDbActionPerformed(evt);
            }
        });

        iLblCharset.setText("Character set:");

        iLblCollation.setText("Collation:");

        comboCharset.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ascii - US ASCII", "utf8 - UTF-8 Unicode", "utf16 - UTF-16 Unicode" }));
        comboCharset.setFocusable(false);

        comboCollation.setFocusable(false);

        javax.swing.GroupLayout pnlManualCreationLayout = new javax.swing.GroupLayout(pnlManualCreation);
        pnlManualCreation.setLayout(pnlManualCreationLayout);
        pnlManualCreationLayout.setHorizontalGroup(
            pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManualCreationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlManualCreationLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRestoreDefaults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCreateDb, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlManualCreationLayout.createSequentialGroup()
                        .addGroup(pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlManualCreationLayout.createSequentialGroup()
                                .addComponent(iLblCollation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(pnlManualCreationLayout.createSequentialGroup()
                                .addComponent(iLblCharset, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))
                        .addGroup(pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboCharset, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboCollation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlManualCreationLayout.createSequentialGroup()
                        .addComponent(iLblName, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNewDbName)))
                .addContainerGap())
        );
        pnlManualCreationLayout.setVerticalGroup(
            pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlManualCreationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNewDbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iLblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblCharset)
                    .addComponent(comboCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboCollation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iLblCollation))
                .addGap(18, 18, 18)
                .addGroup(pnlManualCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRestoreDefaults)
                    .addComponent(btnCreateDb))
                .addContainerGap())
        );

        pnlScriptCreate.setBorder(javax.swing.BorderFactory.createTitledBorder("Crear base de datos desde script"));

        iLblUbicacion.setText("Ubicación:");

        btnExplorar.setText("Explorar...");
        btnExplorar.setFocusable(false);
        btnExplorar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExplorarActionPerformed(evt);
            }
        });

        chkIncludeCreate.setSelected(true);
        chkIncludeCreate.setText("Incluir CREATE DATABASE [nombre] al principio del script");
        chkIncludeCreate.setFocusable(false);

        btnEjecutarScript.setText("Ejecutar");
        btnEjecutarScript.setFocusable(false);
        btnEjecutarScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarScriptActionPerformed(evt);
            }
        });

        lblNombreCreate.setText("Nombre:");

        lblNoticeCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_general/ATTENTION_ICON.png"))); // NOI18N
        lblNoticeCreate.setText("Se asumirá que el script dado ya incluye la sentencia CREATE DATABASE");

        javax.swing.GroupLayout pnlScriptCreateLayout = new javax.swing.GroupLayout(pnlScriptCreate);
        pnlScriptCreate.setLayout(pnlScriptCreateLayout);
        pnlScriptCreateLayout.setHorizontalGroup(
            pnlScriptCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScriptCreateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScriptCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlScriptCreateLayout.createSequentialGroup()
                        .addComponent(lblNombreCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombreCreate))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScriptCreateLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnEjecutarScript, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlScriptCreateLayout.createSequentialGroup()
                        .addComponent(iLblUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRutaScript)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExplorar))
                    .addGroup(pnlScriptCreateLayout.createSequentialGroup()
                        .addComponent(chkIncludeCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 9, Short.MAX_VALUE))
                    .addComponent(lblNoticeCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlScriptCreateLayout.setVerticalGroup(
            pnlScriptCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScriptCreateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScriptCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblUbicacion)
                    .addComponent(txtRutaScript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExplorar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkIncludeCreate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlScriptCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreCreate))
                .addGap(18, 18, 18)
                .addComponent(btnEjecutarScript)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(lblNoticeCreate)
                .addContainerGap())
        );

        lblNoticeCreate.setVisible(false);

        javax.swing.GroupLayout pnlCreationLayout = new javax.swing.GroupLayout(pnlCreation);
        pnlCreation.setLayout(pnlCreationLayout);
        pnlCreationLayout.setHorizontalGroup(
            pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlScriptCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlManualCreation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlCreationLayout.setVerticalGroup(
            pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreationLayout.createSequentialGroup()
                .addComponent(pnlManualCreation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlScriptCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPaneMain.addTab("Crear", pnlCreation);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPaneMain, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPaneMain, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        actionConnectToDatabase();
    }//GEN-LAST:event_btnConnectActionPerformed

    /**
     * The action of connecting to a database. This method has to be called by
     * any {@code Component} or event capable of connecting the user to a
     * database.
     */
    public void actionConnectToDatabase() {
        if (tableDb.getSelectedRow() == -1 && getDefaultDbRow() == -1) {
            UIUtils.showErrorMessage("Conectar a una base de datos", "Debes elegir una base de datos", this);
            return;
        }
        //if there is a selection, prioritise selection over default db
        String dbName = tableDb.getSelectedRow() != -1 ? getSelectedRowValue() : getDefaultDbName();

        if (dbName.equals(SQLUtils.getConnectedDBName())) {
            int confirmation = UIUtils.showYesNoOptionDialog("Conectar a una base de datos", "Ya estás conectado a "
                    + dbName + ".\n¿Te gustaría refrescar la conexión?", JOptionPane.QUESTION_MESSAGE,
                    false, getParent());
            if (confirmation != 0) {
                return;
            }
        }

        GenericWaitingDialog waitDialog = new GenericWaitingDialog("Conectando a " + dbName + "...", true);
        setCursor(UIUtils.WAIT_CURSOR);
        waitDialog.display(() -> {
            if (dbIsNew(dbName)) {
                conManager.setNewConnectionNoRepair(dbName, true, client);
            } else {
                conManager.setNewConnectionNoRepair(dbName, client);
            }
            setCursor(UIUtils.DEFAULT_CURSOR);
            if (!conManager.connectionIsClosed()) { //la conexión fue exitosa
                client.createJTree();
                dispose();
                client.onNewConnection();
            }
        });
    }

    private void btnDropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDropActionPerformed
        actionDropDatabase();
    }//GEN-LAST:event_btnDropActionPerformed

    public void actionDropDatabase() {
        String dbName = tableDb.getSelectedRow() == -1 ? getDefaultDbName() : getSelectedRowValue();
        if (SQLUtils.getConnectedDBName().equalsIgnoreCase(dbName)) {
            int option = UIUtils.showOptionDialog("Eliminar base de datos", "La base de datos está en uso.\n¿Qué deseas hacer?", getParent(),
                    "Cancelar", "Cerrar conexión y eliminar");
            switch (option) {
                case 1:
                    dropDatabase(dbName, true);
                    break;
            }
        } else {
            dropDatabase(dbName, false);
        }
    }

    private void tableDbMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableDbMousePressed
        if (SwingUtilities.isLeftMouseButton(evt)) {
            if (evt.getClickCount() == 2) {
                actionConnectToDatabase();
            }
        }
    }//GEN-LAST:event_tableDbMousePressed

    private void tableDbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableDbKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                actionDropDatabase();
                break;
            case KeyEvent.VK_ENTER:
                actionConnectToDatabase();
                break;
        }
    }//GEN-LAST:event_tableDbKeyPressed

    private void btnConnectKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnConnectKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                actionDropDatabase();
                return;
        }

        //if anything other than the keys above or ENTER is pressed, 
        //give focus to table and select first row.
        if (evt.getKeyCode() != KeyEvent.VK_ENTER) {
            tableDb.setColumnSelectionInterval(1, 1);
            tableDb.setRowSelectionInterval(0, 0);
            tableDb.requestFocus();
        }
    }//GEN-LAST:event_btnConnectKeyPressed

    private void chkShowMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowMasterActionPerformed
        showMasterDB(chkShowMaster.isSelected());
    }//GEN-LAST:event_chkShowMasterActionPerformed

    private void btnCreateDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateDbActionPerformed
        if (txtNewDbName.getText().isEmpty()) {
            UIUtils.showErrorMessage("Error", "El nombre de la base de datos no puede estar vacío.", getParent());
            return;
        }
        if (TextUtils.startsWithNumber(txtNewDbName.getText())) {
            UIUtils.showErrorMessage("Error", "El nombre de la base de datos no puede comenzar con un número.", getParent());
            return;
        }
        if (dbExists(txtNewDbName.getText())) {
            UIUtils.showErrorMessage("Error", "La base de datos ya existe. Elige otro nombre.", getParent());
            return;
        }
        executeCreateScript();
    }//GEN-LAST:event_btnCreateDbActionPerformed

    private void btnRestoreDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestoreDefaultsActionPerformed
        restoreDefaults();
    }//GEN-LAST:event_btnRestoreDefaultsActionPerformed

    private void btnExplorarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExplorarActionPerformed
        FileManager.getInstance().loadFile(filepath -> {
            txtRutaScript.setText(filepath);
        });
    }//GEN-LAST:event_btnExplorarActionPerformed

    private void btnEjecutarScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarScriptActionPerformed
        actionExecuteScript();
    }//GEN-LAST:event_btnEjecutarScriptActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        actionRefreshTableDBs();
    }//GEN-LAST:event_btnRefreshActionPerformed

    public void actionExecuteScript() {
        String ruta = txtRutaScript.getText();
        if (ruta == null || ruta.isEmpty()) {
            UIUtils.showErrorMessage("Crear base de datos", "La ruta no puede estar vacía.", client);
            return;
        }

        String[] executeBeforeSql = null;
        String nombreCreate = txtNombreCreate.getText();
        if (chkIncludeCreate.isSelected()) {
            if (nombreCreate == null || nombreCreate.isEmpty()) {
                UIUtils.showErrorMessage("Crear base de datos", "El nombre de la base de datos no puede estar vacío si se desea incluir la sentencia CREATE DATABASE.", client);
                return;
            }
            executeBeforeSql = new String[]{"CREATE DATABASE `" + nombreCreate + "`", "USE `" + nombreCreate + "`"};
        }
        UIExecuteFromScript uiExecuteFromScript = new UIExecuteFromScript(ruta, executeBeforeSql);
        uiExecuteFromScript.start(() -> {
            tabPaneMain.setSelectedIndex(0);
            actionRefreshTableDBs();
        });
    }

    public void actionRefreshTableDBs() {
        loadTable();
        showMasterDB(chkShowMaster.isSelected());
    }

    public void showMasterDB(boolean flag) {
        if (flag) {
            ((DefaultTableModel) tableDb.getModel()).addRow(new Object[]{null, "mysql"});
            UIUtils.sortTable(tableDb, 1);
        } else {
            removeRow("mysql");
        }
        sessionSettings.put(SessionSettings.Keys.SHOW_MASTER_DB, flag);
        chkShowMaster.setSelected(flag);
    }

    /**
     * Restores all the fields in the 'create' tab to their defaults.
     */
    public void restoreDefaults() {
        txtNewDbName.setText("");
        comboCharset.setSelectedItem("Default");
        comboCollation.setSelectedItem("utf8_general_ci");
    }

    public void executeCreateScript() {
        GenericWaitingDialog waitDialog = new GenericWaitingDialog("Creando base de datos...");
        SQLQuery createDbQuery = new SQLUpdateQuery(scriptCreate()) {
            @Override
            public void onSuccess(int affectedRows) {
                String newDbName = txtNewDbName.getText();
                DefaultTableModel model = (DefaultTableModel) tableDb.getModel();
                model.addRow(new Object[]{NOT_FAVOURITE_ICON, newDbName});
                UIUtils.sortTable(tableDb, 1);
                newDatabases.add(newDbName);
                waitDialog.dispose();
                int opt = UIUtils.showConfirmationMessage("Creación de base de datos", "La base de datos " + newDbName
                        + " fue creada exitosamente.\n¿Te gustaría que SQLess se conecte a ella?", getParent());
                if (opt == 0) {
                    SQLConnectionManager.getInstance().setNewConnection(newDbName, true, client);
                    client.createJTree();
                    dispose();
                    client.onNewConnection();
                }
            }

            @Override
            public void onFailure(String errMessage) {
                waitDialog.dispose();
                UIUtils.showErrorMessage("Error", "La base de datos no se pudo crear.\nEl servidor respondió con mensaje: " + errMessage, client);
            }
        };

        waitDialog.display(() -> {
            createDbQuery.exec();
        });
    }

    /**
     * Produces a SQL CREATE script for a new database.
     *
     * @return a ready to execute SQL CREATE DATABASE statement with data taken
     * from the Create tab.
     */
    public String scriptCreate() {
        String newDbName = txtNewDbName.getText();
        String defCharSet = getCharSetFromCombo();
        String defCollation = comboCollation.getSelectedItem().toString();
        return "CREATE DATABASE `" + newDbName + "` DEFAULT CHARACTER SET " + defCharSet + " DEFAULT COLLATE " + defCollation;
    }

    public String getCharSetFromCombo() {
        String item = comboCharset.getSelectedItem().toString();
        return item.equals("Default") ? "utf8" : item.substring(0, item.indexOf('-') - 1);
    }

    /**
     * Permanently drops a database from the DB engine, and if successful,
     * removes the row that contained said database from the UI table.
     *
     * @param dbName the name of the database to remove.
     * @param closeCurrentConnection Whether to close the connection to said
     * database before dropping it. The caller of this method must set this flag
     * to {@code true} if the database to be dropped is in use at the time this
     * method is called.
     */
    public void dropDatabase(String dbName, boolean closeCurrentConnection) {
        int confirmation = UIUtils.showConfirmationMessage("Eliminar base de datos",
                "¿Estás seguro que quieres eliminar permanentemente la base de datos '" + dbName + "'?", getParent());
        if (confirmation != 0) {
            return;
        }
        if (closeCurrentConnection) {
            SQLConnectionManager.getInstance().setNewConnection("mysql", null);
        }
        String sql = "DROP DATABASE `" + dbName + "`";
        SQLQuery dropDatabaseQuery = new SQLUpdateQuery(sql) {
            @Override
            public void onSuccess(int affectedRows) {
                removeRow(dbName);
                if (closeCurrentConnection) {
                    client.clearJTree();
                }
            }

            @Override
            public void onFailure(String err) {
                UIUtils.showErrorMessage("Eliminar base de datos", err, getParent());
            }
        };
        dropDatabaseQuery.exec();
    }

    /**
     * Removes the row that has the specified value in column index 1.
     *
     * @param value a {@code String}.
     */
    public void removeRow(String value) {
        DefaultTableModel model = (DefaultTableModel) tableDb.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).toString().equalsIgnoreCase(value)) {
                model.removeRow(i);
                break;
            }
        }
    }

    public String getSelectedRowValue() {
        return tableDb.getStringAt(tableDb.getSelectedRow(), 1);
    }

    /**
     * Returns the row number that holds the name of the default database from
     * {@link UserPreferencesLoader}.
     *
     * @return an {@code int} with the row number of the default database. If no
     * default database is found this method will return -1.
     */
    public int getDefaultDbRow() {
        String defaultDbName = getDefaultDbName();
        for (int i = 0; i < tableDb.getRowCount(); i++) {
            String value = tableDb.getStringAt(i, 1);
            if (value.equals(defaultDbName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if a specified database name is the same as the default
     * database's.
     *
     * @param dbName The database name to check against the default database's.
     * @return {@code true} if the given name equals the default database's.
     */
    public boolean dbIsDefault(String dbName) {
        return dbName.equals(getDefaultDbName());
    }

    public boolean dbExists(String db) {
        for (int i = 0; i < tableDb.getRowCount(); i++) {
            if (tableDb.getValueAt(i, 1).toString().equalsIgnoreCase(db)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the default database's name from {@link UserPreferencesLoader}
     * using the {@code Default.Database} key.
     *
     * @return the default database's name.
     */
    public String getDefaultDbName() {
        return UserPreferencesLoader.getInstance().getProperty("Default.Database");
    }

    /**
     * Represents the action of clicking the button in column 0. This makes the
     * database held by that row the default database in
     * {@link UserPreferencesLoader}.
     */
    private class ActionAddFavourite extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedDbName = getSelectedRowValue();
            String newDefaultDb;
            UserPreferencesLoader loader = UserPreferencesLoader.getInstance();
            if (selectedDbName.equals(getDefaultDbName())) {
                newDefaultDb = loader.getDefaultFor("Default.Database"); //clears default db
            } else {
                newDefaultDb = selectedDbName;
            }

            for (int i = 0; i < tableDb.getRowCount(); i++) {
                tableDb.setValueAt(NOT_FAVOURITE_ICON, i, 0); //limpiamos favorito anterior
                String valueAt = tableDb.getValueAt(i, 1).toString();
                if (valueAt.equals(newDefaultDb)) {
                    tableDb.setValueAt(FAVOURITE_ICON, i, 0); //seteanos favorito nuevo
                }
            }

            loader.set("Default.Database", newDefaultDb);
            loader.flushFile();
        }

    }

    private Action actionDropDatabase = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            actionDropDatabase();
        }
    };

    private class CharsetChangeListener implements ItemListener {

        private final DefaultComboBoxModel<String> utf8Collations
                = new DefaultComboBoxModel<>(new String[]{"utf8_general_ci", "utf8_unicode_ci", "utf8_spanish_ci"});
        private final DefaultComboBoxModel<String> utf16Collations
                = new DefaultComboBoxModel<>(new String[]{"utf16_general_ci", "utf16_unicode_ci", "utf16_spanish_ci"});
        private final DefaultComboBoxModel<String> asciiCollations
                = new DefaultComboBoxModel<>(new String[]{"ascii_general_ci", "ascii_bin"});

        public CharsetChangeListener() {
            comboCollation.setModel(utf8Collations);
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                switch (event.getItem().toString()) {
                    case "ascii - US ASCII":
                        comboCollation.setModel(asciiCollations);
                        break;
                    case "utf16 - UTF-16 Unicode":
                        comboCollation.setModel(utf16Collations);
                        break;
                    default:
                        comboCollation.setModel(utf8Collations);
                        break;
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnCreateDb;
    private javax.swing.JButton btnDrop;
    private javax.swing.JButton btnEjecutarScript;
    private javax.swing.JButton btnExplorar;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRestoreDefaults;
    private javax.swing.JCheckBox chkIncludeCreate;
    private javax.swing.JCheckBox chkShowMaster;
    private javax.swing.JComboBox<String> comboCharset;
    private javax.swing.JComboBox<String> comboCollation;
    private javax.swing.JLabel iLblCharset;
    private javax.swing.JLabel iLblCollation;
    private javax.swing.JLabel iLblHost;
    private javax.swing.JLabel iLblName;
    private javax.swing.JLabel iLblUbicacion;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblNombreCreate;
    private javax.swing.JLabel lblNoticeCreate;
    private javax.swing.JMenuItem menuItemDropDb;
    private javax.swing.JPanel pnlCreation;
    private javax.swing.JPanel pnlManager;
    private javax.swing.JPanel pnlManualCreation;
    private javax.swing.JPanel pnlScriptCreate;
    private javax.swing.JPopupMenu popTable;
    private javax.swing.JScrollPane scrMain;
    private javax.swing.JTabbedPane tabPaneMain;
    private org.jdesktop.swingx.JXTable tableDb;
    private javax.swing.JTextField txtNewDbName;
    private javax.swing.JTextField txtNombreCreate;
    private javax.swing.JTextField txtRutaScript;
    // End of variables declaration//GEN-END:variables
}
