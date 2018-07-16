package com.sqless.utils;

import com.sqless.queries.SQLQuery;
import com.sqless.queries.SQLSelectQuery;
import com.sqless.ui.UIClient;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class MiscUtils {

    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * Randomly generates a number between min (including) and max (including).
     *
     * @param min The min value.
     * @param max The max value.
     * @return An integer between min including and max including.
     */
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static boolean arrayContains(int[] array, int value) {
        if (array == null) {
            return false;
        }
        for (int t : array) {
            if (t == value) {
                return true;
            }
        }
        return false;
    }

    public static Properties getSystemInfo() {
        return System.getProperties();
    }

    public static void getSystemInfo(Runnable before, Callback<Map<String, String>> after, Runnable onError) {
        before.run();
        SwingWorker<Map<String, String>, Void> systemWorker = new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() throws Exception {
                Map<String, String> infoMap = new HashMap<>();
                Runtime rt = Runtime.getRuntime();
                String[] commands = {"systeminfo"};
                Process proc = rt.exec(commands);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    if (s.startsWith("OS Name")) {
                        infoMap.put("OS Name", s.split(":")[1].trim());
                    } else if (s.startsWith("OS Version")) {
                        infoMap.put("OS Version", s.split(":")[1].trim());
                    } else if (s.startsWith("System Type")) {
                        infoMap.put("System Type", s.split(":")[1].trim());
                    }
                }
                SQLQuery mysqlInfoQuery = new SQLSelectQuery("SHOW VARIABLES;") {
                    @Override
                    public void onSuccess(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            String key = rs.getString(1);
                            String value = rs.getString(2);
                            if (key.equals("version")) {                                
                                infoMap.put("sql-version", value);
                            } else if (key.equals("basedir")) {
                                infoMap.put("sql-basedir", value);
                            }
                        }
                    }                 
                };
                mysqlInfoQuery.exec();
                infoMap.put("user", getSystemInfo().getProperty("user.name"));
                infoMap.put("Java-Home", getSystemInfo().getProperty("java.home"));
                infoMap.put("Java-Version", getSystemInfo().getProperty("java.version"));
                infoMap.put("Java-Vendor", getSystemInfo().getProperty("java.vendor"));
                return infoMap;
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> info = get();
                    after.exec(info);
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                    onError.run();
                }
            }
        };
        systemWorker.execute();
    }

    public static void openDirectory(String directory) {
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + directory);
        } catch (IOException ex) {
            UIUtils.showErrorMessage("Error", "No se pudo abrir el directorio.", UIClient.getInstance());
        }
    }

    public static void openInBrowser(String url) {
        try {
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (IOException | URISyntaxException e) {
            UIUtils.showErrorMessage("Error", "Esta funcionalidad no es soportada. Asegúrate que tu computadora pueda navegar por internet.", UIClient.getInstance());
        }
    }

    /**
     * This method uses a {@code Robot} to simulate the action of pressing keys
     * and then releasing them, by calling the {@code keyPress} and
     * {@code keyRelease} methods in {@code Robot} respectively. The events will
     * be generated in {@code receiver}.
     *
     * @param receiver The {@code Component} that will receive the generated
     * events.
     * @param keyCodes The keycodes to simulate.
     */
    public static void simulateKeyEvent(Component receiver, int... keyCodes) {
        try {
            receiver.requestFocus();
            Robot robot = new Robot();
            for (int keyCode : keyCodes) {
                robot.keyPress(keyCode);
            }
            for (int keyCode : keyCodes) {
                robot.keyRelease(keyCode);
            }
        } catch (AWTException ex) {
            System.err.println("Could not perform robot task: " + ex.getMessage());
        }
    }

    /**
     * Calls {@code simulateKeyEvent()} and uses {@code KeyEvent.VK_CONTROL} as
     * the first argument.
     *
     * @param receiver The {@code Component} that will receive the generated
     * events.
     * @param keyCode A keyCode that will be preceded by a
     * {@code KeyEvent.VK_CONTROL}.
     * @see MiscUtils#simulateKeyEvent(java.awt.Component, int[])
     */
    public static void simulateCtrlKeyEvent(Component receiver, int keyCode) {
        simulateKeyEvent(receiver, KeyEvent.VK_CONTROL, keyCode);
    }

    public static void log(String tag, Object txt) {
        try (FileWriter fw = new FileWriter(System.getProperty("user.home") + "/Desktop/log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            String toWrite = new Date() + " - " + tag + ": " + txt.toString() + "\r\n";
            out.println(toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
