package com.sqless.ui;

import com.sqless.network.PostRequest;
import com.sqless.network.RestRequest;
import com.sqless.userdata.User;
import com.sqless.userdata.UserManager;
import com.sqless.utils.UIUtils;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import us.monoid.web.JSONResource;

public class UILogin extends javax.swing.JDialog {
    
    public UILogin() {
        super(UIClient.getInstance(), true);
        initComponents();
    }
    
    public UILogin(String username) {
        this();
        txtUsername.setText(username);
        txtPassword.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        lblSQLessLogo = new javax.swing.JLabel();
        iLblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        iLblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        hyperlinkCreateAcc = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Log in to SQLess");
        setResizable(false);

        lblSQLessLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSQLessLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/images/ui_login_create/SQLess_logo_mini.png"))); // NOI18N

        iLblPassword.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iLblPassword.setText("Password");

        txtPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        iLblUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iLblUsername.setText("Username");

        txtUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        btnCancel.setAction(actionCancel);
        btnCancel.setText("Cancelar");
        btnCancel.setFocusable(false);

        btnLogin.setAction(actionLogIn);
        btnLogin.setText("Log in");
        btnLogin.setFocusable(false);

        hyperlinkCreateAcc.setText("<html><span style=\"color:blue\"><u>¿No tienes cuenta?</u></span></html>");
        hyperlinkCreateAcc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        hyperlinkCreateAcc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                hyperlinkCreateAccMousePressed(evt);
            }
        });

        lblStatus.setForeground(new java.awt.Color(255, 0, 51));
        lblStatus.setText(" ");

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSQLessLogo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPassword)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(hyperlinkCreateAcc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLogin)
                        .addGap(2, 2, 2))
                    .addComponent(iLblUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(iLblPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUsername)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLogin)
                        .addComponent(btnCancel))
                    .addComponent(hyperlinkCreateAcc, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(btnLogin);

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
    
    private void hyperlinkCreateAccMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hyperlinkCreateAccMousePressed
        dispose();
        UICreateAccount createAccount = new UICreateAccount();
        createAccount.setVisible(true);
    }//GEN-LAST:event_hyperlinkCreateAccMousePressed

    private Action actionLogIn = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUsername.getText();
            String password = UIUtils.getStringFromPasswordField(txtPassword);
            btnLogin.setEnabled(false);
            RestRequest loginAttempt = new PostRequest("http://localhost/WebService/Login.php", "username=" + username, "password=" + password) {
                @Override
                public void onSuccess(JSONResource json) throws Exception {
                    btnLogin.setEnabled(true);
                    boolean success = (boolean) json.get("login_status.success");                    
                    if (success) {
                        String token = (String) json.get("login_status.token");
                        String username = (String) json.get("login_status.user_data.username");
                        UserManager.getInstance().addNew(new User(username, token));
                        dispose();
                    } else {
                        String err = (String) json.get("login_status.err");
                        lblStatus.setText(err);
                    }
                }
            };
            loginAttempt.exec();
        }
    };

    private Action actionCancel = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel hyperlinkCreateAcc;
    private javax.swing.JLabel iLblPassword;
    private javax.swing.JLabel iLblUsername;
    private javax.swing.JLabel lblSQLessLogo;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
