package dfined.patcher_parser.application.gui;

import dfined.patcher_parser.data.data_structure.game.Item;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ItemTile extends HBox {
    Logger log = LogManager.getLogger(ItemTile.class);
    @FXML
    ImageView icon;
    @FXML
    Label itemName;
    @FXML
    Label heroName;
    @FXML
    Label slotName;
    @FXML
    Button install;
    Item item;


    public ItemTile(Item item) {
        super();
        FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("application/ItemTile.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            log.fatal("Unable to load ItemTile fxml. Exiting");
            Platform.exit();
        }
        this.icon.setImage(item.getIcon());
        this.itemName.setText(item.getName());
        this.heroName.setText(item.getHeroName());
        this.slotName.setText(item.getItemSlot());
        this.item = item;
        this.install.addEventFilter(ActionEvent.ACTION, action -> installItem());
    }

    @FXML
    public void installItem(){
        item.installItem();
    }
}
