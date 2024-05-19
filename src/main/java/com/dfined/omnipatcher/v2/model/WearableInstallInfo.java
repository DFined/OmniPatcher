package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.Globals;
import com.dfined.omnipatcher.v2.OmniPatcherV2;
import com.dfined.omnipatcher.v2.filesystem.FileStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class WearableInstallInfo implements InstallableItem<WearableItemSlot> {
    WearableItemSlot slot;
    WearableItem baseItem;

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
        } catch (IOException e) {
            System.out.printf("Error installing item %s to slot %s%n", baseItem.getName(), getSlot().getSlotName());
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

        var overrideNode = new ALTNode(defaultNode.getName(), null, defaultPath.subList(0, defaultPath.size()-1));
        overrideNode.copyChildrenFrom(baseItem.getBackingObject());
        overrideNode.replaceChild("prefab", defaultNode.getFirstChild("prefab"));

        overrides.put(defaultNode.getPathSegments(), List.of(overrideNode));
        return overrides;
    }
}
