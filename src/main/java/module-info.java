module OmniPatcher {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
    requires org.apache.commons.io;
    requires java.desktop;
    requires guava;
    requires zip4j;
    requires com.fasterxml.jackson.databind;
    exports com.dfined.omnipatcher.application;
    exports com.dfined.omnipatcher.filesystem;
    opens com.dfined.omnipatcher.application;
    opens com.dfined.omnipatcher.application.gui;
}