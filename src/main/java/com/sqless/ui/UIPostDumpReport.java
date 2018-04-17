package com.sqless.ui;

import com.sqless.utils.SQLUtils;
import java.awt.EventQueue;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

public class UIPostDumpReport extends javax.swing.JDialog {

    public UIPostDumpReport() {
        super(UIClient.getInstance(), true);
        initComponents();
        setLocationRelativeTo(getParent());
        getRootPane().setDefaultButton(btnCount);
        setTitle("Dump de base de datos '" + SQLUtils.getConnectedDBName() + "'");
    }

    public void showDialog() {
        SwingWorker<Map<String, Integer>, Void> statsWorker = new SwingWorker<Map<String, Integer>, Void>() {
            @Override
            protected Map<String, Integer> doInBackground() throws Exception {
                EventQueue.invokeLater(() -> setVisible(true));
                return SQLUtils.getDbStats();
            }

            @Override
            protected void done() {
                try {
                    Map<String, Integer> stats = get();
                    lblCountFilas.setText(stats.get("ROW_COUNT") + "");
                    lblCountTablas.setText(stats.get("TABLE_COUNT") + "");
                    lblCountVistas.setText(stats.get("VIEW_COUNT") + "");
                    lblCountProcedures.setText(stats.get("PROCEDURE_COUNT") + "");
                    lblCountFunciones.setText(stats.get("FUNCTION_COUNT") + "");
                } catch (InterruptedException | ExecutionException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        };
        statsWorker.execute();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblFilas = new javax.swing.JLabel();
        lblCountFilas = new javax.swing.JLabel();
        lblTablas = new javax.swing.JLabel();
        lblCountTablas = new javax.swing.JLabel();
        lblCountVistas = new javax.swing.JLabel();
        lblVistas = new javax.swing.JLabel();
        lblCountProcedures = new javax.swing.JLabel();
        lblProcedures = new javax.swing.JLabel();
        lblFunciones = new javax.swing.JLabel();
        lblCountFunciones = new javax.swing.JLabel();
        btnCount = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("La operación finalizó con éxito");

        lblFilas.setText("Filas:");

        lblCountFilas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCountFilas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCountFilas.setText("0");

        lblTablas.setText("Tablas:");

        lblCountTablas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCountTablas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCountTablas.setText("0");

        lblCountVistas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCountVistas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCountVistas.setText("0");

        lblVistas.setText("Vistas:");

        lblCountProcedures.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCountProcedures.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCountProcedures.setText("0");

        lblProcedures.setText("Procedures:");

        lblFunciones.setText("Funciones:");

        lblCountFunciones.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCountFunciones.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCountFunciones.setText("0");

        btnCount.setText("OK");
        btnCount.setFocusable(false);
        btnCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCountActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFilas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCountFilas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTablas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCountTablas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblVistas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCountVistas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblProcedures)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCountProcedures, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFunciones)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCountFunciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCount, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFilas)
                    .addComponent(lblCountFilas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTablas)
                    .addComponent(lblCountTablas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVistas)
                    .addComponent(lblCountVistas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProcedures)
                    .addComponent(lblCountProcedures))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFunciones)
                    .addComponent(lblCountFunciones))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(btnCount)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCountActionPerformed
        dispose();
    }//GEN-LAST:event_btnCountActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCount;
    private javax.swing.JLabel lblCountFilas;
    private javax.swing.JLabel lblCountFunciones;
    private javax.swing.JLabel lblCountProcedures;
    private javax.swing.JLabel lblCountTablas;
    private javax.swing.JLabel lblCountVistas;
    private javax.swing.JLabel lblFilas;
    private javax.swing.JLabel lblFunciones;
    private javax.swing.JLabel lblProcedures;
    private javax.swing.JLabel lblTablas;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVistas;
    // End of variables declaration//GEN-END:variables
}
