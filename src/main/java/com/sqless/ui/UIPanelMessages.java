package com.sqless.ui;

import com.sqless.utils.DocStyler;
import com.sqless.utils.MiscUtils;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;

public class UIPanelMessages extends javax.swing.JPanel {
    
    private DocStyler errorStyler;

    public UIPanelMessages() {
        initComponents();
        errorStyler = DocStyler.of(txtMessagesArea, "Consolas", 14);
    }

    public JTextPane getMessagesArea() {
        return txtMessagesArea;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popMenuMessages = new javax.swing.JPopupMenu();
        menuItemCopyMessages = new javax.swing.JMenuItem();
        menuItemSelectAllMessages = new javax.swing.JMenuItem();
        menuItemClearMessages = new javax.swing.JMenuItem();
        scrPaneMessages = new javax.swing.JScrollPane();
        txtMessagesArea = new javax.swing.JTextPane();

        menuItemCopyMessages.setText("Copiar");
        menuItemCopyMessages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCopyMessagesActionPerformed(evt);
            }
        });
        popMenuMessages.add(menuItemCopyMessages);
        popMenuMessages.addSeparator();

        menuItemSelectAllMessages.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSelectAllMessages.setText("Seleccionar todos");
        menuItemSelectAllMessages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSelectAllMessagesActionPerformed(evt);
            }
        });
        popMenuMessages.add(menuItemSelectAllMessages);
        popMenuMessages.addSeparator();

        menuItemClearMessages.setText("Borrar");
        menuItemClearMessages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemClearMessagesActionPerformed(evt);
            }
        });
        popMenuMessages.add(menuItemClearMessages);

        txtMessagesArea.setEditable(false);
        txtMessagesArea.setComponentPopupMenu(popMenuMessages);
        scrPaneMessages.setViewportView(txtMessagesArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemCopyMessagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCopyMessagesActionPerformed
        actionCopy();
    }//GEN-LAST:event_menuItemCopyMessagesActionPerformed

    private void menuItemSelectAllMessagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSelectAllMessagesActionPerformed
        actionSelectAll();
    }//GEN-LAST:event_menuItemSelectAllMessagesActionPerformed

    private void menuItemClearMessagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemClearMessagesActionPerformed
        clear();
    }//GEN-LAST:event_menuItemClearMessagesActionPerformed

    public void appendError(String err) {
        errorStyler.set(err, Color.RED);
    }

    public void clear() {
        txtMessagesArea.setText("");
    }

    public void actionCopy() {
        MiscUtils.simulateCtrlKeyEvent(txtMessagesArea, KeyEvent.VK_C);
    }

    public void actionSelectAll() {
        MiscUtils.simulateCtrlKeyEvent(txtMessagesArea, KeyEvent.VK_A);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem menuItemClearMessages;
    private javax.swing.JMenuItem menuItemCopyMessages;
    private javax.swing.JMenuItem menuItemSelectAllMessages;
    private javax.swing.JPopupMenu popMenuMessages;
    private javax.swing.JScrollPane scrPaneMessages;
    private javax.swing.JTextPane txtMessagesArea;
    // End of variables declaration//GEN-END:variables

}
