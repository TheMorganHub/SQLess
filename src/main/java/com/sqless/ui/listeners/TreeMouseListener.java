package com.sqless.ui.listeners;

import com.sqless.ui.tree.TreeContextMenuHandler;
import com.sqless.ui.tree.SQLessTreeNode;
import com.sqless.utils.SQLUtils;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 * The {@code MouseAdapter} that will listen to Mouse events that occur within
 * the {@code JTree}
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class TreeMouseListener extends MouseAdapter {

    private TreeContextMenuHandler jTreeContextMenuHandler;
    private JTree treeDiagram;

    public TreeMouseListener(JTree treeDiagram, TreeContextMenuHandler contextMenuHandler) {
        this.jTreeContextMenuHandler = contextMenuHandler;
        this.treeDiagram = treeDiagram;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            SQLessTreeNode nodo = getNodeFromLocation(e.getPoint());
            if (locationIsValid(e)) {
                addSelectionPath(e.getPoint());
                jTreeContextMenuHandler.showMenu(nodo.getType(), e.getPoint());
            }
        }
    }

    public boolean locationIsValid(MouseEvent e) {
        return treeDiagram.getPathForLocation(e.getX(), e.getY()) != null;
    }

    private void addSelectionPath(Point location) {
        treeDiagram.getSelectionModel().clearSelection();
        treeDiagram.getSelectionModel()
                .addSelectionPath(treeDiagram.getClosestPathForLocation(location.x, location.y));
    }

    private SQLessTreeNode getNodeFromLocation(Point location) {
        return (SQLessTreeNode) treeDiagram.getClosestPathForLocation(location.x, location.y)
                .getLastPathComponent();
    }
}
