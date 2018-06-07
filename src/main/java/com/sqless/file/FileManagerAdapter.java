package com.sqless.file;

import com.sqless.settings.UserPreferencesLoader;
import com.sqless.ui.UIClient;
import com.sqless.utils.Callback;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * El FileManagerAdapter le permite a los dos FileManagers de SQLess (SQL y
 * Maple) trabajar en conjunto al momento de abrir archivos. Es decir, gracias a
 * esta clase, podemos tener un único botón de "Abrir" con posibilidad de abrir
 * los dos tipos de archivos (.mpl y .sql), y dependiendo de qué tipo de archivo
 * elijamos, el FileManagerAdapter llamará al FileManager que corresponda. Por
 * ejemplo, si elegimos un archivo con extensión .mpl, se llamará a
 * {@link MapleFileManager}.
 * <p>
 * De la misma manera, FileManagerAdapter nos permite procesar la acción de Drag
 * and Drop dinámicamente. Esto quiere decir que si hacemos drag and drop de un
 * archivo Maple en un archivo SQL, el Adapter nos abrirá una nueva pestaña
 * Maple y vice versa.</p>
 *
 * @author Morgan
 */
public class FileManagerAdapter {

    public static void openFile() {
        UIClient client = UIClient.getInstance();
        JTabbedPane tabPane = client.getTabPaneContent();
        UserPreferencesLoader userPrefLoader = UserPreferencesLoader.getInstance();
        JFileChooser chooser = new JFileChooser();
        FileFilter sql = new FileNameExtensionFilter("SQL file (.sql)", "sql");
        FileFilter mpl = new FileNameExtensionFilter("Maple file (.mpl)", "mpl");
        chooser.addChoosableFileFilter(sql);
        chooser.addChoosableFileFilter(mpl);
        chooser.setDialogTitle("Abrir...");
        chooser.setAcceptAllFileFilterUsed(false);

        String defSaveDir = userPrefLoader.getProperty("Default.SaveDir");
        chooser.setCurrentDirectory(FileManager.dirOrFileExists(defSaveDir)
                ? new File(defSaveDir)
                : new File(userPrefLoader.getDefaultFor("Default.SaveDir")));

        int returnVal = chooser.showOpenDialog(client);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getPath().endsWith(".sql")) {
                FileManager.getInstance().doOpenFile(tabPane, chooser.getSelectedFile());
            } else if (chooser.getSelectedFile().getPath().endsWith(".mpl")) {
                MapleFileManager.getInstance().doOpenFile(tabPane, chooser.getSelectedFile());
            }
        }
    }

    public static void dragNDropFile(File file) {
        if (file.getPath().endsWith(".sql")) {
            FileManager.getInstance().processDragNDrop(file);
        } else if (file.getPath().endsWith(".mpl")) {
            MapleFileManager.getInstance().processDragNDrop(file);
        }
    }
}
