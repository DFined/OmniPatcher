package dfined.patcher_parser.data.data_structure.game;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    String name;
    String modelPath;
    List<Item> items;
    List<Item> defaultItems;
    List<Slot> slots;

    public Hero(String name) {
        this.name = name;
        items = new ArrayList<>();
        defaultItems = new ArrayList<>();
        slots = new ArrayList<>();
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

    public void setModelPaths(String modelPaths) {
        this.modelPath = modelPaths;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item){
        this.items.add(item);
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public void addDefaultItem(Item item){
        this.defaultItems.add(item);
    }

    public List<Item> getDefaultItems() {
        return defaultItems;
    }
}
