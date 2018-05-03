package com.sqless.ui;

import com.sqless.utils.UIUtils;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 * Un FrontPanel es un JPanel destinado a utilizarse dentro del
 * {@code JTabbedPane} principal en {@code UIClient}. <br> <br>
 * El FrontPanel da soporte para componentes dinámicos que irán ubicados en una
 * toolbar o un menu. Para hacer uso de los componentes de toolbar y menues
 * dinámicos, será necesario que cada clase que herede de ésta implemente los
 * métodos {@link FrontPanel#getToolbarComponents()} y
 * {@link FrontPanel#getMenuItems()}. <br> <br>
 * El FrontPanel, al ser creado, toma una referencia al {@code JTabbedPane} que
 * lo contendrá. Esta referencia permite hacer operaciones limitadas con el
 * JTabbedPane, como por ejemplo permitirle al FrontPanel saber en qué posición
 * está ubicado.
 *
 *
 * @author David Orquin
 */
public abstract class FrontPanel extends javax.swing.JPanel {

    protected JTabbedPane parentPane;
    protected java.awt.Component[] toolbarComponents;
    protected javax.swing.JMenuItem[] menuItems;
    private Integrity integrity = Integrity.HEALTHY;

    protected enum Integrity {
        HEALTHY, CORRUPT;
    }

    public FrontPanel(JTabbedPane parentPane) {
        this.parentPane = parentPane;
        parentPane.setFocusable(false);
    }

    /**
     * The buttons or components that will be displayed in the toolbar in
     * {@code UIClient} whenever this FrontPanel gets the main focus.
     *
     * @return an array of components.
     */
    public abstract java.awt.Component[] getToolbarComponents();

    /**
     * The menu items that will be added to the main menu bar whenever this
     * FrontPanel gets the main focus.
     *
     * @return an array of {@code JMenuItem} to be added under the "File"
     * submenu.
     */
    public javax.swing.JMenuItem[] getMenuItems() {
        return menuItems;
    }

    /**
     * El método que se va a ejecutar al momento en que este FrontPanel es
     * agregado al TabPane principal. La ejecución de este método es después del
     * constructor.
     */
    public void onCreate() {
    }

    /**
     * Se chequea si la integridad de este FrontPanel no está corrupta.
     * Cualquier FrontPanel durante su funcionamiento puede setear esta
     * propiedad como {@link Integrity#CORRUPT} o {@link Integrity#HEALTHY}. Por
     * ejemplo, el UIClient, utiliza esta propiedad al momento de insertar el
     * FrontPanel en el tab correspondiente. Si la integridad es Corrupta en ese
     * momento, el FrontPanel no será agregado. Por ejemplo, si algo en la
     * inicialización o constuctor de este FrontPanel hizo que {@code integrity}
     * sea CORRUPT, UIClient no agregará este FrontPanel.
     *
     * @return {@code true} si la integridad es 'Healthy', {@code false} si es
     * 'Corrupt'.
     */
    public boolean checkIntegrity() {
        return !integrity.equals(Integrity.CORRUPT);
    }

    public void setIntegrity(Integrity integrity) {
        this.integrity = integrity;
    }

    public abstract String getTabTitle();

    public abstract String getIconsFolder();

    /**
     * Changes this FrontPanel's tab title.
     *
     * @param title the String that will appear as the title of this
     * FrontPanel's tab.
     */
    public void setTabTitle(String title) {
        ((UIButtonTabComponent) parentPane.getTabComponentAt(parentPane.getSelectedIndex())).setTitle(title);
        parentPane.revalidate();
        parentPane.repaint();
    }

    public void setTabToolTipText(String text) {
        parentPane.setToolTipTextAt(getTabIndex(), text);
    }

    /**
     * Este método es llamado automáticamente al momento en que este FrontPanel
     * es cerrado. <br> <br>
     * <b>Importante: </b>Para el correcto funcionamiento de las pestañas en la
     * interfaz principal, es indispensable que todo método que overridee este
     * método llame a {@code super.tabClosing(int tabNum)}.
     *
     * @param tabNum El número de pestaña que le corresponde a este FrontPanel.
     */
    public void tabClosing(int tabNum) {
        parentPane.remove(tabNum);
    }

    /**
     * Returns the {@code JTabbedPane} that houses this FrontPanel.
     *
     * @return
     */
    public JTabbedPane getParentPane() {
        return parentPane;
    }

    /**
     * Returns the index of this FrontPanel within its parent
     * {@code JTabbedPane}
     *
     * @return an {@code int} denoting the index (starting at 0) of this
     * FrontPanel.
     */
    public int getTabIndex() {
        for (int i = 0; i < parentPane.getTabCount(); i++) {
            if (parentPane.getComponentAt(i) == this) {
                return i;
            }
        }
        return -1;
    }

    public ImageIcon getTabIcon() {
        return null;
    }

    /**
     * Returns the {@code JLabel} in charge of displaying this FrontPanel's
     * title.
     *
     * @return the {@code JLabel} used to display the title.
     */
    public JLabel getTabLabel() {
        return ((UIButtonTabComponent) parentPane.getTabComponentAt(parentPane.getSelectedIndex())).getLabel();
    }

    public void boldTitleLabel() {
        UIUtils.boldLabel(getTabLabel());
    }

    public void unboldTitleLabel() {
        UIUtils.normaliseLabel(getTabLabel());
    }
}
