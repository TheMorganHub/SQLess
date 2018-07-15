package com.sqless.ui;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLTable;
import com.sqless.utils.UIUtils;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class UIMoveColumns extends javax.swing.JDialog {

    private SQLTable table;
    private List<SQLColumn> backupColumns;
    private int result;
    public static final int ORDER_CHANGED = 1;
    private int task;

    public UIMoveColumns(SQLTable table, int task) {
        super(UIClient.getInstance(), true);
        initComponents();
        this.table = table;
        this.backupColumns = new ArrayList<>(table.getColumns());
        setTitle("Mover columnas: " + table.getName());
        setToolbar();
        fillTable();
        this.task = task;
    }

    public int showDialog() {
        setVisible(true);
        return result;
    }

    public void fillTable() {
        DefaultTableModel model = (DefaultTableModel) uiTable.getModel();
        if (model.getRowCount() > 0) {
            model.setRowCount(0);
        }
        for (SQLColumn column : table.getColumns()) {
            ImageIcon icon = column.isPK() ? UIUtils.icon("ui_createtable", "PRIMARY_KEY") : column.isFK() ? UIUtils.icon("ui_createtable/ui_movecolumns", "FOREIGN_KEY") : null;
            model.addRow(new Object[]{icon, column.getName()});
        }
    }

    public void setToolbar() {
        toolbar.add(UIUtils.newToolbarBtn(actionMoveUp, null, UIUtils.icon("ui_createtable/ui_movecolumns", "MOVER_ARRIBA")));
        toolbar.add(UIUtils.newToolbarBtn(actionMoveDown, null, UIUtils.icon("ui_createtable/ui_movecolumns", "MOVER_ABAJO")));
        toolbar.add(UIUtils.newToolbarBtn(actionAlphabetically, "Las columnas serán ordenadas alfabeticamente", UIUtils.icon("ui_createtable/ui_movecolumns", "ALPHABETICALLY")));
        toolbar.add(UIUtils.newToolbarBtn(actionKeysFirst, "Las columnas que sean primary/foreign key irán primero", UIUtils.icon("ui_createtable/ui_movecolumns", "KEYS_FIRST")));
        toolbar.add(UIUtils.newSeparator());
        toolbar.add(UIUtils.newToolbarBtn(actionConfirm, "Confirmar cambios", UIUtils.icon("ui_createtable", "SAVE")));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbar = new javax.swing.JToolBar();
        scrPane = new javax.swing.JScrollPane();
        uiTable = new org.jdesktop.swingx.JXTable();
        lblNotice = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(411, 357));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        org.jdesktop.swingx.border.DropShadowBorder dropShadowBorder1 = new org.jdesktop.swingx.border.DropShadowBorder();
        dropShadowBorder1.setShadowSize(3);
        dropShadowBorder1.setShowRightShadow(false);
        toolbar.setBorder(dropShadowBorder1);
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setPreferredSize(new java.awt.Dimension(68, 28));

        DefaultTableModel model = new DefaultTableModel(0, 2) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return ImageIcon.class;
                    default: return String.class;
                }
            }
        };
        uiTable.setBackground(new java.awt.Color(240, 240, 240));
        uiTable.setModel(model);
        uiTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        uiTable.setEditable(false);
        uiTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        uiTable.setGridColor(new java.awt.Color(204, 204, 204));
        uiTable.setRowHeight(25);
        uiTable.setSelectionBackground(new java.awt.Color(233, 243, 253));
        uiTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        scrPane.setViewportView(uiTable);
        uiTable.getTableHeader().setReorderingAllowed(false);
        uiTable.setSortable(false);
        uiTable.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.WHITE));
        uiTable.getColumn(0).setMaxWidth(25);
        uiTable.getColumn(0).setHeaderValue("");
        uiTable.getColumn(1).setHeaderValue("Columnas");

        lblNotice.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        lblNotice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_general/ATTENTION_ICON.png"))); // NOI18N
        lblNotice.setText("Los cambios serán guardados en la base de datos al confirmar este diálogo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrPane)
                    .addComponent(lblNotice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrPane, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNotice)
                .addGap(6, 6, 6))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        table.replaceColumns(backupColumns);
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblNotice;
    private javax.swing.JScrollPane scrPane;
    private javax.swing.JToolBar toolbar;
    private org.jdesktop.swingx.JXTable uiTable;
    // End of variables declaration//GEN-END:variables

    private ActionListener actionMoveUp = e -> {
        int[] selecciones = uiTable.getSelectedRows();
        if (selecciones.length == 0) {
            return;
        }
        if (selecciones[0] > 0) {
            List<SQLColumn> columns = table.getColumns();
            int start = selecciones[0];
            int end = selecciones[selecciones.length - 1];
            for (int i = start; i <= end; i++) {
                SQLColumn col = columns.get(i);
                col.moveUp();
            }
            fillTable();
            uiTable.setRowSelectionInterval(start - 1, end - 1);
        }
    };

    private ActionListener actionMoveDown = e -> {
        int[] selecciones = uiTable.getSelectedRows();
        if (selecciones.length == 0) {
            return;
        }
        if (selecciones[selecciones.length - 1] < uiTable.getRowCount() - 1) {
            List<SQLColumn> columns = table.getColumns();
            int start = selecciones[selecciones.length - 1];
            int end = selecciones[0];
            for (int i = start; i >= end; i--) {
                SQLColumn col = columns.get(i);
                col.moveDown();
            }
            fillTable();
            uiTable.setRowSelectionInterval(start + 1, end + 1);
        }
    };

    private ActionListener actionKeysFirst = e -> {
        List<SQLColumn> sortedCols = new ArrayList<>();
        boolean alreadyFoundPK = false;
        for (int i = 0; i < table.getColumns().size(); i++) {
            SQLColumn column = table.getColumns().get(i);
            if (column.isPK()) {
                sortedCols.add(0, column);
                alreadyFoundPK = true; //si no hay ninguna PK todavía, las FKs van a insertarse en el indice 0
            } else if (column.isFK()) {
                sortedCols.add(sortedCols.isEmpty() || !alreadyFoundPK ? 0 : 1, column);
            } else {
                sortedCols.add(column);
            }
        }
        for (int i = 0; i < sortedCols.size(); i++) { //actualizamos la ordinal position de cada col
            sortedCols.get(i).setOrdinalPosition(i + 1);
        }
        table.replaceColumns(sortedCols);
        fillTable();
    };

    private ActionListener actionAlphabetically = e -> {
        table.getColumns().sort(SQLColumn::compareTo);
        for (int i = 0; i < table.getColumns().size(); i++) { //actualizamos la ordinal position de cada col
            table.getColumns().get(i).setOrdinalPosition(i + 1);
        }
        fillTable();
    };

    public void commit() {
        if (task == UICreateAndModifyTable.TABLE_UPDATE) {
            StringBuilder alterTableBuilder = new StringBuilder("ALTER TABLE `").append(table.getName()).append("`\n");
            boolean anyPosChanged = false;
            for (SQLColumn column : table.getColumns()) {
                if (column.positionChanged()) {
                    anyPosChanged = true;
                    alterTableBuilder.append(column.getChangeColumnStatement()).append(',').append('\n');
                }
            }
            if (!anyPosChanged) {
                dispose();
                return;
            }
            String sql = alterTableBuilder.substring(0, alterTableBuilder.lastIndexOf(",")) + ";";
            SQLQuery alterTableQuery = new SQLUpdateQuery(sql) {
                @Override
                public void onSuccess(int updateCount) { //come back here
                    table.commit();
                    result = 1;
                    dispose();
                }

                @Override
                public void onFailure(String errMessage) {
                    UIUtils.showErrorMessage("Mover columnas", errMessage, UIMoveColumns.this);
                }
            };
            alterTableQuery.exec();
        } else {
            for (SQLColumn column : table.getColumns()) {
                if (column.positionChanged()) {
                    result = 1;
                }
            }
            dispose();
        }
    }

    private ActionListener actionConfirm = e -> {
        commit();
    };

}
