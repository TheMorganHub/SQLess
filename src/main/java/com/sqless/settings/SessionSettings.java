package com.sqless.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * This class uses a {@code HashMap} to store "temporary" settings that only
 * affect the current session. The settings saved within this class are not
 * taken into consideration in future executions of SQLess.
 *
 * @author David Orquin, Tomás Casir, Valeria Fornieles
 */
public class SessionSettings {

    private Map<Keys, Object> sessionPrefs;
    private static final SessionSettings INSTANCE = new SessionSettings();

    public enum Keys {
        SHOW_MASTER_DB;
    }

    private SessionSettings() {
        sessionPrefs = new HashMap<>();
        loadDefaults();
    }

    public void put(Keys key, Object value) {
        sessionPrefs.put(key, value);
    }

    public Object get(Keys key) {
        return sessionPrefs.get(key);
    }

    public void loadDefaults() {
        sessionPrefs.put(Keys.SHOW_MASTER_DB, Boolean.FALSE);
    }

    public static SessionSettings getINSTANCE() {
        return INSTANCE;
    }

}
