package com.sqless.ui.enumeditor;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

public class UISQLSetEditor extends javax.swing.JPanel {
    
    private UISQLSetPanelInner panelInnerPopUp;
    private String[] defaultVals;

    public UISQLSetEditor(String[] defaultVals) {
        initComponents();
        this.defaultVals = defaultVals;
        txtEnumValues.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
    }
    
    public void setValues(String values) {
        txtEnumValues.setText(values);
    }
    
    public String getValues() {
        if (panelInnerPopUp == null) {
            return txtEnumValues.getText();
        }
        StringBuilder sb = new StringBuilder();
        for (String selectedValue : panelInnerPopUp.getSelectedValues()) {
            sb.append(selectedValue).append(",");
        }
        panelInnerPopUp = null;
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        txtEnumValues = new javax.swing.JTextField();
        btnShowPopup = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        txtEnumValues.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        txtEnumValues.setBorder(null);
        txtEnumValues.setPreferredSize(new java.awt.Dimension(0, 13));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(txtEnumValues, gridBagConstraints);

        btnShowPopup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_general/COMBOBOX_ARROW_ICON.png"))); // NOI18N
        btnShowPopup.setBorder(null);
        btnShowPopup.setMargin(new java.awt.Insets(1, 2, 1, 2));
        btnShowPopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowPopupActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnShowPopup, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnShowPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowPopupActionPerformed
        JPopupMenu popMenu = new JPopupMenu();
        panelInnerPopUp = new UISQLSetPanelInner(popMenu, defaultVals, txtEnumValues.getText());
        popMenu.add(panelInnerPopUp);
        popMenu.show(this, btnShowPopup.getLocation().x - 95, btnShowPopup.getLocation().y + 20);
    }//GEN-LAST:event_btnShowPopupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnShowPopup;
    private javax.swing.JTextField txtEnumValues;
    // End of variables declaration//GEN-END:variables
}
