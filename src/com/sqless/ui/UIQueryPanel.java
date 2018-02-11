package com.sqless.ui;

import com.sqless.file.FileManager;
import com.sqless.queries.SQLUIQuery;
import com.sqless.utils.MiscUtils;
import com.sqless.utils.TextUtils;
import com.sqless.utils.UIUtils;
import java.awt.Component;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jsyntaxpane.DefaultSyntaxKit;

public class UIQueryPanel extends FrontPanel {

    private final ImageIcon INTERRUPTED_QUERY_ICON = UIUtils.icon(getIconsFolder(), "INTERRUPTED_QUERY");
    private final ImageIcon FAILED_QUERY_ICON = UIUtils.icon(getIconsFolder(), "FAILED_QUERY");
    private final ImageIcon SUCCESSFUL_QUERY_ICON = UIUtils.icon(getIconsFolder(), "SUCCESSFUL_QUERY");
    private final ImageIcon LOADING_QUERY_ICON = UIUtils.icon(getIconsFolder(), "LOADING", "gif");

    private List<UIPanelResult> resultPanels;
    private UIPanelMessages panelMessages;
    private SQLUIQuery query;
    private String tabOriginalContents;
    private String filePath;

    /**
     * A constructor used for a panel that contains a brand new query.
     *
     * @param parentPane
     * @param contents
     */
    public UIQueryPanel(JTabbedPane parentPane, String contents) {
        this(parentPane, FileManager.getInstance().newFile(), contents);
    }

    public UIQueryPanel(JTabbedPane parentPane, String filePath, String contents) {
        super(parentPane);
        initComponents();
        this.tabOriginalContents = contents;
        initEditor();
        initResultsPanel();
        initMessagesPanel();
        tabPane.setSelectedIndex(0);
        tabPane.addChangeListener((ChangeEvent e) -> {
            int index = tabPane.getSelectedIndex();
            if (tabPane.getComponentAt(index) instanceof UIPanelResult) {
                UIPanelResult panelResult = (UIPanelResult) tabPane.getComponentAt(index);
                updateRowsLabel(panelResult.getRowCount());
            }
        });
        this.filePath = filePath;
    }

    @Override
    public void onCreate() {
        setSQLText(tabOriginalContents);
        sqlEditorPane.requestFocus();
    }

    public void initEditor() {
        DefaultSyntaxKit.initKit();
        sqlEditorPane.setContentType("text/sql");
        sqlEditorPane.setComponentPopupMenu(popMenuEditor);
        sqlEditorPane.getDocument().addDocumentListener(queryDocumentListener);
        sqlEditorPane.addKeyListener(editorListener);
        sqlEditorPane.setDropTarget(fileDragNDrop);
    }

    public void initResultsPanel() {
        resultPanels = new ArrayList<>();
    }

    public void setQuery(SQLUIQuery query) {
        this.query = query;
    }

    public String getFilePath() {
        return filePath;
    }

    public void stopQuery() {
        if (query != null) {
            query.closeQuery();
            btnStop.setEnabled(false);
        }
    }

    public UIPanelResult addResultPanel() {
        UIPanelResult panelResult = new UIPanelResult();
        resultPanels.add(panelResult);
        tabPane.insertTab("Result_" + resultPanels.size(), null, panelResult, null, tabPane.getTabCount() == 0 ? 0 : tabPane.getTabCount() - 1);
        tabPane.setSelectedIndex(0);

        return panelResult;
    }

    public void clearResults() {
        if (tabPane.getTabCount() > 1) {
            for (int i = tabPane.getTabCount() - 2; i >= 0; i--) {
                tabPane.remove(i);
                resultPanels.remove(i);
            }
        }
    }

    public void initMessagesPanel() {
        panelMessages = new UIPanelMessages();
        tabPane.addTab("Messages", panelMessages);
    }

    public void updateRowsLabel(int rows) {
        lblRows.setText("Rows: " + rows + " |");
    }

    public void updateRowsLabel() {
        if (!resultPanels.isEmpty()) {
            updateRowsLabel(resultPanels.get(0).getRowCount());
        }
    }

    public void updateStatusLabel(SQLUIQuery.Status STATUS) {
        switch (STATUS) {
            case LOADING:
                lblRows.setIcon(LOADING_QUERY_ICON);
                break;
            case SUCCESSFUL:
                lblRows.setIcon(SUCCESSFUL_QUERY_ICON);
                break;
            case FAILED:
                lblRows.setIcon(FAILED_QUERY_ICON);
                break;
            case STOPPED:
                lblRows.setIcon(INTERRUPTED_QUERY_ICON);
                break;
        }
    }

    public void updateTimerLabel(String time) {
        lblTimer.setText(time);
    }

    public List<UIPanelResult> getResultPanels() {
        return resultPanels;
    }

    public UIPanelResult getResultPanel(int i) {
        return getResultPanels().get(i);
    }

    public JEditorPane getSqlEditorPane() {
        return sqlEditorPane;
    }

