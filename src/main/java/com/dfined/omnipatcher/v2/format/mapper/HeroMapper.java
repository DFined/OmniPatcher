package com.dfined.omnipatcher.v2.format.mapper;

import com.dfined.omnipatcher.v2.DFUtil;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import com.dfined.omnipatcher.v2.model.Hero;
import com.dfined.omnipatcher.v2.model.WearableItemSlot;

import java.util.ArrayList;

public class HeroMapper extends Mapper<Hero> {
    public static final String MODEL_BASE_NAME = "Model";
    public static final String QUALIFIED_NAME_PATH = "workshop_guide_name";

    public static final HeroMapper INSTANCE = new HeroMapper();

    @Override
    public Hero map(VDFObjectNode node) {
        var path = node.get(MODEL_BASE_NAME);
        int i = 1;
        var models = new ArrayList<String>();
        while (path != null) {
            models.add(path.requireValue(""));
            path = node.get(MODEL_BASE_NAME + i ++);
        }

        var hero = Hero.builder()
                .name(node.getName())
                .qualifiedName(node.get(QUALIFIED_NAME_PATH).requireValue("ERROR"))
                .models(models)
                .build();

        var slots = DFUtil.mapify(node.getObjectsForPaths("ItemSlots/.*", new SlotMapper(hero)), WearableItemSlot::getName);
        hero.setSlots(slots);
        var defaultSlot = slots.containsKey("weapon") ? slots.get("weapon") : new WearableItemSlot(hero, WearableItemSlot.UNKNOWN_SLOT, "0", -1, null);
        hero.setUnknownSlot(defaultSlot);
        return hero;
    }
}
