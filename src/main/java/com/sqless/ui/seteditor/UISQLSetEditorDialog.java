package com.sqless.ui.seteditor;

import com.sqless.ui.UIClient;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UISQLSetEditorDialog extends javax.swing.JDialog {

    /**
     * The values of the set that the column has selected before this UI is
     * loaded.
     */
    private String[] originalColumnValues;

    /**
     * The values that the user has potentially selected at the time this UI is
     * closed. These could include values in {@link #originalColumnValues} or
     * not.
     */
    private String[] userSelectedValues;

    /**
     * All the values available in the set.
     */
    private String[] defaultVals;
    private JTextField txtFieldOnConfirm;

    public UISQLSetEditorDialog(String[] defaultVals, String originalColumnValues, JTextField textField) {
        super(UIClient.getInstance(), true);
        initComponents();
        this.originalColumnValues = originalColumnValues.split(",");
        this.defaultVals = defaultVals;
        this.txtFieldOnConfirm = textField;
        fillPanel();
        setLocationRelativeTo(UIClient.getInstance());
        SwingUtilities.invokeLater(() -> {
            btnOK.requestFocus();
            getRootPane().setDefaultButton(btnOK);
        });
    }

    public String[] getSelectedValues() {
        return userSelectedValues == null ? originalColumnValues : userSelectedValues;
    }

    public boolean valIsSelected(String val) {
        for (String enumVal : originalColumnValues) {
            if (enumVal.equalsIgnoreCase(val)) {
                return true;
            }
        }
        return false;
    }

    private void fillPanel() {
        for (String defaultVal : defaultVals) {
            JCheckBox checkBox = new JCheckBox(defaultVal);
            checkBox.setBackground(Color.WHITE);
            checkBox.setFocusable(false);
            checkBox.setSelected(valIsSelected(defaultVal));
            panelCheckBoxes.add(checkBox);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrPane = new javax.swing.JScrollPane();
        panelCheckBoxes = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Selección de valor default");

        panelCheckBoxes.setBackground(new java.awt.Color(255, 255, 255));
        panelCheckBoxes.setLayout(new javax.swing.BoxLayout(panelCheckBoxes, javax.swing.BoxLayout.Y_AXIS));
        scrPane.setViewportView(panelCheckBoxes);

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancelar");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrPane, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scrPane, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < panelCheckBoxes.getComponentCount(); i++) {
            JCheckBox chk = (JCheckBox) panelCheckBoxes.getComponent(i);
            if (chk.isSelected()) {
                selected.add(chk.getText());
            }
        }

        if (txtFieldOnConfirm == null) {
            String[] toArray = selected.toArray(new String[selected.size()]);
            userSelectedValues = toArray;
        } else {
            txtFieldOnConfirm.setText(String.join(",", selected));
        }

        setVisible(false);
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JPanel panelCheckBoxes;
    private javax.swing.JScrollPane scrPane;
    // End of variables declaration//GEN-END:variables
}