    public UIPanelMessages getPanelMessages() {
        return panelMessages;
    }

    public void setMessage(SQLUIQuery.Status STATUS, String s) {
        switch (STATUS) {
            case FAILED:
                tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
                panelMessages.appendError(s);
                break;
            case SUCCESSFUL:
                tabPane.setSelectedIndex(0);
                panelMessages.clear();
                break;
        }
    }

    public void setSQLText(String sql) {
        sqlEditorPane.setText(TextUtils.normaliseLineEndings(sql));
        //el ingreso de texto en el documento dispara un evento INSERT, esto hace que el Label de esta tab se ponga en negrita.
        //al no ser este un ingreso de texto desde el usuario, volvemos el label a la normalidad
        unboldTitleLabel();
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
        scrPaneEditor = new javax.swing.JScrollPane();
        sqlEditorPane = new javax.swing.JEditorPane();
        tabPane = new javax.swing.JTabbedPane();
        pnlStatus = new javax.swing.JPanel();
        lblTimer = new javax.swing.JLabel();
        lblRows = new javax.swing.JLabel();

        menuItemCut.setText("Cut");
        menuItemCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCutActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemCut);

        menuItemCopy.setText("Copy");
        menuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCopyActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemCopy);

        menuItemPaste.setText("Paste");
        menuItemPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemPasteActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemPaste);
        popMenuEditor.addSeparator();

        menuItemSelectAllEditor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSelectAllEditor.setText("Select all");
        menuItemSelectAllEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSelectAllEditorActionPerformed(evt);
            }
        });
        popMenuEditor.add(menuItemSelectAllEditor);
        popMenuEditor.addSeparator();

        menuItemFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFind.setText("Find...");
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

        sqlEditorPane.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        scrPaneEditor.setViewportView(sqlEditorPane);

        splitPane.setLeftComponent(scrPaneEditor);
        splitPane.setRightComponent(tabPane);

        lblTimer.setText("00:00:00,000");

        lblRows.setText("Rows: 0 |");

        javax.swing.GroupLayout pnlStatusLayout = new javax.swing.GroupLayout(pnlStatus);
        pnlStatus.setLayout(pnlStatusLayout);
        pnlStatusLayout.setHorizontalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatusLayout.createSequentialGroup()
                .addComponent(lblRows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 725, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlStatusLayout.setVerticalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlStatusLayout.createSequentialGroup()
                .addGroup(pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTimer)
                    .addComponent(lblRows))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
            .addComponent(pnlStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        UIUtils.flattenPane(splitPane);
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCutActionPerformed
        actionCut();
    }//GEN-LAST:event_menuItemCutActionPerformed

    private void menuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCopyActionPerformed
        actionCopy();
    }//GEN-LAST:event_menuItemCopyActionPerformed

    private void menuItemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemPasteActionPerformed
        actionPaste();
    }//GEN-LAST:event_menuItemPasteActionPerformed

    private void menuItemSelectAllEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSelectAllEditorActionPerformed
        actionSelectAll();
    }//GEN-LAST:event_menuItemSelectAllEditorActionPerformed

    private void menuItemFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFindActionPerformed
        actionFind();
    }//GEN-LAST:event_menuItemFindActionPerformed

    /**
     * Runs whatever SQL is inside this query panel. This is done by simply
     * simulating a click event on the 'Run' button.
     */
    public void runContents() {
        btnRun.doClick();
    }

    public void disableStopBtn() {
        btnStop.setEnabled(false);
    }

    private DropTarget fileDragNDrop = new DropTarget() {

        @Override
        public synchronized void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                for (File file : droppedFiles) {
                    FileManager.getInstance().dragNDropFile(file);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblRows;
    private javax.swing.JLabel lblTimer;
    private javax.swing.JMenuItem menuItemCopy;
    private javax.swing.JMenuItem menuItemCut;
    private javax.swing.JMenuItem menuItemFind;
    private javax.swing.JMenuItem menuItemPaste;
    private javax.swing.JMenuItem menuItemSelectAllEditor;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPopupMenu popMenuEditor;
    private javax.swing.JScrollPane scrPaneEditor;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JEditorPane sqlEditorPane;
    private javax.swing.JTabbedPane tabPane;
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
    private JMenuItem menuSave;
    private JMenuItem menuSaveAs;

    @Override
    public Component[] getToolbarComponents() {
        if (toolbarComponents == null) {
            btnSave = UIUtils.newToolbarBtn(actionSaveQuery, "", "Guardar esta query", UIUtils.icon("ui_general", "SAVE"));
            btnRun = UIUtils.newToolbarBtn(actionRunQuery, "Run", "", UIUtils.icon(this, "EXECUTE"));
            btnStop = UIUtils.newToolbarBtn(actionStopQuery, "Stop the execution of this query", UIUtils.icon(this, "STOP_EXECUTION"));
            btnStop.setEnabled(false);
            btnUndo = UIUtils.newToolbarBtn(actionUndoQuery, "Undo", UIUtils.icon(this, "UNDO"));
            btnRedo = UIUtils.newToolbarBtn(actionRedoQuery, "Redo", UIUtils.icon(this, "REDO"));
            btnDelete = UIUtils.newToolbarBtn(actionDeleteQuery, "Delete the current query", UIUtils.icon(this, "BORRAR"));
            btnComment = UIUtils.newToolbarBtn(actionCommentQuery, "Comment", UIUtils.icon(this, "COMMENT"));
            btnUncomment = UIUtils.newToolbarBtn(actionUncommentQuery, "Uncomment", UIUtils.icon(this, "UNCOMMENT"));
            btnIndent = UIUtils.newToolbarBtn(actionIndent, "Indent", UIUtils.icon(this, "INDENT"));
            btnUnindent = UIUtils.newToolbarBtn(actionUnindent, "Unindent", UIUtils.icon(this, "UNINDENT"));
            toolbarComponents = new Component[]{btnSave, UIUtils.newSeparator(), btnRun, btnStop, UIUtils.newSeparator(), btnUndo, btnRedo, btnDelete, btnComment, btnUncomment, btnIndent, btnUnindent};
        }
        return toolbarComponents;
    }

    @Override
    public JMenuItem[] getMenuItems() {
        if (menuItems == null) {
            menuSave = new JMenuItem("Save");
            menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
            menuSave.addActionListener(actionSaveQuery);

            menuSaveAs = new JMenuItem("Save as...");
            menuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK | java.awt.event.InputEvent.SHIFT_MASK));
            menuSaveAs.addActionListener(actionSaveQueryAs);
            menuItems = new JMenuItem[]{menuSave, menuSaveAs};
        }
        return menuItems;
    }

    @Override
    public String getIconsFolder() {
        return "ui_querypanel";
    }

    @Override
    public String getTabTitle() {
        return FileManager.getInstance().isNewFile(filePath) ? "SQL_File_" + (FileManager.getInstance().getFilesCreatedThisSession()) + ".sql" : filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.length());
    }

    @Override
    public void tabClosing(int tabNum) {
        super.tabClosing(tabNum);
        if (filePath != null) {
            FileManager.getInstance().removeFile(filePath);
        }
    }

    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void actionCut() {
        MiscUtils.simulateCtrlKeyEvent(sqlEditorPane, KeyEvent.VK_X);
    }

    public void actionCopy() {
        MiscUtils.simulateCtrlKeyEvent(sqlEditorPane, KeyEvent.VK_C);
    }

    public void actionPaste() {
        MiscUtils.simulateCtrlKeyEvent(sqlEditorPane, KeyEvent.VK_V);
    }

    public void actionSelectAll() {
        MiscUtils.simulateCtrlKeyEvent(sqlEditorPane, KeyEvent.VK_A);
    }

    public void actionFind() {
        MiscUtils.simulateCtrlKeyEvent(sqlEditorPane, KeyEvent.VK_H);
    }

    private ActionListener actionSaveQuery = e -> {
        FileManager.getInstance().saveFile(this);
        parentPane.setToolTipTextAt(getTabIndex(), getTabTitle());
    };

    private ActionListener actionSaveQueryAs = e -> {
        FileManager.getInstance().saveFileAs(this);
        parentPane.setToolTipTextAt(getTabIndex(), getTabTitle());
    };

    private ActionListener actionRunQuery = e -> {        
        updateTimerLabel("00:00:00,000");
        clearResults();
        String toExec = sqlEditorPane.getSelectedText() != null ? sqlEditorPane.getSelectedText() : sqlEditorPane.getText();
        query = new SQLUIQuery(toExec, this);
        setQuery(query);
        query.exec();
        btnStop.setEnabled(true);
    };
    private ActionListener actionStopQuery = (e) -> {
        stopQuery();
    };
    private ActionListener actionUndoQuery = (e) -> {
        MiscUtils.simulateCtrlKeyEvent(sqlEditorPane, KeyEvent.VK_Z);
    };
    private ActionListener actionRedoQuery = (e) -> {
        MiscUtils.simulateCtrlKeyEvent(sqlEditorPane, KeyEvent.VK_Y);
    };
    private ActionListener actionDeleteQuery = (e) -> {
        if (!sqlEditorPane.getText().isEmpty()) {
            TextUtils.emptyDoc(sqlEditorPane);
        }
    };
    private ActionListener actionCommentQuery = (e) -> {
        TextUtils.prependToText("-- ", sqlEditorPane);
    };
    private ActionListener actionUncommentQuery = (e) -> {
        TextUtils.removePrependedText("-- ", sqlEditorPane);
    };
    private ActionListener actionIndent = (e) -> {
        TextUtils.prependToText("\t", sqlEditorPane);
    };
    private ActionListener actionUnindent = (e) -> {
        TextUtils.removePrependedText("\t", sqlEditorPane);
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

}