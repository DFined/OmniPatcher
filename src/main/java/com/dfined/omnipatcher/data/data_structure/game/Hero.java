package com.dfined.omnipatcher.data.data_structure.game;

import com.dfined.omnipatcher.data.annotations.PrimaryRegistryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hero implements RegistryEntry {
    @PrimaryRegistryKey
    String name;
    String modelPath;
    List<Item> items;
    HashMap<String, Item> defaultItems;
    HashMap<String, Slot> slots;

    public Hero(String name) {
        this.name = name;
        items = new ArrayList<>();
        defaultItems = new HashMap<>();
        slots = new HashMap<>();
        register();
    }

    public void addItem(Item item){
        this.items.add(item);
    }

    public void addDefaultItem(Item item){
        this.defaultItems.put(item.getItemSlot(),item);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public HashMap<String, Item> getDefaultItems() {
        return defaultItems;
    }

    public void setDefaultItems(HashMap<String, Item> defaultItems) {
        this.defaultItems = defaultItems;
    }

    public HashMap<String, Slot> getSlots() {
        return slots;
    }

    public void setSlots(HashMap<String, Slot> slots) {
        this.slots = slots;
    }


}
