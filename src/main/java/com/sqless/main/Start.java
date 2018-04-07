package com.sqless.main;

import com.sqless.ui.UIClient;
import com.sqless.utils.UIUtils;
import javax.swing.UIManager;

public class Start {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            UIUtils.showErrorMessage("Error", "Hubo un error al cargar el LookAndFeel.", null);
            System.exit(0);
        }
        java.awt.EventQueue.invokeLater(() -> UIClient.getInstance());
    }
}
