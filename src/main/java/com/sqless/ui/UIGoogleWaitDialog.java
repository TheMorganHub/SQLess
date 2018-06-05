package com.sqless.ui;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.sqless.userdata.GoogleUserManager;

/**
 * Un JDialog que aparecerá al momento en que el usuario decide hacer Log in con
 * Google o cuando se encuentran credenciales guardadas localmente. Aprovechamos
 * la funcionalidad del diálogo en "bloquear" la UI principal mientras se espera
 * a que el usuario de todos los permisos necesarios a la aplicación desde el
 * browser o se revisen las credenciales locales. <br><br>
 * Es necesario este diálogo ya que si se hace la autenticación en el Event
 * Dispatch Thread de Swing y el usuario llegase a cerrar el browser, la
 * aplicación queda freezada esperando un token. Si el usuario llegase a
 * cancelar en cualquier momento, la aplicación continuará andando.
 *
 * @author Morgan
 */
public class UIGoogleWaitDialog extends javax.swing.JDialog {

    private Thread secondaryThread;
    private LocalServerReceiver serverReceiver;

    public UIGoogleWaitDialog() {
        super(UIClient.getInstance(), true);
        initComponents();
        setLocationRelativeTo(getParent());
    }

    public void waitForLogin() {
        lblDialog.setText("Esperando a Google...");
        secondaryThread = new Thread(() -> {
            GoogleUserManager.getInstance().logIn(user -> {
                UIClient.getInstance().updateMenuBarForGoogleUser(user);
                dispose();
            }, this);
        });
        secondaryThread.start();
        setVisible(true);
    }

    public void setServerReceiver(LocalServerReceiver serverReceiver) {
        this.serverReceiver = serverReceiver;
    }

    /**
     * Se encarga de llamar a la instancia activa de {@code GoogleUserManager}
     * para autenticar credenciales de Google que existen localmente. Si no se
     * encuentran credenciales locales, este método no hace nada.
     */
    public void authenticateStoredCredentials() {
        if (GoogleUserManager.getInstance().credentialsExistLocally()) {
            lblDialog.setText("Cargando credenciales de Google locales...");
            btnCancelar.setVisible(false);

            secondaryThread = new Thread(() -> {
                GoogleUserManager.getInstance().authenticateStoredCredentials(user -> {
                    UIClient.getInstance().updateMenuBarForGoogleUser(user);
                    dispose();
                }, this);
            });
            secondaryThread.start();
            setVisible(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblDialog = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        lblDialog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDialog.setText("Esperando a Google...");

        btnCancelar.setText("Cancelar");
        btnCancelar.setFocusable(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        progressBar.setIndeterminate(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDialog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblDialog)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(btnCancelar)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Cancela el diálogo y (por las dudas) elimina las credenciales guardadas
     * del usuario.
     */
    public void cancel() {
        if (secondaryThread != null) {
            dispose();
            try {
                if (serverReceiver != null) {
                    serverReceiver.stop();
                }
            } catch (Exception e) {
            }
            GoogleUserManager.getInstance().logOut();
        }
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cancel();
    }//GEN-LAST:event_btnCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel lblDialog;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
