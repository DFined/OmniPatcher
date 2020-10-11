package dfined.omnipatcher.application.gui;

import dfined.omnipatcher.data.data_structure.game.Item;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class StartupTile extends VBox {
    Logger log = LogManager.getLogger(StartupTile.class);
    public StartupTile() {
        super();
        FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("application/StartupTile.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            log.fatal("Unable to load ItemTile fxml. Exiting");
            Platform.exit();
            System.exit(1);
        }
    }
}
