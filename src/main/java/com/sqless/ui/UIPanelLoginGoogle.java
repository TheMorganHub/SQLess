package com.sqless.ui;

import com.sqless.utils.MiscUtils;

public class UIPanelLoginGoogle extends javax.swing.JPanel {

    public UIPanelLoginGoogle() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnLogin = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblAprendeMas = new javax.swing.JLabel();
        lblGoogleLogo = new javax.swing.JLabel();
        lblDescr = new javax.swing.JLabel();

        btnLogin.setText("Iniciar sesión");
        btnLogin.setFocusable(false);
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblAprendeMas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblAprendeMas.setForeground(new java.awt.Color(0, 0, 255));
        lblAprendeMas.setText("<html><u>Aprende más</u></html>");
        lblAprendeMas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAprendeMas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblAprendeMasMousePressed(evt);
            }
        });

        lblGoogleLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_client/GOOGLE_LOGO_ICON.png"))); // NOI18N

        lblDescr.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblDescr.setText("<html>\nAutentícate con Google y obtén acceso <br> a Maple, nuestro dialecto SQL que facilita <br> toda operación con base de datos.\n</html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGoogleLogo)
                    .addComponent(lblDescr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAprendeMas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblGoogleLogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDescr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblAprendeMas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnLogin)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogin)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        doLogIn();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void lblAprendeMasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAprendeMasMousePressed
        MiscUtils.openInBrowser("https://sqless.ddns.net/maple/docs");
    }//GEN-LAST:event_lblAprendeMasMousePressed

    public void doLogIn() {
        UIGoogleWaitDialog waitDialog = new UIGoogleWaitDialog();
        waitDialog.waitForLogin();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAprendeMas;
    private javax.swing.JLabel lblDescr;
    private javax.swing.JLabel lblGoogleLogo;
    // End of variables declaration//GEN-END:variables
}
