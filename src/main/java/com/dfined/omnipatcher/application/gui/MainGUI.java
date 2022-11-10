package com.dfined.omnipatcher.application.gui;

import com.dfined.omnipatcher.data.Session;
import com.dfined.omnipatcher.data.data_structure.game.Item;
import com.dfined.omnipatcher.data.Data;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainGUI extends SplitPane implements SelfRootedGUIScreen {
    private static final String FXML_ADDRESS = "patcher_parser.fxml";
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 700;
    private static final String WINDOW_NAME = "DFined's OmniPatcher";
    Logger log = LogManager.getLogger(MainGUI.class);

    @FXML
    VBox allItemsBox;

    @FXML
    VBox allSessionItemsBox;

    @FXML
    ComboBox heroSelector;

    @FXML
    VBox progressBox;

    @FXML
    ProgressBar progressBar;

    ItemTile leftSelectedTile = null;
    ItemTile installedSelectedTile = null;

    public static ItemTile createItemTile(Item item) {
        return new ItemTile(item);
    }

    @FXML
    public void onHeroSelect() {
        progressBox.setVisible(true);
        heroSelector.setVisible(false);
        allItemsBox.getChildren().clear();
        int itemsSize = Data.getHeroes().get(selectedHero()).getItems().size();
        ArrayList<ItemTile> tiles = new ArrayList<>();
        Thread thread = new Thread(() -> {
            for (Item item : Data.getHeroes().get(selectedHero()).getItems()) {
                ItemTile tile = MainGUI.createItemTile(item);
                tile.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                            if (leftSelectedTile != null) {
                                leftSelectedTile.displayDeselection();
                            }
                            leftSelectedTile = tile;
                            leftSelectedTile.displaySelection();
                        }
                );
                tiles.add(tile);
                progressBar.setProgress((float) tiles.size() / itemsSize);
            }
            Platform.runLater(() -> {
                        allItemsBox.getChildren().addAll(tiles);
                        progressBox.setVisible(false);
                        heroSelector.setVisible(true);
                    }
            );
        });
        thread.start();

    }

    public void updateSessionPane() {
        allSessionItemsBox.getChildren().clear();
        Session.getToInstall().stream()
                .map(name -> Data.getItems().get(name))
                .sorted(Comparator.comparing(Item::getHeroName).thenComparing(Item::getName))
                .forEach((item) -> {
                            ItemTile tile = MainGUI.createItemTile(item);
                            tile.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                                        if (this.installedSelectedTile != null) {
                                            this.installedSelectedTile.displayDeselection();
                                        }
                                        this.installedSelectedTile = tile;
                                        this.installedSelectedTile.displaySelection();
                                    }
                            );
                            allSessionItemsBox.getChildren().add(tile);
                        }
                );
    }

    @FXML
    public void onSaveSession() {
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.SAVE);
        dialog.setVisible(true);
        File[] files = dialog.getFiles();
        if (files.length > 0) {
            Session.saveSession(files[0]);
        }
        updateSessionPane();
    }

    @FXML
    public void onInstallItem() {
        if (leftSelectedTile != null) {
            Session.addItemToInstall(leftSelectedTile.item.getName());
        }
        updateSessionPane();
    }

    @FXML
    public void onLoadSession() {
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        File[] files = dialog.getFiles();
        if (files.length > 0) {
            Session.loadSession(files[0]);
        }
        updateSessionPane();
    }

    @FXML
    public void onClearSession() {
        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear all Items?", "Clear Items?", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            Session.clearSession();
        }
        updateSessionPane();
    }

    @FXML
    public void onRemoveSelected() {
        if (installedSelectedTile != null) {
            Session.removeItemToInstall(installedSelectedTile.item.getName());
            updateSessionPane();
        }
    }

    @FXML
    public void onInstallAll() {
        Item.installAll(false);
    }


    @Override
    public void setup(Stage primaryStage) {
        primaryStage.setOnCloseRequest((event) -> {
                    Platform.exit();
                    System.exit(0);
                }
        );
        progressBox.managedProperty().bind(progressBox.visibleProperty());
        progressBox.setVisible(false);
        List<String> heroes = new ArrayList<>();
        heroes.addAll(Data.getHeroes().keySet());
        Collections.sort(heroes);
        heroSelector.setItems(FXCollections.observableList(heroes));
        heroSelector.getSelectionModel().select(0);
        onHeroSelect();
    }

    @Override
    public String getName() {
        return WINDOW_NAME;
    }

    public String selectedHero() {
        return (String) heroSelector.getSelectionModel().getSelectedItem();
    }


    @Override
    public String getFXMLAddress() {
        return FXML_ADDRESS;
    }

    @Override
    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    @Override
    public int getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }
}
