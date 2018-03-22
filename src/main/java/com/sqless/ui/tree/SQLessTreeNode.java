package com.sqless.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class SQLessTreeNode extends DefaultMutableTreeNode {

    /**
     * Las constantes que representan al tipo de nodo. Cada objeto SQL
     * representado en el árbol visual tendrá un tipo de nodo asociado a él. Por
     * ejemplo, una tabla SQL será de tipo TABLE. Una columna de una tabla será
     * de tipo TABLE_COLUMN.
     */
    public enum NodeType {
        DATABASE, TABLE, TABLE_COLUMN, INDEX, VIEW, VIEW_COLUMN, TRIGGER, FUNCTION, PROCEDURE,
        DUMMY, CAT_TABLES, CAT_VIEWS, CAT_VIEWS_COLUMNS, CAT_FUNCTIONS,
        CAT_COLUMNS, CAT_INDEXES, CAT_TRIGGERS, CAT_PROCEDURES;
    }

    private NodeType type;

    public SQLessTreeNode(Object userObject, NodeType type) {
        super(userObject);
        this.type = type;
    }

    public SQLessTreeNode() {
        super();
        this.type = NodeType.DUMMY;
    }

    public NodeType getType() {
        return type;
    }

    public boolean isOfType(NodeType type) {
        return this.type.equals(type);
    }

}
