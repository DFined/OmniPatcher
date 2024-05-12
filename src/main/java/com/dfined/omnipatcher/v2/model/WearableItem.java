package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTBackedObject;
import com.dfined.omnipatcher.dalt.ALTMapping;
import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.Globals;
import com.dfined.omnipatcher.v2.OmniPatcherV2;
import com.dfined.omnipatcher.v2.filesystem.FileStore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.List;


@Builder
@Getter
@Slf4j
public class WearableItem implements ALTBackedObject, InstallableItem<WearableItemSlot> {
    @JsonIgnore
    ALTNode backingObject;

    @ALTMapping(value = "prefab")
    ItemType type;

    @ALTMapping(value = "item_name")
    String name;

    @ALTMapping(value = "name")
    String qualifiedName;

    @ALTMapping(value = "item_rarity")
    @Builder.Default()
    ItemRarity rarity = ItemRarity.UNKNOWN;

    @ALTMapping(value = "model_player")
    String modelPath;

    @ALTMapping(value = "image_inventory")
    String imagePath;

    @ALTMapping(value = "item_slot")
    String slot_name;

    WearableItemSlot slot;
    List<VisualModifier> visualModifiers;
    boolean workshopAccepted;


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
            if(hero == null){
                log.warn(String.format("Item %s has no hero specified", item.name));
                return null;
            }

            item.slot = new WearableItemSlot(hero, item.slot_name);
        }
        return item;
    }


    @NonNull
    public Hero getHero() {
        return slot.getHero();
    }

    @Override
    public void customInstall(WearableItemSlot slot) {
        try {
            var defaultItem = Globals.DEFAULT_WEARABLE_ITEMS.get(slot);
            if (getModelPath() != null && defaultItem != null && defaultItem.getModelPath() != null) {
                var src = OmniPatcherV2.FSM.getFileForPath(getModelPath() + "_c", FileStore.CACHE);
                if (src != null) {
                    FileUtils.copyFile(src, OmniPatcherV2.FSM.getInDir(defaultItem.getModelPath() + "_c", FileStore.TMP));
                }
            }
        } catch (IOException e) {
            System.out.printf("Error installing item %s to slot %s%n", getName(), getSlot().getSlot());
        }
    }
}
