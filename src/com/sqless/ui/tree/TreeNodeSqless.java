package com.sqless.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeSqless extends DefaultMutableTreeNode {

    public enum NodeType {
        DATABASE, TABLE, TABLE_COLUMN, INDEX, VIEW, VIEW_COLUMN, TRIGGER, FUNCTION, PROCEDURE,
        PARAMETER, DUMMY, CAT_TABLES, CAT_VIEWS, CAT_VIEWS_COLUMNS, CAT_FUNCTIONS,
        CAT_COLUMNS, CAT_INDEXES, CAT_TRIGGERS, CAT_PROCEDURES,
        CAT_PARAMETERS;
    }

    private NodeType type;

    public TreeNodeSqless(Object userObject, NodeType type) {
        super(userObject);
        this.type = type;
    }

    public TreeNodeSqless() {
        super();
        this.type = NodeType.DUMMY;
    }

    public NodeType getType() {
        return type;
    }

    public boolean isCategory() {
        return type.toString().split("_")[0].equals("CAT");
    }

    public boolean isOfType(NodeType type) {
        return this.type.equals(type);
    }

}
