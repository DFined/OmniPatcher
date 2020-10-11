package dfined.omnipatcher.application;

import dfined.omnipatcher.application.gui.GUI;
import dfined.omnipatcher.application.gui.StartupTile;
import dfined.omnipatcher.data.Data;
import dfined.omnipatcher.data.data_structure.io.LineReader;
import dfined.omnipatcher.filesystem.FileManager;
import dfined.omnipatcher.filesystem.FileSystem;
import dfined.omnipatcher.filesystem.ValveResourceManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class OmniPatcher extends Application {
    private static final Logger log = LogManager.getLogger(OmniPatcher.class.getSimpleName());
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 500;
    ApplicationSettings settings;
    GUI controller;
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("application/patcher_parser.fxml"));
    static OmniPatcher instance;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        //Load and instantiate settings for the current run
        settings = ApplicationSettings.loadSettings();
        //Setup the basic GUI for the resource loading period
        GUI.setupLoadingGUI(primaryStage);
        assertGameDirIsValid();
        if(!ValveResourceManager.validateDecompilerLocation()){
            JOptionPane.showMessageDialog(null, "No valveFormatDecompiler.exe file found in current working dir! Please start the application from the install dir", "Error starting app", JOptionPane.ERROR_MESSAGE);
            Platform.exit();
            System.exit(1);
        }
        //Initiate the loading and initialization of the main data in the background.
        Thread thread = new Thread(() -> {
            instance.setupData();
            Platform.runLater(() -> {
                        Parent root = null;
                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, "Unable to load application layout. " + e.getMessage(), "Error starting app", JOptionPane.ERROR_MESSAGE);
                            log.fatal("Unable to load application layout.", e);
                            Platform.exit();
                            System.exit(1);
                        }
                        controller = loader.getController();
                        primaryStage.setScene(new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT));
                        primaryStage.setOnCloseRequest((event) -> {
                                    Platform.exit();
                                    System.exit(0);
                                }
                        );
                        instance.setupGUI();
                    }
            );
        });
        thread.start();
    }

    private void assertGameDirIsValid() {
        String selectMessage = "It appears the Dota 2 directory is set incorrectly. " +
                "Would you like to choose a new one?";
        File gameExec = new File(settings.getDotaDir(), "game/bin/win32/dota2.exe");
        if (!gameExec.exists()) {
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

    public void setupGUI() {
        controller.init();
    }

    public static void test(LineReader lines) {
        try {
            for (int i = 0; i < 20; i++) {
                System.out.println(lines.readLine());
            }
        } catch (IOException e) {

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
