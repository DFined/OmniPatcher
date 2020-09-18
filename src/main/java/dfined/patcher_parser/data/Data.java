package dfined.patcher_parser.data;

import dfined.patcher_parser.application.ApplicationSettings;
import dfined.patcher_parser.application.Param;
import dfined.patcher_parser.data.data_structure.DataMap;
import dfined.patcher_parser.data.data_structure.DataPaths;
import dfined.patcher_parser.data.data_structure.game.Hero;
import dfined.patcher_parser.data.data_structure.game.Item;
import dfined.patcher_parser.data.data_structure.game.ItemTypes;
import dfined.patcher_parser.data.data_structure.game.Slot;
import dfined.patcher_parser.data.data_structure.io.BufferedLineReader;
import dfined.patcher_parser.data.data_structure.io.IndexMapper;
import dfined.patcher_parser.filesystem.FileSystem;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Data {

    private static final Logger log = LogManager.getLogger(Data.class.getSimpleName());
    private static List<Item> items;
    private static Map<String, Hero> heroes;
    private static DataMap gameData;

    public static void setup(ApplicationSettings settings) {
        File index = FileSystem.getManager(settings.getFileManager()).getFromLocal(Param.INDEX_PATH);
        File models_index = FileSystem.getManager(settings.getFileManager()).getFromLocal(Param.MODELS_PATH);
        DataMap modelsData;
        try {
            //Load the main index file
            gameData = new DataMap((HashMap<String, Object>) IndexMapper.parseEntry(new BufferedLineReader(index)).getValue());
            //Load the models index file
            modelsData = new DataMap((HashMap<String, Object>) IndexMapper.parseEntry(new BufferedLineReader(models_index)).getValue());
            //All Items maps
            DataMap itemsData = gameData.getByPath(DataPaths.ITEMS_PATH);
            //All Models map
            DataMap models = modelsData.getByPath(DataPaths.MODELS_PATH);
            //List of all hero names
            List<String> heroNames = new ArrayList<>();
            heroNames.addAll(models.getMap().keySet());
            //Create Hero objects
            heroes = heroNames.stream().map(Hero::new).collect(Collectors.toMap(Hero::getName, hero -> hero));
            //Parse base models into hero objects
            heroes.forEach(
                    (key, value) -> value.setModelPaths(
                            ((HashMap<String, String>) models.getMap().get(key)).keySet().stream().findFirst().get()
                    )
            );
            //Create Item Objects
            items = itemsData.getMap().entrySet().stream().map(entry -> new Item(entry.getKey(), (HashMap<String, Object>) entry.getValue())).collect(Collectors.toList());
            //Populate Item lists in Hero objects
            items.forEach(item -> {
                        if (heroes.containsKey(item.getHero()) && item.isType(ItemTypes.WEARABLE_TYPE)) {
                            heroes.get(item.getHero()).addItem(item);
                        }
                    }
            );

            items.forEach(item -> {
                        if (heroes.containsKey(item.getHero()) && item.isType(ItemTypes.DEFAULT_TYPE)) {
                            heroes.get(item.getHero()).addDefaultItem(item);
                        }
                    }
            );

            //Populate available slots in hero objects
            heroes.values().forEach(hero -> {
                        hero.getDefaultItems().stream()
                                .map(item -> {
                                            String model = item.getModel();
                                            return new Slot(item.getSlot(), model);
                                        }
                                )
                                .forEach(slot -> hero.getSlots().add(slot));
                    }
            );

        } catch (IOException e) {
            log.fatal("Unable to read data index. Exiting.", e);
            Platform.exit();
        }
    }

    public static List<Item> getItems() {
        return items;
    }

    public static Map<String, Hero> getHeroes() {
        return heroes;
    }

    public static DataMap getGameData() {
        return gameData;
    }
}
