package com.sqless.utils;

import com.sqless.sql.objects.SQLColumn;
import com.sqless.sql.objects.SQLTable;
import com.sqless.ui.FrontPanel;
import com.sqless.ui.tree.SQLessTreeNode;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import org.jdesktop.swingx.JXTable;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import java.net.URL;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Helper class that contains methods useful for UI.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class UIUtils {

    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    public static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
    public static final Font SEGOE_UI_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SEGOE_UI_FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Color DARK_GREEN = new Color(62, 137, 39);
    public static final Color COOL_GREEN = new Color(0x84DB66);
    public static final String ICONS_PATH = "/icons/";

    public enum CellEdit {
        STOP, CANCEL
    }

    /**
     * Makes a split pane invisible. Only contained components are shown.
     *
     * @param splitPane
     */
    public static void flattenPane(JSplitPane splitPane) {
        splitPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        BasicSplitPaneUI flatDividerSplitPaneUI = new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void setBorder(Border b) {
                    }
                };
            }
        };
        splitPane.setUI(flatDividerSplitPaneUI);
        splitPane.setBorder(null);
    }

    /**
     * Selecciona un nodo en el {@code JTree} dado programáticamente.
     *
     * @param tree el {@code JTree} en el que se hará la selección.
     * @param node el nodo a seleccionar.
     */
    public static void selectTreeNode(JTree tree, TreeNode node) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        tree.setSelectionPath(new TreePath(model.getPathToRoot(node)));
    }

    /**
     * Performs several tasks on a given table's columns to enhance its visual
     * quality, including:
     * <ul>
     * <li>Resizes the columns that hold datetime or enum/set values to fit the
     * button that expands the popup menu.</li>
     * <li>Adds an extra 15px to the preferred width of all the columns that use
     * a {@code DatePickerCellEditor} or hold an enum or set.</li>
     * </ul>
     * Note: this method assumes that the UI table has already been packed
     * beforehand by calling {@link JXTable#packAll()} and that the UI table has
     * the same amount of columns as the {@code SQLTable}.
     *
     * @param table the UI table.
     * @param reference a SQL table that will serve as reference for the
     * columns.
     */
    public static void enhanceTableColumns(JXTable table, SQLTable reference) {
        List<SQLColumn> sqlColumns = reference.getColumns();
        for (int i = 0; i < sqlColumns.size(); i++) {
            SQLColumn sqlColumn = sqlColumns.get(i);
            TableColumn uiTableColumn = table.getColumn(i);
            if (sqlColumn.isTimeBased() || sqlColumn.getDataType().equals("enum") || sqlColumn.getDataType().equals("set")) {
                table.packColumn(i, uiTableColumn.getPreferredWidth(), uiTableColumn.getPreferredWidth() + 15);
            }
        }
    }

    /**
     * Para ({@link CellEdit#STOP}) o cancela ({@link CellEdit#CANCEL}) la
     * edición de una celda.
     *
     * @param table la tabla que se está editando.
     * @param action Qué acción se debe tomar. Si se cancela, a diferencia de
     * parar, los cambios de edición se descartan.
     * @return {@code true} si la acción de edición pudo pararse (STOP)
     * exitosamente o si la acción es CANCEL. Si ninguna celda se está editando
     * al momento de llamar a este método, se retornará {@code true}.
     */
    public static boolean interruptCellEdit(JTable table, CellEdit action) {
        if (table.getCellEditor() != null) {
            if (action == CellEdit.STOP) {
                if (!table.getCellEditor().stopCellEditing()) {
                    return false;
                }
            } else if (action == CellEdit.CANCEL) {
                table.getCellEditor().cancelCellEditing();
                return true;
            }
        }
        return true;
    }

    public static JComboBox<String> makeComboBoxForEnumColumn(SQLColumn column) {
        return createEditableComboBox(SQLUtils.getEnumLikeValuesAsArray(column.getEnumLikeValues(false)));
    }

    public static void scrollToTop(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(0);
        });
    }

    public static void scrollToBottom(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    /**
     * Crea un nuevo {@code MouseListener} para una tabla el cual activará el
     * {@code JPopupMenu} dado. Este método ya incluye toda la lógica
     * relacionada a cambios de selecciones, etc.
     *
     * @param popMenu El {@link JPopupMenu} que aparecerá.
     * @param table La tabla sobre la cual aparecerá el {@code JPopupMenu}.
     * @return un {@code MouseListener} listo para ser asignado a la tabla dada.
     */
    public static MouseAdapter mouseListenerWithPopUpMenuForJTable(JPopupMenu popMenu, JTable table) {
        return new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    UIUtils.interruptCellEdit(table, UIUtils.CellEdit.STOP);
                    int r = table.rowAtPoint(e.getPoint());
                    int c = table.columnAtPoint(e.getPoint());
                    if (r >= 0 && r < table.getRowCount()) {
                        int[] selectedRows = table.getSelectedRows();
                        int[] selectedCols = table.getSelectedColumns();
                        if (selectedRows.length == 0 || !MiscUtils.arrayContains(selectedRows, r) || !MiscUtils.arrayContains(selectedCols, c)) {
                            table.setColumnSelectionInterval(c, c);
                            table.setRowSelectionInterval(r, r);
                        }
                    } else {
                        table.clearSelection();
                    }

                    int rowindex = table.getSelectedRow();
                    if (rowindex < 0) {
                        return;
                    }
                    popMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
    }

    public static JComboBox createEditableComboBox(String[] model) {
        JComboBox editableCombobox = new JComboBox(model) {
            @Override
            public void updateUI() {
                setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                    @Override
                    protected JButton createArrowButton() {
                        JButton b = new JButton(icon("ui_general", "COMBOBOX_ARROW"));
                        b.setContentAreaFilled(true);
                        b.setFocusPainted(false);
                        b.setBorder(BorderFactory.createEmptyBorder());
                        return b;
                    }
                });
            }
        };
        ((JTextField) editableCombobox.getEditor().getEditorComponent()).setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
        editableCombobox.setEditable(true);
        editableCombobox.setBorder(BorderFactory.createEmptyBorder());
        return editableCombobox;
    }

    public static JComboBox createEditableComboBox() {
        return createEditableComboBox(new String[0]);
    }

    /**
     * Changes the properties of the font contained within a {@code JLabel} to
     * {@code Font.BOLD}. If the font is already bold, this method does nothing.
     *
     * @param label The {@code JLabel} to bold.
     */
    public static void boldLabel(JLabel label) {
        if (label.getFont().getStyle() == Font.BOLD) {
            return;
        }
        label.setFont(SEGOE_UI_FONT_BOLD);
    }

    /**
     * Changes the properties of the font contained within a {@code JLabel} to
     * {@code Font.PLAIN}.
     *
     * @param label The {@code JLabel} to normalise.
     * @see #boldLabel
     */
    public static void normaliseLabel(JLabel label) {
        label.setFont(SEGOE_UI_FONT);
    }

    public static void changeLabelColour(JLabel label, Color color) {
        label.setForeground(color);
    }

    /**
     * Boosts the mousewheel scrolling on a given {@code JScrollPane} by an
     * extra 15px.
     *
     * @param pane The {@code JScrollPane} to improve.
     */
    public static void improveScrollPaneScroll(JScrollPane pane) {
        pane.addMouseWheelListener((MouseWheelEvent e) -> {
            javax.swing.JScrollBar vertical = pane.getVerticalScrollBar();
            switch (e.getWheelRotation()) {
                case -1: //arriba
                    vertical.setValue(vertical.getValue() - 15);
                    break;
                case 1: //abajo
                    vertical.setValue(vertical.getValue() + 15);
                    break;
            }
        });
    }

    public static String getStringFromPasswordField(JPasswordField passwordField) {
        return new String(passwordField.getPassword());
    }

    /**
     * Sorts a given {@code JXTable} using {@code columnIndex} as a guide.
     *
     * @param table the {@code JXTable} to sort.
     * @param columnIndex the index to use as guide.
     */
    public static void sortTable(JTable table, int columnIndex) {
        DefaultRowSorter sorter = (DefaultRowSorter) table.getRowSorter();
        List list = new ArrayList();
        list.add(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING));
        sorter.setSortKeys(list);
        sorter.sort();
    }

    /**
     * Shows a confirm dialog.
     *
     * @param text The message to display.
     * @param title The title of the dialog.
     * @param parent The parent {@code Component} of this dialog.
     * @return an {@code int} depending on the option that was chosen.
     *
     * <ul>
     * <li>Yes: 0</li>
     * <li>No: 1</li>
     * </ul>
     */
    public static int showConfirmationMessage(String title, String text, java.awt.Component parent) {
        return JOptionPane.showConfirmDialog(parent, TextUtils.splitIntoLines(text),
                title, JOptionPane.YES_NO_OPTION);
    }

    /**
     * Displays a simple option dialog with {@code title} as the title and
     * {@code message} as the body of the dialog. The dialog will have 'Yes',
     * and 'No' options by default, but setting {@code dontAskAgain} to
     * {@code true} will display 'No, don't ask me again'.
     *
     * @param title the title of this dialog.
     * @param message the message this dialog will display on its body.
     * @param messageType The type of message to display taken from
     * {@code JOptionPane} static final fields.
     * @param dontAskAgain this will include a "No, don't ask me again option".
     * @param parent the parent {@code Component} of this dialog.
     * @return an {@code int} depending on the chosen option.
     * <ul>
     * <li><b>Yes</b> option: 0</li>
     * <li><b>No</b> option: 1</li>
     * <li><b>No, don't ask me again</b> option: 2</li>
     * </ul>
     * @see UIUtils#showOptionDialog(java.lang.String, java.lang.String,
     * java.awt.Component, java.lang.String[])
     */
    public static int showYesNoOptionDialog(String title, String message, int messageType,
            boolean dontAskAgain, java.awt.Component parent) {
        Object[] options = !dontAskAgain
                ? new Object[]{"Yes", "No"}
                : new Object[]{"Yes", "No", "No, don't ask me again"};
        return JOptionPane.showOptionDialog(parent, TextUtils.splitIntoLines(message), title,
                JOptionPane.DEFAULT_OPTION, messageType, null, options, options[0]);
    }

    /**
     * Displays a simple option dialog with {@code title} as the title and
     * {@code message} as the body of the dialog. The dialog will have
     * {@code options} {@code String} array as its displayed options.
     *
     * @param title the title of this dialog.
     * @param message the message of this dialog.
     * @param parent the parent {@code Component} of this dialog.
     * @param options a string or many strings that will be displayed as options
     * to choose from.
     * @return an {@code int} depending on the option clicked. 0 starting from
     * the left.
     * @see UIUtils#showOptionDialog(java.lang.String, java.lang.String,
     * java.awt.Component)
     */
    public static int showOptionDialog(String title, String message,
            java.awt.Component parent, String... options) {
        return JOptionPane.showOptionDialog(parent, TextUtils.splitIntoLines(message), title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    /**
     * Displays an error message using the {@link JOptionPane#ERROR_MESSAGE}
     * variant of dialog.
     * <p>
     * <b>Note:</b> The error message will be split into 15-word long lines.</p>
     *
     * @param title The title of the dialog.
     * @param message The error message.
     * @param parent The owner of this dialog.
     */
    public static void showErrorMessage(String title, String message, java.awt.Component parent) {
        JOptionPane.showMessageDialog(parent, TextUtils.splitIntoLines(message),
                title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showMessage(String cuerpo, java.awt.Component parent) {
        JOptionPane.showMessageDialog(parent, TextUtils.splitIntoLines(cuerpo));
    }

    public static void showMessage(String title, String body, java.awt.Component parent) {
        JOptionPane.showMessageDialog(parent, body, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(String title, String body, java.awt.Component parent) {
        JOptionPane.showMessageDialog(parent, TextUtils.splitIntoLines(body), title, JOptionPane.WARNING_MESSAGE);
    }

    public static String showInputDialog(String title, String body, java.awt.Component parent) {
        return JOptionPane.showInputDialog(parent, TextUtils.splitIntoLines(body), title,
                JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Checks if a {@code TreeNodeMorgan} contains a dummy child used as a
     * placeholder and removes it.
     *
     * @param node a {@code DefaultMutableTreeNode}
     * @return {@code true} if the first child of the node is
     * {@code instanceof DummyNode}
     */
    public static boolean nodeHasDummy(SQLessTreeNode node) {
        if (node.getChildCount() > 0 && ((SQLessTreeNode) node.getChildAt(0))
                .isOfType(SQLessTreeNode.NodeType.DUMMY)) {
            node.remove(0);
            return true;
        }
        return false;
    }

    public static SQLessTreeNode dummyNode() {
        return new SQLessTreeNode();
    }

    public static JButton newToolbarBtn(ActionListener action, String title, String tooltip, ImageIcon icon) {
        JButton btn = new JButton(title);
        btn.setFont(SEGOE_UI_FONT);
        btn.addActionListener(action);
        btn.setFocusable(false);
        btn.setMargin(new Insets(2, 8, 2, 8));
        btn.setIcon(icon);
        btn.setToolTipText(tooltip == null || tooltip.isEmpty() ? null : tooltip);
        return btn;
    }

    public static JButton newToolbarBtn(ActionListener action, String tooltip, ImageIcon icon) {
        return newToolbarBtn(action, "", tooltip, icon);
    }

    public static ImageIcon icon(FrontPanel frontPanel, String fileName) {
        return icon(frontPanel.getIconsFolder(), fileName);
    }

    public static ImageIcon icon(String folder, String fileName) {
        return icon(folder, fileName, "png");
    }

    public static ImageIcon icon(String folder, String fileName, String extension) {
        URL fileUrl = UIUtils.class.getResource(ICONS_PATH + folder + "/" + fileName + (fileName.endsWith("_ICON") ? "" : "_ICON") + "." + extension.toLowerCase());

        if (fileUrl == null) { //let's try uppercase extension now
            fileUrl = UIUtils.class.getResource(ICONS_PATH + folder + "/" + fileName + (fileName.endsWith("_ICON") ? "" : "_ICON") + "." + extension.toUpperCase());
            if (fileUrl == null) { //uppercase check failed, let's return a generic MISSING icon
                return new ImageIcon(UIUtils.class.getResource(ICONS_PATH + "ui_general/MISSING_ICON.png"));
            }
        }

        return new ImageIcon(fileUrl);
    }

    public static JSeparator newSeparator() {
        return new JToolBar.Separator();
    }
}
