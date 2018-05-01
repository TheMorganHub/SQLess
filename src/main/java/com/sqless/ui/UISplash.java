package com.sqless.ui;

import com.sqless.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UISplash extends JFrame {

    private JLabel lblContectandoStatus;

    public UISplash() {
        try {
            setIconImage(new ImageIcon(getClass().getResource("/images/ui_general/SQLess_logo_mini.png")).getImage());
            setUndecorated(true);            
            setLayout(new GridBagLayout());
            setDefaultCloseOperation(EXIT_ON_CLOSE);           

            ImagePane pane = new ImagePane(ImageIO.read(getClass().getResource("/images/ui_splash/SQLess_SPLASH.png")));
            pane.setLayout(new BorderLayout());
            add(pane);

            JPanel pnlLbl = new JPanel();
            pnlLbl.setOpaque(false);
            pnlLbl.setLayout(new FlowLayout(FlowLayout.RIGHT));
            lblContectandoStatus = new JLabel();
            lblContectandoStatus.setFont(UIUtils.SEGOE_UI_FONT_BOLD);
            lblContectandoStatus.setForeground(Color.GRAY);
            lblContectandoStatus.setHorizontalAlignment(JLabel.CENTER);
            pnlLbl.add(lblContectandoStatus);
            pane.add(pnlLbl, BorderLayout.SOUTH);

            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        } catch (IOException ex) {
        }
    }

    public void updateStatus(String message) {
        lblContectandoStatus.setText(message);
    }

    static class ImagePane extends JPanel {

        private Image background;

        public ImagePane(Image image) {
            background = image;
        }

        @Override
        public Dimension getPreferredSize() {
            return background == null ? new Dimension(0, 0) : new Dimension(background.getWidth(this), background.getHeight(this));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (background != null) {
                Insets insets = getInsets();

                int width = getWidth() - 1 - (insets.left + insets.right);
                int height = getHeight() - 1 - (insets.top + insets.bottom);

                int x = (width - background.getWidth(this)) / 2;
                int y = (height - background.getHeight(this)) / 2;

                g.drawImage(background, x, y, this);
            }

        }

    }
}
