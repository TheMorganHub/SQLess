package com.sqless.main;

import com.sqless.ui.UIClient;
import com.sqless.utils.UIUtils;

public class Start {

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            UIUtils.showErrorMessage("Error", "Hubo un error al cargar el LookAndFeel.", null);
            System.exit(0);
        }
        java.awt.EventQueue.invokeLater(() -> {
            UIClient client = UIClient.getInstance();
        });
    }
}
