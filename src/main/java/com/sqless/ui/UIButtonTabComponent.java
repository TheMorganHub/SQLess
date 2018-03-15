package com.sqless.ui;

import com.sqless.utils.UIUtils;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Component to be used as tabComponent; Contains a JLabel to show the title and
 * a JButton to close the tab it belongs to
 */
public class UIButtonTabComponent extends JPanel {

    private final JTabbedPane pane;
    private JLabel label;
    private final ImageIcon CLOSE_TAB_ICON
            = new ImageIcon(getClass().getResource("/icons/ui_client/CLOSE_TAB_ICON.png"));
    private final ImageIcon CLOSE_TAB_ROLLOVER_ICON
            = new ImageIcon(getClass().getResource("/icons/ui_client/CLOSE_TAB_ROLLOVER_ICON.png"));
    private final ImageIcon CLOSE_TAB_ICON_PRESSED
            = new ImageIcon(getClass().getResource("/icons/ui_client/CLOSE_TAB_PRESSED_ICON.png"));

    public UIButtonTabComponent(final JTabbedPane pane) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.pane = pane;
        setOpaque(false);

        label = new JLabel() {
            @Override
            public String getText() {
                int i = pane.indexOfTabComponent(UIButtonTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };

        add(label);
        label.setFont(UIUtils.SEGOE_UI_FONT);
        //add more space between the label and the button

        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    /**
     * Constructor utilizado para cuando una cierta pestaña es modificada
     * mediante Guardar como, ya que es posible que el usuario necesite guardar
     * el archivo con otro nombre, y este constructor permite modificar el
     * nombre en la pestaña ya existente.
     *
     * @param pane
     * @param lblTitle
     */
    public UIButtonTabComponent(JTabbedPane pane, String lblTitle) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.pane = pane;
        setOpaque(false);

        //make JLabel read titles from JTabbedPane        
        label = new JLabel(lblTitle);

        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        label.revalidate();
        label.repaint();

        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    public void setTitle(String title) {
        pane.setTitleAt(pane.indexOfTabComponent(this), title);
        label.revalidate();
        label.repaint();
    }

    public JLabel getLabel() {
        return label;
    }

    private class TabButton extends JButton implements ActionListener {

        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);

            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(UIButtonTabComponent.this);
            if (i != -1) {
                ((FrontPanel) pane.getComponentAt(i)).tabClosing(i);
            }
        }

        //we don't want to update UI for this button
        @Override
        public void updateUI() {
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setIcon(CLOSE_TAB_ICON);
            if (getModel().isRollover()) {
                setIcon(CLOSE_TAB_ROLLOVER_ICON);
            }
            if (getModel().isPressed()) {
                setIcon(CLOSE_TAB_ICON_PRESSED);
            }
        }
    }
}
