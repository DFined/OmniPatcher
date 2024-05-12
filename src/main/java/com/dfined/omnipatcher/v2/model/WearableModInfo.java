package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.Globals;

import java.util.HashMap;
import java.util.Map;

public class WearableModInfo {
    Map<Hero, Map<WearableItemSlot, WearableItem>> availableHeroSlots = new HashMap<>();

    public WearableModInfo(){
        init();
    }

    public void init() {
        for(var hero : Globals.getHEROES().values()){
            availableHeroSlots.put(hero, new HashMap<>());
            for (var slot : hero.slots.values()) {
                availableHeroSlots.get(hero).put(slot, null);
            }
        }
    }

    public boolean isItemEnqueued(WearableItemSlot slot) {
        return availableHeroSlots.get(slot.getHero()).get(slot) != null;
    }

    public WearableItem install(WearableInstallInfo wearableInstallInfo) {
        return availableHeroSlots.get(wearableInstallInfo.getSlot().getHero()).put(
                wearableInstallInfo.getSlot(),
                wearableInstallInfo.item
        );
    }

    public void uninstall(WearableItemSlot slot) {
        availableHeroSlots.get(slot.getHero()).remove(slot);
    }
}
