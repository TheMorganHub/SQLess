package com.sqless.utils;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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

    /**
     * Constructs a {@code String} of a given length made of random characters
     * from uppercase A-Z and numbers 0-9.
     *
     * @param length The length of the {@code String}.
     * @return A randomised {@code String}.
     */
    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (random(0, 100) < 50) {
                sb.append((char) random(65, 90));
            } else {
                sb.append(random(0, 9));
            }
        }
        return sb.toString();
    }

    public static Properties getSystemInfo() {
        return System.getProperties();
    }

    /**
     * Returns a timestamp of the current time in {@code String} format.
     *
     * @return a timestamp as 'hh:mm:ss'.
     */
    public static String timeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        return format.format(new Date(System.currentTimeMillis()));
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

    /**
     * Calculates the maximum integer value in a list.
     *
     * @param list
     * @return the maximum value.
     */
    public static int maxFromList(List<Integer> list) {
        int max = 0;
        for (Integer integer : list) {
            if (integer > max) {
                max = integer;
            }
        }
        return max;
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
