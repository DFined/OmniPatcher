package com.dfined.omnipatcher.application.gui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter

public class VBoxWithSelection<T extends Pane & Selectable> extends VBox implements ContainerWithSelection<T> {
    private final Consumer<T> onSelectCallback;

    T selected;

    public VBoxWithSelection(Consumer<T> onSelectCallback) {
        super();
        this.onSelectCallback = onSelectCallback;
    }

    @Override
    public void add(T value) {
        this.getChildren().add(value);
    }

    @Override
    public void onSelect(T value) {
        onSelectCallback.accept(value);
    }
}
