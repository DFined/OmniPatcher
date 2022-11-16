package com.dfined.omnipatcher.data;

import com.dfined.omnipatcher.data.data_structure.game.Hero;
import com.dfined.omnipatcher.data.data_structure.game.Item;
import com.dfined.omnipatcher.data.data_structure.game.ItemTypes;
import com.dfined.omnipatcher.data.data_structure.game.Slot;
import com.dfined.omnipatcher.v2.io.BufferedLineReader;
import com.dfined.omnipatcher.data.data_structure.io.IndexMapper;
import com.dfined.omnipatcher.filesystem.FileManagerUtils;
import com.dfined.omnipatcher.application.ApplicationSettings;
import com.dfined.omnipatcher.application.Param;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.dfined.omnipatcher.data.data_structure.DataMap;
import com.dfined.omnipatcher.data.data_structure.DataPaths;
import com.dfined.omnipatcher.data.data_structure.HashDataMap;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Data {
    private static final String HERO_PATCH_PATH = "F:\\DFined\\Java\\ParserPatcher\\data\\dicts\\hero_patch_file.json";
    private static final Logger log = LogManager.getLogger(Data.class.getSimpleName());
    private static Map<String, Item> items;
    private static Map<String, Hero> heroes;
    private static DataMap gameData;

    public static void setup(ApplicationSettings settings) throws IOException {
        File index = FileManagerUtils.fromSourceToLocal(Param.INDEX_PATH);
        File models_index = FileManagerUtils.fromSourceToLocal(Param.MODELS_PATH);
        DataMap modelsData;
        try {
            items = new HashMap<>();
            //Load the main index file
            gameData = IndexMapper.parseEntry(new BufferedLineReader(index));
            //Load the models index file
            modelsData = IndexMapper.parseEntry(new BufferedLineReader(models_index));
            //All Items maps
            List<DataMap> itemsData = gameData.listTypeByRegex(DataPaths.ITEMS_PATH, DataMap.class);
            //All Models map
            HashMap<String,String> patchHeroNames = FileManagerUtils.readFileAsHashMap(HERO_PATCH_PATH, new ExceptionInfo());
            HashDataMap models = modelsData.getSingleType(DataPaths.HERO_NAMES_PATH, HashDataMap.class);
            for(String name: patchHeroNames.keySet()){
                HashDataMap map = new HashDataMap(name, "");
                map.put(patchHeroNames.get(name),name);
                models.put(name, map);
            }
            //List of all hero names
            List<String> heroNames = new ArrayList<>();
            heroNames.addAll(models.keySet());
            //patch new heroes into the data
            heroNames.addAll(Arrays.asList());
            //Create Hero objects
            heroes = heroNames.stream().map(Hero::new).collect(Collectors.toMap(Hero::getName, hero -> hero));
            //Parse base models into hero objects
            heroes.forEach(
                    (key, value) -> value.setModelPath(
                            ((HashMap<String, String>) models.get(key)).keySet().stream().findFirst().get()
                    )

            );
            //Create Item Objects
            itemsData.stream().map(dataMap -> new Item((HashDataMap) dataMap)).forEach(item -> {
                String key = item.getName();
                while (items.containsKey(key)) {
                    key += "Duplicate";
                }
                items.put(key, item);
            });
            //Populate Default Item lists in Hero objects
            items.values().stream()
                    .filter(item -> heroes.containsKey(item.getHeroName()) && item.isType(ItemTypes.DEFAULT_TYPE))
                    .forEach(item -> Registries.get(Hero.class, Collections.singleton(item.getHeroName())).addDefaultItem(item));
            //Populate available slots in hero objects
            heroes.values().forEach(hero -> {
                        hero.getDefaultItems().values().stream()
                                .map(item -> new Slot(item.getItemSlot(), item.getModelPlayer()))
                                .forEach(slot -> hero.getSlots().put(slot.getName(), slot));
                    }
            );
            //Populate Item lists in Hero objects
            items.values().stream()
                    .filter(item -> heroes.containsKey(item.getHeroName()) && item.isType(ItemTypes.WEARABLE_TYPE))
                    .filter(item -> {
                                Hero hero = Registries.get(Hero.class, Collections.singleton(item.getHeroName()));
                                if (hero.getSlots().get(item.getItemSlot()) != null) {
                                    if (hero.getSlots().get(item.getItemSlot()).getPath() != null) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                    )
                    .forEach(item -> Registries.get(Hero.class, Collections.singleton(item.getHeroName())).addItem(item));
            //Sort each Hero's Item list alphabetically
            heroes.values().forEach(hero -> hero.getItems().sort(Comparator.comparing(Item::getName)));

        } catch (IOException e) {
            log.fatal("Unable to read data index. Exiting.", e);
            Platform.exit();
            System.exit(1);
        }
    }

    public static Map<String, Item> getItems() {
        return items;
    }

    public static Map<String, Hero> getHeroes() {
        return heroes;
    }

    public static DataMap getGameData() {
        return gameData;
    }
}
