package com.sqless.ui;

import com.sqless.main.GoogleLogin;
import com.sqless.userdata.GoogleUserManager;
import com.sqless.utils.UIUtils;

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

    public UIGoogleWaitDialog() {
        super(UIClient.getInstance(), true);
        initComponents();
    }

    public void waitForLogin() {
        lblDialog.setText("Esperando a Google...");
        secondaryThread = new Thread(() -> {
            GoogleLogin login = new GoogleLogin(googleUser -> {
                if (secondaryThread != null) {
                    GoogleUserManager.getInstance().addNew(googleUser);
                    UIClient.getInstance().updateMenuBarForGoogleUser(googleUser);
                } else { //si el usuario canceló el dialogo pero aceptó la página del browser
                    UIUtils.showErrorMessage("Autenticación con Google", "Hubo un error al iniciar sesión. El token dado ya no es válido.", null);
                    GoogleUserManager.getInstance().logOut();
                }
                dispose();
            });
            login.start();
        });
        secondaryThread.start();
        setVisible(true);
    }

    /**
     * Se encarga de llamar a la instancia activa de {@code GoogleUserManager}
     * para autenticar credenciales de Google que existen localmente. Si no se
     * encuentran credenciales locales, este método no hace nada.
     */
    public void authenticateStoredCredentials() {
        if (GoogleUserManager.getInstance().credentialsExistLocally()) {
            lblDialog.setText("Autenticando credenciales Google locales...");

            secondaryThread = new Thread(() -> {
                GoogleUserManager.getInstance().authenticateStoredCredentials(user -> {
                    if (secondaryThread != null) {
                        UIClient.getInstance().updateMenuBarForGoogleUser(user);
                    } else { //si el usuario canceló el dialogo pero aceptó la página del browser
                        UIUtils.showErrorMessage("Autenticación con Google", "Hubo un error al iniciar sesión. El token dado ya no es válido.", null);
                        GoogleUserManager.getInstance().logOut();
                    }
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

        lblDialog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDialog.setText("Esperando a Google...");

        btnCancelar.setText("Cancelar");
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
            secondaryThread.interrupt();
            secondaryThread = null;
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
