package com.dfined.omnipatcher.application.gui;

import com.dfined.omnipatcher.data.Session;
import com.dfined.omnipatcher.data.data_structure.game.Item;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ItemTile extends HBox {
    private static final String NORMAL_BACKGROUND_COLOR = "eeeeee";
    private static final String SELECTED_BACKGROUND_COLOR = "d0d0d0";
    Logger log = LogManager.getLogger(ItemTile.class);
    @FXML
    ImageView icon;
    @FXML
    Label itemName;
    @FXML
    Label heroName;
    @FXML
    Label slotName;
    Item item;
    @FXML
    HBox backgroundBox;


    public ItemTile(Item item) {
        super();
        FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("ItemTile.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            log.fatal("Unable to load ItemTile fxml. Exiting");
            Platform.exit();
            System.exit(1);
        }
        this.icon.setImage(item.getIcon());
        this.itemName.setText(item.getName());
        this.heroName.setText(item.getHeroName());
        this.slotName.setText(item.getItemSlot());
        this.item = item;
    }

    @FXML
    public void installItem() {
        Session.addItemToInstall(this.item.getName());
    }

    public void displaySelection() {
        this.backgroundBox.styleProperty().setValue(String.format("-fx-background-color : #%s", SELECTED_BACKGROUND_COLOR));
    }

    public void displayDeselection() {
        this.backgroundBox.styleProperty().setValue(String.format("-fx-background-color : #%s", NORMAL_BACKGROUND_COLOR));
    }
}
