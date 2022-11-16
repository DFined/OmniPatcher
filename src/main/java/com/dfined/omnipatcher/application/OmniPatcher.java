package com.dfined.omnipatcher.application;

import com.dfined.omnipatcher.v2.ui.GUI;
import com.dfined.omnipatcher.application.gui.MainGUI;
import com.dfined.omnipatcher.application.gui.StartupGUI;
import com.dfined.omnipatcher.data.Data;
import com.dfined.omnipatcher.data.Session;
import com.dfined.omnipatcher.data.data_structure.game.Item;
import com.dfined.omnipatcher.filesystem.FileManager;
import com.dfined.omnipatcher.filesystem.FileSystem;
import com.dfined.omnipatcher.v2.filesystem.ValveResourceManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class OmniPatcher extends Application {
    private static final String LOADING_GUI_NAME = "LOADING_GUI";
    private static final String MAIN_GUI_NAME = "MAIN_GUI";
    private static final String RUN_GAME_PARAMETER = "RunGame";
    private static final String GAME_EXEC_PATH = "game/bin/win32/dota2.exe";
    private static final String ACTIVE_SESSION_RELATIVE = "sessions/activeProfile.txt";
    private static final Logger log = LogManager.getLogger(OmniPatcher.class.getSimpleName());
    ApplicationSettings settings;
    GUI gui;
    static OmniPatcher instance;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        gui = new GUI(primaryStage);

        //Check if we are in correct dir (i.e. ValveFormatDecompiler exec is present)
        assertValidLaunchDir();

        //Load and instantiate settings for the current run
        settings = ApplicationSettings.loadSettings();

        //Setup the basic GUI for the resource loading period
        gui.addAndOpenScreen(LOADING_GUI_NAME, new StartupGUI());

        //Assert dota dir is set and valid
        assertGameDirIsValid();

        Map<String, String> parameters = getParameters().getNamed();

        //Initiate the loading and initialization of the main data in the background.
        Thread thread = new Thread(() -> {
            instance.setupData();
            if (parameters.containsKey(RUN_GAME_PARAMETER)) {
                File activeProfile = new File(System.getProperty("user.dir"), ACTIVE_SESSION_RELATIVE);
                if (!activeProfile.exists()) {
                    FileDialog dialog = new FileDialog((Frame) null, "Select item profile to install");
                    dialog.setMode(FileDialog.LOAD);
                    dialog.setVisible(true);
                    File[] files = dialog.getFiles();
                    try {
                        FileUtils.copyFile(files[0], activeProfile);
                    } catch (IOException ioException) {
                        log.fatal("Unable to copy active profile file. Exiting.", ioException);
                        Platform.exit();
                        System.exit(1);
                    }
                }
                Session.loadSession(activeProfile);
                Item.installAll(true);
                Platform.runLater(
                        () -> {
                            Platform.exit();
                            System.exit(0);
                        }
                );
            } else {
                Platform.runLater(() -> gui.addAndOpenScreen(MAIN_GUI_NAME, new MainGUI()));
            }
        });
        thread.start();

    }

    public void assertValidLaunchDir() {
        if (!ValveResourceManager.validateDecompilerLocation()) {
            JOptionPane.showMessageDialog(null, "No valveFormatDecompiler.exe file found in current working dir! Please start the application from the install dir", "Error starting app", JOptionPane.ERROR_MESSAGE);
            Platform.exit();
            System.exit(1);
        }
    }

    private void assertGameDirIsValid() {
        String selectMessage = "It appears the Dota 2 directory is set incorrectly. " +
                "Would you like to choose a new one?";
        while (!getFileManager().existsInLocal(settings.getDotaDir(), GAME_EXEC_PATH)) {
            //Game directory is set incorrectly. Does user want to choose a new one?
            int reply = JOptionPane.showConfirmDialog(null, selectMessage, "Select dota 2 directory?", JOptionPane.OK_CANCEL_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Please choose the base Dota2 directory");
                File dotaDir = directoryChooser.showDialog(null);
                settings.setDotaDir(dotaDir);
            } else {
                JOptionPane.showMessageDialog(null, "Dota 2 directory not set. Load impossible. Exiting.", "Error starting app", JOptionPane.ERROR_MESSAGE);
                Platform.exit();
                System.exit(1);
            }
        }
    }

    public void setupData() {
        FileSystem.setup(settings);
        try {
            Data.setup(settings);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to load data. " + e.getMessage(), "Error starting app", JOptionPane.ERROR_MESSAGE);
            log.fatal("Unable to load data.", e);
            Platform.exit();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static OmniPatcher getInstance() {
        return instance;
    }

    public ApplicationSettings getSettings() {
        return settings;
    }

    public static FileManager getFileManager() {
        return FileSystem.getManager(getInstance().settings.getFileManager());
    }
}
