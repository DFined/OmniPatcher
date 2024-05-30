package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTBackedObject;
import com.dfined.omnipatcher.dalt.ALTMapping;
import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.Globals;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Builder
@Getter
@Slf4j
public class WearableItem implements ALTBackedObject {
    @JsonIgnore
    private ALTNode backingObject;

    @ALTMapping(value = "prefab")
    private ItemType type;

    @ALTMapping(value = "item_name")
    private String name;

    @ALTMapping(value = "name")
    private String qualifiedName;

    @ALTMapping(value = "item_rarity")
    @Builder.Default()
    private ItemRarity rarity = ItemRarity.UNKNOWN;

    @ALTMapping(value = "model_player")
    @Setter
    private String modelPath;

    @ALTMapping(value = "image_inventory")
    private String imagePath;

    @ALTMapping(value = "item_slot")
    private String slot_name;

    private WearableItemSlot slot;
    private List<VisualModifier> visualModifiers;

    private List<String> styles;


    public static WearableItem fromData(ALTNode node) {
        var item = WearableItem.builder().backingObject(node).build();
        ALTBackedObject.baseDeserialize(item, node);

        var heroes = node.getFirstChild("used_by_heroes");
        if (heroes == null) {
            log.warn(String.format("Item %s has no hero specified", item.name));
            return null;
        }
        var heroNames = heroes.getChildNames();
        if (!heroNames.isEmpty()) {
            var hero = Globals.getHero(heroNames.get(0));
            if (hero == null) {
                log.warn(String.format("Item %s has no hero specified", item.name));
                return null;
            }

            item.slot = new WearableItemSlot(hero, item.slot_name);
        }


        item.styles = new ArrayList<>();

        var visualsNode = node.getFirstChild("visuals");
        if (visualsNode != null) {
            item.visualModifiers = new ArrayList<>();
            visualsNode.streamChildren().forEach(vNode -> item.visualModifiers.add(VisualModifier.fromData(vNode)));
            var stylesNode = visualsNode.getFirstChild("styles");
            if (stylesNode != null) {
                item.styles.addAll(stylesNode.getChildNodes().keySet());
            }
        }

        return item;
    }


    @NonNull
    public Hero getHero() {
        return slot.getHero();
    }
}
