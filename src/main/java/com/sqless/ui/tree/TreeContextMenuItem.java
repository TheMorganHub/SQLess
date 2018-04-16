package com.sqless.ui.tree;

import com.sqless.ui.listeners.TreeContextMenuItemListener;
import javax.swing.JMenuItem;

public class TreeContextMenuItem extends JMenuItem {

    public enum ContextItemFunctionality {
        SELECT, EXECUTE, DROP, CREATE, RENAME, MODIFY, EDIT, OTHER;
    }

    private ContextItemFunctionality functionality;
    private String actionKey;

    public TreeContextMenuItem(String text, ContextItemFunctionality functionality) {
        super(text);
        addMouseListener(TreeContextMenuHandler.getMenuItemListenerInstance());
        this.functionality = functionality;
    }

    /**
     * Crea un nuevo menu item que ejecutará la acción identificada por
     * {@code actionKey} dada. El mapa que contiene todas las acciones con las
     * keys se encuentra en {@link TreeContextMenuItemListener}.
     *
     * @param text El texto que tendrá el menu item.
     * @param actionKey La key que identificará a la acción a ejecutar.
     */
    public TreeContextMenuItem(String text, String actionKey) {
        this(text, ContextItemFunctionality.OTHER);
        this.actionKey = actionKey;
    }

    /**
     * El identificador de la acción que ejecutará el nodo asociado a este menu
     * item. El mapeo de acciones con sus keys se encuentra en
     * {@link TreeContextMenuItemListener}.
     *
     * @return
     */
    public String getActionKey() {
        return actionKey;
    }

    public ContextItemFunctionality getItemFunctionality() {
        return functionality;
    }
}
