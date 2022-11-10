package com.dfined.omnipatcher.data;

import com.dfined.omnipatcher.application.Globals;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Session {
    static Logger log = LogManager.getLogger(Session.class);
    private static Set<String> toInstall = new HashSet<>();

    public static Set<String> getToInstall() {
        return toInstall;
    }

    public static void addItemToInstall(String itemName) {
        toInstall.add(itemName);
    }

    public static void removeItemToInstall(String itemName) {
        toInstall.remove(itemName);
    }

    public static void clearSession() {
        toInstall.clear();
    }

    public static void saveSession(File file) {
        try {
            FileUtils.writeStringToFile(file, Globals.OBJECT_MAPPER.writeValueAsString(toInstall), "UTF8");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Unable to save to file '%s'", file.getAbsolutePath()), "Error saving session", JOptionPane.ERROR_MESSAGE);
            log.warn("Error saving session", e);
        }
    }

    public static void loadSession(File file) {
        if (file.exists()) {
            try {
                toInstall = Globals.OBJECT_MAPPER.readValue(FileUtils.readFileToString(file, "UTF8"), HashSet.class);
                return;
            } catch (IOException e) {
                log.warn("Error saving session", e);
            }
        }
        JOptionPane.showMessageDialog(null, String.format("Unable to load to file '%s'", file.getAbsolutePath()), "Error saving session", JOptionPane.ERROR_MESSAGE);
    }
}
