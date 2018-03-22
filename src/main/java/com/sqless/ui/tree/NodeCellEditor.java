package com.sqless.ui.tree;

import com.sqless.sql.objects.SQLObject;
import static com.sqless.utils.UIUtils.icon;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Morgan
 */
public class NodeCellEditor extends DefaultTreeCellEditor {

    public NodeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
        ((NodeTreeModel) tree.getModel()).setCellEditor(this);
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        SQLessTreeNode node = (SQLessTreeNode) value;
        SQLObject nodeObject = (SQLObject) node.getUserObject();
        super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
        DefaultTextField txtField = (DefaultTextField) editingComponent;
        txtField.setText(nodeObject.getName());
        return editingContainer;
    }

    @Override
    protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        SQLessTreeNode node = (SQLessTreeNode) value;
        if (renderer != null) {
            switch (node.getType()) {
                case DATABASE:
                    editingIcon = icon("jtree", "DB");
                    break;
                case CAT_TABLES:
                    editingIcon = icon("jtree", "CAT_TABLE");
                    break;
                case CAT_VIEWS:
                    editingIcon = icon("jtree", "CAT_VIEW");
                    break;
                case CAT_PROCEDURES:
                    editingIcon = icon("jtree", "CAT_PROCEDURE");
                    break;
                case CAT_FUNCTIONS:
                    editingIcon = icon("jtree", "CAT_FUNCTION");
                    break;
                case CAT_COLUMNS:
                case CAT_INDEXES:
                case CAT_TRIGGERS:
                case CAT_VIEWS_COLUMNS:
                    editingIcon = icon("jtree", "STANDARD_FOLDER");
                    break;
                case TABLE:
                    editingIcon = icon("jtree", "TABLE");
                    break;
                case TABLE_COLUMN:
                    if (((NodeCellRenderer) renderer).nodoEsPrimaryKey(node)) {
                        editingIcon = icon("jtree", "PRIMARY_KEY");
                    } else if (((NodeCellRenderer) renderer).nodoEsForeignKey(node)) {
                        editingIcon = icon("jtree", "FOREIGN_KEY");
                    } else {
                        editingIcon = icon("jtree", "COLUMN");
                    }
                    break;
                case VIEW_COLUMN:
                    editingIcon = icon("jtree", "COLUMN");
                    break;
                case INDEX:
                    editingIcon = icon("jtree", "INDEX");
                    break;
                case TRIGGER:
                    editingIcon = icon("jtree", "TRIGGER");
                    break;
                case VIEW:
                    editingIcon = icon("jtree", "VIEWS");
                    break;
                case PROCEDURE:
                    editingIcon = icon("jtree", "STORED_PROCEDURE");
                    break;
                case FUNCTION:
                    editingIcon = icon("jtree", "FUNCTION");
                    break;
            }

            if (editingIcon != null) {
                offset = renderer.getIconTextGap()
                        + editingIcon.getIconWidth();
            } else {
                offset = renderer.getIconTextGap();
            }
        } else {
            editingIcon = null;
            offset = 0;
        }
    }

    @Override
    public boolean stopCellEditing() {
        tree.setEditable(false);
        return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        tree.setEditable(false);
        super.cancelCellEditing();
    }

    @Override
    protected void startEditingTimer() {
        if (timer == null) {
            timer = new Timer(700, this);
            timer.setRepeats(false);
        }
        timer.start();
    }

}
