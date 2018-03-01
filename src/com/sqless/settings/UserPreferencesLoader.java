package com.sqless.settings;

import com.sqless.utils.UIUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.filechooser.FileSystemView;
import com.sqless.file.FileManager;

public class UserPreferencesLoader {

    public final String USER_DIR = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()
            + "\\" + "SQLess";
    public final File SETTINGS_FILE = new File((USER_DIR) + "\\settings.properties");
    public boolean forceReload;
    private static final UserPreferencesLoader instance = new UserPreferencesLoader();

    private Properties userProperties;

    private UserPreferencesLoader() {
        userProperties = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };
    }

    /**
     * Checks whether it's the first time this program is running.
     *
     * @return {@code true} if no file with
     * {@link UserPreferencesLoader#SETTINGS_FILE}'s path exists.
     */
    public boolean isFirstTime() {
        return !Files.exists(Paths.get(SETTINGS_FILE.getPath()));
    }

    public void set(String key, String value) {
        userProperties.setProperty(key, value);
    }

    /**
     * Stores the current properties in {@code userProperties} into a file
     * {@link UserPreferencesLoader#SETTINGS_FILE}.
     */
    public void flushFile() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            userProperties.store(output, null);
        } catch (IOException ex) {
            UIUtils.showErrorMessage("Error", "Could not save user properties.", null);
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Loads the file that contains the user settings to memory.
     */
    public void loadFile() {
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            userProperties.load(input);
        } catch (IOException ex) {
            UIUtils.showErrorMessage("Error", "Could not load user properties.", null);
            System.err.println(ex.getMessage());
        }
    }

    /**
     * This method overloads
     * {@link UserPreferencesLoader#getProperty(paquete.UserPreferencesLoader.Keys)}
     * by converting a {@code String} key into valid properties key of type
     * {@link Keys} using
     * {@link UserPreferencesLoader#stringToKey(java.lang.String)}.
     * <p>
     * <b>WARNING:</b> exercise caution if using this method. Key names might
     * change and the change will not be reflected on Strings where this method
     * is used.</p>
     *
     * @param key a key as a {@code String}
     * @return a value as an {@code Object}
     */
    public String getProperty(String key) {
        return (String) userProperties.getOrDefault(key, getDefaultFor(key));
    }

    /**
     * Returns the default value for a given key.
     *
     * @param key a {@code Keys} object.
     * @return The default value associated with a key. Or {@code null} if the
     * key doesn't have a default value associated with it.
     */
    public String getDefaultFor(String key) {
        switch (key) {
            case "Default.SaveDir":
                FileManager.createDirIfNotExists(USER_DIR + "\\Saves");
                return USER_DIR + "\\Saves";
            case "Ask.DeprecatedTypes":
            case "Ask.UncommittedRows":
                return "true";
            case "Editor.DisplayRows":
                return "100";
            case "Default.Database":
                return "N/A";
        }
        return null;
    }

    /**
     * Creates the directory that will contain the user settings and the save
     * files. This directory will be located at
     * {@link UserPreferencesLoader#USER_DIR}.
     */
    public void makeDir() {
        File newDir = new File(USER_DIR);
        newDir.mkdirs();
    }

    /**
     * Returns this singleton's instance of {@code UserPreferencesLoader}.
     *
     * @return a {@code UserPreferencesLoader} instance.
     */
    public static UserPreferencesLoader getInstance() {
        return instance;
    }
}
