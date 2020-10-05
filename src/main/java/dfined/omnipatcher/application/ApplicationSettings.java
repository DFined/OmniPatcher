package dfined.omnipatcher.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dfined.omnipatcher.filesystem.FileSystem;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ApplicationSettings {
    private static final String DEFAULT_SETTINGS_FILE = "application_settings.json";
    private static final String SETTINGS_ENCODING = "UTF-8";
    private static final Logger log = LogManager.getLogger(OmniPatcher.class.getSimpleName());
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static ClassLoader classLoader = ApplicationSettings.class.getClassLoader();

    private File localDir = new File(System.getProperty("user.dir"),"/data/local");
    private File tempDir = new File(System.getProperty("user.dir"),"/data/output");
    private File sourceDir = new File("C:/Program Files/steam/steamapps/common/dota2/dota");
    private File dotaDir = new File("C:/Program Files/steam/steamapps/common/dota2/dota");
    private FileSystem.Manager fileManager = FileSystem.Manager.SIMPLE_FILE_MANAGER;

    public static ApplicationSettings loadSettings() {
        File settingsFile = new File(System.getProperty("user.dir"), DEFAULT_SETTINGS_FILE);
        String json;
        try {
            //Try to load default settings;
            json = FileUtils.readFileToString(settingsFile, SETTINGS_ENCODING);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                log.warn("No settings file found in current working dir! Please start the application from the install dir" + settingsFile.getAbsolutePath());
            }
            throw new IllegalStateException("Failed to find or load settings. Startup impossible. Exiting.", e);
        }
        ApplicationSettings settings = gson.fromJson(json, ApplicationSettings.class);
        settings.save();
        log.info("Successfully loaded settings. Starting app");
        return settings;
    }

    public boolean save(){
        File settingsFile = new File(System.getProperty("user.dir"), DEFAULT_SETTINGS_FILE);
        if(!settingsFile.exists()){
            log.error("Unable to locate settings file in working directory. Aborting write");
            return false;
        }
        try {
            FileUtils.writeStringToFile(settingsFile, gson.toJson(this), SETTINGS_ENCODING);
        } catch (IOException e) {
            log.error("Unable to write settings! This IS an error.",e);
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

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
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
}
