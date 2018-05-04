package com.sqless.ui.listeners;

import com.sqless.utils.UIUtils;
import com.sqless.ui.UIClient;
import com.sqless.ui.tree.SQLessTreeNode;
import com.sqless.sql.objects.SQLIndex;
import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLTable;
import com.sqless.sql.objects.SQLTrigger;
import com.sqless.sql.objects.SQLDataObject;
import com.sqless.sql.objects.SQLExecutable;
import com.sqless.utils.SQLUtils;
import com.sqless.sql.objects.SQLView;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import static com.sqless.ui.tree.SQLessTreeNode.NodeType.*;

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
        SQLessTreeNode node = (SQLessTreeNode) event.getPath().getLastPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) ((JTree) event.getSource()).getModel();
        client.setCursor(UIUtils.WAIT_CURSOR);        
        try {
            if (node.isOfType(CAT_TABLES)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLUtils.getConnectedDB().loadTables();
                    for (SQLTable table : SQLUtils.getConnectedDB().getTables()) {
                        SQLessTreeNode tableNode = new SQLessTreeNode(table, TABLE);
                        tableNode.add(UIUtils.dummyNode());
                        node.add(tableNode);
                        model.reload(node);
                    }
                }
            }

            if (node.isOfType(TABLE)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLessTreeNode columnsNode = new SQLessTreeNode("Columnas", CAT_COLUMNS);
                    SQLessTreeNode indexesNode = new SQLessTreeNode("Indices", CAT_INDEXES);
                    SQLessTreeNode triggersNode = new SQLessTreeNode("Triggers", CAT_TRIGGERS);
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
                        SQLessTreeNode columnNode = new SQLessTreeNode(column, TABLE_COLUMN);
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
                        SQLessTreeNode indexNode = new SQLessTreeNode(index, INDEX);
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
                        SQLessTreeNode triggerNode = new SQLessTreeNode(trigger, TRIGGER);
                        node.add(triggerNode);
                        triggerNode.setAllowsChildren(false);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_PROCEDURES)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLUtils.getConnectedDB().loadProcedures();
                    for (SQLExecutable procedure : SQLUtils.getConnectedDB().getProcedures()) {
                        SQLessTreeNode procedureNode = new SQLessTreeNode(procedure, PROCEDURE);
                        node.add(procedureNode);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_FUNCTIONS)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLUtils.getConnectedDB().loadFunctions();
                    for (SQLExecutable function : SQLUtils.getConnectedDB().getFunctions()) {
                        SQLessTreeNode functionNode = new SQLessTreeNode(function, FUNCTION);
                        node.add(functionNode);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(CAT_VIEWS)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLUtils.getConnectedDB().loadViews();
                    for (SQLView view : SQLUtils.getConnectedDB().getViews()) {
                        SQLessTreeNode viewNode = new SQLessTreeNode(view, VIEW);
                        viewNode.add(UIUtils.dummyNode());
                        node.add(viewNode);
                    }
                    model.reload(node);
                }
            }

            if (node.isOfType(VIEW)) {
                if (UIUtils.nodeHasDummy(node)) {
                    SQLessTreeNode columnsNode = new SQLessTreeNode("Columnas", CAT_VIEWS_COLUMNS);
                    SQLessTreeNode indexesNode = new SQLessTreeNode("Indices", CAT_INDEXES);
                    SQLessTreeNode triggersNode = new SQLessTreeNode("Triggers", CAT_TRIGGERS);
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
                        SQLessTreeNode columnNode = new SQLessTreeNode(column, VIEW_COLUMN);
                        node.add(columnNode);
                    }
                    model.reload(node);
                }
            }
        } catch (NullPointerException ex) {
            //potencialmente salta cuando la conexión se cerró y se volvió a restablecer
            //poco ortodoxo catchear un NullPointerException pero en esta ocasión es necesario.
        } finally {
            client.setCursor(UIUtils.DEFAULT_CURSOR);
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    }
}
