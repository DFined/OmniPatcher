package dfined.patcher_parser.data.data_structure.game;

import dfined.patcher_parser.application.PatcherParser;
import dfined.patcher_parser.application.gui.GUI;
import dfined.patcher_parser.application.gui.VisualModifier;
import dfined.patcher_parser.data.Data;
import dfined.patcher_parser.data.ValveFormatDecompiler;
import dfined.patcher_parser.filesystem.FileManager;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Item {
    private static final String FILE_POSTFIX = "_c";
    static Logger log = LogManager.getLogger(GUI.class);
    private static final String DEFAULT_SLOT = "weapon";
    private static final String USED_BY_LOC = "used_by_heroes";
    private static final String NAME_LOC = "name";
    private static final String DESCRIPTION_LOC = "item_description";
    private static final String SLOT_LOC = "item_slot";
    private static final String IMAGE_LOC = "image_inventory";
    private static final String MODEL_LOC = "model_player";
    private static final String PREFAB_LOC = "prefab";
    private static final String VISUALS_LOC = "visuals";

    private static final String ICONS_PATH_PREFIX = "/panorama/images/";
    private static final String ICONS_INPUT_EXTENSION = "_png.vtex_c";
    private static final String ICONS_OUTPUT_EXTENSION = ".png";

    String hero = null;
    String name;
    String description;
    String slot;
    String model;
    String iconName;
    String type;
    String selfKey;
    ArrayList<VisualModifier> visuals;


    public Item(String key, HashMap<String, Object> map) {
        this.selfKey = key;
        Object usedBy = map.get(USED_BY_LOC);
        if (usedBy instanceof HashMap) {
            hero = ((HashMap<String, String>) usedBy).keySet().stream().findFirst().orElse(null);
        }
        name = (String) map.get(NAME_LOC);
        description = (String) map.get(DESCRIPTION_LOC);
        slot = (String) map.get(SLOT_LOC);
        slot = slot == null ? DEFAULT_SLOT : slot;
        model = (String) map.get(MODEL_LOC);
        iconName = (String) map.get(IMAGE_LOC);
        type = (String) map.get(PREFAB_LOC);

        if (map.get(VISUALS_LOC) instanceof HashMap) {
            Hero heroObj = getHeroObject();
            if (heroObj != null) {
                visuals = new ArrayList<>();
                Collection<Object> visualList = ((HashMap<String, Object>) map.get(VISUALS_LOC)).values();
                visuals.addAll(visualList.stream()
                        .filter(obj -> obj instanceof HashMap)
                        .map(vMap -> VisualModifier.makeModifier((HashMap<String, Object>) vMap, heroObj))
                        .filter(visualModifier -> visualModifier != null).collect(Collectors.toList()));
            }
        }
    }


    public String getName() {
        return name;
    }

    public String getHero() {
        return hero;
    }

    public String getDescription() {
        return description;
    }

    public String getSlot() {
        return slot;
    }

    public Image getIcon() {
        Image image = null;
        if (iconName != null) {
            FileManager manager = PatcherParser.getFileManager();
            String iconPath = ICONS_PATH_PREFIX + iconName;
            try {
                image = new Image(new FileInputStream(ValveFormatDecompiler.getResource(iconPath, ICONS_INPUT_EXTENSION, ICONS_OUTPUT_EXTENSION, true)));
            } catch (FileNotFoundException e) {
                log.warn(String.format("Unable to find file %s", iconPath), e);
            }
        }
        return image;
    }

    public String getModel() {
        return model;
    }

    public boolean heroEqual(String hero) {
        return this.hero != null ? this.hero.equals(hero) : false;
    }

    public boolean isType(String type) {
        return this.type != null ? this.type.equals(type) : false;
    }

    public void installItem() {
        //TODO Rewrite all of this horror after POC
        //Placeholder getter
        Hero hero = Data.getHeroes().get(getHero());
        List<Slot> slots = hero.getSlots();
        Slot targetSlot = slots.stream().filter(slot -> slot.getName().equals(getSlot())).findFirst().get();
        FileManager fileManager = PatcherParser.getFileManager();
        String outputPath = PatcherParser.getInstance().getSettings().getTempDir().getPath();
        fileManager.putIntoRepo(getModel() + FILE_POSTFIX, outputPath, targetSlot.getPath() + FILE_POSTFIX);



        //Install visuals
        visuals.forEach(visual -> {
            fileManager.putIntoRepo(visual.getModifier() + FILE_POSTFIX, outputPath, visual.getAsset() + FILE_POSTFIX);
                }
        );
        Item defaultItem = hero.getDefaultItems().stream().filter(item -> item.slot.equals(targetSlot.name)).findAny().get();
        HashMap<String, Object> defaultItemMap = (HashMap<String, Object>) ((HashMap)Data.getGameData().getMap().get("items")).get(defaultItem.selfKey);
        HashMap<String, Object> thisItemMap = (HashMap<String, Object>) ((HashMap)Data.getGameData().getMap().get("items")).get(selfKey);
        if(thisItemMap.containsKey("visuals")) {
            defaultItemMap.put("visuals", thisItemMap.get("visuals"));
        }
        defaultItemMap.put("model_player",thisItemMap.get("model_player"));
        defaultItemMap.put("portraits",thisItemMap.get("portraits"));

    }

    public Hero getHeroObject() {
        return Data.getHeroes().get(hero);
    }

}

