package com.sqless.ui;

import com.sqless.file.FileManager;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.utils.SQLUtils;
import com.sqless.utils.UIUtils;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

public class UIDatabaseDumper extends javax.swing.JDialog {

    private File mysqldumpFile;
    private DumperWorker worker;

    public UIDatabaseDumper(File mysqldumpFile) {
        super(UIClient.getInstance(), true);
        initComponents();
        this.mysqldumpFile = mysqldumpFile;
        setLocationRelativeTo(getParent());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressDump = new javax.swing.JProgressBar();
        btnCancelar = new javax.swing.JButton();
        lblDumping = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        progressDump.setIndeterminate(true);

        btnCancelar.setText("Cancelar");
        btnCancelar.setFocusable(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        lblDumping.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDumping.setText("Dumping...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressDump, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar)))
                .addContainerGap())
            .addComponent(lblDumping, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblDumping)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressDump, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    public void start() {
        createCommand();
    }

    private void createCommand() {
        FileManager.getInstance().saveFileAs("sql", chosenPath -> {
            worker = new DumperWorker(chosenPath);
            worker.execute();
        });
    }

    private class DumperWorker extends SwingWorker<Void, Void> {

        private SQLConnectionManager conManager;
        private String chosenPath;
        private Process proc;

        public DumperWorker(String chosenPath) {
            this.conManager = SQLConnectionManager.getInstance();
            this.chosenPath = chosenPath;
        }

        @Override
        protected Void doInBackground() throws Exception {
            EventQueue.invokeLater(() -> {
                lblDumping.setText("Creando dump de base de datos '" + SQLUtils.getConnectedDBName() + "'...");
                setVisible(true);
            });
            String filePath = mysqldumpFile.getPath().replace(".exe", "");
            proc = Runtime.getRuntime().exec(new String[]{filePath, "-h" + conManager.getHostName(), "-u" + conManager.getUsername(),
                "-p" + conManager.getPassword(), SQLUtils.getConnectedDBName(), "--routines", "-r" + chosenPath});
            proc.waitFor();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                cleanUpAfterSuccess();
            } catch (ExecutionException ex) {
                Throwable thrown = ex.getCause();
                if (thrown.getMessage().contains("error=2")) {
                    UIUtils.showErrorMessage("Error", "Hubo un error al cargar el módulo de dumpeo de base de datos.\nPor favor, cierra SQLess e intenta nuevamente.", UIDatabaseDumper.this);
                }
            } catch (InterruptedException | CancellationException ex) {
                proc.destroy();
                cleanUpAfterCancel(chosenPath);
            } finally {
                setVisible(false);
            }
        }

        public void cleanUpAfterSuccess() {
            setVisible(false);
            UIPostDumpReport postDumpReport = new UIPostDumpReport();
            postDumpReport.showDialog();
        }

        public void cleanUpAfterCancel(String chosenPath) {
            setVisible(false);
            if (FileManager.dirOrFileExists(chosenPath)) {
                try {
                    FileManager.deleteFile(chosenPath);
                    UIUtils.showMessage("Database dump", "El usuario canceló la operación", UIDatabaseDumper.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel lblDumping;
    private javax.swing.JProgressBar progressDump;
    // End of variables declaration//GEN-END:variables
}
