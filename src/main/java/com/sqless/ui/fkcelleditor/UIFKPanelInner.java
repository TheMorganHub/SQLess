package com.sqless.ui.fkcelleditor;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLSelectQuery;
import com.sqless.sql.objects.SQLForeignKey;
import com.sqless.utils.SQLUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        uiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int row = uiTable.rowAtPoint(e.getPoint());
                if (row == -1) {
                    return;
                }
                actionOK.actionPerformed(null);
            }
        });
        uiTable.getActionMap().put("CONFIRM", actionOK);
        uiTable.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "CONFIRM");
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
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        scrTable = new javax.swing.JScrollPane();
        uiTable = new org.jdesktop.swingx.JXTable();

        setMaximumSize(new java.awt.Dimension(350, 32767));

        btnCancelar.setAction(actionCancel);
        btnCancelar.setText("Cancelar");
        btnCancelar.setFocusable(false);
        btnCancelar.setMaximumSize(new java.awt.Dimension(350, 32767));

        btnOK.setAction(actionOK);
        btnOK.setText("OK");
        btnOK.setFocusable(false);
        btnOK.setMaximumSize(new java.awt.Dimension(350, 32767));
        btnOK.setPreferredSize(new java.awt.Dimension(75, 23));

        txtSearch.setMaximumSize(new java.awt.Dimension(350, 32767));

        btnSearch.setAction(actionSearch);
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_edittable/SEARCH_ICON.png"))); // NOI18N
        btnSearch.setFocusable(false);
        btnSearch.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnSearch.setMaximumSize(new java.awt.Dimension(350, 32767));

        scrTable.setMaximumSize(new java.awt.Dimension(350, 32767));

        uiTable.setBackground(new java.awt.Color(240, 240, 240));
        uiTable.setModel(new javax.swing.table.DefaultTableModel());
        uiTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        uiTable.setEditable(false);
        uiTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        uiTable.setGridColor(new java.awt.Color(204, 204, 204));
        uiTable.setMaximumSize(new java.awt.Dimension(350, 32767));
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(scrTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(scrTable, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
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
