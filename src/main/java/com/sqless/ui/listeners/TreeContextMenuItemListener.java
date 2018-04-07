package com.sqless.ui.listeners;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.sql.objects.SQLDroppable;
import com.sqless.sql.objects.SQLExecutable;
import com.sqless.ui.tree.TreeContextMenuItem;
import com.sqless.sql.objects.SQLSelectable;
import com.sqless.utils.UIUtils;
import com.sqless.ui.UIClient;
import com.sqless.ui.tree.SQLessTreeNode;
import com.sqless.sql.objects.SQLTable;
import com.sqless.ui.UICreateTableSQLess;
import com.sqless.ui.UIEditTable;
import com.sqless.ui.UIExecuteCallable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Clase que maneja la acción de click e interacción con cada
 * <code>TreeContextMenuItem</code> de un <code>ContextMenu</code> del diagrama
 * JTree.
 */
public class TreeContextMenuItemListener extends MouseAdapter {

    private UIClient client;

    public TreeContextMenuItemListener() {
        client = UIClient.getInstance();
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
        }
    }

    public void doDrop(SQLessTreeNode node) {
        int confirmacion = UIUtils.showConfirmationMessage("Drop object",
                "Would you like to permanently drop this object?", client);
        if (confirmacion != 0) {
            return;
        }
        SQLQuery dropQuery = new SQLUpdateQuery(((SQLDroppable) node.getUserObject()).getDropStatement(), true) {
            @Override
            public void onSuccess(int updateCount) {
                SwingUtilities.invokeLater(() -> {
                    if (!node.isOfType(SQLessTreeNode.NodeType.DATABASE)) {
                        JTree tree = client.getTree();
                        UIUtils.selectTreeNode(tree, node.getParent());
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
                uiModifyTable.prepararUI();
                client.sendToNewTab(uiModifyTable);
                break;
        }
    }

    public void doEdit(SQLessTreeNode node) {
        switch (node.getType()) {
            case TABLE:
                SQLTable table = (SQLTable) node.getUserObject();
                UIEditTable uiEditTable = new UIEditTable(client.getTabPaneContent(), table);
                if (uiEditTable.tableAllowsModifications()) {
                    client.sendToNewTab(uiEditTable);
                } else {
                    UIUtils.showErrorMessage("Edit rows", "Para usar esta funcionalidad, la tabla debe tener una columna marcada como primary key.", null);
                }
                break;
        }
    }

    public void doCreate(SQLessTreeNode node) {
        switch (node.getType()) {
            case CAT_TABLES:
                UICreateTableSQLess uiCreateTable = new UICreateTableSQLess(client.getTabPaneContent(), null);
                uiCreateTable.prepararUI();
                client.sendToNewTab(uiCreateTable);
                break;
            case TABLE: //create column
                break;
        }
    }

    public void doRename(SQLessTreeNode node) {
        client.getTree().setEditable(true);
        client.getTree().startEditingAtPath(new TreePath(node.getPath()));
    }

    private SQLessTreeNode getCallingNode() {
        return (SQLessTreeNode) client.getTree().getSelectionPath().getLastPathComponent();
    }
}
