package com.dfined.omnipatcher.data.data_structure.game;

import com.dfined.omnipatcher.data.Session;
import com.dfined.omnipatcher.data.annotations.FieldDataRegex;
import com.dfined.omnipatcher.data.annotations.IgnoreDataMapping;
import com.dfined.omnipatcher.data.data_structure.io.BufferedLineWriter;
import com.dfined.omnipatcher.data.data_structure.io.IndexWriter;
import com.dfined.omnipatcher.application.ApplicationSettings;
import com.dfined.omnipatcher.application.Param;
import com.dfined.omnipatcher.application.OmniPatcher;
import com.dfined.omnipatcher.data.Data;
import com.dfined.omnipatcher.data.Registries;
import com.dfined.omnipatcher.filesystem.ValveResourceManager;
import com.dfined.omnipatcher.data.annotations.DefaultValue;
import com.dfined.omnipatcher.data.data_structure.HashDataMap;
import com.dfined.omnipatcher.filesystem.FileManager;
import com.dfined.omnipatcher.filesystem.FileManagerUtils;
import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
                image = new Image(new FileInputStream(ValveResourceManager.getResource(iconPath, ICONS_INPUT_EXTENSION, ICONS_OUTPUT_EXTENSION, true)));
            } catch (IOException e) {
                log.warn(String.format("Error loading icon '%s'", iconPath), e);
            }
        }
        return image;
    }

    public void installItem() throws IOException {
        Hero hero = Registries.get(Hero.class, Collections.singleton(heroName));
        HashMap<String, Slot> slots = hero.getSlots();
        Slot targetSlot = slots.get(getItemSlot());
        FileManager fileManager = OmniPatcher.getFileManager();
        File outputPath = OmniPatcher.getInstance().getSettings().getTempDir();

        //Install main model file if it exists. It might not for items such as personas
        if (modelPlayer != null && targetSlot.getPath()!=null) {
            FileManagerUtils.fromSourceToRepo(modelPlayer + FILE_POSTFIX, outputPath, targetSlot.getPath() + FILE_POSTFIX);
        }else{
            System.out.println();
        }

        //Install visuals
        for (VisualModifier visual : visuals) {
            if(visual.getAsset()!=null) {
                FileManagerUtils.fromSourceToRepo(visual.getModifier() + FILE_POSTFIX, outputPath, visual.getAsset() + FILE_POSTFIX);
            }else{
                System.out.println();
            }
        }

        Item defaultItem = hero.getDefaultItems().get(targetSlot.getName());

        HashDataMap defaultItemMap = Data.getGameData().getSingleType(defaultItem.internalBackingPath(), HashDataMap.class);
        HashMap<String, Object> thisItemMap = Data.getGameData().getSingleType(this.internalBackingPath(), HashDataMap.class);
        if (thisItemMap.containsKey("visuals")) {
            defaultItemMap.put("visuals", thisItemMap.get("visuals"));
        }
        if (thisItemMap.containsKey("model_player")) {
            defaultItemMap.put("model_player", thisItemMap.get("model_player"));
        }
        if (thisItemMap.containsKey("portraits")) {
            defaultItemMap.put("portraits", thisItemMap.get("portraits"));
        }

    }

    public static void installAll(boolean silent) {
        File repo = OmniPatcher.getInstance().getSettings().getTempDir();
        String errorMsg = "";
        if (!repo.exists()) {
            repo.mkdir();
        }
        if (repo.isDirectory()) {
            String[] files = repo.list();
            int clear = JOptionPane.YES_OPTION;
            if (files.length != 0) {
                if(!silent) {
                    clear = JOptionPane.showConfirmDialog(null, "Selected repo is not empty. Clear?", "Clear Repo?", JOptionPane.YES_NO_OPTION);
                }
            }
            if (clear == JOptionPane.YES_OPTION) {
                try {
                    FileUtils.cleanDirectory(repo);
                    for (Item item : Session.getToInstall().stream().map(name -> Data.getItems().get(name)).collect(Collectors.toList())) {
                        item.installItem();
                    }

                    FileManager manager = OmniPatcher.getFileManager();
                    ApplicationSettings settings = OmniPatcher.getInstance().getSettings();
                    File file = manager.createFileInRepo(settings.getTempDir(), Param.INDEX_PATH);
                    IndexWriter.writeObject(new BufferedLineWriter(file), "items_game", (HashDataMap) Data.getGameData());

                    if (settings.getVpkDir().exists()) {
                        FileUtils.cleanDirectory(settings.getVpkDir());
                    } else {
                        settings.getVpkDir().mkdir();
                    }
                    ValveResourceManager.installDirectoryToGame(settings.getTempDir(), settings.getVpkFile());
                    ValveResourceManager.convertGameinfo(settings.getGameinfoFile());
                    if(!silent) {
                        JOptionPane.showMessageDialog(null, "Mods installed.", "Mods Installed", JOptionPane.INFORMATION_MESSAGE);
                    }
                    return;
                } catch (IOException e) {
                    errorMsg = "Encountered filesystem error during install. " + e.getMessage();
                    log.warn(String.format("Encountered filesystem error during install. '%s'", errorMsg));
                }
            }
        } else {
            errorMsg = String.format("Repo is not a directory: '%s'", repo.getPath());
        }
        JOptionPane.showMessageDialog(null, errorMsg, "Error installing mods", JOptionPane.ERROR_MESSAGE);
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

