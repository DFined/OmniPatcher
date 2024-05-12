package com.dfined.omnipatcher.v2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WearableInstallInfo {
    WearableItemSlot slot;
    WearableItem item;
}
