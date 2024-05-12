package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.DFUtil;
import com.fasterxml.jackson.databind.util.LinkedNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@EqualsAndHashCode
public class Hero {
    private static final List<String> BLACKLISTED_SLOTS = List.of(
            "ability_effects_1", "ability_effects_2", "ability_effects_3", "ability_effects_4", "ability_effects_5", "ability_effects_6"
    );
    String name;
    String qualifiedName;
    List<String> models;
    @EqualsAndHashCode.Exclude
    Map<String, WearableItemSlot> slots;

    public HashMap<String, WearableItemSlot> getValidSlots() {
        return DFUtil.filter(slots, WearableItemSlot::isValidSlot);
    }

    public static Hero fromData(ALTNode node) {
        var hero = Hero.builder().build();
        hero.qualifiedName = node.tryGetValue("workshop_guide_name");
        hero.name = node.getName();

        hero.models = new ArrayList<>();
        hero.models.add("Model");
        //TODO support multi model heroes

        hero.slots = new HashMap<>();
        var slotGroup = node.getFirstChild("ItemSlots");
        var slotNodes = slotGroup.getChildNodes();
        for (var slotNodeName : slotNodes.keySet()) {
            var slotNode = slotGroup.getFirstChild(slotNodeName);
            var slotName = slotNode.tryGetValue("SlotName");
            if (!BLACKLISTED_SLOTS.contains(slotName)) {
                hero.slots.put(slotName, new WearableItemSlot(hero, slotName));
            }
        }
        return hero;
    }
}
