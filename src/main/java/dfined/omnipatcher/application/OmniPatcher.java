package dfined.omnipatcher.application;

import com.sun.javaws.exceptions.InvalidArgumentException;
import dfined.omnipatcher.application.gui.GUI;
import dfined.omnipatcher.data.Data;
import dfined.omnipatcher.data.data_structure.io.LineReader;
import dfined.omnipatcher.filesystem.FileManager;
import dfined.omnipatcher.filesystem.FileSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
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
        settings = ApplicationSettings.loadSettings();
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle(Param.APPLICATION_TITLE);
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        primaryStage.setWidth(DEFAULT_WIDTH);
        primaryStage.setHeight(DEFAULT_HEIGHT);
        primaryStage.setOnCloseRequest((event) ->{ Platform.exit(); System.exit(0); });
        instance = this;
        setup();
    }

    public void setup() throws InvalidArgumentException {
        FileSystem.setup(settings);
        try {
            Data.setup(settings);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to load data. " + e.getMessage(), "Error starting app", JOptionPane.ERROR_MESSAGE);
            log.fatal("Unable to load data.", e);
            Platform.exit();
            System.exit(1);
        }
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
