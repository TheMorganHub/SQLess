package com.sqless.ui;

import com.sqless.utils.UIUtils;
import com.sqless.utils.SQLUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.settings.SessionSettings;
import com.sqless.utils.TextUtils;
import com.sqless.settings.UserPreferencesLoader;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;

public class UIDBExplorer extends javax.swing.JDialog {

    private UIClient client;
    private SQLConnectionManager conManager;
    private final ImageIcon FAVOURITE_ICON = new ImageIcon(getClass().
            getResource("/res/icons/ui_dbexplorer/FAVOURITE_ICON.png"));
    private SessionSettings sessionSettings;

    public UIDBExplorer(UIClient client) {
        super(client, true);
        this.client = client;
        this.conManager = SQLConnectionManager.getInstance();
        this.sessionSettings = SessionSettings.getINSTANCE();
        initComponents();

        prepareUI();
        setLocationRelativeTo(client);
        getRootPane().setDefaultButton(btnConnect);
    }

    public void prepareUI() {
        lblHost.setText(conManager.getServerHostname());
        loadTable();
        comboCharset.addItemListener(new CharsetChangeListener());
        showMasterDB((Boolean) sessionSettings.get(SessionSettings.Keys.SHOW_MASTER_DB));
        pack();
    }

    public void loadTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("");
        model.addColumn("Database");
        for (String dbName : SQLUtils.retrieveDBNamesFromServer(chkShowMaster.isSelected())) {
            Object[] row = {FAVOURITE_ICON, dbName};
            model.addRow(row);
        }

        tableDb.setModel(model);
        selectFirstRowIfNoDb();
        tableDb.setCellEditor(new UIButtonColumn(tableDb, new ActionAddFavourite(), 0, true));
        tableDb.packAll();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPaneMain = new javax.swing.JTabbedPane();
        pnlManager = new javax.swing.JPanel();
        btnDrop = new javax.swing.JButton();
        btnConnect = new javax.swing.JButton();
        scrMain = new javax.swing.JScrollPane();
        tableDb = new JXTable() {
            public boolean isCellEditable(int row, int column) {
                return column == 0 && !tableDb.getValueAt(row, 1).toString().equals("master");
            }
        };
        iLblHost = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        chkShowMaster = new javax.swing.JCheckBox();
        pnlCreation = new javax.swing.JPanel();
        iLblName = new javax.swing.JLabel();
        txtNewDbName = new javax.swing.JTextField();
        btnRestoreDefaults = new javax.swing.JButton();
        btnCreateDb = new javax.swing.JButton();
        iLblCharset = new javax.swing.JLabel();
        iLblCollation = new javax.swing.JLabel();
        comboCharset = new javax.swing.JComboBox<>();
        comboCollation = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database explorer");

        tabPaneMain.setFocusable(false);

