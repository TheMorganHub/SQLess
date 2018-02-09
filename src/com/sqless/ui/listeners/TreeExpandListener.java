package com.sqless.ui.listeners;

import com.sqless.utils.UIUtils;
import com.sqless.ui.UIClient;
import com.sqless.ui.tree.TreeNodeSqless;
import com.sqless.sql.objects.SQLIndex;
import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLTable;
import com.sqless.sql.objects.SQLTrigger;
import com.sqless.sql.objects.SQLDataObject;
import com.sqless.utils.SQLUtils;
import com.sqless.sql.objects.SQLView;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import static com.sqless.ui.tree.TreeNodeSqless.NodeType.*;

/**
 * A listener that handles the action that has to happen BEFORE a JTree node is
 * expanded.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class TreeExpandListener implements TreeWillExpandListener {

    private UIClient client;

    public TreeExpandListener() {
        this.client = UIClient.getInstance();
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        TreePath path = event.getPath();
        TreeNodeSqless node = (TreeNodeSqless) event.getPath().getLastPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) ((JTree) event.getSource()).getModel();
        client.setCursor(UIUtils.WAIT_CURSOR);
        try {
            if (node.isOfType(CAT_TABLES)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLUtils.getConnectedDB().loadTables();
                    for (SQLTable table : SQLUtils.getConnectedDB().getTables()) {
                        TreeNodeSqless tableNode = new TreeNodeSqless(table, TABLE);
                        tableNode.add(UIUtils.dummyNode());
                        node.add(tableNode);
                        model.reload(node);
                    }
                }
            }

            if (node.isOfType(TABLE)) {
                if (UIUtils.nodeHasDummy(node)) {
                    TreeNodeSqless columnsNode = new TreeNodeSqless("Columns", CAT_COLUMNS);
                    TreeNodeSqless indexesNode = new TreeNodeSqless("Indexes", CAT_INDEXES);
                    TreeNodeSqless triggersNode = new TreeNodeSqless("Triggers", CAT_TRIGGERS);
                    columnsNode.add(UIUtils.dummyNode());
                    indexesNode.add(UIUtils.dummyNode());
                    triggersNode.add(UIUtils.dummyNode());
                    node.add(columnsNode);
                    node.add(indexesNode);
                    node.add(triggersNode);
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_COLUMNS)) {
                if (UIUtils.nodeHasDummy(node)) {
                    System.out.println("Loading columns...");
                    String selectableName = path.getPathComponent(path.getPathCount() - 2).toString();
                    SQLDataObject selectable = SQLUtils.getConnectedDB().getTableObjectByName(selectableName);
                    selectable.loadColumns();
                    for (SQLColumn column : selectable.getColumns()) {
                        TreeNodeSqless columnNode = new TreeNodeSqless(column, TABLE_COLUMN);
                        node.add(columnNode);
                        columnNode.setAllowsChildren(false);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_INDEXES)) {
                if (UIUtils.nodeHasDummy(node)) {
                    String selectableName = path.getPathComponent(path.getPathCount() - 2).toString();
                    SQLDataObject selectable = SQLUtils.getConnectedDB().getTableObjectByName(selectableName);
                    selectable.loadIndexes();
                    for (SQLIndex index : selectable.getIndexes()) {
                        TreeNodeSqless indexNode = new TreeNodeSqless(index, INDEX);
                        node.add(indexNode);
                        indexNode.setAllowsChildren(false);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_TRIGGERS)) {
                if (UIUtils.nodeHasDummy(node)) {
                    String selectableName = path.getPathComponent(path.getPathCount() - 2).toString();
                    SQLDataObject selectable = SQLUtils.getConnectedDB().getTableObjectByName(selectableName);
                    selectable.loadTriggers();
                    for (SQLTrigger trigger : selectable.getTriggers()) {
                        TreeNodeSqless triggerNode = new TreeNodeSqless(trigger, TRIGGER);
                        node.add(triggerNode);
                        triggerNode.setAllowsChildren(false);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_PROCEDURES)) {
                throw new UnsupportedOperationException("Not implemented yet.");
            }

            if (node.isOfType(CAT_FUNCTIONS)) {
                throw new UnsupportedOperationException("Not implemented yet.");
            }

            if (node.isOfType(PROCEDURE) || node.isOfType(FUNCTION)) {
                throw new UnsupportedOperationException("Not implemented yet.");
            }

            if (node.isOfType(CAT_PARAMETERS)) {
                throw new UnsupportedOperationException("Not implemented yet.");
            }

            if (node.isOfType(CAT_VIEWS)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLUtils.getConnectedDB().loadViews();
                    for (SQLView view : SQLUtils.getConnectedDB().getViews()) {
                        TreeNodeSqless viewNode = new TreeNodeSqless(view, VIEW);
                        viewNode.add(UIUtils.dummyNode());
                        node.add(viewNode);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(VIEW)) {
                if (UIUtils.nodeHasDummy(node)) {
                    TreeNodeSqless columnsNode = new TreeNodeSqless("Columns", CAT_VIEWS_COLUMNS);
                    TreeNodeSqless indexesNode = new TreeNodeSqless("Indexes", CAT_INDEXES);
                    TreeNodeSqless triggersNode = new TreeNodeSqless("Triggers", CAT_TRIGGERS);
                    columnsNode.add(UIUtils.dummyNode());
                    indexesNode.add(UIUtils.dummyNode());
                    triggersNode.add(UIUtils.dummyNode());
                    node.add(columnsNode);
                    node.add(indexesNode);
                    node.add(triggersNode);
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_VIEWS_COLUMNS)) {
                if (UIUtils.nodeHasDummy(node)) {
                    String selectableName = path.getPathComponent(path.getPathCount() - 2).toString();
                    SQLDataObject selectable = SQLUtils.getConnectedDB().getTableObjectByName(selectableName);
                    selectable.loadColumns();
                    for (SQLColumn column : selectable.getColumns()) {
                        TreeNodeSqless columnNode = new TreeNodeSqless(column, VIEW_COLUMN);
                        node.add(columnNode);
                    }
                    model.reload(node);
                }
            }
        } finally {
            client.setCursor(UIUtils.DEFAULT_CURSOR);
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    }
}
