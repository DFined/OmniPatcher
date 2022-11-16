package com.dfined.omnipatcher.v2.format.mapper;

import com.dfined.omnipatcher.v2.DFUtil;
import com.dfined.omnipatcher.v2.Globals;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import com.dfined.omnipatcher.v2.model.*;

import java.util.List;

public class WearableItemMapper extends Mapper<WearableItem> {
    public static final WearableItemMapper INSTANCE = new WearableItemMapper();
    @Override
    public WearableItem map(VDFObjectNode node) {
        var prefabKey = node.getValue("prefab");
        var prefab = DFUtil.safeGetEnum(prefabKey, ItemType.class);

        var heroName = node.getNamesForPaths("used_by_heroes/.*");
        if(heroName.isEmpty()){
            System.out.println("Item without hero! " +  node.getName());
            return null;
        }
        var hero = Globals.getHero(heroName.get(0));
        if(hero==null){
            System.out.println("Item with unknown hero! " +  node.getName());
            return null;
        }

        var slotName = node.getValueOrDefault("item_slot", hero.getUnknownSlot().getName());
        var slot = hero.getSlots().get(slotName);
        if(slot == null){
            slot = hero.getUnknownSlot();
        }

        var visuals = AutoMapper.lookup(VisualModifier.class, node, "visuals/.*", List.of("styles", "alternate_icons"));

        boolean accepted = Integer.parseInt(node.getValueOrDefault("workshop_accepted", "1")) > 0;

        return WearableItem.builder()
                .type(prefab)
                .name(node.getValueOrDefault("item_name", "UnknownItemName"))
                .qualifiedName(node.getValueOrDefault("name", "UnknownName"))
                .rarity(DFUtil.getEnumWithDefault(node.getValueOrDefault("item_rarity", null), ItemRarity.COMMON))
                .modelPath(node.getValueOrDefault("model_player", null))
                .imagePath(node.getValueOrDefault("image_inventory",null))
                .slot(slot)
                .visualModifiers(visuals)
                .vdfObject(node)
                .workshopAccepted(accepted)
                .build();
    }
}
