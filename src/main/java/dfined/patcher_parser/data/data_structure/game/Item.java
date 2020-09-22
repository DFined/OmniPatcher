package dfined.patcher_parser.data.data_structure.game;

import dfined.patcher_parser.application.PatcherParser;
import dfined.patcher_parser.data.Data;
import dfined.patcher_parser.data.Registries;
import dfined.patcher_parser.data.ValveFormatDecompiler;
import dfined.patcher_parser.data.annotations.DefaultValue;
import dfined.patcher_parser.data.annotations.FieldDataRegex;
import dfined.patcher_parser.data.annotations.IgnoreDataMapping;
import dfined.patcher_parser.data.data_structure.HashDataMap;
import dfined.patcher_parser.filesystem.FileManager;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Item extends GameData {
    @IgnoreDataMapping
    static Logger log = LogManager.getLogger(Item.class);

    private static final String FILE_POSTFIX = "_c";
    private static final String DEFAULT_SLOT = "weapon";
    private static final String USED_BY_REGEX = "used_by_heroes/.*";
    private static final String VISUALS_LOC = "visuals/.*";

    private static final String ICONS_PATH_PREFIX = "/panorama/images/";
    private static final String ICONS_INPUT_EXTENSION = "_png.vtex_c";
    private static final String ICONS_OUTPUT_EXTENSION = ".png";

    @IgnoreDataMapping
    List<VisualModifier> visuals;

    String name;
    String itemDescription;
    @DefaultValue(value = DEFAULT_SLOT)
    String itemSlot;
    String modelPlayer;
    String imageInventory;
    String prefab;
    @FieldDataRegex(regex = USED_BY_REGEX, getKeys = true)
    String heroName;

    public Item(HashDataMap map) {
        super(map);
        List<HashDataMap> visualMaps = map.listTypeByRegex(VISUALS_LOC, HashDataMap.class);
        if (heroName != null) {
            Hero hero = Registries.get(Hero.class, Collections.singleton(heroName));
            visuals = visualMaps.stream()
                    .filter(vis -> hero != null)
                    .map(vMap -> new VisualModifier(vMap, hero))
                    .filter(VisualModifier::isAllowed)
                    .collect(Collectors.toList());
        }
    }

    public Image getIcon() {
        Image image = null;
        if (imageInventory != null) {
            String iconPath = ICONS_PATH_PREFIX + imageInventory;
            try {
                image = new Image(new FileInputStream(ValveFormatDecompiler.getResource(iconPath, ICONS_INPUT_EXTENSION, ICONS_OUTPUT_EXTENSION, true)));
            } catch (FileNotFoundException e) {
                log.warn(String.format("Unable to find file %s", iconPath), e);
            }
        }
        return image;
    }

    public void installItem() {
        Hero hero = Registries.get(Hero.class, Collections.singleton(heroName));
        HashMap<String, Slot> slots = hero.getSlots();
        Slot targetSlot = slots.get(getItemSlot());
        FileManager fileManager = PatcherParser.getFileManager();
        String outputPath = PatcherParser.getInstance().getSettings().getTempDir().getPath();
        fileManager.putIntoRepo(modelPlayer + FILE_POSTFIX, outputPath, targetSlot.getPath() + FILE_POSTFIX);

        //Install visuals
        visuals.forEach(visual -> fileManager.putIntoRepo(visual.getModifier() + FILE_POSTFIX, outputPath, visual.getAsset() + FILE_POSTFIX));
        Item defaultItem = hero.getDefaultItems().get(targetSlot.getName());

        HashDataMap defaultItemMap = Data.getGameData().getSingleType(defaultItem.internalBackingPath(), HashDataMap.class);
        HashMap<String, Object> thisItemMap = Data.getGameData().getSingleType(this.internalBackingPath(), HashDataMap.class);
        if (thisItemMap.containsKey("visuals")) {
            defaultItemMap.put("visuals", thisItemMap.get("visuals"));
        }
        defaultItemMap.put("model_player", thisItemMap.get("model_player"));
        defaultItemMap.put("portraits", thisItemMap.get("portraits"));

    }

    public String getName() {
        return name;
    }

    public String getItemSlot() {
        return itemSlot;
    }

    public String getModelPlayer() {
        return modelPlayer;
    }

    public String getHeroName() {
        return heroName;
    }

    public boolean isType(String type) {
        return this.prefab.equals(type);
    }
}

