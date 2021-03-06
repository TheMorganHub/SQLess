package com.sqless.ui.tree;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLUpdateQuery;
import com.sqless.sql.objects.SQLRenameable;
import com.sqless.utils.UIUtils;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Morgan
 */
public class NodeTreeModel extends DefaultTreeModel {

    private NodeCellEditor cellEditor;

    public NodeTreeModel(TreeNode root) {
        super(root);
    }

    public void setCellEditor(NodeCellEditor cellEditor) {
        this.cellEditor = cellEditor;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        SQLessTreeNode aNode = (SQLessTreeNode) path.getLastPathComponent();

        aNode.setUserObject(aNode.getUserObject());
        rename(aNode, (SQLRenameable) aNode.getUserObject(), newValue.toString());
    }

    public void rename(SQLessTreeNode node, SQLRenameable sqlObj, String newName) {
        SQLQuery renameQuery = new SQLUpdateQuery(sqlObj.getRenameStatement(newName)) {
            @Override
            public void onSuccess(int updateCount) {
                sqlObj.rename(newName);
                nodeChanged(node);
                cellEditor.stopCellEditing();
            }

            @Override
            public void onFailure(String errMessage) {
                UIUtils.showErrorMessage("Renombrar objeto", "No se pudo renombrar el objeto. El servidor respondió con el mensaje:\n" + errMessage, null);
                cellEditor.cancelCellEditing();
            }
        };
        renameQuery.exec();
    }

}
