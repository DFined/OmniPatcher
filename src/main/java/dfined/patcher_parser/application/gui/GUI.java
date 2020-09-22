package dfined.patcher_parser.application.gui;

import dfined.patcher_parser.application.ApplicationSettings;
import dfined.patcher_parser.application.Param;
import dfined.patcher_parser.application.PatcherParser;
import dfined.patcher_parser.data.Data;
import dfined.patcher_parser.data.data_structure.HashDataMap;
import dfined.patcher_parser.data.data_structure.game.Item;
import dfined.patcher_parser.data.data_structure.io.BufferedLineWriter;
import dfined.patcher_parser.data.data_structure.io.IndexWriter;
import dfined.patcher_parser.filesystem.FileManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class GUI {
    Logger log = LogManager.getLogger(GUI.class);
    @FXML
    Button installAll;

    @FXML
    VBox allItemsBox;

    @FXML
    ComboBox heroSelector;

    public void init() {
        //Hero selector setup
        List<String> heroes = new ArrayList<>();
        heroes.addAll(Data.getHeroes().keySet());
        Collections.sort(heroes);
        heroSelector.setItems(FXCollections.observableList(heroes));
        heroSelector.getSelectionModel().select(0);
    }

    static final GUI INSTANCE = new GUI();

    public GUI() {
    }

    public static Pane createItemTile(Item item) {
        return new ItemTile(item);
    }

    @FXML
    public void onHeroSelect() {
        allItemsBox.getChildren().subList(1,allItemsBox.getChildren().size()).clear();
        Data.getHeroes().get(selectedHero()).getItems().forEach((item) -> allItemsBox.getChildren().add(GUI.createItemTile(item)));
    }

    @FXML
    public void onInstallAll() {
        FileManager manager = PatcherParser.getFileManager();
        ApplicationSettings settings = PatcherParser.getInstance().getSettings();
        File file = manager.getFromRepo(settings.getTempDir().getPath(), Param.INDEX_PATH);
        IndexWriter.writeObject(new BufferedLineWriter(file), "items_game", (HashDataMap) Data.getGameData());
    }


    public String selectedHero() {
        return (String) heroSelector.getSelectionModel().getSelectedItem();
    }



}
