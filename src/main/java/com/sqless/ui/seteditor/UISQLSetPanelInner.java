package com.sqless.ui.seteditor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class UISQLSetPanelInner extends javax.swing.JPanel {

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
    private JPopupMenu parent;
    private JTextField txtFieldOnConfirm;

    public UISQLSetPanelInner(JPopupMenu parent, String[] defaultVals, String originalColumnValues) {
        initComponents();
        this.originalColumnValues = originalColumnValues.split(",");
        this.defaultVals = defaultVals;
        this.parent = parent;
        fillPanel();
    }

    /**
     * Crea un nuevo panel. Usar este constructor para ocasiones en las que se
     * utilice este panel en conjunto con un componente que no sea una JTable.
     *
     * @param parent El JPopupMenu padre de este panel.
     * @param defaultVals Los valores que esta columna puede aceptar en su set.
     * @param originalColumnValues Los valores que la columna tiene asignado en
     * su set actualmente al abrirse esta ventana.
     * @param txtFieldOnConfirm Un panel de texto en el cual se podrían ver
     * reflejados los cambios al presionar el botón de confirmación.
     */
    public UISQLSetPanelInner(JPopupMenu parent, String[] defaultVals, String originalColumnValues, JTextField txtFieldOnConfirm) {
        this(parent, defaultVals, originalColumnValues);
        this.txtFieldOnConfirm = txtFieldOnConfirm;
    }

    private void fillPanel() {
        for (String defaultVal : defaultVals) {
            JCheckBox checkBox = new JCheckBox(defaultVal);
            checkBox.setBackground(Color.WHITE);
            checkBox.setFocusable(false);
            checkBox.setSelected(valIsSelected(defaultVal));
            panelCheckBoxes.add(checkBox);
        }
        panelCheckBoxes.revalidate();
        panelCheckBoxes.repaint();
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBack = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        scrPane = new javax.swing.JScrollPane();
        panelCheckBoxes = new javax.swing.JPanel();

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

        javax.swing.GroupLayout panelBackLayout = new javax.swing.GroupLayout(panelBack);
        panelBack.setLayout(panelBackLayout);
        panelBackLayout.setHorizontalGroup(
            panelBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBackLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );
        panelBackLayout.setVerticalGroup(
            panelBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBackLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(panelBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addGap(6, 6, 6))
        );

        panelCheckBoxes.setBackground(new java.awt.Color(255, 255, 255));
        panelCheckBoxes.setLayout(new javax.swing.BoxLayout(panelCheckBoxes, javax.swing.BoxLayout.Y_AXIS));
        scrPane.setViewportView(panelCheckBoxes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scrPane, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(panelBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
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

        parent.setVisible(false);
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        parent.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JPanel panelBack;
    private javax.swing.JPanel panelCheckBoxes;
    private javax.swing.JScrollPane scrPane;
    // End of variables declaration//GEN-END:variables
}
