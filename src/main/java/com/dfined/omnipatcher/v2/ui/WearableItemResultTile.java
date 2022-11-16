package com.dfined.omnipatcher.v2.ui;

import com.dfined.omnipatcher.application.gui.Selectable;
import com.dfined.omnipatcher.v2.filesystem.ValveResourceManager;
import com.dfined.omnipatcher.v2.model.WearableItemSlot;
import com.dfined.omnipatcher.v2.model.WearableItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.dfined.omnipatcher.v2.filesystem.GamePathConstants.ICONS_PATH_PREFIX;

@Slf4j
@Getter
public class WearableItemResultTile extends BorderPane implements Selectable {
    private static final String FXML_ADDRESS = "wearable_item_result_tile.fxml";

    private final WearableItem item;
    @Getter
    @Setter
    private WearableItemSlot slot;

    @FXML
    ImageView iconImage;
    @FXML
    Label itemNameLabel;
    @FXML
    Label rarityLabel;
    @FXML
    Label slotLabel;
    @FXML
    Label heroLabel;

    public WearableItemResultTile(WearableItem item) {
        super();
        this.item = item;
        FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource(FXML_ADDRESS));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            log.error("Unable to load ItemTile fxml. Exiting");
            Platform.exit();
            System.exit(1);
        }
        itemNameLabel.setText(item.getQualifiedName());

        var rarity = item.getRarity();
        rarityLabel.setText(rarity.name());
        rarityLabel.setTextFill(Color.color(rarity.getR(), rarity.getG(), rarity.getB()));

        slotLabel.setText(item.getSlot().getName());

        heroLabel.setText(item.getSlot().getHero().getQualifiedName());

        iconImage.setImage(ValveResourceManager.getIcon(ICONS_PATH_PREFIX + item.getImagePath()));
    }

    @Override
    public void onSelect() {
        setColor("888888");
    }

    @Override
    public void onDeselect() {
        setColor("bbbbbb");
    }

    private void setColor(String color){
        this.setStyle("-fx-background-color: " + color.toString());
        this.styleProperty().setValue(String.format("-fx-background-color : #%s", color));

    }
}
