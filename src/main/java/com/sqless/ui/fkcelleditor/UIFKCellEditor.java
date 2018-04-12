package com.sqless.ui.fkcelleditor;

import com.sqless.sql.objects.SQLForeignKey;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class UIFKCellEditor extends javax.swing.JPanel {
    
    private UIFKPanelInner panelInnerPopUp;
    private SQLForeignKey fk;

    public UIFKCellEditor(SQLForeignKey fk) {
        initComponents();
        this.fk = fk;
        txtFKValue.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
    }
    
    public void setValue(String value) {
        txtFKValue.setText(value);
    }
    
    public String getValue() {
        if (panelInnerPopUp == null) {
            return txtFKValue.getText();
        }
        String val = panelInnerPopUp.getSelectedValue();
        panelInnerPopUp = null;
        return val;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        txtFKValue = new javax.swing.JTextField();
        btnShowPopup = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        txtFKValue.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        txtFKValue.setBorder(null);
        txtFKValue.setPreferredSize(new java.awt.Dimension(0, 13));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(txtFKValue, gridBagConstraints);

        btnShowPopup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_general/COMBOBOX_ARROW_ICON.png"))); // NOI18N
        btnShowPopup.setBorder(null);
        btnShowPopup.setFocusable(false);
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
        panelInnerPopUp = new UIFKPanelInner(popMenu, fk, txtFKValue.getText());
        popMenu.add(panelInnerPopUp);        
        popMenu.show(this, btnShowPopup.getLocation().x - 95, btnShowPopup.getLocation().y + 20);
        popMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                panelInnerPopUp.setCancelled(true);
            }
        });
    }//GEN-LAST:event_btnShowPopupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnShowPopup;
    private javax.swing.JTextField txtFKValue;
    // End of variables declaration//GEN-END:variables
}
