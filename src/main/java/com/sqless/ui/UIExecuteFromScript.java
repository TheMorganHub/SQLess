package com.sqless.ui;

import com.sqless.queries.SQLBatchQuery;
import com.sqless.utils.UIUtils;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

public class UIExecuteFromScript extends javax.swing.JDialog {

    private String filePath;
    private ScriptExecutor scriptExecutor;

    public UIExecuteFromScript(String filePath) {
        super(UIClient.getInstance(), true);
        initComponents();
        this.filePath = filePath + (!filePath.endsWith(".sql") ? ".sql" : "");
        setLocationRelativeTo(getParent());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressExecution = new javax.swing.JProgressBar();
        lblExecuting = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        progressExecution.setIndeterminate(true);

        lblExecuting.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblExecuting.setText("Ejecutando...");

        btnCancelar.setText("Cancelar");
        btnCancelar.setFocusable(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressExecution, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelar)))
                .addContainerGap())
            .addComponent(lblExecuting, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblExecuting)
                .addGap(11, 11, 11)
                .addComponent(progressExecution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void start() {
        scriptExecutor = new ScriptExecutor();
        scriptExecutor.execute();
    }

    private class ScriptExecutor extends SwingWorker<Void, Void> {

        private SQLBatchQuery scriptQuery;
        private boolean cancelled;

        @Override
        protected Void doInBackground() throws Exception {
            EventQueue.invokeLater(() -> setVisible(true));
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                String delimiter = ";";
                StringBuilder buffer = new StringBuilder();
                scriptQuery = new SQLBatchQuery() {
                    @Override
                    public void onSuccess(int[] updateCounts) {
                        System.out.println(Arrays.toString(updateCounts));
                        //TODO success report
                    }

                    @Override
                    public void onFailure(int[] updateCounts, String errMessage) {
                        System.err.println(errMessage);
                        //TODO error report
                    }
                };
                while ((line = reader.readLine()) != null) {
                    if (buffer.length() == 0 && line.startsWith("DELIMITER")) {
                        delimiter = line.split(" ")[1];
                        continue;
                    }
                    if (line.startsWith("--") || line.isEmpty()) {
                        continue;
                    }
                    buffer.append(line.replaceFirst("\\h+$", "")).append("\n"); //remueve los espacios en blanco (si es que los hay) después de DELIMITER [delimitador]
                    if (line.endsWith(delimiter)) {
                        String statement = buffer.toString().replace(delimiter, ""); //buffer.toString().replace(delimiter, ";")
                        scriptQuery.addBatch(statement);
                        buffer.setLength(0);
                    }
                    if (cancelled) {
                        break;
                    }
                }
                if (!cancelled) {
                    scriptQuery.exec();
                }
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            } catch (ExecutionException ex) {
                dispose();
                UIUtils.showErrorMessage("Error", "Hubo un error al ejecutar el archivo dado."
                        + "\nAsegúrate que la ruta es la correcta y que el archivo SQL es válido y no está corrupto.", UIClient.getInstance());
            } finally {
                dispose();
            }
        }

        public void cancelQuery() {
            scriptQuery.closeQuery();
            cancelled = true;
        }

    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Thread closeQueryThread = new Thread(() -> {
            scriptExecutor.cancelQuery();
            EventQueue.invokeLater(() -> dispose());
        });
        closeQueryThread.start();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel lblExecuting;
    private javax.swing.JProgressBar progressExecution;
    // End of variables declaration//GEN-END:variables
}
