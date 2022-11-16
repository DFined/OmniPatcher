package com.dfined.omnipatcher.v2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WearableInstallInfo implements InstallInfo<WearableItemSlot, WearableItem> {
    WearableItem item;
    WearableItemSlot slot;
}
