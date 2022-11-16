package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.DFUtil;
import com.dfined.omnipatcher.v2.OmniPatcherV2;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.dfined.omnipatcher.v2.filesystem.FileStore;
import com.dfined.omnipatcher.v2.filesystem.GamePathConstants;
import com.dfined.omnipatcher.v2.filesystem.ValveResourceManager;
import com.dfined.omnipatcher.v2.format.ValveDumpsterFormatWriter;
import com.dfined.omnipatcher.v2.format.raw.InstallableItem;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import lombok.Getter;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ItemsToInstall {
    @Getter
    private static ItemsToInstall instance;
    private final HashMap<Hero, HashMap<WearableItemSlot, WearableItem>> wearableItems = new HashMap<>();

    private final ModInfo<WearableItemSlot, WearableItem, WearableInstallInfo> wearables = new ModInfo<>();

    private final VDFObjectNode srcNode;

    public ItemsToInstall(VDFObjectNode srcNode) {
        this.srcNode = srcNode;
        instance = this;
    }

    public boolean isItemEnqueued(WearableItemSlot slot){
        return wearableItems.containsKey(slot.getHero()) && wearableItems.get(slot.getHero()).containsKey(slot);
    }

    public WearableItem install(WearableItem item) {
        return DFUtil.getOrInit(wearableItems, item.getHero(), HashMap::new).put(item.getSlot(), item);
    }

    public WearableItem install(WearableItemSlot slot, WearableItem item) {
        return DFUtil.getOrInit(wearableItems, slot.getHero(), HashMap::new).put(slot, item);
    }

    public void uninstall(WearableItemSlot slot) {
        if(wearableItems.containsKey(slot.getHero())){
            wearableItems.get(slot.getHero()).remove(slot);
        }
    }

    public Map<WearableItemSlot, ? extends InstallableItem<WearableItemSlot>> getWearableItems() {
        return StreamEx.of(wearableItems.values())
                .flatMapToEntry(items -> EntryStream.of(items).toMap())
                .mapValues(item -> (InstallableItem<WearableItemSlot>) item)
                .nonNullKeys()
                .nonNullValues()
                .toMap();
    }

    public void performInstall(File dotaRootDir) throws IOException {
        OmniPatcherV2.FSM.clearDir(FileStore.TMP);
        var items = getWearableItems();
        EntryStream.of(items).forKeyValue((k, v) -> v.customInstall(k));
        try {
            var wearables = EntryStream.of(getWearableItems())
                    .mapKeys(WearableItemSlot::getDefaultItem)
                    .nonNullKeys()
                    .mapKeys(WearableItem::getVdfObject)
                    .nonNullKeys()
                    .mapKeys(VDFObjectNode::getPath)
                    .toMap();
            ValveDumpsterFormatWriter.write(OmniPatcherV2.FSM.getInDir(GamePathConstants.ITEMS_GAME_PATH, FileStore.TMP), srcNode, wearables);
        } catch (IOException e) {
            throw new IllegalStateException(new ExceptionInfo().toString(), e);
        }
        ValveResourceManager.installDirectoryToGame(OmniPatcherV2.FSM.getDir(FileStore.TMP), new File(dotaRootDir, GamePathConstants.MODS_VPK_PATH));
        ValveResourceManager.convertGameinfo(new File(dotaRootDir, GamePathConstants.GAMEINFO_PATH));
    }

    private void randomizeInstall(HashMap<String, Hero> heroes, Map<Hero, Map<WearableItemSlot, List<WearableItem>>> itemsByHero) {
        for (Hero hero : heroes.values()) {
            for (WearableItemSlot slot : hero.getValidSlots().values()) {
                var item = DFUtil.randomOfList(itemsByHero.get(hero).getOrDefault(slot, List.of()));
                if (item == null) {
                    System.out.printf("Error: no items known for hero %s and slot %s\n", hero.getName(), slot.getName());
                } else {
                    install(item);
                }
            }
        }
    }
}
