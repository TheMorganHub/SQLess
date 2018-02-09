package com.sqless.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The {@code PreferenceLoader} class allows the program to save/remember and
 * retrieve user preferences such as window size, window position, etc from
 * previous use.
 * <p>
 * <b>Note:</b> this class should only be used for settings that can't be
 * modified within the {@code UISettings} frame.</p>
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class PreferenceLoader {

    private static final PreferenceLoader instance = new PreferenceLoader();

    private Preferences prefs;

    /**
     * The root path.
     */
    private static final String PATH = "sqless";

    /**
     * The path that will contain keys and values for the application.
     */
    private static final String PATH_SETTINGS = "settings";

    /**
     * The {@code boolean} that will serve to signal if this is the first time
     * the program was run.
     */
    private boolean wasFirstTime;

    public enum PrefKey {
        window_width, window_height, window_posX, window_posY,
        split_Main_divider, window_state;
    }

    private PreferenceLoader() {
        prefs = Preferences.userRoot().node(PATH);
        try {
            if (!prefs.nodeExists(PATH_SETTINGS)) {
                System.out.println("PREPARING FOR FIRST TIME USE...");
                performFirstTimeTasks();
            } else {
                prefs = Preferences.userRoot().node(PATH + "/" + PATH_SETTINGS);
                showPrefs();
            }
        } catch (BackingStoreException e) {
        }
    }

    /**
     * Performs tasks that need to occur the very first time the user runs the
     * program. This method will create a node within the registry from
     * {@code PATH_SETTINGS}, thus signalling that the program has been run at
     * least once for the future.
     */
    private void performFirstTimeTasks() {
        prefs = Preferences.userRoot().node(PATH + "/" + PATH_SETTINGS);
        setDefaults();
        wasFirstTime = true;
    }

    public void showPrefs() {
        for (PrefKey value : PrefKey.values()) {
            System.out.println(value + ": " + get(value));
        }
    }

    /**
     * Attempts to retrieve a preference.
     *
     * @param key The type of preference to retrieve.
     * @return the value of said preference. {@code -1} if the key is invalid or
     * cannot be found.
     */
    public String get(PrefKey key) {
        return prefs.get(key.toString(), "-1");
    }

    public int getAsInt(PrefKey key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Stores a value in a preference identified by {@code PrefKey key}. If a
     * preference with the given key doesn't exist, this will create it.
     *
     * @param key The {@code PrefKey} key that will identify the value.
     * @param value
     */
    public void set(PrefKey key, String value) {
        prefs.put(key.toString(), value);
    }

    public boolean wasFirstTime() {
        return wasFirstTime;
    }

    public void flush() {
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
        }
    }

    /**
     * Sets default values for some keys. This method will only be called the
     * very first time the program is executed.
     */
    private void setDefaults() {
        set(PrefKey.window_width, "1280");
        set(PrefKey.window_height, "720");
        set(PrefKey.split_Main_divider, "270");
        set(PrefKey.window_state, "0");
    }

    public static PreferenceLoader getInstance() {
        return instance;
    }
}
