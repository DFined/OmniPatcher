package dfined.patcher_parser.data;

import dfined.patcher_parser.application.ApplicationSettings;
import dfined.patcher_parser.application.Param;
import dfined.patcher_parser.data.data_structure.DataMap;
import dfined.patcher_parser.data.data_structure.DataPaths;
import dfined.patcher_parser.data.data_structure.HashDataMap;
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
        String tt = "/a/b/c/";
        String[] aa = tt.split("/");
        try {
            //Load the main index file
            gameData = IndexMapper.parseEntry(new BufferedLineReader(index));
            //Load the models index file
            modelsData = IndexMapper.parseEntry(new BufferedLineReader(models_index));
            //All Items maps
            List<DataMap> itemsData = gameData.listTypeByRegex(DataPaths.ITEMS_PATH, DataMap.class);
            //All Models map
            HashDataMap models = modelsData.getSingleType(DataPaths.HERO_NAMES_PATH, HashDataMap.class);
            //List of all hero names
            List<String> heroNames = new ArrayList<>();
            heroNames.addAll(models.keySet());
            //Create Hero objects
            heroes = heroNames.stream().map(Hero::new).collect(Collectors.toMap(Hero::getName, hero -> hero));
            //Parse base models into hero objects
            heroes.forEach(
                    (key, value) -> value.setModelPath(
                            ((HashMap<String, String>) models.get(key)).keySet().stream().findFirst().get()
                    )
            );
            //Create Item Objects
            items = itemsData.stream().map(dataMap -> new Item((HashDataMap) dataMap)).collect(Collectors.toList());
            //Populate Item lists in Hero objects
            items.stream()
                    .filter(item -> heroes.containsKey(item.getHeroName()) && item.isType(ItemTypes.WEARABLE_TYPE))
                    .forEach(item -> Registries.get(Hero.class, Collections.singleton(item.getHeroName())).addItem(item));
            //Populate Default Item lists in Hero objects
            items.stream()
                    .filter(item -> heroes.containsKey(item.getHeroName()) && item.isType(ItemTypes.DEFAULT_TYPE))
                    .forEach(item -> Registries.get(Hero.class, Collections.singleton(item.getHeroName())).addDefaultItem(item));
            //Populate available slots in hero objects
            heroes.values().forEach(hero -> {
                        hero.getDefaultItems().values().stream()
                                .map(item -> new Slot(item.getItemSlot(), item.getModelPlayer()))
                                .forEach(slot -> hero.getSlots().put(slot.getName(), slot));
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
