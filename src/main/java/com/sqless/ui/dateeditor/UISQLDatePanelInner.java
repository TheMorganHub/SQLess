package com.sqless.ui.dateeditor;

import com.sqless.sql.objects.SQLColumn;
import com.sqless.ui.UIClient;
import com.sqless.utils.DataTypeUtils;
import javax.swing.JTextField;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;

public class UISQLDatePanelInner extends javax.swing.JDialog {

    private SQLColumn column;
    private JTextField txtOnConfirm;
    private String actualValue;

    public UISQLDatePanelInner(SQLColumn column, String actualValue, JTextField txtOnConfirm) {
        super(UIClient.getInstance(), true);
        initComponents();
        this.column = column;
        this.txtOnConfirm = txtOnConfirm;
        this.actualValue = actualValue;
        initUI();
        getRootPane().setDefaultButton(btnOK);
        setLocationRelativeTo(getParent());
    }

    public void initUI() {
        datePicker.setFormats(column.getDataType().equals("date") ? DataTypeUtils.MYSQL_DATE_FORMAT : DataTypeUtils.MYSQL_DATETIME_FORMAT);
        datePicker.getMonthView().setSelectionModel(new SingleDaySelectionModel());
        datePicker.getEditor().setVisible(false);
        datePicker.addActionListener(e -> {
            txtDate.setText(datePicker.getEditor().getText());
        });
        if (actualValue != null) {
            txtDate.setText(actualValue);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        datePicker = new org.jdesktop.swingx.JXDatePicker();
        btnCurrTimestamp = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();

        setResizable(false);

        btnCurrTimestamp.setText("CURRENT_TIMESTAMP");
        btnCurrTimestamp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCurrTimestampActionPerformed(evt);
            }
        });

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnCurrTimestamp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btnCancelar)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCurrTimestamp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnOK))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        String newValue = txtDate.getText();
        if (!actualValue.equals(newValue)) {
            txtOnConfirm.setText(newValue);
        }
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnCurrTimestampActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCurrTimestampActionPerformed
        if (!actualValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
            txtOnConfirm.setText("CURRENT_TIMESTAMP");
        }
        dispose();
    }//GEN-LAST:event_btnCurrTimestampActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCurrTimestamp;
    private javax.swing.JButton btnOK;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JTextField txtDate;
    // End of variables declaration//GEN-END:variables
}
