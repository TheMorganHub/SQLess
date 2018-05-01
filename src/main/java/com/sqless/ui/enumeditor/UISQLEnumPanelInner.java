package com.sqless.ui.enumeditor;

import javax.swing.DefaultListModel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class UISQLEnumPanelInner extends javax.swing.JPanel {

    private JPopupMenu parent;
    private String[] availableValues;
    private String actualValue;
    private JTextField txtFieldOnConfirm;

    public UISQLEnumPanelInner(JPopupMenu parent, String[] availableValues, String actualValue, JTextField txtFieldOnConfirm) {
        initComponents();
        this.parent = parent;
        this.availableValues = availableValues;
        this.actualValue = actualValue;
        this.txtFieldOnConfirm = txtFieldOnConfirm;
        initList();
    }

    public void initList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String availableValue : availableValues) {
            model.addElement(availableValue);
        }
        listEnum.setModel(model);
        listEnum.setSelectedValue(actualValue, true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrList = new javax.swing.JScrollPane();
        listEnum = new javax.swing.JList<>();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        listEnum.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        listEnum.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrList.setViewportView(listEnum);

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancelar");
        btnCancel.setMaximumSize(new java.awt.Dimension(47, 23));
        btnCancel.setMinimumSize(new java.awt.Dimension(47, 23));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrList, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        if (!actualValue.equalsIgnoreCase(listEnum.getSelectedValue())) {
            txtFieldOnConfirm.setText(listEnum.getSelectedValue());
        }
        parent.setVisible(false);
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        parent.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JList<String> listEnum;
    private javax.swing.JScrollPane scrList;
    // End of variables declaration//GEN-END:variables
}
