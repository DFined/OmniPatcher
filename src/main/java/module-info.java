module OmniPatcher {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
    requires org.apache.commons.io;
    requires java.desktop;
    requires com.google.common;
    requires one.util.streamex;
    requires zip4j;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires jsr305;
    requires org.slf4j;
    requires org.controlsfx.controls;
    exports com.dfined.omnipatcher.application;
    exports com.dfined.omnipatcher.filesystem;
    exports com.dfined.omnipatcher.v2.model;
    opens com.dfined.omnipatcher.application;
    opens com.dfined.omnipatcher.application.gui;
    exports com.dfined.omnipatcher.v2;
    opens com.dfined.omnipatcher.v2;
    exports com.dfined.omnipatcher.v2.filesystem;
    opens com.dfined.omnipatcher.v2.ui;
}