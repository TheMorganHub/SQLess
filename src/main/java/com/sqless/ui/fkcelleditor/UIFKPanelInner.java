package com.sqless.ui.fkcelleditor;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLSelectQuery;
import com.sqless.sql.objects.SQLForeignKey;
import com.sqless.utils.SQLUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class UIFKPanelInner extends javax.swing.JPanel {

    private SQLForeignKey fk;
    private String originalValue;
    private Component parent;
    private boolean cancelled;

    public UIFKPanelInner(Component parent, SQLForeignKey fk, String originalValue) {
        initComponents();
        this.parent = parent;
        this.fk = fk;
        this.originalValue = originalValue;
        loadTable();
        loadKeybindings();
    }

    public void loadKeybindings() {
        txtSearch.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ACTION_SEARCH");
        txtSearch.getActionMap().put("ACTION_SEARCH", actionSearch);        
        EventQueue.invokeLater(() -> {
            txtSearch.requestFocus();
        });
    }

    public void loadTable() {
        SQLQuery query = new SQLSelectQuery("SELECT * FROM `" + fk.getReferencedTableName() + "` LIMIT 50") {
            @Override
            public void onSuccess(ResultSet rs) throws SQLException {
                ResultSetMetaData rsmd = rs.getMetaData();
                Vector<String> colNames = new Vector();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    colNames.add(rsmd.getColumnName(i + 1));
                }
                Vector<Vector<String>> rows = new Vector<>();
                while (rs.next()) {
                    Vector<String> row = new Vector<>();
                    for (int i = 0; i < colNames.size(); i++) {
                        row.add(rs.getString(i + 1));
                    }
                    rows.add(row);
                }
                DefaultTableModel model = new DefaultTableModel(rows, colNames);
                uiTable.setModel(model);
                uiTable.packAll();
            }
        };
        query.exec();
    }

    private DefaultTableModel getModel() {
        return (DefaultTableModel) uiTable.getModel();
    }

    private int getColNumForFKName() {
        DefaultTableModel model = getModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (model.getColumnName(i).equals(fk.getReferencedColumnName())) {
                return i;
            }
        }
        return -1;
    }

    public void setCancelled(boolean flag) {
        this.cancelled = flag;
    }

    public String getSelectedValue() {
        if (!cancelled) {
            int selectedRow = uiTable.getSelectedRow();
            if (selectedRow != -1) {
                Object val = getModel().getValueAt(selectedRow, getColNumForFKName());
                return val == null ? null : val.toString();
            }
        }
        return originalValue;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancelar = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        scrTable = new javax.swing.JScrollPane();
        uiTable = new org.jdesktop.swingx.JXTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();

        btnCancelar.setAction(actionCancel);
        btnCancelar.setText("Cancelar");
        btnCancelar.setFocusable(false);

        btnOK.setAction(actionOK);
        btnOK.setText("OK");
        btnOK.setFocusable(false);
        btnOK.setPreferredSize(new java.awt.Dimension(75, 23));

        uiTable.setBackground(new java.awt.Color(240, 240, 240));
        uiTable.setModel(new javax.swing.table.DefaultTableModel());
        uiTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        uiTable.setEditable(false);
        uiTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        uiTable.setGridColor(new java.awt.Color(204, 204, 204));
        uiTable.setRowHeight(20);
        uiTable.setSelectionBackground(new java.awt.Color(233, 243, 253));
        uiTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        uiTable.setShowGrid(true);
        scrTable.setViewportView(uiTable);
        uiTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        uiTable.getTableHeader().setReorderingAllowed(false);
        uiTable.setSortable(false);
        uiTable.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.WHITE));
        uiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        uiTable.getActionMap().remove("find");

        btnSearch.setAction(actionSearch);
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_edittable/SEARCH_ICON.png"))); // NOI18N
        btnSearch.setFocusable(false);
        btnSearch.setMargin(new java.awt.Insets(2, 4, 2, 4));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
            .addComponent(scrTable)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(scrTable, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSearch)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelar)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private Action actionOK = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ((JPopupMenu) parent).setVisible(false);
        }
    };

    private Action actionCancel = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ((JPopupMenu) parent).setVisible(false);
            cancelled = true;
        }
    };

    private Action actionSearch = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<Vector<String>> filas = SQLUtils.findAnyWithStringInTable(fk.getReferencedTableName(), txtSearch.getText());
            if (filas.isEmpty()) {
                return;
            }
            Vector<String> columnas = filas.remove(0);
            getModel().setDataVector(filas, columnas);
            uiTable.packAll();
        }
    };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnSearch;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JTextField txtSearch;
    private org.jdesktop.swingx.JXTable uiTable;
    // End of variables declaration//GEN-END:variables
}
