package com.sqless.main;

import com.sqless.ui.UIClient;
import com.sqless.utils.UIUtils;
import javax.swing.UIManager;

public class Start {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            java.awt.EventQueue.invokeLater(() -> UIClient.getInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            UIUtils.showErrorMessage("Error al cargar módulos principales", "El sistema no es compatible con SQLess.", null);
        }
    }
}
