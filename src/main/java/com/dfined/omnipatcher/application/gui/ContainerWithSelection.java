package com.dfined.omnipatcher.application.gui;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.List;

public interface ContainerWithSelection<T extends Pane & Selectable> {
    T getSelected();
    void setSelected(T value);

    void add(T value);
    void onSelect(T value);

    default void select(){
        var sel = getSelected();
        if(sel != null){
            sel.onSelect();
        }
    }

    default void deselect(){
        var sel = getSelected();
        if(sel != null){
            sel.onDeselect();
            setSelected(null);
        }
    }

    default void addSelectables(T value) {
        value.addEventHandler(MouseEvent.ANY, event -> mouseEventHandler(event, value));
        add(value);
    }

    default void addAllSelectables(List<T> values) {
        values.forEach(this::addSelectables);
    }

    default void mouseEventHandler(MouseEvent event, T value) {
        if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED) || event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            deselect();
            setSelected(value);
            select();
            onSelect(value);
        }
    }
}
