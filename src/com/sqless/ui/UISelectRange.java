package com.sqless.ui;

import com.sqless.utils.UIUtils;
import javax.swing.SpinnerNumberModel;

public class UISelectRange extends javax.swing.JDialog {

    private int rowCount;

    public UISelectRange(int rowCount) {
        super(UIClient.getInstance(), true);
        initComponents();
        this.rowCount = rowCount;
        getRootPane().setDefaultButton(btnGo);
        setLocationRelativeTo(getParent());
    }

    public int[] showDialog() {
        prepareUI();
        setVisible(true);
        int start = (int) spinnerStart.getValue();
        int end = (int) spinnerEnd.getValue();
        if (start > end) {
            UIUtils.showErrorMessage("Range selection", "The start of the selection "
                    + "cannot be a number larger than the end.", getParent());
            return new int[]{-1, -1};
        }
        return new int[]{start - 1, end - 1};
    }

    private void prepareUI() {
        SpinnerNumberModel startModel = (SpinnerNumberModel) spinnerStart.getModel();
        SpinnerNumberModel endModel = (SpinnerNumberModel) spinnerEnd.getModel();
        startModel.setMinimum(1);
        endModel.setMaximum(rowCount);
        endModel.setValue(rowCount);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContainer = new javax.swing.JPanel();
        iLblSelStart = new javax.swing.JLabel();
        iLblSelEnd = new javax.swing.JLabel();
        spinnerStart = new javax.swing.JSpinner();
        spinnerEnd = new javax.swing.JSpinner();
        btnGo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Range selection");
        setResizable(false);

        iLblSelStart.setText("Selection start");

        iLblSelEnd.setText("Selection end");

        spinnerStart.setModel(new javax.swing.SpinnerNumberModel(1, null, null, 1));
        spinnerStart.setFocusable(false);

        spinnerEnd.setModel(new javax.swing.SpinnerNumberModel());
        spinnerEnd.setFocusable(false);

        btnGo.setText("Go");
        btnGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlContainerLayout = new javax.swing.GroupLayout(pnlContainer);
        pnlContainer.setLayout(pnlContainerLayout);
        pnlContainerLayout.setHorizontalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iLblSelEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(iLblSelStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlContainerLayout.createSequentialGroup()
                        .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(spinnerEnd)
                                .addComponent(spinnerStart, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnGo))
                        .addGap(0, 54, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlContainerLayout.setVerticalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iLblSelStart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iLblSelEnd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(btnGo)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoActionPerformed
        dispose();
    }//GEN-LAST:event_btnGoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGo;
    private javax.swing.JLabel iLblSelEnd;
    private javax.swing.JLabel iLblSelStart;
    private javax.swing.JPanel pnlContainer;
    private javax.swing.JSpinner spinnerEnd;
    private javax.swing.JSpinner spinnerStart;
    // End of variables declaration//GEN-END:variables
}