        btnDrop.setText("Drop");
        btnDrop.setFocusable(false);
        btnDrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDropActionPerformed(evt);
            }
        });

        btnConnect.setText("Connect");
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
        HighlightPredicate myPredicate = new HighlightPredicate() {
            @Override
            public boolean isHighlighted(Component component, org.jdesktop.swingx.decorator.ComponentAdapter adapter) {
                return dbIsDefault(adapter.getValue().toString()) || adapter.row == getDefaultDbRow();
            }
        };
        ColorHighlighter highlighter = new ColorHighlighter(myPredicate, UIUtils.COOL_GREEN, Color.BLACK);
        tableDb.addHighlighter(highlighter);

        iLblHost.setText("Host:");

        lblHost.setText("###");

        chkShowMaster.setText("Show 'mysql' database");
        chkShowMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowMasterActionPerformed(evt);
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
                        .addComponent(iLblHost)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlManagerLayout.createSequentialGroup()
                        .addComponent(chkShowMaster)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                        .addComponent(btnDrop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConnect)))
                .addContainerGap())
        );
        pnlManagerLayout.setVerticalGroup(
            pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManagerLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblHost)
                    .addComponent(lblHost))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrMain, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkShowMaster, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnDrop)
                        .addComponent(btnConnect)))
                .addContainerGap())
        );

        tabPaneMain.addTab("Management", pnlManager);

        pnlCreation.setBorder(javax.swing.BorderFactory.createTitledBorder("Create a database"));

        iLblName.setText("Name:");

        btnRestoreDefaults.setText("Restore defaults");
        btnRestoreDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestoreDefaultsActionPerformed(evt);
            }
        });

        btnCreateDb.setText("Create");
        btnCreateDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateDbActionPerformed(evt);
            }
        });

        iLblCharset.setText("Character set:");

        iLblCollation.setText("Collation:");

        comboCharset.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ascii - US ASCII", "utf8 - UTF-8 Unicode", "utf16 - UTF-16 Unicode" }));

        javax.swing.GroupLayout pnlCreationLayout = new javax.swing.GroupLayout(pnlCreation);
        pnlCreation.setLayout(pnlCreationLayout);
        pnlCreationLayout.setHorizontalGroup(
            pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCreationLayout.createSequentialGroup()
                        .addGap(0, 228, Short.MAX_VALUE)
                        .addComponent(btnRestoreDefaults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCreateDb))
                    .addGroup(pnlCreationLayout.createSequentialGroup()
                        .addGroup(pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(iLblName, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iLblCharset)
                            .addComponent(iLblCollation))
                        .addGap(18, 18, 18)
                        .addGroup(pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNewDbName)
                            .addComponent(comboCharset, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboCollation, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlCreationLayout.setVerticalGroup(
            pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblName)
                    .addComponent(txtNewDbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iLblCharset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboCollation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iLblCollation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 206, Short.MAX_VALUE)
                .addGroup(pnlCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRestoreDefaults)
                    .addComponent(btnCreateDb))
                .addContainerGap())
        );

        tabPaneMain.addTab("Creation", pnlCreation);

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
        String dbName;
        //if there is a selection, prioritise selection over default db
        dbName = tableDb.getSelectedRow() == -1 ? getDefaultDbName() : getSelectedRowValue();

        if (dbName.equals(SQLUtils.getConnectedDBName())) {
            int confirmation = UIUtils.showYesNoOptionDialog("Connect to database", "You are already connected to "
                    + dbName + ".\nWould you like to refresh the connection?", JOptionPane.QUESTION_MESSAGE,
                    false, getParent());
            if (confirmation != 0) {
                return;
            }
        }

        conManager.setNewConnection(dbName, client);
        client.createJTree();
        dispose();
    }

    private void btnDropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDropActionPerformed
        actionDropDatabase();
    }//GEN-LAST:event_btnDropActionPerformed

    public void actionDropDatabase() {
        String dbName = tableDb.getSelectedRow() == -1 ? getDefaultDbName() : getSelectedRowValue();
        if (SQLUtils.getConnectedDBName().equals(dbName)) {
            int option = UIUtils.showOptionDialog("Drop database", "The database is current in use and "
                    + "cannot be dropped.\nWhat would you like to do?", getParent(),
                    "Cancel", "Close connection and drop");
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
            UIUtils.showErrorMessage("Error", "The database name cannot be empty.", getParent());
            return;
        }
        if (TextUtils.startsWithNumber(txtNewDbName.getText())) {
            UIUtils.showErrorMessage("Error", "The database name cannot start with a number.", getParent());
            return;
        }
        executeCreateScript();
    }//GEN-LAST:event_btnCreateDbActionPerformed

    private void btnRestoreDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestoreDefaultsActionPerformed
        restoreDefaults();
    }//GEN-LAST:event_btnRestoreDefaultsActionPerformed

    public void showMasterDB(boolean flag) {
        if (flag) {
            ((DefaultTableModel) tableDb.getModel()).addRow(new Object[]{FAVOURITE_ICON, "mysql"});
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
        UIUtils.showMessage("Restore defaults", "Default options have been restored successfully.", getParent());
    }

    public void executeCreateScript() {
        SQLQuery createDbQuery = new SQLUpdateQuery(scriptCreate()) {
            @Override
            public void onSuccess(int affectedRows) {
                UIUtils.showMessage("Database creation", "The database " + txtNewDbName.getText()
                        + " has been created successfully.", getParent());
                DefaultTableModel model = (DefaultTableModel) tableDb.getModel();
                model.addRow(new Object[]{FAVOURITE_ICON, txtNewDbName.getText()});
                UIUtils.sortTable(tableDb, 1);
            }
        };
        createDbQuery.exec();
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
        return "CREATE DATABASE " + newDbName + " DEFAULT CHARACTER SET " + defCharSet + " DEFAULT COLLATE " + defCollation;
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
     * @param closeConnection Whether to close the connection to said database
     * before dropping it. The caller of this method must set this flag to
     * {@code true} if the database to be dropped is in use at the time this
     * method is called.
     */
    public void dropDatabase(String dbName, boolean closeConnection) {
        int confirmation = UIUtils.showConfirmationMessage("Drop database ",
                "Are you sure you wish to permanently drop database " + dbName + "?", getParent());
        if (confirmation != 0) {
            return;
        }
        String sql = (closeConnection ? "USE mysql; " : "") + "DROP DATABASE " + dbName;
        SQLQuery dropDatabaseQuery = new SQLUpdateQuery(sql) {
            @Override
            public void onSuccess(int affectedRows) {
                UIUtils.showMessage("Drop database", "Database " + dbName + " has "
                        + "been dropped successfully.", getParent());
                removeRow(dbName);
                if (closeConnection) {
                    client.clearJTree();
                }
            }

            @Override
            public void onFailure(String err) {
                UIUtils.showErrorMessage("Drop database", err, getParent());
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
            if (model.getValueAt(i, 1).toString().equals(value)) {
                model.removeRow(i);
                break;
            }
        }
    }

    public String getSelectedRowValue() {
        return tableDb.getStringAt(tableDb.getSelectedRow(), 1);
    }

    /**
     * Selects the first row if no default db is found. If a default DB is
     * found, this method does nothing.
     */
    public void selectFirstRowIfNoDb() {
        int defaultDbRow = getDefaultDbRow();
        if (defaultDbRow == -1 && tableDb.getRowCount() > 1) {
            tableDb.setColumnSelectionInterval(1, 1);
            tableDb.setRowSelectionInterval(0, 0);
        }

        //si no hay ninguna DB, ir a creation tab
        if (tableDb.getRowCount() == 0) {
            tabPaneMain.setSelectedIndex(1);
            txtNewDbName.requestFocus();
        }
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
            String message;
            UserPreferencesLoader loader = UserPreferencesLoader.getInstance();
            if (selectedDbName.equals(getDefaultDbName())) {
                newDefaultDb = loader.getDefaultFor("Default.Database"); //clears default db
                message = "The default database has been cleared.";

            } else {
                newDefaultDb = selectedDbName;
                message = "The default database is now " + newDefaultDb;
            }

            loader.set("Default.Database", newDefaultDb);
            loader.flushFile();
            tableDb.clearSelection();
            selectFirstRowIfNoDb();
            UIUtils.showMessage("Default database change", message, getParent());
        }

    }

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
    private javax.swing.JButton btnRestoreDefaults;
    private javax.swing.JCheckBox chkShowMaster;
    private javax.swing.JComboBox<String> comboCharset;
    private javax.swing.JComboBox<String> comboCollation;
    private javax.swing.JLabel iLblCharset;
    private javax.swing.JLabel iLblCollation;
    private javax.swing.JLabel iLblHost;
    private javax.swing.JLabel iLblName;
    private javax.swing.JLabel lblHost;
    private javax.swing.JPanel pnlCreation;
    private javax.swing.JPanel pnlManager;
    private javax.swing.JScrollPane scrMain;
    private javax.swing.JTabbedPane tabPaneMain;
    private org.jdesktop.swingx.JXTable tableDb;
    private javax.swing.JTextField txtNewDbName;
    // End of variables declaration//GEN-END:variables
}
