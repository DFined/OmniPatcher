package dfined.patcher_parser.application;

import com.sun.javaws.exceptions.InvalidArgumentException;
import dfined.patcher_parser.application.gui.GUI;
import dfined.patcher_parser.data.Data;
import dfined.patcher_parser.data.data_structure.io.LineReader;
import dfined.patcher_parser.filesystem.FileManager;
import dfined.patcher_parser.filesystem.FileSystem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PatcherParser extends Application {
    private static final Logger log = LogManager.getLogger(PatcherParser.class.getSimpleName());
    ApplicationSettings settings;
    GUI controller;
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("application/patcher_parser.fxml"));
    static PatcherParser instance;

    @Override
    public void start(Stage primaryStage) throws Exception {
        settings = ApplicationSettings.loadSettings();
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle(Param.APPLICATION_TITLE);
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        primaryStage.setWidth(1000);
        primaryStage.setHeight(500);
        instance = this;
        setup();
    }

    public void setup() throws InvalidArgumentException {
        FileSystem.setup(settings);
        Data.setup(settings);
        controller.init();
    }

    public static void test(LineReader lines){
        try {
            for (int i = 0; i < 20; i++) {
                System.out.println(lines.readLine());
            }
        }catch (IOException e){

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static PatcherParser getInstance() {
        return instance;
    }

    public ApplicationSettings getSettings() {
        return settings;
    }

    public static FileManager getFileManager(){
        return FileSystem.getManager(getInstance().settings.getFileManager());
    }
}
