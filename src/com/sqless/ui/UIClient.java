package com.sqless.ui;

import com.sqless.ui.tree.NodeCellEditor;
import com.sqless.ui.tree.TreeNodeSqless;
import com.sqless.ui.tree.NodeTreeModel;
import com.sqless.ui.tree.NodeCellRenderer;
import com.sqless.ui.tree.TreeContextMenuHandler;
import com.sqless.utils.UIUtils;
import com.sqless.utils.SQLUtils;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.file.FileManager;
import com.sqless.settings.PreferenceLoader;
import com.sqless.ui.listeners.TreeExpandListener;
import com.sqless.ui.listeners.TreeMouseListener;
import com.sqless.settings.UserPreferencesLoader;
import static com.sqless.settings.PreferenceLoader.PrefKey.*;
import static com.sqless.ui.tree.TreeNodeSqless.NodeType.*;
import com.sqless.userdata.User;
import com.sqless.userdata.UserManager;

public class UIClient extends javax.swing.JFrame {

    private TreeContextMenuHandler jTreeContextMenuHandler;
    private PreferenceLoader prefLoader;
    private UserPreferencesLoader userPrefLoader;
    private static final UIClient instance = new UIClient();

    private UIClient() {
        initComponents();
        loadSplashScreen();
    }

    public void loadSplashScreen() {
        UISplash splash = new UISplash();
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() {
                publish("Loading SQLess preferences...");
                prefLoader = PreferenceLoader.getInstance();
                if (prefLoader.wasFirstTime()) {
                    publish("Preparing for first time use...");
                }
                initPrefs();

                publish("Loading user preferences...");
                userPrefLoader = UserPreferencesLoader.getInstance();
                if (userPrefLoader.isFirstTime()) {
                    UIConnectionWizard uiConWizard = new UIConnectionWizard(splash,
                            UIConnectionWizard.Task.CREATE);
                    uiConWizard.setVisible(true);
                } else {
                    userPrefLoader.loadFile();
                }

                SQLConnectionManager conManager = SQLConnectionManager.getInstance();
                publish("Connecting to DB engine at: " + conManager.getClientHostname());

                conManager.setNewConnection("mysql", UIClient.this);
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    splash.updateStatus(message);
                }
            }

