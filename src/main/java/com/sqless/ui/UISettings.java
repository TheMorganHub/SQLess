package com.sqless.ui;

import com.sqless.utils.SQLUtils;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.sqless.settings.UserPreferencesLoader;
import com.sqless.utils.MiscUtils;

public class UISettings extends javax.swing.JDialog {

    private UIClient client;
    private UserPreferencesLoader userPreferences;
    private List<Component> modifiedComponents;
    /**
     * The flag that will decide if the queueListener in each settings component
     * will take events. This is done to avoid {@code NullPointerExceptions}
     * upon loading the UI due to "false" events caused by {@code setText()} or
     * methods that modify components at the time of initialisation. This flag
     * will be set to {@code true} after everything runs in
     * {@link UISettings#prepareUI()}.
     */
    private boolean enableListeners;

    public UISettings(UIClient client) {
        super(client, true);
        initComponents();
        this.client = client;
        this.userPreferences = UserPreferencesLoader.getInstance();
        setLocationRelativeTo(client);
        prepareUI();
        getRootPane().setDefaultButton(btnSave);
        modifiedComponents = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabSettings = new javax.swing.JTabbedPane();
        pnlConnection = new javax.swing.JPanel();
        pnlAuthentication = new javax.swing.JPanel();
        btnModifySettings = new javax.swing.JButton();
        pnlDatabases = new javax.swing.JPanel();
        comboDatabases = new javax.swing.JComboBox<>();
        iLblDefaultDbs = new javax.swing.JLabel();
        pnlDirectories = new javax.swing.JPanel();
        iLblDefaultSaveDir = new javax.swing.JLabel();
        txtDefaultSaveDir = new javax.swing.JTextField();
        btnBrowseDir = new javax.swing.JButton();
        btnRestoreDefaults = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Preferencias");

        tabSettings.setFocusable(false);

        pnlAuthentication.setBorder(javax.swing.BorderFactory.createTitledBorder("Autenticación"));
        pnlAuthentication.setName("pnlAuthentication"); // NOI18N

        btnModifySettings.setText("Edita la configuración de tu conexión");
        btnModifySettings.setFocusable(false);
        btnModifySettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifySettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAuthenticationLayout = new javax.swing.GroupLayout(pnlAuthentication);
        pnlAuthentication.setLayout(pnlAuthenticationLayout);
        pnlAuthenticationLayout.setHorizontalGroup(
            pnlAuthenticationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAuthenticationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnModifySettings)
                .addContainerGap(316, Short.MAX_VALUE))
        );
        pnlAuthenticationLayout.setVerticalGroup(
            pnlAuthenticationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAuthenticationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnModifySettings)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatabases.setBorder(javax.swing.BorderFactory.createTitledBorder("Bases de datos"));

        comboDatabases.setFocusable(false);
        comboDatabases.setName("Default.Database"); // NOI18N
        comboDatabases.addActionListener(new ActionAddToQueue(comboDatabases));

        iLblDefaultDbs.setText("Base de datos predeterminada");

        javax.swing.GroupLayout pnlDatabasesLayout = new javax.swing.GroupLayout(pnlDatabases);
        pnlDatabases.setLayout(pnlDatabasesLayout);
        pnlDatabasesLayout.setHorizontalGroup(
            pnlDatabasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatabasesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iLblDefaultDbs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboDatabases, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDatabasesLayout.setVerticalGroup(
            pnlDatabasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatabasesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatabasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblDefaultDbs)
                    .addComponent(comboDatabases, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlConnectionLayout = new javax.swing.GroupLayout(pnlConnection);
        pnlConnection.setLayout(pnlConnectionLayout);
        pnlConnectionLayout.setHorizontalGroup(
            pnlConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConnectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlAuthentication, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDatabases, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlConnectionLayout.setVerticalGroup(
            pnlConnectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConnectionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAuthentication, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDatabases, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(197, Short.MAX_VALUE))
        );

        tabSettings.addTab("Conexión", pnlConnection);

        iLblDefaultSaveDir.setText("Directorio de almacenamiento de archivos");

        txtDefaultSaveDir.setName("Default.SaveDir"); // NOI18N
        txtDefaultSaveDir.getDocument().addDocumentListener(new TextFieldChangeListener(txtDefaultSaveDir));

        btnBrowseDir.setText("Explorar...");
        btnBrowseDir.setFocusable(false);
        btnBrowseDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseDirActionPerformed(evt);
            }
        });

        btnRestoreDefaults.setText("Restaurar predeterminado");
        btnRestoreDefaults.setFocusable(false);
        btnRestoreDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestoreDefaultsActionPerformed(evt);
            }
        });

        btnOpen.setText("Abrir");
        btnOpen.setFocusable(false);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDirectoriesLayout = new javax.swing.GroupLayout(pnlDirectories);
        pnlDirectories.setLayout(pnlDirectoriesLayout);
        pnlDirectoriesLayout.setHorizontalGroup(
            pnlDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDirectoriesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDirectoriesLayout.createSequentialGroup()
                        .addComponent(txtDefaultSaveDir)
                        .addGap(8, 8, 8)
                        .addComponent(btnBrowseDir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpen))
                    .addGroup(pnlDirectoriesLayout.createSequentialGroup()
                        .addGroup(pnlDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(iLblDefaultSaveDir)
                            .addComponent(btnRestoreDefaults))
                        .addGap(0, 348, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlDirectoriesLayout.setVerticalGroup(
            pnlDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDirectoriesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iLblDefaultSaveDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDefaultSaveDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBrowseDir))
                    .addComponent(btnOpen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRestoreDefaults)
                .addContainerGap(269, Short.MAX_VALUE))
        );

        tabSettings.addTab("Directorios", pnlDirectories);

        btnSave.setText("Guardar");
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancelar");
        btnCancel.setFocusable(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnApply.setText("Aplicar");
        btnApply.setEnabled(false);
        btnApply.setFocusable(false);
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabSettings)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancel)
                        .addGap(8, 8, 8)
                        .addComponent(btnApply)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(tabSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel)
                    .addComponent(btnApply))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Populates the UI with values taken from {@code userPreferences}.
     * <p>
     * This method uses the components' names as the keys to retrieve the
     * properties from {@code userPreferences}. In order for this method to work
     * properly, it is imperative that when a new (input) component is added,
     * its name be set to a valid key in {@code userPreferences}.</p>
     *
     * @see UserPreferencesLoader.Keys
     */
    public void prepareUI() {
        //authentication panel
        for (Component component : pnlAuthentication.getComponents()) {
            if (component instanceof JTextField) {
                JTextField field = (JTextField) component;
                field.setText((String) userPreferences.getProperty(field.getName()));
            }
        }

        for (Component component : pnlDatabases.getComponents()) {
            if (component instanceof JComboBox) {
                populateDefaultDbCombobox((JComboBox) component);
            }
        }

        for (Component component : pnlDirectories.getComponents()) {
            if (component instanceof JTextField) {
                JTextField field = (JTextField) component;
                field.setText((String) userPreferences.getProperty(field.getName()));
            }
        }

        enableListeners = true;
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    public void populateDefaultDbCombobox(JComboBox combo) {
        combo.removeAllItems();
        String defaultDB = userPreferences.getProperty(combo.getName());
        combo.addItem(userPreferences.getDefaultFor(combo.getName()));
        for (String dbName : SQLUtils.retrieveDBNamesFromServer()) {
            combo.addItem(dbName);
            if (defaultDB.equals(dbName)) {
                combo.setSelectedItem(dbName);
            }
        }
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        commitSave();
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        commitSave();
        btnApply.setEnabled(false);
    }//GEN-LAST:event_btnApplyActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        MiscUtils.openDirectory(txtDefaultSaveDir.getText());
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnRestoreDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestoreDefaultsActionPerformed
        txtDefaultSaveDir.setText((String) userPreferences.getDefaultFor("Default.SaveDir"));
    }//GEN-LAST:event_btnRestoreDefaultsActionPerformed

    private void btnBrowseDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseDirActionPerformed
        JFileChooser fc = new JFileChooser();
        String dir = (String) userPreferences.getProperty("Default.SaveDir");
        fc.setCurrentDirectory(new File(dir));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Elige una carpeta...");
        int returnVal = fc.showDialog(client, "Confirmar");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File newSaveDir = fc.getSelectedFile();
            txtDefaultSaveDir.setText(newSaveDir.getPath());
        }
    }//GEN-LAST:event_btnBrowseDirActionPerformed

    private void btnModifySettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifySettingsActionPerformed
        UIConnectionWizard modifyConnection = new UIConnectionWizard(client, UIConnectionWizard.Task.MODIFY);
        int outcome = modifyConnection.showDialog();
        if (outcome == UIConnectionWizard.CONNECTION_CHANGED) {
            populateDefaultDbCombobox(comboDatabases);
            client.clearJTree();
            client.removeAllFrontPanels(false, UICreateAndModifyTable.class, UIEditTable.class);
        }
    }//GEN-LAST:event_btnModifySettingsActionPerformed

    /**
     * Saves the settings that have been modified in this session into
     * {@code userPreferences}, flushing the file, thus making the changes
     * permanent.
     * <p>
     * <b>Note:</b> Authentication settings are not included in this method
     * because those require their own special interface.</p>
     */
    public void commitSave() {
        for (Component modifiedComponent : modifiedComponents) {
            if (modifiedComponent instanceof JTextField) {
                userPreferences.set(modifiedComponent.getName(),
                        ((JTextField) modifiedComponent).getText());
            }
            if (modifiedComponent instanceof JCheckBox) {
                userPreferences.set(modifiedComponent.getName(),
                        "" + ((JCheckBox) modifiedComponent).isSelected());
            }
            if (modifiedComponent instanceof JSpinner) {
                userPreferences.set(modifiedComponent.getName(),
                        "" + ((JSpinner) modifiedComponent).getValue());
            }
            if (modifiedComponent instanceof JComboBox) {
                userPreferences.set(modifiedComponent.getName(),
                        (String) ((JComboBox) modifiedComponent).getSelectedItem());
            }
        }
        modifiedComponents.clear();
        userPreferences.flushFile();
    }

    /**
     * Adds a recently modified component to the queue of components that
     * contain modified values.
     *
     * @param comp The {@code Component} that has been recently modified.
     */
    public void addToQueue(Component comp) {
        modifiedComponents.add(comp);
    }

    /**
     * Checks whether a given {@code Component} is in the queue of
     * {@code modifiedComponents}.
     *
     * @param comp The {@code Component} to check.
     * @return {@code true} if the {@code comp} is found within
     * {@code modifiedComponents}.
     */
    public boolean isInQueue(Component comp) {
        for (Component modifiedComponent : modifiedComponents) {
            if (comp == modifiedComponent) {
                return true;
            }
        }
        return false;
    }

    /**
     * This class represents the action of adding a Component to the queue of
     * components that have modified values. Components that support
     * {@code ActionEvent} can implement this class directly. Others will have
     * to inherit from this class, and implement their own queueListener and
     * then call {@code actionPerformed} on this class. E.g: inside a method
     * {@code insertUpdate} in {@code DocumentListener}, you would call
     * {@code super.actionPerformed(null)} to notify this class of changes that
     * have occurred, and for the component to be added to the queue.
     */
    private class ActionAddToQueue implements ActionListener {

        private Component source;

        public ActionAddToQueue(Component source) {
            this.source = source;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            queue();
        }

        /**
         * Adds the source of this event to the list of modified components to
         * signal that this component has a modified value. It then sets the
         * flag {@code fired} to {@code true} to signal that there's no need to
         * further listen to more events from this source.
         */
        public void queue() {
            if (enableListeners) {
                if (!isInQueue(source)) {
                    addToQueue(source);
                    btnApply.setEnabled(true);
                }
            }
        }
    }

    /**
     * The {@code DocumentListener} class that all {@code JTextFields} or
     * Components that make use of {@code Document} within this form will have
     * use.
     */
    private class TextFieldChangeListener extends ActionAddToQueue implements DocumentListener {

        public TextFieldChangeListener(Component source) {
            super(source);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            super.actionPerformed(null);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            super.actionPerformed(null);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnBrowseDir;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnModifySettings;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnRestoreDefaults;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> comboDatabases;
    private javax.swing.JLabel iLblDefaultDbs;
    private javax.swing.JLabel iLblDefaultSaveDir;
    private javax.swing.JPanel pnlAuthentication;
    private javax.swing.JPanel pnlConnection;
    private javax.swing.JPanel pnlDatabases;
    private javax.swing.JPanel pnlDirectories;
    private javax.swing.JTabbedPane tabSettings;
    private javax.swing.JTextField txtDefaultSaveDir;
    // End of variables declaration//GEN-END:variables
}
