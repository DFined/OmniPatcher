package com.dfined.omnipatcher.application;

import com.dfined.omnipatcher.filesystem.FileSystem;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ApplicationSettings {
    private static final String DEFAULT_SETTINGS_FILE = "application_settings.json";
    private static final String SETTINGS_ENCODING = "UTF-8";
    private static final String GAMEINFO_RELATIVE_PATH = "game/dota/gameinfo.gi";
    private static final String VPK_RELATIVE_PATH = "game/dota/pak01_dir.vpk";
    private static final String VPK_TARGET_PATH = "game/mods";
    private static final String VPK_TARGET_NAME = "pak01_dir.vpk";
    private static final Logger log = LogManager.getLogger(OmniPatcher.class.getSimpleName());
    private static ClassLoader classLoader = ApplicationSettings.class.getClassLoader();

    private File localDir = new File(System.getProperty("user.dir"), "/data/local");
    private File tempDir = new File(System.getProperty("user.dir"), "/data/output");
    transient private File sourceFile = new File("C:/Program Files/steam/steamapps/common/dota2/dota");
    private File dotaDir = new File("C:/Program Files/steam/steamapps/common/dota2/dota");
    private File vpkDir = new File("C:/Program Files/steam/steamapps/common/dota2/dota/game/mods");
    private File vpkFile = new File("C:/Program Files/steam/steamapps/common/dota2/dota/game/mods/pak01_dir.vpk");
    private File gameinfoFile = new File("C:/Program Files/steam/steamapps/common/dota2/dota/game/mods/pak01_dir.vpk");
    private FileSystem.Manager fileManager = FileSystem.Manager.VPK_FILE_MANAGER;

    public static ApplicationSettings loadSettings() {
        File settingsFile = new File(System.getProperty("user.dir"), DEFAULT_SETTINGS_FILE);
        String json;
        ApplicationSettings settings;
        try {
            //Try to load default settings;
            json = FileUtils.readFileToString(settingsFile, SETTINGS_ENCODING);
            settings = Globals.OBJECT_MAPPER.readValue(json, ApplicationSettings.class);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                log.warn("No settings file found in current working dir! Please start the application from the install dir" + settingsFile.getAbsolutePath());
            }
            settings = new ApplicationSettings();
        }

        settings.setSourceFile(new File(settings.getDotaDir(), VPK_RELATIVE_PATH));
        settings.setVpkDir(new File(settings.getDotaDir(), VPK_TARGET_PATH));
        settings.setVpkFile(new File(settings.getVpkDir(), VPK_TARGET_NAME));
        settings.setGameinfoFile(new File(settings.getDotaDir(), GAMEINFO_RELATIVE_PATH));
        //settings.save();
        log.info("Successfully loaded settings. Starting app");
        return settings;
    }

    public boolean save() {
        try {
            File settingsFile = new File(System.getProperty("user.dir"), DEFAULT_SETTINGS_FILE);
            if (!settingsFile.exists()) {
                log.warn("Unable to locate settings file in working directory. Creating new one.");
                settingsFile.createNewFile();
                return false;
            }
            FileUtils.writeStringToFile(settingsFile, Globals.OBJECT_MAPPER.writeValueAsString(this), SETTINGS_ENCODING);
        } catch (IOException e) {
            log.error("Unable to write settings! This IS an error.", e);
            return false;
        }
        return true;
    }

    public File localDir() {
        return localDir;
    }

    public void setLocalDir(File localDir) {
        this.localDir = localDir;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public FileSystem.Manager getFileManager() {
        return fileManager;
    }

    public File getLocalDir() {
        return localDir;
    }

    public File getTempDir() {
        return tempDir;
    }

    public File getDotaDir() {
        return dotaDir;
    }

    public void setDotaDir(File dotaDir) {
        this.dotaDir = dotaDir;
        this.setSourceFile(new File(this.getDotaDir(), VPK_RELATIVE_PATH));
        this.setVpkDir(new File(this.getDotaDir(), VPK_TARGET_PATH));
        this.setVpkFile(new File(this.getVpkDir(), VPK_TARGET_NAME));
        this.setGameinfoFile(new File(this.getDotaDir(), GAMEINFO_RELATIVE_PATH));
        this.save();

    }

    public File getVpkDir() {
        return vpkDir;
    }

    public void setVpkDir(File vpkDir) {
        this.vpkDir = vpkDir;
    }

    public File getVpkFile() {
        return vpkFile;
    }

    public void setVpkFile(File vpkFile) {
        this.vpkFile = vpkFile;
    }

    public File getGameinfoFile() {
        return gameinfoFile;
    }

    public void setGameinfoFile(File gameinfoFile) {
        this.gameinfoFile = gameinfoFile;
    }
}
