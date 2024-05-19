package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTNode;

import java.util.List;
import java.util.Map;

public interface InstallableItem<Slot> {

    /**
     * Allows an installable item to perform custom actions when it is installed. For example moving files around
     * to replace default models or effects on the filesystem level in addition to items_game alterations
     * @param slot the slot to install the item to
     */
    public void customInstall(Slot slot);

    /**
     * This is the logic which determines how to install the item within the items_game file.
     * Creates a replacement ALT node for the default item for this slot
     * @param slot the slot to install the item to. Must have a valid default item
     * @return the override ALT node or null if it couldn't be created
     */
    public Map<List<String>, List<ALTNode>> toALTOverride(WearableItemSlot slot);
}