            @Override
            protected void done() {
                splash.dispose();
                setVisible(true);

                UIDBExplorer uiConDB = new UIDBExplorer();
                uiConDB.setVisible(true);
                initSecondaryComponents();
            }

        };
        worker.execute();
    }

    public JTabbedPane getTabPaneContent() {
        return tabPaneContent;
    }

    public void initSecondaryComponents() {
        installListenersToContentTabPane();
        initJTree();
        UserManager.getInstance().authenticateStoredToken(e -> {
            updateMenuBarForUser((User) e.getSource());
        });
    }

    public void initPrefs() {
        //window state
        int extendedState = prefLoader.getAsInt(PreferenceLoader.PATH_SETTINGS, window_state);
        setExtendedState(extendedState == Frame.ICONIFIED ? 0 : extendedState);

        //size
        if (extendedState != Frame.MAXIMIZED_BOTH) {
            setSize(prefLoader.getAsInt(PreferenceLoader.PATH_SETTINGS, window_width), prefLoader.getAsInt(PreferenceLoader.PATH_SETTINGS, window_height));
        }

        //position
        if (!prefLoader.wasFirstTime()) {
            setLocation(prefLoader.getAsInt(PreferenceLoader.PATH_SETTINGS, window_posX), prefLoader.getAsInt(PreferenceLoader.PATH_SETTINGS, window_posY));
        }

        //dividers
        splitMain.setDividerLocation(prefLoader.getAsInt(PreferenceLoader.PATH_SETTINGS, split_Main_divider));
    }

    public void initJTree() {
        NodeCellRenderer treeNodeCellRenderer = new NodeCellRenderer();
        treeDiagram.setCellRenderer(treeNodeCellRenderer);
        treeDiagram.setCellEditor(new NodeCellEditor(treeDiagram, treeNodeCellRenderer));
        treeDiagram.addTreeWillExpandListener(new TreeExpandListener());
        jTreeContextMenuHandler = new TreeContextMenuHandler(treeDiagram);
        treeDiagram.addMouseListener(new TreeMouseListener(treeDiagram, jTreeContextMenuHandler));
    }

    /**
     * Creates a new {@code UIQueryPanel} and sends it the given SQL statement.
     *
     * @param sql The SQL that will appear within the panel and be immediately
     * executed.
     */
    public void createNewQueryPanelAndRun(String sql) {
        UIQueryPanel queryPanel = new UIQueryPanel(tabPaneContent, sql);
        sendToNewTab(queryPanel);
        queryPanel.runContents();
    }

    public void createJTree() {
        TreeNodeSqless root = new TreeNodeSqless(SQLUtils.getConnectedDB(), DATABASE);
        TreeNodeSqless tables = new TreeNodeSqless("Tables", CAT_TABLES);
        TreeNodeSqless views = new TreeNodeSqless("Views", CAT_VIEWS);
        TreeNodeSqless functions = new TreeNodeSqless("Functions", CAT_FUNCTIONS);
        TreeNodeSqless procedures = new TreeNodeSqless("Procedures", CAT_PROCEDURES);
        tables.add(UIUtils.dummyNode());

        views.add(UIUtils.dummyNode());
        functions.add(UIUtils.dummyNode());
        procedures.add(UIUtils.dummyNode());

        root.add(tables);
        root.add(views);
        root.add(functions);
        root.add(procedures);

        DefaultTreeModel model = (DefaultTreeModel) treeDiagram.getModel();
        model.setRoot(root);
    }

    public void clearJTree() {
        treeDiagram.setModel(new DefaultTreeModel(new TreeNodeSqless()));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        submenuLoggedIn = new javax.swing.JMenu();
        menuLogOut = new javax.swing.JMenuItem();
        toolbarTop = new javax.swing.JToolBar();
        btnNewQuery = new javax.swing.JButton();
        btnOpenFile = new javax.swing.JButton();
        topSeparator = new javax.swing.JToolBar.Separator();
        splitMain = new javax.swing.JSplitPane();
        tabPaneContent = new javax.swing.JTabbedPane();
        pnlDiagram = new javax.swing.JPanel();
        scrlDiagram = new javax.swing.JScrollPane();
        UIManager.put("Tree.paintLines", Boolean.FALSE);
        treeDiagram = new javax.swing.JTree();
        toolbarDiagrama = new javax.swing.JToolBar();
        btnDbManager = new javax.swing.JButton();
        btnRefreshJTree = new javax.swing.JButton();
        barMenu = new javax.swing.JMenuBar();
        submenuArchivo = new javax.swing.JMenu();
        menuNewFile = new javax.swing.JMenuItem();
        menuOpen = new javax.swing.JMenuItem();
        submenuTools = new javax.swing.JMenu();
        menuSettings = new javax.swing.JMenuItem();
        submenuHelp = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenuItem();
        submenuLogin = new javax.swing.JMenu();
        menuLoginSQLess = new javax.swing.JMenuItem();
        menuLoginGoogle = new javax.swing.JMenuItem();

        submenuLoggedIn.setName("submenuLoggedIn"); // NOI18N

        menuLogOut.setAction(actionLogOut);
        menuLogOut.setText("Log out");
        menuLogOut.setName("menuLogOut"); // NOI18N
        submenuLoggedIn.add(menuLogOut);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SQLess - David Orquin, Tomás Casir, Valeria Fornieles");
        setIconImage(new ImageIcon(getClass().getResource("/res/images/ui_general/SQLess_logo_mini.png")).getImage());
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        org.jdesktop.swingx.border.DropShadowBorder dropShadowBorder1 = new org.jdesktop.swingx.border.DropShadowBorder();
        dropShadowBorder1.setShadowSize(3);
        dropShadowBorder1.setShowRightShadow(false);
        toolbarTop.setBorder(dropShadowBorder1);
        toolbarTop.setFloatable(false);
        toolbarTop.setRollover(true);
        toolbarTop.setName("toolbarTop"); // NOI18N

        btnNewQuery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/ui_client/NEW_QUERY_ICON.png"))); // NOI18N
        btnNewQuery.setToolTipText("New SQL file...");
        btnNewQuery.setFocusable(false);
        btnNewQuery.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewQuery.setMargin(new java.awt.Insets(2, 5, 2, 5));
        btnNewQuery.setName("btnNewQuery"); // NOI18N
        btnNewQuery.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewQueryActionPerformed(evt);
            }
        });
        toolbarTop.add(btnNewQuery);

        btnOpenFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/ui_client/OPEN_FILE_ICON.png"))); // NOI18N
        btnOpenFile.setToolTipText("Open SQL file...");
        btnOpenFile.setFocusable(false);
        btnOpenFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenFile.setMargin(new java.awt.Insets(2, 5, 2, 5));
        btnOpenFile.setName("btnOpenFile"); // NOI18N
        btnOpenFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFileActionPerformed(evt);
            }
        });
        toolbarTop.add(btnOpenFile);

        topSeparator.setName("topSeparator"); // NOI18N
        toolbarTop.add(topSeparator);

        splitMain.setDividerLocation(270);
        splitMain.setContinuousLayout(true);
        splitMain.setMinimumSize(new java.awt.Dimension(3, 7));
        splitMain.setName("splitMain"); // NOI18N

        tabPaneContent.setName("tabPaneContent"); // NOI18N
        splitMain.setRightComponent(tabPaneContent);

        org.jdesktop.swingx.border.DropShadowBorder dropShadowBorder2 = new org.jdesktop.swingx.border.DropShadowBorder();
        dropShadowBorder2.setCornerSize(5);
        dropShadowBorder2.setShadowSize(3);
        dropShadowBorder2.setShowLeftShadow(true);
        dropShadowBorder2.setShowTopShadow(true);
        pnlDiagram.setBorder(dropShadowBorder2);
        pnlDiagram.setName("pnlDiagram"); // NOI18N

        scrlDiagram.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        scrlDiagram.setName("scrlDiagram"); // NOI18N

        treeDiagram.setModel(new NodeTreeModel(new com.sqless.ui.tree.TreeNodeSqless()));
        treeDiagram.setMinimumSize(new java.awt.Dimension(270, 0));
        treeDiagram.setName("treeDiagram"); // NOI18N
        treeDiagram.setRowHeight(18);
        scrlDiagram.setViewportView(treeDiagram);

        toolbarDiagrama.setFloatable(false);
        toolbarDiagrama.setRollover(true);
        toolbarDiagrama.setName("toolbarDiagrama"); // NOI18N

        btnDbManager.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/ui_client/DB_MANAGER_ICON.png"))); // NOI18N
        btnDbManager.setToolTipText("Database explorer");
        btnDbManager.setFocusable(false);
        btnDbManager.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDbManager.setName("btnDbManager"); // NOI18N
        btnDbManager.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDbManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDbManagerActionPerformed(evt);
            }
        });
        toolbarDiagrama.add(btnDbManager);

        btnRefreshJTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/ui_client/REFRESH_ICON.png"))); // NOI18N
        btnRefreshJTree.setToolTipText("Refresh");
        btnRefreshJTree.setFocusable(false);
        btnRefreshJTree.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefreshJTree.setName("btnRefreshJTree"); // NOI18N
        btnRefreshJTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshJTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshJTreeActionPerformed(evt);
            }
        });
        toolbarDiagrama.add(btnRefreshJTree);

        javax.swing.GroupLayout pnlDiagramLayout = new javax.swing.GroupLayout(pnlDiagram);
        pnlDiagram.setLayout(pnlDiagramLayout);
        pnlDiagramLayout.setHorizontalGroup(
            pnlDiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiagramLayout.createSequentialGroup()
                .addComponent(toolbarDiagrama, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(scrlDiagram)
        );
        pnlDiagramLayout.setVerticalGroup(
            pnlDiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDiagramLayout.createSequentialGroup()
                .addComponent(toolbarDiagrama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(scrlDiagram, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE))
        );

        splitMain.setLeftComponent(pnlDiagram);

        barMenu.setName("barMenu"); // NOI18N

        submenuArchivo.setText("File");
        submenuArchivo.setName("submenuArchivo"); // NOI18N

        menuNewFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuNewFile.setText("New SQL file...");
        menuNewFile.setName("menuNewFile"); // NOI18N
        menuNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewFileActionPerformed(evt);
            }
        });
        submenuArchivo.add(menuNewFile);

        menuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuOpen.setText("Open...");
        menuOpen.setName("menuOpen"); // NOI18N
        menuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        submenuArchivo.add(menuOpen);

        barMenu.add(submenuArchivo);

        submenuTools.setText("Tools");
        submenuTools.setName("submenuTools"); // NOI18N

        menuSettings.setText("Settings");
        menuSettings.setName("menuSettings"); // NOI18N
        menuSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSettingsActionPerformed(evt);
            }
        });
        submenuTools.add(menuSettings);

        barMenu.add(submenuTools);

        submenuHelp.setText("Help");
        submenuHelp.setName("submenuHelp"); // NOI18N

        menuAbout.setText("About");
        menuAbout.setName("menuAbout"); // NOI18N
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        submenuHelp.add(menuAbout);

        barMenu.add(submenuHelp);
        barMenu.add(Box.createHorizontalGlue());

        submenuLogin.setText("Log in");
        submenuLogin.setName("submenuLogin"); // NOI18N

        menuLoginSQLess.setAction(actionLogIn);
        menuLoginSQLess.setText("Log in (SQLess)");
        menuLoginSQLess.setName("menuLoginSQLess"); // NOI18N
        submenuLogin.add(menuLoginSQLess);

        menuLoginGoogle.setText("Log in (Google)");
        menuLoginGoogle.setName("menuLoginGoogle"); // NOI18N
        submenuLogin.add(menuLoginGoogle);

        barMenu.add(Box.createHorizontalGlue());

        barMenu.add(submenuLogin);

        setJMenuBar(barMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbarTop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1288, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbarTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        com.sqless.utils.UIUtils.flattenPane(splitMain);
        //com.sqless.utils.UIUtils.flattenPane(splitEast);
        splitMain.setResizeWeight(0);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        SQLConnectionManager.getInstance().closeConnection();

        prefLoader.set(PreferenceLoader.PATH_SETTINGS, window_width, "" + getSize().width);
        prefLoader.set(PreferenceLoader.PATH_SETTINGS, window_height, "" + getSize().height);
        prefLoader.set(PreferenceLoader.PATH_SETTINGS, window_posX, "" + getLocation().x);
        prefLoader.set(PreferenceLoader.PATH_SETTINGS, window_posY, "" + getLocation().y);
        prefLoader.set(PreferenceLoader.PATH_SETTINGS, split_Main_divider, "" + splitMain.getDividerLocation());
        prefLoader.set(PreferenceLoader.PATH_SETTINGS, window_state, "" + getExtendedState());
        prefLoader.flush();
    }//GEN-LAST:event_formWindowClosing

    private void btnDbManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDbManagerActionPerformed
        (new UIDBExplorer()).setVisible(true);
    }//GEN-LAST:event_btnDbManagerActionPerformed

    private void btnRefreshJTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshJTreeActionPerformed
        TreePath path = treeDiagram.getSelectionPath();
        if (path == null) {
            return;
        }
        if (((TreeNodeSqless) path.getLastPathComponent()).isRoot()) {
            createJTree();
            return;
        }
        TreeNodeSqless selectedNode = ((TreeNodeSqless) treeDiagram.getLastSelectedPathComponent());

        if (selectedNode.getAllowsChildren()) {
            if (selectedNode.getChildCount() > 0) {
                if (!((TreeNodeSqless) selectedNode.getChildAt(0)).isOfType(DUMMY)) {
                    selectedNode.removeAllChildren();
                    selectedNode.add(UIUtils.dummyNode());
                    treeDiagram.collapsePath(path);
                    treeDiagram.expandPath(path); //this triggers the expand event so stuff under this node is loaded
                }
            } else {
                selectedNode.add(UIUtils.dummyNode());
                treeDiagram.collapsePath(path);
                treeDiagram.expandPath(path); //this triggers the expand event so stuff under this node is loaded
                treeDiagram.collapsePath(path);
            }
        }
    }//GEN-LAST:event_btnRefreshJTreeActionPerformed

    private void menuNewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewFileActionPerformed
        sendToNewTab(new UIQueryPanel(tabPaneContent, ""));
    }//GEN-LAST:event_menuNewFileActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        FileManager.getInstance().openFile();
    }//GEN-LAST:event_menuOpenActionPerformed

    private void btnNewQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewQueryActionPerformed
        sendToNewTab(new UIQueryPanel(tabPaneContent, ""));
    }//GEN-LAST:event_btnNewQueryActionPerformed

    private void btnOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenFileActionPerformed
        FileManager.getInstance().openFile();
    }//GEN-LAST:event_btnOpenFileActionPerformed

    private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
        actionAbout();
    }//GEN-LAST:event_menuAboutActionPerformed

    public void actionAbout() {
        UIAbout uiAbout = new UIAbout();
        uiAbout.setVisible(true);
    }

    private void menuSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSettingsActionPerformed
        actionSettings();
    }//GEN-LAST:event_menuSettingsActionPerformed

    public void updateMenuBarForUser(User user) {
        barMenu.remove(submenuLogin);
        submenuLoggedIn.setText(user.getUsername());
        barMenu.add(submenuLoggedIn);
        barMenu.revalidate();
    }

    public void actionSettings() {
        UISettings uiSettings = new UISettings(this);
        uiSettings.setVisible(true);
    }

    public void sendToNewTab(FrontPanel frontUI) {
        tabPaneContent.addTab(frontUI.getTabTitle(), null, frontUI, frontUI.getTabTitle());
        tabPaneContent.setTabComponentAt(tabPaneContent.getTabCount() - 1, new UIButtonTabComponent(tabPaneContent));
        tabPaneContent.setSelectedIndex(tabPaneContent.getTabCount() - 1);
        frontUI.onCreate();
    }

    public List<UIQueryPanel> getQueryPanels() {
        List<UIQueryPanel> queryContentPanels = new ArrayList<>();
        for (int i = 0; i < tabPaneContent.getTabCount(); i++) {
            if (tabPaneContent.getComponentAt(i) instanceof UIQueryPanel) {
                queryContentPanels.add((UIQueryPanel) tabPaneContent.getComponentAt(i));
            }
        }
        return queryContentPanels;
    }

    private void installListenersToContentTabPane() {
        tabPaneContent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int clickedTab = tabPaneContent.indexAtLocation(e.getX(), e.getY());
                if (clickedTab == -1) {
                    return;
                }
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    ((FrontPanel) tabPaneContent.getComponentAt(clickedTab)).tabClosing(clickedTab);
                }
            }
        });
        tabPaneContent.addChangeListener(e -> {
            if (tabPaneContent.getSelectedIndex() == -1) {
                removeNonStandardMenuItems();
                removeNonStandardToolbarIcons();
                return;
            }
            addMenuBarItems();
            replaceToolbarIcons();
        });
    }

    /**
     * Remplaza los íconos en la toolbar con los íconos traidos del FrontPanel
     * actual.
     */
    public void replaceToolbarIcons() {
        FrontPanel frontUI = (FrontPanel) tabPaneContent.getComponentAt(tabPaneContent.getSelectedIndex());

        removeNonStandardToolbarIcons();
        Component[] toolbarComponents = frontUI.getToolbarComponents(); //tira exception desde JAR

        if (toolbarComponents != null) {
            for (Component toolbarComponent : toolbarComponents) {
                toolbarTop.add(toolbarComponent);
            }
            toolbarTop.revalidate();
        }
    }

    public void removeNonStandardToolbarIcons() {
        int componentCount = toolbarTop.getComponentCount();
        for (int i = componentCount - 1; i > 2; i--) {
            toolbarTop.remove(i);
        }
        toolbarTop.revalidate();
        toolbarTop.repaint();
    }

    public void addMenuBarItems() {
        FrontPanel frontUI = (FrontPanel) tabPaneContent.getComponentAt(tabPaneContent.getSelectedIndex());
        removeNonStandardMenuItems();
        JMenuItem[] menuItems = frontUI.getMenuItems();
        if (menuItems != null) {
            for (JMenuItem menuItem : menuItems) {
                submenuArchivo.add(menuItem);
            }
        }
    }

    public void removeNonStandardMenuItems() {
        int menuCount = submenuArchivo.getItemCount();
        for (int i = menuCount - 1; i > 1; i--) {
            submenuArchivo.remove(i);
        }
    }

    public JTree getTree() {
        return treeDiagram;
    }

    /**
     * Returns the only instance of {@code UIClient}.
     *
     * @return this singleton's instance.
     */
    public static UIClient getInstance() {
        return instance;
    }

    private Action actionLogIn = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UILogin uiLogin = new UILogin();
            uiLogin.setVisible(true);
            User user = UserManager.getInstance().getActive();
            if (user != null) {
                updateMenuBarForUser(user);
            }
        }
    };

    private Action actionLogOut = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserManager.getInstance().logOut();
            barMenu.remove(submenuLoggedIn);
            barMenu.add(submenuLogin);
            barMenu.revalidate();
            barMenu.repaint();
        }
    };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar barMenu;
    private javax.swing.JButton btnDbManager;
    private javax.swing.JButton btnNewQuery;
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JButton btnRefreshJTree;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuItem menuLogOut;
    private javax.swing.JMenuItem menuLoginGoogle;
    private javax.swing.JMenuItem menuLoginSQLess;
    private javax.swing.JMenuItem menuNewFile;
    private javax.swing.JMenuItem menuOpen;
    private javax.swing.JMenuItem menuSettings;
    private javax.swing.JPanel pnlDiagram;
    private javax.swing.JScrollPane scrlDiagram;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JMenu submenuArchivo;
    private javax.swing.JMenu submenuHelp;
    private javax.swing.JMenu submenuLoggedIn;
    private javax.swing.JMenu submenuLogin;
    private javax.swing.JMenu submenuTools;
    private javax.swing.JTabbedPane tabPaneContent;
    private javax.swing.JToolBar toolbarDiagrama;
    private javax.swing.JToolBar toolbarTop;
    private javax.swing.JToolBar.Separator topSeparator;
    private javax.swing.JTree treeDiagram;
    // End of variables declaration//GEN-END:variables
}
