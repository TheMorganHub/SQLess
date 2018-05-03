package com.sqless.ui.listeners;

import com.sqless.file.FileManager;
import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.sql.connection.SQLConnectionManager;
import com.sqless.sql.objects.SQLDroppable;
import com.sqless.sql.objects.SQLExecutable;
import com.sqless.ui.tree.TreeContextMenuItem;
import com.sqless.sql.objects.SQLSelectable;
import com.sqless.utils.UIUtils;
import com.sqless.ui.UIClient;
import com.sqless.ui.tree.SQLessTreeNode;
import com.sqless.sql.objects.SQLTable;
import com.sqless.ui.UICreateTableSQLess;
import com.sqless.ui.UIDatabaseDumper;
import com.sqless.ui.UIEditTable;
import com.sqless.ui.UIExecuteCallable;
import com.sqless.ui.UIExecuteFromScript;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

/**
 * Clase que maneja la acción de click e interacción con cada
 * <code>TreeContextMenuItem</code> de un <code>ContextMenu</code> del diagrama
 * JTree.
 */
public class TreeContextMenuItemListener extends MouseAdapter {

    private UIClient client;
    /**
     * Un mapa de acciones que ejecutarán los nodos con funcionalidad OTHER
     * dependiendo de la key que los menu item tengan.
     *
     * @see TreeContextMenuItem#getActionKey()
     */
    private Map<String, ActionListener> actionMap;

    public TreeContextMenuItemListener() {
        client = UIClient.getInstance();
        loadActions();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        TreeContextMenuItem source = (TreeContextMenuItem) e.getSource();
        SQLessTreeNode node = getCallingNode();
        switch (source.getItemFunctionality()) {
            case SELECT:
                doSelect(node);
                break;
            case DROP:
                doDrop(node);
                break;
            case EXECUTE:
                doExecute(node);
                break;
            case CREATE:
                doCreate(node);
                break;
            case RENAME:
                doRename(node);
                break;
            case MODIFY:
                doModify(node);
                break;
            case EDIT:
                doEdit(node);
                break;
            case OTHER:
                actionOther(node, source);
                break;
        }
    }

    private void loadActions() {
        actionMap = new HashMap<>();
        actionMap.put("EXPORT_DB", actionExportDb);
        actionMap.put("EXPORT_DB_NO_DATA", actionExportDbNoData);
        actionMap.put("EXECUTE_FROM_SCRIPT", actionExecuteFromScript);
        actionMap.put("SELECT_AS_MAPLE", actionSelectAsMaple);
    }

    public void doDrop(SQLessTreeNode node) {
        int confirmacion = UIUtils.showConfirmationMessage("Eliminar objeto",
                "¿Estás seguro que deseas eliminar permanentemente este objeto?", client);
        if (confirmacion != 0) {
            return;
        }
        SQLQuery dropQuery = new SQLUpdateQuery(((SQLDroppable) node.getUserObject()).getDropStatement(), true) {
            @Override
            public void onSuccess(int updateCount) {
                SwingUtilities.invokeLater(() -> {
                    if (node.isOfType(SQLessTreeNode.NodeType.DATABASE)) {
                        client.clearJTree();
                        SQLConnectionManager.getInstance().setNewConnection("mysql", null);
                    } else {
                        UIUtils.selectTreeNode(client.getTree(), node.getParent());
                        client.refreshJTree();
                    }
                });
            }
        };
        dropQuery.exec();
    }

    public void doSelect(SQLessTreeNode node) {
        client.createNewQueryPanelAndRun(((SQLSelectable) node.getUserObject()).getSelectStatement(1000));
    }

    public void doExecute(SQLessTreeNode node) {
        SQLExecutable executable = (SQLExecutable) node.getUserObject();
        UIExecuteCallable uiExecuteCallable = new UIExecuteCallable(executable);
        uiExecuteCallable.execute();
    }

    public void doModify(SQLessTreeNode node) {
        switch (node.getType()) {
            case TABLE:
                SQLTable table = (SQLTable) node.getUserObject();
                UICreateTableSQLess uiModifyTable = new UICreateTableSQLess(client.getTabPaneContent(), table);
                client.sendToNewTab(uiModifyTable);
                break;
        }
    }

