package com.dfined.omnipatcher.v2.ui;

import com.dfined.omnipatcher.v2.model.ItemsToInstall;
import com.dfined.omnipatcher.v2.model.WearableItemSlot;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
public class InstallSlotTile extends TitledPane {
    private static final String FXML_ADDRESS = "install_slot_tile.fxml";

    @FXML
    Label titleLabel;

    @FXML
    StackPane itemStackPane;

    @FXML
    Label noItemLabel;
    @Getter
    private final WearableItemSlot slot;

    @FXML
    Button removeButton;

    Consumer<WearableItemSlot> installCallback;
    Consumer<WearableItemSlot> removeCallback;

    public InstallSlotTile(
            WearableItemSlot slot, Consumer<WearableItemSlot> installCallback, Consumer<WearableItemSlot> removeCallback
    ) {
        super();
        FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource(FXML_ADDRESS));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            log.error("Unable to load InstallSlotTile fxml. Exiting");
            Platform.exit();
            System.exit(1);
        }

        titleLabel.setText(slot.getSlotName());
        this.slot = slot;
        this.installCallback = installCallback;
        this.removeCallback = removeCallback;
        updateItemInfo();
    }

    void updateItemInfo() {
        var item = ItemsToInstall.getInstance().getWearables().getItemForSlot(slot);
        if(itemStackPane.getChildren().size() > 1){
            itemStackPane.getChildren().remove(1);
        }
        if (item != null) {
            var tile = new WearableItemResultTile(item.getBaseItem());
            itemStackPane.getChildren().add(tile);
            noItemLabel.setVisible(false);
            removeButton.setVisible(true);
        } else {
            noItemLabel.setVisible(true);
            removeButton.setVisible(false);
        }
    }

    @FXML
    void onInstallToSlot() {
        installCallback.accept(slot);
        updateItemInfo();
    }

    @FXML
    void onRemoveFromSlot(){
        removeCallback.accept(slot);
        updateItemInfo();
    }

}
