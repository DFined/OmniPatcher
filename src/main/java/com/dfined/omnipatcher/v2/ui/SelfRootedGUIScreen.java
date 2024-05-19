package com.dfined.omnipatcher.v2.ui;

import com.dfined.omnipatcher.v2.OmniPatcherV2;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public interface SelfRootedGUIScreen extends GUIScreen {
    @Override
    default Parent create(){
        String adress = getFXMLAddress();
        var res = OmniPatcherV2.class.getClassLoader().getResource(adress);
        FXMLLoader loader = new FXMLLoader(res);
        loader.setRoot(this);
        loader.setController(this);
        try {
            return loader.load();
        } catch (IOException e) {
            Logger log = LogManager.getLogger(OmniPatcherV2.class);
            log.fatal("Unable to load GUI screen fxml. Exiting",e);
            Platform.exit();
            System.exit(1);
        }
        return null;
    }

    String getFXMLAddress();
}
