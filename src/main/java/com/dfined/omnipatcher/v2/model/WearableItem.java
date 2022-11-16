package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.OmniPatcherV2;
import com.dfined.omnipatcher.v2.filesystem.FileStore;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.List;


@Builder
@Getter
public class WearableItem extends Item<WearableItemSlot> {
    VDFObjectNode vdfObject;

    ItemType type;

    String name;
    String qualifiedName;
    ItemRarity rarity;
    String modelPath;
    String imagePath;
    WearableItemSlot slot;
    List<VisualModifier> visualModifiers;
    boolean workshopAccepted;

    @NonNull
    public Hero getHero() {
        return slot.getHero();
    }

    @Override
    public List<String> getDefaults() {
        return List.of("prefab", "item_rarity", "item_slot", "item_name", "item_type_name", "used_by_heroes");
    }

    @Override
    public void customInstall(WearableItemSlot slot) {
        try {
            if (getModelPath() != null && slot.getDefaultItem() != null && slot.getDefaultItem().getModelPath() != null) {
                var src = OmniPatcherV2.FSM.getFileForPath(getModelPath() + "_c", FileStore.CACHE);
                if (src != null) {
                    FileUtils.copyFile(src, OmniPatcherV2.FSM.getInDir(slot.getDefaultItem().getModelPath() + "_c", FileStore.TMP));
                }
            }
        } catch (IOException e) {
            System.out.printf("Error installing item %s to slot %s%n", getName(), getSlot().getName());
        }
    }
}
