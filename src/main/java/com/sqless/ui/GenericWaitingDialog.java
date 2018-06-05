package com.sqless.ui;

import com.sqless.utils.AsyncAction;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GenericWaitingDialog extends javax.swing.JDialog {

    private Thread secondaryThread;
    private boolean invisible;

    public GenericWaitingDialog(String waitingText) {
        super(UIClient.getInstance(), true);
        initComponents();
        lblWaiting.setText(waitingText);
        lblNotice.setVisible(false);
        btnCancelar.setVisible(false);
        setLocationRelativeTo(getParent());
    }

    public GenericWaitingDialog(String waitingText, boolean invisible) {
        this(waitingText);
        this.invisible = invisible;
    }

    public void countTime() {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
        threadPool.schedule(() -> displayLongTaskNotice(), 10, TimeUnit.SECONDS);
    }

    public void display(AsyncAction action) {
        secondaryThread = new Thread(() -> {
            action.exec();
            dispose();
        });
        countTime();
        secondaryThread.start();
        setVisible(!invisible);
    }

    public void displayLongTaskNotice() {
        lblNotice.setVisible(true);
        btnCancelar.setVisible(true);
        btnCancelar.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblWaiting = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        btnCancelar = new javax.swing.JButton();
        lblNotice = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        lblWaiting.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWaiting.setText("Waiting...");

        progressBar.setIndeterminate(true);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        lblNotice.setForeground(new java.awt.Color(204, 0, 0));
        lblNotice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNotice.setText("La tarea está tardando demasiado. Haz click en Cancelar si deseas cancelarla.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblWaiting, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNotice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblWaiting)
                .addGap(11, 11, 11)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblNotice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelar)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        if (secondaryThread != null && secondaryThread.isAlive()) {
            secondaryThread.interrupt();
        }
        
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel lblNotice;
    private javax.swing.JLabel lblWaiting;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
