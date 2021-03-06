package com.sqless.ui.tree;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import com.sqless.ui.listeners.TreeContextMenuItemListener;
import static com.sqless.ui.tree.SQLessTreeNode.NodeType.*;
import static com.sqless.ui.tree.TreeContextMenuItem.*;

/**
 * Clase que maneja todo lo relacionado a menús de contexto y comportamiento de
 * los mismos. Es decir, el texto que tendrán al aparecer, el ser mostrados,
 * etc. Para funcionalidad específica de un item de menú, ver
 * <code>TreeContextMenuItemListener</code>
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class TreeContextMenuHandler {

    private JTree treeDiagram;
    private List<TreeMenuItemSet> itemSets;
    private JPopupMenu menu;
    public static TreeContextMenuItemListener itemListener = new TreeContextMenuItemListener();

    public TreeContextMenuHandler(JTree treeDiagram) {
        this.treeDiagram = treeDiagram;
        loadJTreeContextMenus();
    }

    public void loadJTreeContextMenus() {
        itemSets = new ArrayList<>();

        TreeMenuItemSet tableCategoriesContext = new TreeMenuItemSet(CAT_TABLES);
        tableCategoriesContext.add(new TreeContextMenuItem("Nueva tabla", ContextItemFunctionality.CREATE));

        TreeMenuItemSet selectableContext = new TreeMenuItemSet(TABLE, TABLE_COLUMN, VIEW, VIEW_COLUMN);
        selectableContext.add(new TreeContextMenuItem("SELECT (LIMIT 1000)", ContextItemFunctionality.SELECT));
        selectableContext.add(new TreeContextMenuItem("SELECT (Maple)", "SELECT_AS_MAPLE"));

        TreeMenuItemSet tableObjectContext = new TreeMenuItemSet(TABLE);
        tableObjectContext.add(new TreeContextMenuItem("Modificar tabla", ContextItemFunctionality.MODIFY));
        tableObjectContext.add(new TreeContextMenuItem("Editar filas", ContextItemFunctionality.EDIT));

        TreeMenuItemSet columnTableObjectContext = new TreeMenuItemSet(TABLE, TABLE_COLUMN, VIEW);
        columnTableObjectContext.add(new TreeContextMenuItem("Renombrar", ContextItemFunctionality.RENAME));

        TreeMenuItemSet executableContext = new TreeMenuItemSet(FUNCTION, PROCEDURE);
        executableContext.add(new TreeContextMenuItem("Ejecutar", ContextItemFunctionality.EXECUTE));
        
        TreeMenuItemSet databaseContext = new TreeMenuItemSet(DATABASE);
        databaseContext.add(new TreeContextMenuItem("Exportar estructura y datos...", "EXPORT_DB"));
        databaseContext.add(new TreeContextMenuItem("Exportar sólo estructura...", "EXPORT_DB_NO_DATA"));
        
        TreeMenuItemSet databaseContext2 = new TreeMenuItemSet(DATABASE);        
        databaseContext2.add(new TreeContextMenuItem("Ejecutar desde archivo SQL...", "EXECUTE_FROM_SCRIPT"));

        TreeMenuItemSet genericMenu = new TreeMenuItemSet(DATABASE, TABLE_COLUMN, TABLE, VIEW, VIEW_COLUMN, INDEX,
                FUNCTION, PROCEDURE, TRIGGER);
        genericMenu.add(new TreeContextMenuItem("Eliminar", ContextItemFunctionality.DROP));

//        el orden de agregado afecta la posición de los items. Los que se agregan primero van a aparecer primero.
        itemSets.add(tableCategoriesContext);
        itemSets.add(selectableContext);        
        itemSets.add(tableObjectContext);
        itemSets.add(executableContext);
        itemSets.add(columnTableObjectContext);
        itemSets.add(databaseContext);
        itemSets.add(databaseContext2);
        itemSets.add(genericMenu);
    }

    public JPopupMenu getMenuForType(SQLessTreeNode.NodeType type) {
        menu = new JPopupMenu();
        int typesFound = 0;
        for (TreeMenuItemSet itemSet : itemSets) {
            SQLessTreeNode.NodeType[] nodeTypes = itemSet.belongsTo();
            for (SQLessTreeNode.NodeType nodeType : nodeTypes) {
                if (type.equals(nodeType)) {
                    typesFound++;

                    if (typesFound > 1) {
                        menu.addSeparator();
                    }
                    for (int i = 0; i < itemSet.itemCount(); i++) {
                        menu.add(itemSet.get(i));
                    }

                }
            }
        }
        return menu;
    }

    public static TreeContextMenuItemListener getMenuItemListenerInstance() {
        return itemListener;
    }

    /**
     * Shows a menu belonging to a given {@code NodeType}. If the
     * {@code NodeType} doesn't have a menu associated with it, this method will
     * do nothing.
     *
     * @param belongsTo A {@code NodeType}.
     * @param location The location as a {@code Point} in which this menu will
     * appear.
     */
    public void showMenu(SQLessTreeNode.NodeType belongsTo, Point location) {
        JPopupMenu menuToShow = getMenuForType(belongsTo);
        if (menuToShow != null) {
            menuToShow.show(treeDiagram, location.x, location.y);
        }
    }
}
