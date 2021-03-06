package com.sqless.ui;

import com.sqless.utils.UIUtils;
import java.awt.Color;
import java.awt.Frame;
import javax.swing.SwingWorker;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.settings.UserPreferencesLoader;
import com.sqless.utils.TextUtils;
import java.awt.event.ItemEvent;
import java.util.concurrent.ExecutionException;

public class UIConnectionWizard extends javax.swing.JDialog {

    public enum Task {
        REPAIR, CREATE, MODIFY;
    }

    private UserPreferencesLoader userPrefLoader;
    private Frame parent;
    private Task task;
    public static int CONNECTION_CHANGED = 641;
    private int outcome = -1;
    private char defaultEchoChar;

    public UIConnectionWizard(java.awt.Frame parent, Task task) {
        super(parent, true);
        initComponents();
        this.parent = parent;
        this.userPrefLoader = UserPreferencesLoader.getInstance();
        setLocationRelativeTo(parent);
        this.task = task;
        if (isTask(Task.MODIFY) || isTask(Task.REPAIR)) {
            prepareUI();
            setTitle(isTask(Task.MODIFY) ? "Editar una conexión" : "Reparar una conexión");
            btnContinue.setText("Aplicar");
        } else {
            setTitle("Crear una conexión");
        }
        defaultEchoChar = txtPassword.getEchoChar();
        getRootPane().setDefaultButton(btnTestConnection);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContainer = new javax.swing.JPanel();
        lblLogin = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();
        lblPort = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnContinue = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnTestConnection = new javax.swing.JButton();
        iLblConStatus = new javax.swing.JLabel();
        lblConnectionStatus = new javax.swing.JLabel();
        chkShowPassword = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        lblLogin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ui_general/SQLess_logo_mini.png"))); // NOI18N
        lblLogin.setText("Log in");
        lblLogin.setIconTextGap(10);

        lblUserName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUserName.setText("Usuario:");

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPassword.setText("Contraseña:");

        lblHost.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblHost.setText("Host:");

        lblPort.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPort.setText("Puerto:");

        txtPort.setText("3306");

        btnContinue.setText("Continuar");
        btnContinue.setEnabled(false);
        btnContinue.setFocusable(false);
        btnContinue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinueActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancelar");
        btnCancel.setFocusable(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnTestConnection.setText("Probar conexión");
        btnTestConnection.setFocusable(false);
        btnTestConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestConnectionActionPerformed(evt);
            }
        });

        iLblConStatus.setText("Estado de la conexión:");

        chkShowPassword.setText("Mostrar contraseña");
        chkShowPassword.setFocusable(false);
        chkShowPassword.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkShowPasswordItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlContainerLayout = new javax.swing.GroupLayout(pnlContainer);
        pnlContainer.setLayout(pnlContainerLayout);
        pnlContainerLayout.setHorizontalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlContainerLayout.createSequentialGroup()
                        .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblHost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtHost)
                            .addComponent(txtUserName)
                            .addComponent(txtPort, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(txtPassword))
                        .addContainerGap())
                    .addGroup(pnlContainerLayout.createSequentialGroup()
                        .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlContainerLayout.createSequentialGroup()
                                .addGap(0, 64, Short.MAX_VALUE)
                                .addComponent(btnTestConnection)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnContinue))
                            .addGroup(pnlContainerLayout.createSequentialGroup()
                                .addComponent(lblLogin)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContainerLayout.createSequentialGroup()
                        .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlContainerLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(chkShowPassword))
                            .addGroup(pnlContainerLayout.createSequentialGroup()
                                .addComponent(iLblConStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblConnectionStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        pnlContainerLayout.setVerticalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(lblLogin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHost)
                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPort)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserName)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkShowPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iLblConStatus)
                    .addComponent(lblConnectionStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTestConnection)
                    .addComponent(btnCancel)
                    .addComponent(btnContinue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueActionPerformed
        testConnection(true);
    }//GEN-LAST:event_btnContinueActionPerformed

    public int showDialog() {
        setVisible(true);
        return outcome;
    }

    /**
     * Hace un test de la conexión utilizando los valores dados en los campos de
     * texto.
     *
     * @param andContinue si {@code true}, el método guardará los datos de
     * conexión en el archivo de preferencias. Si {@code false}, el archivo no
     * se actualizará.
     */
    public void testConnection(boolean andContinue) {
        String hostName = txtHost.getText();
        String port = txtPort.getText();
        String username = txtUserName.getText();
        String password = UIUtils.getStringFromPasswordField(txtPassword);
        if (hostName.isEmpty() || port.isEmpty() || username.isEmpty()) {
            UIUtils.showErrorMessage("Error", "El único campo que puede estar vacío es la contraseña.", UIClient.getInstance());
            setLabelStatus(false);
            return;
        }
        if (!TextUtils.isNumeric(port)) {
            UIUtils.showErrorMessage("Error", "El puerto dado es inválido.", parent);
            setLabelStatus(false);
            return;
        }

        enableInputFields(false);
        enableContinue(false);
        setCursor(UIUtils.WAIT_CURSOR);
        UIUtils.changeLabelColour(lblConnectionStatus, Color.BLACK);
        lblConnectionStatus.setText("Probando...");

        SwingWorker<Boolean, Void> tester = new SwingWorker<Boolean, Void>() {
            SQLConnectionManager conManager = SQLConnectionManager.getInstance();

            @Override
            protected Boolean doInBackground() throws Exception {
                boolean result = conManager.testConnection(username, password, hostName, port, parent);
                String oldHostName = conManager.getHostName();
                String oldPort = conManager.getPort();
                String oldUsername = conManager.getUsername();
                String oldPassword = conManager.getPassword();
                if (result && andContinue) { //si el test fue exitoso y el usuario presionó el boton continue/aplicar
                    userPrefLoader.makeDir();
                    userPrefLoader.set("Connection.Host", hostName);
                    userPrefLoader.set("Connection.Port", port);
                    userPrefLoader.set("Connection.Username", username);
                    userPrefLoader.set("Connection.Password", password);
                    userPrefLoader.set("Default.Database", userPrefLoader.getDefaultFor("Default.Database"));
                    userPrefLoader.flushFile();

                    if (isTask(Task.MODIFY) && (!hostName.equals(oldHostName) || !port.equals(oldPort) || !username.equals(oldUsername) || !password.equals(oldPassword))) {
                        conManager.closeConnection();
                        conManager.setNewConnection("mysql", parent);
                        outcome = CONNECTION_CHANGED;
                    }
                }
                return result;
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    setLabelStatus(success);
                    enableContinue(success);
                    setCursor(UIUtils.DEFAULT_CURSOR);
                    enableInputFields(true);
                    if (success) {
                        getRootPane().setDefaultButton(btnContinue);
                        if (andContinue) {
                            dispose();
                        }
                    }
                } catch (ExecutionException e) {
                } catch (InterruptedException ex) {

                }

            }
        };

        tester.execute();
    }

    public void setLabelStatus(boolean successful) {
        lblConnectionStatus.setText(successful ? "Exitoso" : "Fallido");
        UIUtils.changeLabelColour(lblConnectionStatus, successful ? UIUtils.DARK_GREEN : Color.RED);
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (isTask(Task.MODIFY)) {
            dispose();
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestConnectionActionPerformed
        testConnection(false);
    }//GEN-LAST:event_btnTestConnectionActionPerformed

    private void chkShowPasswordItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkShowPasswordItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            txtPassword.setEchoChar((char) 0);
        } else {
            txtPassword.setEchoChar(defaultEchoChar);
        }
    }//GEN-LAST:event_chkShowPasswordItemStateChanged

    public void prepareUI() {
        txtHost.setText((String) userPrefLoader.getProperty("Connection.Host"));
        txtPort.setText((String) userPrefLoader.getProperty("Connection.Port"));
        txtUserName.setText((String) userPrefLoader.getProperty("Connection.Username"));
        txtPassword.setText((String) userPrefLoader.getProperty("Connection.Password"));
    }

    public void enableInputFields(boolean flag) {
        txtHost.setEnabled(flag);
        txtPort.setEnabled(flag);
        txtUserName.setEnabled(flag);
        txtPassword.setEnabled(flag);
        btnTestConnection.setEnabled(flag);
        btnCancel.setEnabled(flag);
    }

    public void enableContinue(boolean flag) {
        btnContinue.setEnabled(flag);
    }

    public boolean isTask(Task task) {
        return this.task.equals(task);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnContinue;
    private javax.swing.JButton btnTestConnection;
    private javax.swing.JCheckBox chkShowPassword;
    private javax.swing.JLabel iLblConStatus;
    private javax.swing.JLabel lblConnectionStatus;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel pnlContainer;
    private javax.swing.JTextField txtHost;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
