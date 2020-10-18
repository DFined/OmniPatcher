package dfined.omnipatcher.application.gui;

import javafx.scene.Parent;
import javafx.stage.Stage;

public interface GUIScreen {
    public Parent create();

    public int getDefaultWidth();
    public int getDefaultHeight();

    public void setup(Stage primaryStage);

    public String getName();
}
