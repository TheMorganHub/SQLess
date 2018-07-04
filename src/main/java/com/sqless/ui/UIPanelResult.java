package com.sqless.ui;

import com.sqless.file.FileManager;
import com.sqless.utils.MiscUtils;
import com.sqless.utils.TextUtils;
import com.sqless.utils.UIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class UIPanelResult extends javax.swing.JPanel {

    public UIPanelResult() {
        initComponents();
        translateSwingXComponents();
    }

    private void translateSwingXComponents() {
        tableResult.setLocale(new Locale("es", "ES"));
        Action findAction = tableResult.getActionMap().get("find");
        tableResult.getActionMap().put("find", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findAction.actionPerformed(e);
                SwingUtilities.invokeLater(() -> {
                    Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
                    if (activeWindow instanceof JDialog) {
                        activeWindow.pack();
                    }
                });
            }
        });        
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
        menuItemFind = new javax.swing.JMenuItem();
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

        menuItemCopyCategory.setText("Copiar");

        menuItemCopyRowsWithHeaders.setAction(actionCopyRowsWithHeaders);
        menuItemCopyRowsWithHeaders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_client/COPYTABLEWITHHEADERS_ICON.png"))); // NOI18N
        menuItemCopyRowsWithHeaders.setText("Con encabezados");
        menuItemCopyCategory.add(menuItemCopyRowsWithHeaders);

        menuItemCopyRows.setAction(actionCopyRows);
        menuItemCopyRows.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_client/COPYTABLEICON.png"))); // NOI18N
        menuItemCopyRows.setText("Sin encabezados");
        menuItemCopyCategory.add(menuItemCopyRows);

        popMenuTable.add(menuItemCopyCategory);
        popMenuTable.addSeparator();

        menuItemSelectRange.setAction(actionSelectRange);
        menuItemSelectRange.setText("Seleccionar por rango...");
        popMenuTable.add(menuItemSelectRange);

        menuItemSelectAllTable.setAction(actionSelectAll);
        menuItemSelectAllTable.setText("Seleccionar todo");
        popMenuTable.add(menuItemSelectAllTable);
        popMenuTable.addSeparator();

        menuItemFind.setAction(actionFindInTable);
        menuItemFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFind.setText("Buscar...");
        popMenuTable.add(menuItemFind);
        popMenuTable.addSeparator();

        menuItemSaveTableAs.setAction(actionSaveTableAs);
        menuItemSaveTableAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ui_general/SAVE_ICON.png"))); // NOI18N
        menuItemSaveTableAs.setText("Guardar tabla como...");
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
                    actionSelectAll.actionPerformed(null);
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

    public void actionCopyRows(boolean includeHeaders) {
        if (tableResult.getRowCount() == 0) {
            UIUtils.showWarning("Copiar resultados", "La tabla debe tener al menos una fila para poder ser copiada.", UIClient.getInstance());
            return;
        }
        int[] selectedRows = tableResult.getSelectedRows();
        int[] selectedColumns = tableResult.getSelectedColumns();
        if (selectedColumns == null || selectedRows == null) {
            return;
        }
        setCursor(UIUtils.WAIT_CURSOR);
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
                setCursor(UIUtils.DEFAULT_CURSOR);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu menuItemCopyCategory;
    private javax.swing.JMenuItem menuItemCopyRows;
    private javax.swing.JMenuItem menuItemCopyRowsWithHeaders;
    private javax.swing.JMenuItem menuItemFind;
    private javax.swing.JMenuItem menuItemSaveTableAs;
    private javax.swing.JMenuItem menuItemSelectAllTable;
    private javax.swing.JMenuItem menuItemSelectRange;
    private javax.swing.JPopupMenu popMenuTable;
    private javax.swing.JScrollPane scrTableResult;
    private org.jdesktop.swingx.JXTable tableResult;
    // End of variables declaration//GEN-END:variables

    private Action actionFindInTable = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MiscUtils.simulateCtrlKeyEvent(tableResult, KeyEvent.VK_F);
        }
    };
    
    private Action actionSelectAll = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tableResult.getRowCount() == 0) {
                UIUtils.showWarning("Selección de filas", "El número de filas debe ser al menos uno para hacer uso de esta funcionalidad.", UIClient.getInstance());
                return;
            }
            tableResult.setColumnSelectionInterval(1, tableResult.getColumnCount() - 1);
            tableResult.setRowSelectionInterval(0, tableResult.getRowCount() - 1);
        }
    };

    private Action actionSelectRange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tableResult.getRowCount() == 0) {
                UIUtils.showWarning("Seleccionar rango", "El número de filas debe ser al menos uno para hacer uso de esta funcionalidad.", UIClient.getInstance());
                return;
            }
            UISelectRange uISelectRange = new UISelectRange(tableResult.getRowCount());
            int[] range = uISelectRange.showDialog();
            if (range[0] > -1 && range[1] > -1) {
                tableResult.setColumnSelectionInterval(1, tableResult.getColumnCount() - 1);
                tableResult.setRowSelectionInterval(range[0], range[1]);
            }
        }
    };

    private Action actionSaveTableAs = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileManager.getInstance().saveTableAs(tableResult);
        }
    };

    private Action actionCopyRows = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            actionCopyRows(false);
        }
    };

    private Action actionCopyRowsWithHeaders = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            actionCopyRows(true);
        }
    };

    /**
     * Un simple {@code TableCellRenderer} que colorea los valores null de una
     * tabla en gris claro.
     */
    public static class NullSQLCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel component = (JLabel) super.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
            if (value == null) {
                component.setText("<html><span style=\"color:gray\">Null</span></html>");
            }
            return component;
        }
    }

}
