package com.sqless.file;

import com.sqless.utils.UIUtils;
import com.sqless.ui.UIClient;
import com.sqless.settings.UserPreferencesLoader;
import com.sqless.ui.UIQueryPanel;
import com.sqless.utils.Callback;
import com.sqless.utils.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The class that handles interactions between the {@code UIClient} and the
 * Files it opens, closes, and saves.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class FileManager {

    /**
     * The {@code List} that contains the paths of all opened files. The order
     * of objects in this {@code List} must always match the tabs that are open
     * in the {@code UIClient}
     */
    private List<File> openedFiles;

    private UIClient client;

    private static FileManager instance = new FileManager();

    /**
     * The number of <b>new</b> files that were created this session.
     */
    private int filesCreatedThisSession;

    private FileManager() {
        this.openedFiles = new ArrayList<>();
        this.client = UIClient.getInstance();
    }

    public static FileManager getInstance() {
        return instance;
    }

    /**
     * Adds a file to the {@code openedFiles} List.
     *
     * @param file
     */
    private void addFile(File file) {
        openedFiles.add(file);
    }

    /**
     * Replaces a file at a specified index. Use this method only when the Path
     * of a file changes. The Path of a file may change when saved using the
     * 'Save as...' interface or when attempting to save a file created during
     * the current session using the "Create new..." button.
     * <p>
     * Note: Files created during the current session are created with an empty
     * path by default, and thus, when a file is saved to a proper path, the
     * latter must change.</p>
     *
     * @param index The index of the file.
     * @param file The new {@code File} that will be used to replace the old
     * {@code File} at the given index.
     */
    public void replaceFileAtIndex(int index, File file) {
        openedFiles.set(index, file);
    }

    public boolean fileIsAlreadyOpen(File file) {
        for (File openedFile : openedFiles) {
            if (file.getPath().equals(openedFile.getPath())) {
                return true;
            }
        }
        return false;
    }

    public boolean fileIsAlreadyOpen(String path) {
        for (File openedFile : openedFiles) {
            if (openedFile.getPath().equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calls {@link addFile()} and uses the {@code UIClient} object in this
     * class to set the file contents in a new tab.
     *
     * @param file The file to read and open.
     */
    public void acceptAndOpenFile(File file) {
        if (!dirOrFileExists(file.getPath())) {
            UIUtils.showErrorMessage("File not found", file.getName() + " does not exist.", client);
            openFile();
            return;
        }

        try {
            String contents = new String(Files.readAllBytes(Paths.get(file.getPath())));
            client.sendToNewTab(new UIQueryPanel(client.getTabPaneContent(), file.getPath(), contents));
            addFile(file);
        } catch (java.io.IOException e) {
            UIUtils.showErrorMessage("Error", "Could not open file at " + file, client);
            System.err.println("FILE MANAGER: " + e.getMessage());
        }
    }

    /**
     * Crea un nuevo archivo con su path siguiendo la nomenclatura
     * "New_File_[numero de archivos creados en esta sesión]"
     *
     * @return El path del archivo creado.
     */
    public String newFile() {
        filesCreatedThisSession++;
        File file = new File("SQL_File_" + filesCreatedThisSession);
        addFile(file);
        return file.getPath();
    }

    public File getFile(String path) {
        for (File openedFile : openedFiles) {
            if (path.equals(openedFile.getPath())) {
                return openedFile;
            }
        }
        return null;
    }

    public int getFileIndex(String path) {
        for (int i = 0; i < openedFiles.size(); i++) {
            if (openedFiles.get(i).getPath().equals(path)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isNewFile(String filePath) {
        return !filePath.contains("\\");
    }

    public void openFile() {
        JTabbedPane tabPane = client.getTabPaneContent();
        UserPreferencesLoader userPrefLoader = UserPreferencesLoader.getInstance();
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "SQL File (.sql)", "SQL");
        chooser.setDialogTitle("Open...");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);

        String defSaveDir = userPrefLoader.getProperty("Default.SaveDir");
        chooser.setCurrentDirectory(FileManager.dirOrFileExists(defSaveDir)
                ? new File(defSaveDir)
                : new File(userPrefLoader.getDefaultFor("Default.SaveDir")));
        chooser.setAcceptAllFileFilterUsed(false);

        int returnVal = chooser.showOpenDialog(client);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (fileIsAlreadyOpen(chooser.getSelectedFile())) { //el archivo ya existe
                String filePath = chooser.getSelectedFile().getPath();
                List<UIQueryPanel> queryPanels = UIClient.getInstance().getQueryPanels();
                for (UIQueryPanel queryPanel : queryPanels) {
                    String queryPaneFilePath = queryPanel.getFilePath();
                    if (queryPaneFilePath != null && queryPaneFilePath.equals(filePath)) {
                        int tabIndex = queryPanel.getTabIndex();
                        tabPane.setSelectedIndex(tabIndex);
                        break;
                    }
                }
            } else {
                acceptAndOpenFile(chooser.getSelectedFile());
            }
        }
    }

    public void dragNDropFile(File file) {
        JTabbedPane tabPane = client.getTabPaneContent();
        if (fileIsSQL(file)) {
            if (fileIsAlreadyOpen(file)) { //el archivo ya existe
                List<UIQueryPanel> queryPanels = UIClient.getInstance().getQueryPanels();
                for (UIQueryPanel queryPanel : queryPanels) {
                    String queryPaneFilePath = queryPanel.getFilePath();
                    if (queryPaneFilePath != null && queryPaneFilePath.equals(file.getPath())) {
                        int tabIndex = queryPanel.getTabIndex();
                        tabPane.setSelectedIndex(tabIndex);
                        break;
                    }
                }
            } else {
                acceptAndOpenFile(file);
            }
        }
    }

    public void saveFile(UIQueryPanel queryPanel) {
        String filePath = queryPanel.getFilePath();
        String contents = queryPanel.getSqlEditorPane().getText();
        if (isNewFile(filePath)) { //si es un archivo nuevo
            saveFileAs(queryPanel);
            return;
        }
        queryPanel.unboldTitleLabel();

        try (FileWriter fw = new FileWriter(getFile(filePath))) {
            fw.write(new String(contents.getBytes()));
        } catch (java.io.IOException ex) {
            UIUtils.showErrorMessage("Save file", "Could not save file", client);
            ex.printStackTrace();
        }
    }

    public void saveFileAs(UIQueryPanel queryPanel) {
        String filePath = queryPanel.getFilePath();
        String contents = queryPanel.getSqlEditorPane().getText();
        JTabbedPane tabPane = queryPanel.getParentPane();
        UserPreferencesLoader userPrefLoader = UserPreferencesLoader.getInstance();
        if (tabPane.getTabCount() == 0) {
            return;
        }
        int selectedIndex = tabPane.getSelectedIndex();
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "SQL File (.sql)", "SQL");
        chooser.setDialogTitle("Save as...");
        chooser.setFileFilter(filter);
        String defSaveDir = userPrefLoader.getProperty("Default.SaveDir");
        chooser.setCurrentDirectory(FileManager.dirOrFileExists(defSaveDir)
                ? new File(defSaveDir)
                : new File(userPrefLoader.getDefaultFor("Default.SaveDir")));
        chooser.setAcceptAllFileFilterUsed(false);

        //"suggests" a file name name based on the name of the selected index
        chooser.setSelectedFile(new File(tabPane.getTitleAt(selectedIndex)));
        boolean success = false;
        int returnVal = chooser.showSaveDialog(client);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selFile = chooser.getSelectedFile();
            String selFileFullPath = FileManager.appendExtension(selFile.getPath(), "sql");
            String selFileName = FileManager.appendExtension(selFile.getName(), "sql");
            if (FileManager.dirOrFileExists(selFileFullPath)) {
                int overwrite = overwriteFile(selFileName);
                if (overwrite != 0) {
                    saveFileAs(queryPanel);
                    return;
                }
            }
            try (FileWriter fw = new FileWriter(selFileFullPath)) {
                fw.write(new String(contents.getBytes()));
                success = true;
            } catch (java.io.IOException ex) {
                UIUtils.showErrorMessage("Error", "Could not save file", client);
            }

            if (success) {
                tabPane.setTitleAt(selectedIndex, selFileName);
                replaceFileAtIndex(getFileIndex(filePath), new File(selFileFullPath));
                queryPanel.updateFilePath(selFileFullPath);
                queryPanel.unboldTitleLabel();
            }
        }
    }

    public void saveFileAs(String extension, String contents) {
        UserPreferencesLoader userPrefLoader = UserPreferencesLoader.getInstance();
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                extension + " File (." + extension + ")", extension);
        chooser.setDialogTitle("Save as...");
        chooser.setFileFilter(filter);
        String defSaveDir = userPrefLoader.getProperty("Default.SaveDir");
        chooser.setCurrentDirectory(FileManager.dirOrFileExists(defSaveDir)
                ? new File(defSaveDir)
                : new File(userPrefLoader.getDefaultFor("Default.SaveDir")));
        chooser.setAcceptAllFileFilterUsed(false);

        int returnVal = chooser.showSaveDialog(client);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selFile = chooser.getSelectedFile();
            String selFileFullPath = FileManager.appendExtension(selFile.getPath(), "sql");
            String selFileName = FileManager.appendExtension(selFile.getName(), "sql");
            if (FileManager.dirOrFileExists(selFileFullPath)) {
                int overwrite = overwriteFile(selFileName);
                if (overwrite != 0) {
                    saveFileAs(extension, contents);
                    return;
                }
            }
            try (FileWriter fw = new FileWriter(selFileFullPath)) {
                fw.write(new String(contents.getBytes()));
            } catch (java.io.IOException ex) {
                UIUtils.showErrorMessage("Error", "Could not save file", client);
            }
        }
    }

    /**
     * Muestra un {@code JFileChooser} pero al apretarse el botón de aceptar, el
     * vez de guardar el archivo, se ejecuta el callback dado. A este callback
     * se le pasará como parámetro la ruta seleccionada por el usuario en el
     * {@code JFileChooser}.
     *
     * @param extension
     * @param callback
     */
    public void saveFileAs(String extension, Callback<String> callback) {
        UserPreferencesLoader userPrefLoader = UserPreferencesLoader.getInstance();
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                extension + " File (." + extension + ")", extension);
        chooser.setDialogTitle("Save as...");
        chooser.setFileFilter(filter);
        String defSaveDir = userPrefLoader.getProperty("Default.SaveDir");
        chooser.setCurrentDirectory(FileManager.dirOrFileExists(defSaveDir)
                ? new File(defSaveDir)
                : new File(userPrefLoader.getDefaultFor("Default.SaveDir")));
        chooser.setAcceptAllFileFilterUsed(false);

        int returnVal = chooser.showSaveDialog(client);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selFile = chooser.getSelectedFile();
            String selFileFullPath = FileManager.appendExtension(selFile.getPath(), extension);
            String selFileName = FileManager.appendExtension(selFile.getName(), extension);
            if (FileManager.dirOrFileExists(selFileFullPath)) {
                int overwrite = overwriteFile(selFileName);
                if (overwrite != 0) {
                    saveFileAs(extension, callback);
                    return;
                }
            }
            callback.exec(selFileFullPath);
        }
    }

    public int overwriteFile(String fileName) {
        return UIUtils.showYesNoOptionDialog("Overwrite file?", fileName + " already exists."
                + "\nWould you like to overwrite the file with this one?", JOptionPane.WARNING_MESSAGE,
                false, client);
    }

    public void removeFile(String filePath) {
        for (File openedFile : openedFiles) {
            if (openedFile.getPath().equals(filePath)) {
                openedFiles.remove(openedFile);
                break;
            }
        }
    }

    public void saveTableAs(JTable table) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filterCsv = new FileNameExtensionFilter(
                "Comma delimited file (.csv)", "csv");
        FileNameExtensionFilter filterTxt = new FileNameExtensionFilter(
                "Plain text (.txt)", "txt");
        chooser.setDialogTitle("Save...");
        chooser.addChoosableFileFilter(filterCsv);
        chooser.addChoosableFileFilter(filterTxt);
        chooser.setAcceptAllFileFilterUsed(false);

        int returnVal = chooser.showSaveDialog(UIClient.getInstance());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FileNameExtensionFilter fileFilter = (FileNameExtensionFilter) chooser.getFileFilter();
            String extension = fileFilter.getExtensions()[0];
            File file = chooser.getSelectedFile();
            boolean commaDelimited = extension.equals("csv");
            try (FileWriter fw = new FileWriter(appendExtension(file.getPath(), extension))) {
                fw.write(TextUtils.tableToString(table, true, commaDelimited, true));
            } catch (IOException ex) {
                UIUtils.showErrorMessage("Error", "Could not save file " + file.getName(), UIClient.getInstance());
            }
        }
    }

    public int getFilesCreatedThisSession() {
        return filesCreatedThisSession;
    }

    /**
     * Evaluates whether a {@code File} is of type SQL.
     *
     * @param file The {@code File} to evaluate.
     * @return {@code true} if the file has a .sql extension.
     */
    public boolean fileIsSQL(File file) {
        return fileIsSQL(file.getPath());
    }

    public boolean fileIsSQL(String filepath) {
        return filepath.endsWith(".sql");
    }

    /**
     * Appends a specified file extension to a given {@code String}. If the
     * {@code String} already ends with {@code .extension}, this method will do
     * nothing.
     *
     * @param fileIdentifier The file path or the file name.
     * @param extension The extension to append.
     * @return A {@code String} with {@code .extension} appended to it.
     */
    public static String appendExtension(String fileIdentifier, String extension) {
        if (fileIdentifier.endsWith("." + extension)) {
            return fileIdentifier;
        }
        return fileIdentifier + "." + extension;
    }

    /**
     * Creates a directory in {@code pathName} if it doesn't already exist.
     *
     * @param pathname The place in which to create the folder.
     */
    public static void createDirIfNotExists(String pathname) {
        if (!dirOrFileExists(pathname)) {
            File newDir = new File(pathname);
            newDir.mkdirs();
        }
    }

    /**
     * Checks whether a directory or file exists.
     *
     * @param pathName
     * @return
     */
    public static boolean dirOrFileExists(String pathName) {
        return Files.exists(Paths.get(pathName));
    }

    public static boolean isDirEmpty(String path) {
        try {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(path))) {
                return !dirStream.iterator().hasNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

}
