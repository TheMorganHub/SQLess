package com.sqless.ui.tree;

import javax.swing.JMenuItem;

public class TreeContextMenuItem extends JMenuItem {

    public enum ContextItemFunctionality {
        SELECT, EXECUTE, DROP, CREATE, RENAME, MODIFY, EDIT;
    }

    private ContextItemFunctionality functionality;
    
    public TreeContextMenuItem(String text, ContextItemFunctionality functionality) {
        super(text);
        addMouseListener(TreeContextMenuHandler.getMenuItemListenerInstance());
        this.functionality = functionality;
    }

    public ContextItemFunctionality getItemFunctionality() {
        return functionality;
    }
}
