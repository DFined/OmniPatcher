package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.DataHelper;
import com.dfined.omnipatcher.v2.Globals;
import com.dfined.omnipatcher.v2.OmniPatcherV2;
import com.dfined.omnipatcher.v2.filesystem.FileStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class WearableInstallInfo implements InstallableItem<WearableItemSlot> {
    WearableItemSlot slot;
    WearableItem baseItem;
    int style;

    @Override
    public void customInstall(WearableItemSlot slot) {
        try {
            var defaultItem = Globals.DEFAULT_WEARABLE_ITEMS.get(slot);
            if (baseItem.getModelPath() != null && defaultItem != null && defaultItem.getModelPath() != null) {
                var src = OmniPatcherV2.FSM.getFileForPath(baseItem.getModelPath() + "_c", FileStore.CACHE);
                if (src != null) {
                    FileUtils.copyFile(src, OmniPatcherV2.FSM.getInDir(defaultItem.getModelPath() + "_c", FileStore.TMP));
                }
            }
            for (var modifier : baseItem.getVisualModifiers()) {
                if (modifier.getType() == null) {
                    continue;
                }
                if (modifier.getStyle() != null && modifier.getStyle() != style) {
                    continue;
                }
                switch (modifier.getType()) {
                    case ENTITY_MODEL -> {
                        var baseModels = DataHelper.getBaseEntityModel(modifier.getAsset());
                        if (baseModels != null) {
                            //TODO support multi model heroes
                            var src = OmniPatcherV2.FSM.getFileForPath(modifier.modifier + "_c", FileStore.CACHE);
                            var dst = OmniPatcherV2.FSM.getInDir(baseModels.get(0) + "_c", FileStore.TMP);
                            FileUtils.copyFile(src, dst);
                        }
                    }
                    case MODEL, PARTICLE, PARTICLE_COMBINED -> {
                        var src = OmniPatcherV2.FSM.getFileForPath(modifier.modifier + "_c", FileStore.CACHE);
                        var dst = OmniPatcherV2.FSM.getInDir(modifier.asset + "_c", FileStore.TMP);
                        FileUtils.copyFile(src, dst);
                    }
                    case ABILITY_ICON -> {
                        var src = OmniPatcherV2.FSM.getFileForPath(
                                DataHelper.getAbilityIconPath(modifier.modifier), FileStore.CACHE
                        );
                        var dst = OmniPatcherV2.FSM.getInDir(
                                DataHelper.getAbilityIconPath(modifier.asset), FileStore.TMP
                        );
                        FileUtils.copyFile(src, dst);
                    }
                    case ICON_REPLACEMENT_HERO -> {
                        var src = OmniPatcherV2.FSM.getFileForPath(
                                DataHelper.getHeroIconPathBase(modifier.modifier), FileStore.CACHE
                        );
                        var dst = OmniPatcherV2.FSM.getInDir(
                                DataHelper.getHeroIconPathBase(modifier.asset), FileStore.TMP
                        );
                        FileUtils.copyFile(src, dst);

                        src = OmniPatcherV2.FSM.getFileForPath(
                                DataHelper.getHeroIconPathIcons(modifier.modifier), FileStore.CACHE
                        );
                        dst = OmniPatcherV2.FSM.getInDir(
                                DataHelper.getHeroIconPathSelection(modifier.asset), FileStore.TMP
                        );
                        FileUtils.copyFile(src, dst);
                    }
                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("Error installing item %s to slot %s%n", baseItem.getName(), getSlot().getSlotName());
            e.printStackTrace();
        }
    }


    @Override
    public Map<List<String>, List<ALTNode>> toALTOverride(WearableItemSlot slot) {
        var defaultItem = Globals.DEFAULT_WEARABLE_ITEMS.get(slot);
        if (defaultItem == null) {
            return null;
        }
        var overrides = new HashMap<List<String>, List<ALTNode>>();

        var defaultNode = defaultItem.backingObject;
        var defaultPath = defaultItem.backingObject.getPathSegments();

        var overrideNode = new ALTNode(defaultNode.getName(), null, defaultPath.subList(0, defaultPath.size() - 1));
        overrideNode.copyChildrenFrom(baseItem.getBackingObject());
        overrideNode.replaceChild("prefab", defaultNode.getFirstChild("prefab"));

        overrides.put(defaultNode.getPathSegments(), List.of(overrideNode));
        return overrides;
    }
}
