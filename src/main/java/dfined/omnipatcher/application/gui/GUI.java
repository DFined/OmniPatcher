package dfined.omnipatcher.application.gui;

import dfined.omnipatcher.application.OmniPatcher;
import dfined.omnipatcher.data.Data;
import dfined.omnipatcher.data.Session;
import dfined.omnipatcher.data.data_structure.game.Item;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUI {
    Logger log = LogManager.getLogger(GUI.class);
    @FXML
    Button installAll;

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

    public void init() {
        //Hero selector setup
        progressBox.managedProperty().bind(progressBox.visibleProperty());
        progressBox.setVisible(false);
        List<String> heroes = new ArrayList<>();
        heroes.addAll(Data.getHeroes().keySet());
        Collections.sort(heroes);
        heroSelector.setItems(FXCollections.observableList(heroes));
        heroSelector.getSelectionModel().select(0);
    }

    static final GUI INSTANCE = new GUI();

    public GUI() {
    }

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
                ItemTile tile = GUI.createItemTile(item);
                tile.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                            if (leftSelectedTile != null) {
                                leftSelectedTile.displayDeselection();
                            }
                            leftSelectedTile = tile;
                            leftSelectedTile.displaySelection();
                        }
                );
                tiles.add(tile);
                progressBar.setProgress((float)tiles.size() / itemsSize);
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
                            ItemTile tile = GUI.createItemTile(item);
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
        File repo = OmniPatcher.getInstance().getSettings().getTempDir();
        String errorMsg = "";
        if (repo.isDirectory()) {
            String[] files = repo.list();
            int clear = JOptionPane.YES_OPTION;
            if (files.length != 0) {
                clear = JOptionPane.showConfirmDialog(null, "Selected repo is not empty. Clear?", "Clear Repo?", JOptionPane.YES_NO_OPTION);

            }
            if (clear == JOptionPane.YES_OPTION) {
                try {
                    FileUtils.cleanDirectory(repo);
                    for (Item item : Session.getToInstall().stream().map(name -> Data.getItems().get(name)).collect(Collectors.toList())) {
                        item.installItem();
                    }
                    Item.installAll();
                    return;
                } catch (IOException e) {
                    errorMsg = "Encountered filesystem error during install. " + e.getMessage();
                    log.warn(String.format("Encountered filesystem error during install. '%s'", e.getMessage()));
                    e.printStackTrace();
                }
            }
        } else {
            errorMsg = String.format("Repo is not a directory: '%s'", repo.getAbsolutePath());
        }
        JOptionPane.showMessageDialog(null, errorMsg, "Error installing mods", JOptionPane.ERROR_MESSAGE);
    }


    public String selectedHero() {
        return (String) heroSelector.getSelectionModel().getSelectedItem();
    }


}