    public void doEdit(SQLessTreeNode node) {
        switch (node.getType()) {
            case TABLE:
                SQLTable table = (SQLTable) node.getUserObject();
                UIEditTable uiEditTable = new UIEditTable(client.getTabPaneContent(), table);
                if (!uiEditTable.checkIntegrity()) {
                    break;
                }
                if (uiEditTable.tableAllowsModifications()) {
                    client.sendToNewTab(uiEditTable);
                } else {
                    UIUtils.showErrorMessage("Editar filas", "Para usar esta funcionalidad, la tabla debe tener una columna marcada como primary key.\nSi la tabla tiene una PK, "
                            + "asegúrate que la conexión con la base de datos esté activa.", UIClient.getInstance());
                }
                break;
        }
    }

    public void doCreate(SQLessTreeNode node) {
        switch (node.getType()) {
            case CAT_TABLES:
                UICreateTableSQLess uiCreateTable = new UICreateTableSQLess(client.getTabPaneContent(), null);
                client.sendToNewTab(uiCreateTable);
                break;
        }
    }

    public void doRename(SQLessTreeNode node) {
        client.getTree().setEditable(true);
        client.getTree().startEditingAtPath(new TreePath(node.getPath()));
    }

    public void actionOther(SQLessTreeNode node, TreeContextMenuItem source) {
        String key = source.getActionKey();
        ActionListener action = actionMap.get(key);
        if (action != null) {
            action.actionPerformed(new ActionEvent(node, 0, key));
        }
    }

    private SQLessTreeNode getCallingNode() {
        return (SQLessTreeNode) client.getTree().getSelectionPath().getLastPathComponent();
    }

    private ActionListener actionExportDb = e -> {
        try (InputStream link = getClass().getResourceAsStream("/sqldump/mysqldump.exe")) {
            File tempDirectory = new File(System.getProperty("java.io.tmpdir") + "/SQLess");
            File tempFile = new File(tempDirectory.getPath() + "/mysqldump.exe");
            if (tempDirectory.exists()) {
                if (!tempFile.exists()) {
                    Files.copy(link, tempFile.getAbsoluteFile().toPath());
                } else {
                    System.out.println("El archivo ya existe");
                }
                UIDatabaseDumper dbDumper = new UIDatabaseDumper(tempFile);
                dbDumper.start();
            } else {
                if (tempDirectory.mkdir()) {
                    Files.copy(link, tempFile.getAbsoluteFile().toPath());
                }
            }
            tempFile.deleteOnExit();
        } catch (IOException ex) {
            UIUtils.showErrorMessage("Error", ex.getMessage(), null);
        }
    };

    private ActionListener actionExportDbNoData = e -> {
        try (InputStream link = getClass().getResourceAsStream("/sqldump/mysqldump.exe")) {
            File tempDirectory = new File(System.getProperty("java.io.tmpdir") + "/SQLess");
            File tempFile = new File(tempDirectory.getPath() + "/mysqldump.exe");
            if (tempDirectory.exists()) {
                if (!tempFile.exists()) {
                    Files.copy(link, tempFile.getAbsoluteFile().toPath());
                } else {
                    System.out.println("El archivo ya existe");
                }
                UIDatabaseDumper dbDumper = new UIDatabaseDumper(tempFile, true);
                dbDumper.start();
            } else {
                if (tempDirectory.mkdir()) {
                    Files.copy(link, tempFile.getAbsoluteFile().toPath());
                }
            }
            tempFile.deleteOnExit();
        } catch (IOException ex) {
            UIUtils.showErrorMessage("Error", ex.getMessage(), null);
        }
    };

    private ActionListener actionExecuteFromScript = e -> {
        FileManager.getInstance().loadFile(fileContents -> {
            UIExecuteFromScript uiExecuteFromScript = new UIExecuteFromScript(fileContents);
            uiExecuteFromScript.start();
        });
    };

    private ActionListener actionSelectAsMaple = e -> {
        SQLessTreeNode node = (SQLessTreeNode) e.getSource();
        client.createNewMapleQueryPanelAndRun(((SQLSelectable) node.getUserObject()).getMapleSelectStatement(1000));
    };
}
