package com.sqless.ui.tree;

import com.sqless.utils.UIUtils;
import com.sqless.sql.objects.SQLColumn;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import static com.sqless.ui.tree.SQLessTreeNode.NodeType.*;
import static com.sqless.utils.UIUtils.icon;

/**
 * The class in charge of rendering each cell of the JTree.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class NodeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        SQLessTreeNode node = (SQLessTreeNode) value;

        switch (node.getType()) {
            case DATABASE:
                setIcon(icon("jtree", "DB"));
                break;
            case CAT_TABLES:
                setIcon(icon("jtree", "CAT_TABLE"));
                break;
            case CAT_COLUMNS:
                setIcon(icon("jtree", "STANDARD_FOLDER"));
                break;
            case CAT_VIEWS:
                setIcon(icon("jtree", "CAT_VIEW"));
                break;
            case CAT_PROCEDURES:
                setIcon(icon("jtree", "CAT_PROCEDURE"));
                break;
            case CAT_FUNCTIONS:
                setIcon(icon("jtree", "CAT_FUNCTION"));
                break;
            case CAT_INDEXES:
                setIcon(icon("jtree", "STANDARD_FOLDER"));
                break;
            case CAT_TRIGGERS:
                setIcon(icon("jtree", "STANDARD_FOLDER"));
                break;
            case CAT_VIEWS_COLUMNS:
                setIcon(icon("jtree", "STANDARD_FOLDER"));
                break;
            case TABLE:
                setIcon(icon("jtree", "TABLE"));
                break;
            case TABLE_COLUMN:
                if (nodoEsPrimaryKey(node)) {
                    setIcon(icon("jtree", "PRIMARY_KEY"));
                } else if (nodoEsForeignKey(node)) {
                    setIcon(icon("jtree", "FOREIGN_KEY"));
                } else {
                    setIcon(icon("jtree", "COLUMN"));
                }
                break;
            case VIEW_COLUMN:
                setIcon(icon("jtree", "COLUMN"));
                break;
            case INDEX:
                setIcon(icon("jtree", "INDEX"));
                break;
            case TRIGGER:
                setIcon(icon("jtree", "TRIGGER"));
                break;
            case VIEW:
                setIcon(icon("jtree", "VIEW"));
                break;
            case PROCEDURE:
                setIcon(icon("jtree", "STORED_PROCEDURE"));
                break;
            case FUNCTION:
                setIcon(icon("jtree", "FUNCTION"));
                break;
        }

        ((javax.swing.JLabel) this).setIconTextGap(7);
        ((javax.swing.JLabel) this).setFont(UIUtils.SEGOE_UI_FONT);
        return this;
    }

    boolean nodoEsPrimaryKey(SQLessTreeNode nodo) {
        return ((SQLColumn) nodo.getUserObject()).isPK();
    }

    boolean nodoEsForeignKey(SQLessTreeNode nodo) {
        return ((SQLColumn) nodo.getUserObject()).isFK();
    }
}
