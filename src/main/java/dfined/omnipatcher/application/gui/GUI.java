package dfined.omnipatcher.application.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class GUI {
    Stage primaryStage;

    HashMap<String, GUIScreen> screens = new HashMap<>();
    HashMap<String, Parent> screenRoots = new HashMap<>();

    public GUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void addScreen(String name, GUIScreen supplier) {
        screens.put(name, supplier);
    }

    public void openScreen(String name) {
        if (screens.containsKey(name)) {
            GUIScreen screen = screens.get(name);
            screenRoots.putIfAbsent(name, screens.get(name).create());
            Parent root = screenRoots.get(name);
            screen.setup(primaryStage);
            primaryStage.setScene(new Scene(root, screen.getDefaultWidth(), screen.getDefaultHeight()));
            primaryStage.setTitle(screen.getName());
            primaryStage.show();
        }
    }

    public void addAndOpenScreen(String name, GUIScreen supplier) {
        addScreen(name, supplier);
        openScreen(name);
    }
}
