package com.sqless.ui;

import com.sqless.file.FileManagerAdapter;
import com.sqless.file.MapleFileManager;
import com.sqless.queries.MapleQuery;
import com.sqless.userdata.GoogleUserManager;
import com.sqless.utils.MiscUtils;
import com.sqless.utils.TextUtils;
import com.sqless.utils.UIUtils;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jsyntaxpane.DefaultSyntaxKit;

public class UIMapleQueryPanel extends FrontPanel {

    private final ImageIcon INTERRUPTED_QUERY_ICON = UIUtils.icon(getIconsFolder(), "INTERRUPTED_QUERY");
    private final ImageIcon FAILED_QUERY_ICON = UIUtils.icon(getIconsFolder(), "FAILED_QUERY");
    private final ImageIcon SUCCESSFUL_QUERY_ICON = UIUtils.icon(getIconsFolder(), "SUCCESSFUL_QUERY");
    private final ImageIcon LOADING_QUERY_ICON = UIUtils.icon(getIconsFolder(), "LOADING", "gif");
    private List<UIPanelResult> resultPanels;
    private UIPanelMessages panelMessages;
    private String tabOriginalContents;
    private String filePath;
    private MapleQuery mapleQuery;

    /**
     * A constructor used for a panel that contains a brand new query.
     *
     * @param parentPane
     * @param contents
     */
    public UIMapleQueryPanel(JTabbedPane parentPane, String contents) {
        this(parentPane, MapleFileManager.getInstance().newFile(), contents);
    }

    /**
     * Inicializa un nuevo UIMapleQueryPanel. Si el usuario activo en
     * {@code GoogleUserManager} es null, este UIMapleQueryPanel será marcado
     * como corrupto y se invalidará.
     *
     * @param parentPane
     * @param filePath
     * @param contents
     */
    public UIMapleQueryPanel(JTabbedPane parentPane, String filePath, String contents) {
        super(parentPane);
        initComponents();
        if (GoogleUserManager.getInstance().getActive() != null) {
            this.tabOriginalContents = contents;
            initEditor();
            initResultsPanel();
            initMessagesPanel();
            tabPane.setSelectedIndex(0);
            tabPane.addChangeListener(changeEvent -> {
                int index = tabPane.getSelectedIndex();
                if (tabPane.getComponentAt(index) instanceof UIPanelResult) {
                    UIPanelResult panelResult = (UIPanelResult) tabPane.getComponentAt(index);
                    updateRowsLabel(panelResult.getRowCount());
                }
            });
            this.filePath = filePath;
        } else {
            UIUtils.showWarning("Aviso", "Debes estar logueado para hacer una consulta con Maple.\nHaz click en 'Iniciar sesión'.", UIClient.getInstance());
            setIntegrity(Integrity.CORRUPT);
        }
    }

    public void initEditor() {
        DefaultSyntaxKit.initKit();
        txtSQLQuery.setContentType("text/sql");
        txtSQLQuery.setComponentPopupMenu(null);
        txtMapleQuery.setContentType("text/sql");
        txtMapleQuery.setComponentPopupMenu(popMenuEditor);
        txtMapleQuery.getDocument().addDocumentListener(queryDocumentListener);
        txtMapleQuery.addKeyListener(editorListener);
        txtMapleQuery.setDropTarget(fileDragNDrop);
    }

    private void initResultsPanel() {
        resultPanels = new ArrayList<>();
    }

    public void initMessagesPanel() {
        panelMessages = new UIPanelMessages();
        tabPane.addTab("Mensajes", panelMessages);
    }

    @Override
    public void onCreate() {
        setMapleText(tabOriginalContents);
        txtMapleQuery.requestFocus();
        String tooltipText = filePath != null && !filePath.endsWith(".mpl") ? filePath + ".mpl" : filePath;
        setTabToolTipText(tooltipText);
        EventQueue.invokeLater(() -> splitPane.setDividerLocation(0.65));
    }

    public void setQuery(MapleQuery mapleQuery) {
        this.mapleQuery = mapleQuery;
    }

    public void runContents() {
        btnRun.doClick();
    }

    public void setMapleText(String sql) {
        txtMapleQuery.setText(TextUtils.normaliseLineEndings(sql));
        //el ingreso de texto en el documento dispara un evento INSERT, esto hace que el Label de esta tab se ponga en negrita.
        //al no ser este un ingreso de texto desde el usuario, volvemos el label a la normalidad
        unboldTitleLabel();
    }

