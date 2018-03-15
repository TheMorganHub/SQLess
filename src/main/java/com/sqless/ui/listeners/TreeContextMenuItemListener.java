package com.sqless.ui.listeners;

import com.sqless.sql.objects.SQLDroppable;
import com.sqless.sql.objects.SQLExecutable;
import com.sqless.ui.tree.TreeContextMenuItem;
import com.sqless.sql.objects.SQLSelectable;
import com.sqless.utils.UIUtils;
import com.sqless.ui.UIClient;
import com.sqless.ui.tree.TreeNodeSqless;
import com.sqless.sql.objects.SQLTable;
import com.sqless.ui.UICreateTableSQLess;
import com.sqless.ui.UIEditTable;
import com.sqless.ui.UIExecuteCallable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        TreeNodeSqless node = getCallingNode();
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

    public void doDrop(TreeNodeSqless node) {
        int confirmacion = UIUtils.showConfirmationMessage("Drop object",
                "Would you like to permanently drop this object?", client);
        if (confirmacion != 0) {
            return;
        }

        client.createNewQueryPanelAndRun(((SQLDroppable) node.getUserObject()).getDropStatement());
    }

    public void doSelect(TreeNodeSqless node) {
        client.createNewQueryPanelAndRun(((SQLSelectable) node.getUserObject()).getSelectStatement(1000));
    }

    public void doExecute(TreeNodeSqless node) {
        SQLExecutable executable = (SQLExecutable) node.getUserObject();
        UIExecuteCallable uiExecuteCallable = new UIExecuteCallable(executable);
        uiExecuteCallable.execute();
    }

    public void doModify(TreeNodeSqless node) {
        switch (node.getType()) {
            case TABLE:
                SQLTable table = (SQLTable) node.getUserObject();
                UICreateTableSQLess uiModifyTable = new UICreateTableSQLess(client.getTabPaneContent(), table);
                uiModifyTable.prepararUI();
                client.sendToNewTab(uiModifyTable);
                break;
        }
    }

    public void doEdit(TreeNodeSqless node) {
        switch (node.getType()) {
            case TABLE:
                SQLTable table = (SQLTable) node.getUserObject();
                UIEditTable uiEditTable = new UIEditTable(client.getTabPaneContent(), table);
                client.sendToNewTab(uiEditTable);
                break;
        }
    }

    public void doCreate(TreeNodeSqless node) {
        switch (node.getType()) {
            case CAT_TABLES:
                UICreateTableSQLess uiCreateTable = new UICreateTableSQLess(client.getTabPaneContent(), null);
                uiCreateTable.prepararUI();
                UIClient.getInstance().sendToNewTab(uiCreateTable);
                break;
            case TABLE: //create column
                break;
        }
    }

    public void doRename(TreeNodeSqless node) {
        client.getTree().setEditable(true);
        client.getTree().startEditingAtPath(new TreePath(node.getPath()));
    }

    private TreeNodeSqless getCallingNode() {
        return (TreeNodeSqless) client.getTree().getSelectionPath().getLastPathComponent();
    }
}
