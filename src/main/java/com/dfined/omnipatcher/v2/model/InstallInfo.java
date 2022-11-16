package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.format.raw.InstallableItem;

public interface InstallInfo<S extends Slot, I extends InstallableItem<S>> {
    S getSlot();
    I getItem();
}
