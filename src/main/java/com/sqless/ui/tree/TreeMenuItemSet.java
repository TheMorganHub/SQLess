package com.sqless.ui.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Use a {@code TreeMenuItemSet} to group {@code TreeContextMenuItems} by
 * {@code NodeTypes}.
 * <p>
 * {@code TreeContextMenuItems} contained within a {@code TreeMenuItemSet}
 * object will appear for nodes of type specified in this object's
 * constructor.</p>
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class TreeMenuItemSet {

    private SQLessTreeNode.NodeType[] belongsTo;
    private List<TreeContextMenuItem> menuItems;

    /**
     * Inicializa un nuevo set de menu items que aparecerán para nodos del tipo
     * dado.
     *
     * @param belongsTo Los nodos del árbol para el cual aparecerán estos menu
     * items.
     */
    public TreeMenuItemSet(SQLessTreeNode.NodeType... belongsTo) {
        this.belongsTo = belongsTo;
        menuItems = new ArrayList<>();
    }

    public void add(TreeContextMenuItem menuItem) {
        menuItems.add(menuItem);
    }

    public int itemCount() {
        return menuItems.size();
    }

    public TreeContextMenuItem get(int n) {
        return menuItems.get(n);
    }

    /**
     * The types of node for which the items in this {@code TreeMenuItemSet}
     * should appear.
     *
     * @return a {@code NodeType} array with the nodes associated with this
     * {@code TreeMenuItemSet}.
     */
    public SQLessTreeNode.NodeType[] belongsTo() {
        return belongsTo;
    }

}
