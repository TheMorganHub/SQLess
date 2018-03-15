package com.sqless.ui;

import com.sqless.network.PostRequest;
import com.sqless.network.RestRequest;
import com.sqless.utils.UIUtils;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import us.monoid.web.JSONResource;

public class UICreateAccount extends javax.swing.JDialog {

    public UICreateAccount() {
        super(UIClient.getInstance(), true);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        lblSQLessLogo = new javax.swing.JLabel();
        iLblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        iLblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        iLblPasswordRepeat = new javax.swing.JLabel();
        txtPasswordRepeat = new javax.swing.JPasswordField();
        lblStatus = new javax.swing.JLabel();
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblUsarCuentaExistente = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Crear una cuenta SQLess");
        setResizable(false);

        lblSQLessLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSQLessLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ui_login_create/SQLess_logo_mini.png"))); // NOI18N

        iLblUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iLblUsername.setText("Username");

        txtUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        iLblPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iLblPassword.setText("Password");

        txtPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        iLblPasswordRepeat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iLblPasswordRepeat.setText("Repetir password");

        txtPasswordRepeat.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblStatus.setForeground(new java.awt.Color(255, 51, 51));
        lblStatus.setText(" ");

        btnConfirmar.setAction(actionConfirm);
        btnConfirmar.setText("Confirmar");
        btnConfirmar.setFocusable(false);
        getRootPane().setDefaultButton(btnConfirmar);

        btnCancelar.setAction(actionCancel);
        btnCancelar.setText("Cancelar");
        btnCancelar.setFocusable(false);

        lblUsarCuentaExistente.setText("<html><span style=\"color:blue\"><u>Usar cuenta existente</u></span></html>");
        lblUsarCuentaExistente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblUsarCuentaExistente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblUsarCuentaExistenteMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSQLessLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
            .addComponent(iLblUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(iLblPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(iLblPasswordRepeat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtUsername)
                    .addComponent(txtPasswordRepeat, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                        .addComponent(lblUsarCuentaExistente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnConfirmar)))
                .addContainerGap())
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSQLessLogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(iLblUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(iLblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(iLblPasswordRepeat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPasswordRepeat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnConfirmar)
                        .addComponent(btnCancelar))
                    .addComponent(lblUsarCuentaExistente, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblUsarCuentaExistenteMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUsarCuentaExistenteMousePressed
        dispose();
        UILogin uiLogin = new UILogin();
        uiLogin.setVisible(true);
    }//GEN-LAST:event_lblUsarCuentaExistenteMousePressed

    private Action actionConfirm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUsername.getText();
            String password = UIUtils.getStringFromPasswordField(txtPassword);
            String repeatPassword = UIUtils.getStringFromPasswordField(txtPasswordRepeat);
            if (!password.equals(repeatPassword)) {
                lblStatus.setText("Las contraseñas dadas no son iguales");
                return;
            }
            RestRequest confirmRequest = new PostRequest("http://localhost/WebService/createAccount", "username=" + username, "password=" + password) {
                @Override
                public void onSuccess(JSONResource json) throws Exception {
                    boolean success = (boolean) json.get("creation_status.success");
                    if (success) {
                        UIUtils.showMessage("Creación de cuenta", "La cuenta " + username + " ha sido creada.", null);
                        dispose();
                        UILogin uiLogin = new UILogin(username);
                        uiLogin.setVisible(true);
                    } else {
                        lblStatus.setText((String) json.get("creation_status.err"));
                    }
                }
            };
            confirmRequest.exec();
        }
    };

    private Action actionCancel = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JLabel iLblPassword;
    private javax.swing.JLabel iLblPasswordRepeat;
    private javax.swing.JLabel iLblUsername;
    private javax.swing.JLabel lblSQLessLogo;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUsarCuentaExistente;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPasswordRepeat;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
