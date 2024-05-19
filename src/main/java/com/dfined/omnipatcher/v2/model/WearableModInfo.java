package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.dalt.Tuple;
import com.dfined.omnipatcher.v2.Globals;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WearableModInfo {
    Map<Hero, Map<WearableItemSlot, WearableInstallInfo>> availableHeroSlots = new HashMap<>();

    public WearableModInfo() {
        init();
    }

    public void init() {
        for (var hero : Globals.getHEROES().values()) {
            availableHeroSlots.put(hero, new HashMap<>());
            for (var slot : hero.slots.values()) {
                availableHeroSlots.get(hero).put(slot, null);
            }
        }
    }

    public WearableInstallInfo getItemForSlot(WearableItemSlot slot) {
        return availableHeroSlots.get(slot.getHero()).get(slot);
    }

    public void install(WearableInstallInfo wearableInstallInfo) {
        availableHeroSlots.get(wearableInstallInfo.getSlot().getHero()).put(
                wearableInstallInfo.getSlot(),
                wearableInstallInfo
        );
    }

    public void uninstall(WearableItemSlot slot) {
        availableHeroSlots.get(slot.getHero()).remove(slot);
    }


    private EntryStream<WearableItemSlot, WearableInstallInfo> generateItemStream(){
        return StreamEx.of(availableHeroSlots.values())
                .flatMap(EntryStream::of)
                .mapToEntry(Map.Entry::getKey, Map.Entry::getValue);
    }

    public void doCustomInstall() {
        generateItemStream()
                .nonNullValues()
                .forKeyValue((slot, item) -> item.customInstall(slot));
    }

    /**
     * Generate the overrides map for writing the ALT data back into the items_game file
     */
    public Map<List<String>, List<ALTNode>> getOverrides() {
        return generateItemStream()
                .nonNullValues()
                .flatMapKeyValue((slot, item) -> EntryStream.of(item.toALTOverride(slot)))
                .toMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
