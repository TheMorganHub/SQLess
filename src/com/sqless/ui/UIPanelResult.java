package com.sqless.ui;

import com.sqless.file.FileManager;
import com.sqless.utils.TextUtils;
import com.sqless.utils.UIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class UIPanelResult extends javax.swing.JPanel {

    public UIPanelResult() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popMenuTable = new javax.swing.JPopupMenu();
        menuItemCopyCategory = new javax.swing.JMenu();
        menuItemCopyRowsWithHeaders = new javax.swing.JMenuItem();
        menuItemCopyRows = new javax.swing.JMenuItem();
        menuItemSelectRange = new javax.swing.JMenuItem();
        menuItemSelectAllTable = new javax.swing.JMenuItem();
        menuItemSaveTableAs = new javax.swing.JMenuItem();
        scrTableResult = new javax.swing.JScrollPane();
        tableResult = new JXTable() {
            @Override public void changeSelection(
                int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                if (convertColumnIndexToModel(columnIndex) == 0) {
                    return;
                }
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        menuItemCopyCategory.setText("Copy");

        menuItemCopyRowsWithHeaders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/ui_client/COPYTABLEWITHHEADERS_ICON.png"))); // NOI18N
        menuItemCopyRowsWithHeaders.setText("With headers");
        menuItemCopyRowsWithHeaders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                menuItemCopyRowsWithHeadersMouseReleased(evt);
            }
        });
        menuItemCopyCategory.add(menuItemCopyRowsWithHeaders);

        menuItemCopyRows.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/ui_client/COPYTABLEICON.png"))); // NOI18N
        menuItemCopyRows.setText("Without headers");
        menuItemCopyRows.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                menuItemCopyRowsMouseReleased(evt);
            }
        });
        menuItemCopyCategory.add(menuItemCopyRows);

        popMenuTable.add(menuItemCopyCategory);
        popMenuTable.addSeparator();

        menuItemSelectRange.setText("Select range...");
        menuItemSelectRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSelectRangeActionPerformed(evt);
            }
        });
        popMenuTable.add(menuItemSelectRange);

        menuItemSelectAllTable.setText("Select all");
        menuItemSelectAllTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSelectAllTableActionPerformed(evt);
            }
        });
        popMenuTable.add(menuItemSelectAllTable);
        popMenuTable.addSeparator();

        menuItemSaveTableAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/ui_client/SAVE_ICON.png"))); // NOI18N
        menuItemSaveTableAs.setText("Save table as...");
        menuItemSaveTableAs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                menuItemSaveTableAsMouseReleased(evt);
            }
        });
        popMenuTable.add(menuItemSaveTableAs);

        scrTableResult.setBorder(null);

        tableResult.setBackground(new java.awt.Color(240, 240, 240));
        tableResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableResult.setComponentPopupMenu(popMenuTable);
        tableResult.setEditable(false);
        tableResult.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tableResult.setGridColor(new java.awt.Color(204, 204, 204));
        tableResult.setRowHeight(20);
        tableResult.setSelectionBackground(new java.awt.Color(233, 243, 253));
        tableResult.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tableResult.setShowGrid(true);
        scrTableResult.setViewportView(tableResult);
        tableResult.getTableHeader().setReorderingAllowed(false);
        tableResult.setSortable(false);
        tableResult.addHighlighter(HighlighterFactory.createAlternateStriping(Color.WHITE, Color.WHITE));
        HighlightPredicate numberPredicate = new HighlightPredicate() {
            @Override
            public boolean isHighlighted(Component component, org.jdesktop.swingx.decorator.ComponentAdapter adapter) {
                return adapter.column == 0;
            }
        };
        ColorHighlighter highlighter = new ColorHighlighter(numberPredicate, TextUtils.BRIGHT_GREY, TextUtils.DARK_GREY);
        tableResult.addHighlighter(highlighter);
        tableResult.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableResult.setCellSelectionEnabled(true);
        tableResult.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int clickedIndex = tableResult.columnAtPoint(e.getPoint());
                if (clickedIndex == -1 || tableResult.getModel().getRowCount() == 0) {
                    return;
                }

                if (clickedIndex == 0) {
                    actionSelectAll();
                } else {
                    tableResult.setColumnSelectionInterval(clickedIndex, clickedIndex);
                    tableResult.setRowSelectionInterval(0, tableResult.getRowCount() - 1);
                }

            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrTableResult, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrTableResult, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemCopyRowsWithHeadersMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuItemCopyRowsWithHeadersMouseReleased
        actionCopyRows(true);
    }//GEN-LAST:event_menuItemCopyRowsWithHeadersMouseReleased

    private void menuItemCopyRowsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuItemCopyRowsMouseReleased
        actionCopyRows(false);
    }//GEN-LAST:event_menuItemCopyRowsMouseReleased

    private void menuItemSelectRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSelectRangeActionPerformed
        actionSelectRange();
    }//GEN-LAST:event_menuItemSelectRangeActionPerformed

    private void menuItemSelectAllTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSelectAllTableActionPerformed
        actionSelectAll();
    }//GEN-LAST:event_menuItemSelectAllTableActionPerformed

    private void menuItemSaveTableAsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuItemSaveTableAsMouseReleased
        actionSaveTableAs();
    }//GEN-LAST:event_menuItemSaveTableAsMouseReleased

    public void actionSelectAll() {
        tableResult.setColumnSelectionInterval(1, tableResult.getColumnCount() - 1);
        tableResult.setRowSelectionInterval(0, tableResult.getRowCount() - 1);
    }
    
    public void actionSelectRange() {
        if (tableResult.getRowCount() == 0) {
            UIUtils.showErrorMessage("Not enough rows", "The number of rows must "
                    + "be at least one in order to use this feature.", this);
            return;
        }
        UISelectRange uISelectRange = new UISelectRange(tableResult.getRowCount());
        int[] range = uISelectRange.showDialog();
        if (range[0] > -1 && range[1] > -1) {
            tableResult.setColumnSelectionInterval(1, tableResult.getColumnCount() - 1);
            tableResult.setRowSelectionInterval(range[0], range[1]);
        }
    }

    public void actionSaveTableAs() {
        FileManager.getInstance().saveTableAs(tableResult);
    }

    public void actionCopyRows(boolean includeHeaders) {
        int[] selectedRows = tableResult.getSelectedRows();
        int[] selectedColumns = tableResult.getSelectedColumns();
        if (selectedColumns == null || selectedRows == null) {
            return;
        }
        UIClient.getInstance().setCursor(UIUtils.WAIT_CURSOR);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            String forClipboard;

            @Override
            protected Void doInBackground() throws Exception {
                forClipboard = TextUtils.tableToString(tableResult, includeHeaders, false, false);
                return null;
            }

            @Override
            protected void done() {
                StringSelection selection = new StringSelection(forClipboard);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                UIClient.getInstance().setCursor(UIUtils.DEFAULT_CURSOR);
            }
        };
        worker.execute();
    }

    public JXTable getTable() {
        return tableResult;
    }

    public int getRowCount() {
        return tableResult.getRowCount();
    }

    public DefaultTableModel getTableModel() {
        return (DefaultTableModel) getTable().getModel();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu menuItemCopyCategory;
    private javax.swing.JMenuItem menuItemCopyRows;
    private javax.swing.JMenuItem menuItemCopyRowsWithHeaders;
    private javax.swing.JMenuItem menuItemSaveTableAs;
    private javax.swing.JMenuItem menuItemSelectAllTable;
    private javax.swing.JMenuItem menuItemSelectRange;
    private javax.swing.JPopupMenu popMenuTable;
    private javax.swing.JScrollPane scrTableResult;
    private org.jdesktop.swingx.JXTable tableResult;
    // End of variables declaration//GEN-END:variables
}
