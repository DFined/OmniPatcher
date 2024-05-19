package com.dfined.omnipatcher.v2;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.dfined.omnipatcher.v2.filesystem.CachedVPKFileManager;
import com.dfined.omnipatcher.v2.filesystem.FileStore;
import com.dfined.omnipatcher.v2.filesystem.GamePathConstants;
import com.dfined.omnipatcher.v2.filesystem.OPFileUtil;
import com.dfined.omnipatcher.v2.format.ValveDumpsterFormatWriter;
import com.dfined.omnipatcher.v2.model.Hero;
import com.dfined.omnipatcher.v2.model.ItemType;
import com.dfined.omnipatcher.v2.model.WearableItem;
import com.dfined.omnipatcher.v2.model.WearableItemSlot;
import com.dfined.omnipatcher.v2.ui.GUI;
import com.dfined.omnipatcher.v2.ui.OmniPatcherUi;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class OmniPatcherV2 extends Application {
    public static final File dotaRootDir = new File("C:\\Program Files (x86)\\Steam\\steamapps\\common\\dota 2 beta");
    public static final File localCacheDir = new File("E:\\DFined\\Design\\ParserPatcher\\local");
    //File system manager
    public static CachedVPKFileManager FSM;

    private static GUI gui;

    //TODO 1 - save config, 2 - styles, 4 - Tiny stuff, 5 - Selected intsall queue info, 6 - preload vpk, 7 - export

    public void run() {
        try {
            String versionId = OPFileUtil.SFM.readFileAsString(dotaRootDir, "game\\bin\\built_from_cl.txt").trim();
            File vpk = new File(dotaRootDir, GamePathConstants.PAK_01_DIR_VPK_PATH);

            FSM = CachedVPKFileManager.create(vpk, localCacheDir, versionId);
            var heroesFile = FSM.getFileForPath(GamePathConstants.HERO_INFO_PATH, FileStore.CACHE);
            var itemsFile = FSM.getFileForPath(GamePathConstants.ITEMS_GAME_PATH, FileStore.CACHE);

            var blacklistedHeroNames = List.of("Version", "npc_dota_hero_base", "npc_dota_hero_target_dummy");
            var heroesRaw = OPFileUtil.readVDF(heroesFile);
            List<ALTNode> heroesList = heroesRaw.findNodesByPaths(Arrays.asList(new String[]{null}));
            heroesList = heroesList.stream()
                    .filter(heroNode -> !blacklistedHeroNames.contains(heroNode.getName()))
                    .toList();
            var heroes = StreamEx.of(heroesList)
                    .map(Hero::fromData)
                    .toMap(Hero::getName, Function.identity());
            Globals.initHeroes(heroes);

            Globals.ROOT_NODE = OPFileUtil.readVDF(itemsFile);

//            var itemsTempFile = new File("test.txt");
//            ValveDumpsterFormatWriter.write(itemsTempFile, Globals.ROOT_NODE, Map.of());
            var items = Globals.ROOT_NODE.findNodesByPaths(Arrays.asList("items", null)).stream()
                    .map(WearableItem::fromData)
                    .toList();
            var itemsList = DFUtil.streamExByType(items, WearableItem.class)
                    .filterBy(WearableItem::getType, ItemType.WEARABLE)
                    .toList();

            Globals.ITEMS.addAll(itemsList);
            var defaultItemsByHero = EntryStream.of(DFUtil.streamExByType(items, WearableItem.class)
                            .filterBy(WearableItem::getType, ItemType.DEFAULT_ITEM)
                            .groupingBy(WearableItem::getHero))
                    .mapValues(values -> DFUtil.groupMapify(values, WearableItem::getSlot))
                    .toMap();
            validateDefaultItems(heroes, defaultItemsByHero);
            Platform.runLater(() -> gui.addAndOpenScreen(new OmniPatcherUi()));
        } catch (Exception e) {
            throw new IllegalStateException(new ExceptionInfo().toString(), e);
        }
    }


    private static void validateDefaultItems(Map<String, Hero> heroes, Map<Hero, Map<WearableItemSlot, List<WearableItem>>> defaultItemsByHero) {
        for (Hero hero : heroes.values()) {
            for (WearableItemSlot slot : hero.getValidSlots().values()) {
                var defaults = defaultItemsByHero.get(hero).getOrDefault(slot, List.of());
                if (defaults.size() > 1) {
                    //This really, really shouldn't happen ever, but this is valve data soooo
                    System.out.printf("Error: more than 1 default item for hero %s and slot %s\n", hero.getName(), slot.getSlotName());
                    continue;
                }
                if (defaults.isEmpty()) {
                    System.out.printf("Error: no default items known for hero %s and slot %s\n", hero.getName(), slot.getSlotName());
                    continue;
                }
                Globals.DEFAULT_WEARABLE_ITEMS.put(slot, defaults.get(0));
            }
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        gui = new GUI(primaryStage);
        Thread thread = new Thread(this::run);
        thread.start();
    }
}