    public void updateRowsLabel(int rows) {
        lblFilas.setText("Filas: " + rows + " |");
    }

    public void updateRowsLabel() {
        if (!resultPanels.isEmpty()) {
            updateRowsLabel(resultPanels.get(0).getRowCount());
        }
    }

    public void setMs(long ms) {
        lblMs.setText(ms + "ms");
    }

    public void enableStopBtn(boolean flag) {
        btnStop.setEnabled(flag);
    }

    public void enableRunBtn(boolean flag) {
        btnRun.setEnabled(flag);
    }

    public void updateStatusLabel(MapleQuery.Status STATUS) {
        switch (STATUS) {
            case LOADING:
                lblFilas.setIcon(LOADING_QUERY_ICON);
                break;
            case SUCCESSFUL:
                lblFilas.setIcon(SUCCESSFUL_QUERY_ICON);
                break;
            case FAILED:
                lblFilas.setIcon(FAILED_QUERY_ICON);
                break;
            case STOPPED:
                lblFilas.setIcon(INTERRUPTED_QUERY_ICON);
                break;
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void clearResults() {
        if (tabPane.getTabCount() > 1) {
            for (int i = tabPane.getTabCount() - 2; i >= 0; i--) {
                tabPane.remove(i);
                resultPanels.remove(i);
            }
        }
    }

    public UIPanelResult newResultPanel() {
        return new UIPanelResult();
    }

    public void addResultPanel(UIPanelResult panelResult) {
        resultPanels.add(panelResult);
        tabPane.insertTab("Resultado_" + resultPanels.size(), null, panelResult, null, tabPane.getTabCount() == 0 ? 0 : tabPane.getTabCount() - 1);
        tabPane.setSelectedIndex(0);
    }

    public void clearMessages() {
        panelMessages.clear();
    }

    public void clearConvertedSQL() {
        txtSQLQuery.setText("");
    }

    public void stopQuery() {
        if (mapleQuery != null) {
            mapleQuery.stopQuery();
            btnStop.setEnabled(false);
        }
    }

    public void setMessage(MapleQuery.Status STATUS, String s) {
        switch (STATUS) {
            case FAILED:
                tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
                panelMessages.appendError(s);
                break;
            case SUCCESSFUL:
                tabPane.setSelectedIndex(0);
                panelMessages.clear();
                break;
            case STOPPED:
                tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
                panelMessages.appendError("La ejecución fue interrumpida.");
                break;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popMenuEditor = new javax.swing.JPopupMenu();
        menuItemCut = new javax.swing.JMenuItem();
        menuItemCopy = new javax.swing.JMenuItem();
        menuItemPaste = new javax.swing.JMenuItem();
        menuItemSelectAllEditor = new javax.swing.JMenuItem();
        menuItemFind = new javax.swing.JMenuItem();
        splitPane = new javax.swing.JSplitPane();
        tabPaneQuery = new javax.swing.JTabbedPane();
        scrPaneMaple = new javax.swing.JScrollPane();
        txtMapleQuery = new javax.swing.JEditorPane();
        scrPaneSQL = new javax.swing.JScrollPane();
        txtSQLQuery = new javax.swing.JEditorPane();
        tabPane = new javax.swing.JTabbedPane();
        lblFilas = new javax.swing.JLabel();
        lblMs = new javax.swing.JLabel();

        menuItemCut.setText("Cortar");
        menuItemCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCutActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemCut);

        menuItemCopy.setText("Copiar");
        menuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCopyActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemCopy);

        menuItemPaste.setText("Pegar");
        menuItemPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemPasteActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemPaste);
        popMenuEditor.addSeparator();

        menuItemSelectAllEditor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSelectAllEditor.setText("Seleccionar todo");
        menuItemSelectAllEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSelectAllEditorActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemSelectAllEditor);
        popMenuEditor.addSeparator();

        menuItemFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFind.setText("Buscar...");
        menuItemFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFindActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemFind);

        splitPane.setBorder(null);
        splitPane.setDividerLocation(450);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);

        tabPaneQuery.setFocusable(false);

        txtMapleQuery.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        scrPaneMaple.setViewportView(txtMapleQuery);

        tabPaneQuery.addTab("Maple", scrPaneMaple);

        txtSQLQuery.setEditable(false);
        scrPaneSQL.setViewportView(txtSQLQuery);

        tabPaneQuery.addTab("SQL", scrPaneSQL);

        splitPane.setLeftComponent(tabPaneQuery);

        tabPane.setFocusable(false);
        splitPane.setRightComponent(tabPane);

        lblFilas.setText("Filas: 0 |");

        lblMs.setText("0ms");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblFilas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMs, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFilas)
                    .addComponent(lblMs)))
        );

        UIUtils.flattenPane(splitPane);
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCutActionPerformed
        MiscUtils.simulateCtrlKeyEvent(txtMapleQuery, KeyEvent.VK_X);
    }//GEN-LAST:event_menuItemCutActionPerformed

    private void menuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCopyActionPerformed
        MiscUtils.simulateCtrlKeyEvent(txtMapleQuery, KeyEvent.VK_C);
    }//GEN-LAST:event_menuItemCopyActionPerformed

    private void menuItemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemPasteActionPerformed
        MiscUtils.simulateCtrlKeyEvent(txtMapleQuery, KeyEvent.VK_V);
    }//GEN-LAST:event_menuItemPasteActionPerformed

    private void menuItemSelectAllEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSelectAllEditorActionPerformed
        MiscUtils.simulateCtrlKeyEvent(txtMapleQuery, KeyEvent.VK_A);
    }//GEN-LAST:event_menuItemSelectAllEditorActionPerformed

    private void menuItemFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFindActionPerformed
        MiscUtils.simulateCtrlKeyEvent(txtMapleQuery, KeyEvent.VK_H);
    }//GEN-LAST:event_menuItemFindActionPerformed

    public void setConvertedSQL(String converted) {
        txtSQLQuery.setText(converted);
    }

    private DropTarget fileDragNDrop = new DropTarget() {

        @Override
        public synchronized void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (!droppedFiles.isEmpty()) {
                    FileManagerAdapter.dragNDropFile(droppedFiles.get(0));
                }
            } catch (java.awt.datatransfer.UnsupportedFlavorException | java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
    };

    private DocumentListener queryDocumentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            boldTitleLabel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            boldTitleLabel();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    private KeyAdapter editorListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_F5:
                    btnRun.doClick();
                    break;
                case KeyEvent.VK_F6:
                    btnStop.doClick();
                    break;
            }
        }
    };

    public JEditorPane getMapleEditorPane() {
        return txtMapleQuery;
    }

    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblFilas;
    private javax.swing.JLabel lblMs;
    private javax.swing.JMenuItem menuItemCopy;
    private javax.swing.JMenuItem menuItemCut;
    private javax.swing.JMenuItem menuItemFind;
    private javax.swing.JMenuItem menuItemPaste;
    private javax.swing.JMenuItem menuItemSelectAllEditor;
    private javax.swing.JPopupMenu popMenuEditor;
    private javax.swing.JScrollPane scrPaneMaple;
    private javax.swing.JScrollPane scrPaneSQL;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTabbedPane tabPaneQuery;
    private javax.swing.JEditorPane txtMapleQuery;
    private javax.swing.JEditorPane txtSQLQuery;
    // End of variables declaration//GEN-END:variables
    private JButton btnSave;
    private JButton btnRun;
    private JButton btnStop;
    private JButton btnUndo;
    private JButton btnRedo;
    private JButton btnDelete;
    private JButton btnComment;
    private JButton btnUncomment;
    private JButton btnIndent;
    private JButton btnUnindent;
    private JButton btnManual;
    private JMenuItem menuSave;
    private JMenuItem menuSaveAs;

    @Override
    public Component[] getToolbarComponents() {
        if (toolbarComponents == null) {
            btnSave = UIUtils.newToolbarBtn(actionSaveQuery, "", "Guardar estas consultas", UIUtils.icon("ui_general", "SAVE"));
            btnRun = UIUtils.newToolbarBtn(actionRunQuery, "Ejecutar", "", UIUtils.icon(this, "EXECUTE"));
            btnStop = UIUtils.newToolbarBtn(actionStopQuery, "Parar la ejecución de esta consulta", UIUtils.icon(this, "STOP_EXECUTION"));
            btnStop.setEnabled(false);
            btnUndo = UIUtils.newToolbarBtn(actionUndoQuery, "Deshacer", UIUtils.icon(this, "UNDO"));
            btnRedo = UIUtils.newToolbarBtn(actionRedoQuery, "Rehacer", UIUtils.icon(this, "REDO"));
            btnDelete = UIUtils.newToolbarBtn(actionDeleteQuery, "Borrar la consulta actual", UIUtils.icon(this, "BORRAR"));
            btnComment = UIUtils.newToolbarBtn(actionCommentQuery, "Comentar", UIUtils.icon(this, "COMMENT"));
            btnUncomment = UIUtils.newToolbarBtn(actionUncommentQuery, "Descomentar", UIUtils.icon(this, "UNCOMMENT"));
            btnIndent = UIUtils.newToolbarBtn(actionIndent, "Indentar", UIUtils.icon(this, "INDENT"));
            btnUnindent = UIUtils.newToolbarBtn(actionUnindent, "Desindentar", UIUtils.icon(this, "UNINDENT"));
            btnManual = UIUtils.newToolbarBtn(actionOpenMapleManual, "Manual de usuario de Maple", UIUtils.icon(this, "MAPLE_MANUAL"));
            toolbarComponents = new Component[]{btnSave, UIUtils.newSeparator(), btnRun, btnStop, UIUtils.newSeparator(), btnUndo, btnRedo, btnDelete, btnComment, btnUncomment, btnIndent, btnUnindent,
                UIUtils.newSeparator(), btnManual};
        }
        return toolbarComponents;
    }

    @Override
    public JMenuItem[] getMenuItems() {
        if (menuItems == null) {
            menuSave = new JMenuItem("Guardar");
            menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
            menuSave.addActionListener(actionSaveQuery);

            menuSaveAs = new JMenuItem("Guardar como...");
            menuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK | java.awt.event.InputEvent.SHIFT_MASK));
            menuSaveAs.addActionListener(actionSaveQueryAs);
            menuItems = new JMenuItem[]{menuSave, menuSaveAs};
        }
        return menuItems;
    }

    @Override
    public void tabClosing(int tabNum) {
        super.tabClosing(tabNum);
        if (filePath != null) {
            MapleFileManager.getInstance().removeFile(filePath);
        }
    }

    @Override
    public String getTabTitle() {
        if (filePath == null) {
            return null;
        }
        return MapleFileManager.getInstance().isNewFile(filePath)
                ? "Maple_File_" + (MapleFileManager.getInstance().getFilesCreatedThisSession()) + ".mpl" : filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.length());
    }

    @Override
    public String getIconsFolder() {
        return "ui_maplequery";
    }

    @Override
    public ImageIcon getTabIcon() {
        return UIUtils.icon(this, "MAPLE_SMALL");
    }

    private ActionListener actionOpenMapleManual = e -> {
        MiscUtils.openInBrowser("https://sqless.000webhostapp.com/maple/docs");
    };
    private ActionListener actionRunQuery = e -> {
        clearResults();
        String toExec = txtMapleQuery.getSelectedText() != null ? txtMapleQuery.getSelectedText() : txtMapleQuery.getText();
        mapleQuery = new MapleQuery(toExec, this);
        setQuery(mapleQuery);
        mapleQuery.exec();
    };
    private ActionListener actionStopQuery = (e) -> {
        stopQuery();
    };
    private ActionListener actionSaveQuery = e -> {
        MapleFileManager.getInstance().saveFile(this);
        setTabToolTipText(filePath);
    };
    private ActionListener actionSaveQueryAs = e -> {
        MapleFileManager.getInstance().saveFileAs(this);
        setTabToolTipText(filePath);
    };
    private ActionListener actionUndoQuery = (e) -> {
        MiscUtils.simulateCtrlKeyEvent(txtMapleQuery, KeyEvent.VK_Z);
    };
    private ActionListener actionRedoQuery = (e) -> {
        MiscUtils.simulateCtrlKeyEvent(txtMapleQuery, KeyEvent.VK_Y);
    };
    private ActionListener actionDeleteQuery = (e) -> {
        if (!txtMapleQuery.getText().isEmpty()) {
            TextUtils.emptyDoc(txtMapleQuery);
        }
    };
    private ActionListener actionCommentQuery = (e) -> {
        TextUtils.prependToText("-- ", txtMapleQuery);
    };
    private ActionListener actionUncommentQuery = (e) -> {
        TextUtils.removePrependedText("-- ", txtMapleQuery);
    };
    private ActionListener actionIndent = (e) -> {
        TextUtils.prependToText("\t", txtMapleQuery);
    };
    private ActionListener actionUnindent = (e) -> {
        TextUtils.removePrependedText("\t", txtMapleQuery);
    };

}
