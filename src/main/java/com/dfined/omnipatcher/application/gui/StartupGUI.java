package com.dfined.omnipatcher.application.gui;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartupGUI extends VBox implements SelfRootedGUIScreen{
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 200;
    private static final String WINDOW_NAME = "DFined's OmniPatcher";
    private static final String FXML_ADDRESS = "StartupTile.fxml";
    public StartupGUI() {
        super();
    }

    @Override
    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    @Override
    public int getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }

    @Override
    public void setup(Stage primaryStage) {
    }

    @Override
    public String getName() {
        return WINDOW_NAME;
    }

    @Override
    public String getFXMLAddress() {
        return FXML_ADDRESS;
    }
}
