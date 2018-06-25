package com.sqless.ui;

import com.sqless.userdata.GoogleUser;
import com.sqless.userdata.GoogleUserManager;
import com.sqless.utils.MiscUtils;
import com.sqless.utils.UIUtils;
import javax.swing.JPopupMenu;

public class UIPanelLoggedIn extends javax.swing.JPanel {

    private JPopupMenu popParent;

    public UIPanelLoggedIn(JPopupMenu popMenu) {
        initComponents();
        this.popParent = popMenu;
        GoogleUser activeUser = GoogleUserManager.getInstance().getActive();
        if (activeUser != null) {
            lblAccountInfo.setText(activeUser.getNombre() + " (" + activeUser.getEmail() + ")");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        btnLogOut = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblAccountInfo = new javax.swing.JLabel();
        lblPruebalo = new javax.swing.JLabel();
        lblMapleIcon = new javax.swing.JLabel();
        lblAprende = new javax.swing.JLabel();

        lblTitle.setFont(UIUtils.ROBOTO_LIGHT);
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ui_general/SQLess_logo_mini.png"))); // NOI18N
        lblTitle.setText("Bienvenido a SQLess!");

        btnLogOut.setText("Cerrar sesión");
        btnLogOut.setFocusable(false);
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblAccountInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAccountInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_client/GOOGLE_LOGO_SMALL_ICON.png"))); // NOI18N

        lblPruebalo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblPruebalo.setForeground(new java.awt.Color(0, 0, 255));
        lblPruebalo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPruebalo.setText("<html><u>Pruébalo</u></html>");
        lblPruebalo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblPruebalo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblPruebaloMousePressed(evt);
            }
        });

        lblMapleIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMapleIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_client/MAPLE_LOGO.png"))); // NOI18N

        lblAprende.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblAprende.setForeground(new java.awt.Color(0, 0, 255));
        lblAprende.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAprende.setText("<html><u>Aprende Maple</u></html>");
        lblAprende.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAprende.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblAprendeMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMapleIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAccountInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(lblAprende, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(lblPruebalo, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAccountInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMapleIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAprende, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPruebalo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogOut)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(btnLogOut)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogOutActionPerformed
        doLogOut();
    }//GEN-LAST:event_btnLogOutActionPerformed

    private void lblPruebaloMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPruebaloMousePressed
        MiscUtils.openInBrowser("https://sqless.ddns.net/maple");
    }//GEN-LAST:event_lblPruebaloMousePressed

    private void lblAprendeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAprendeMousePressed
        MiscUtils.openInBrowser("https://sqless.ddns.net/maple/docs");
    }//GEN-LAST:event_lblAprendeMousePressed

    public void doLogOut() {
        popParent.setVisible(false);
        int opt = UIUtils.showConfirmationMessage("Cerrar sesión", "¿Estás seguro que deseas cerrar sesión con la cuenta " + GoogleUserManager.getInstance().getActive().getEmail() + "?"
                + "\nAl cerrar sesión perderás acceso a Maple y otras funcionalidades.", UIClient.getInstance());
        if (opt == 0) {
            UIClient.getInstance().onUserLogOut();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogOut;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAccountInfo;
    private javax.swing.JLabel lblAprende;
    private javax.swing.JLabel lblMapleIcon;
    private javax.swing.JLabel lblPruebalo;
    private javax.swing.JLabel lblTitle;
    // End of variables declaration//GEN-END:variables
}
