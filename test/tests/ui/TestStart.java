package tests.ui;

import com.sqless.ui.UIClient;
import com.sqless.utils.UIUtils;
import com.sun.java.swing.plaf.windows.DesktopProperty;
import java.awt.Color;
import java.util.Map;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

/**
 * Un test con un estilo "dark theme" para Windows.
 *
 * @author Morgan
 */
public class TestStart {

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            UIUtils.showErrorMessage("Error", "Hubo un error al cargar el LookAndFeel.", null);
            System.exit(0);
        }
        UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
        for (Map.Entry<Object, Object> entry : uiDefaults.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof DesktopProperty) {
                DesktopProperty desktopProperty = (DesktopProperty) value;
                Object realValue = desktopProperty.createValue(null);
                if (realValue instanceof ColorUIResource) {
                    Color realColor = (Color) realValue;
                    if (realColor.equals(new Color(240, 240, 240))) {
                        uiDefaults.put(key, new Color(0x202020));
                    } else if (realColor.equals(new Color(0xFFFFFF))) {
                        uiDefaults.put(key, new Color(0x2D2D2D));
                    }

                }

            }
        }
        uiDefaults.put("Label.foreground", new Color(0x707070));
        uiDefaults.put("RadioButton.foreground", new Color(0x707070));
        uiDefaults.put("Tree.textForeground", new Color(0x707070));

        java.awt.EventQueue.invokeLater(() -> {
            UIClient client = UIClient.getInstance();
        });
    }
}
