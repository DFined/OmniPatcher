package com.dfined.omnipatcher.v2.format.raw;

import com.dfined.omnipatcher.v2.model.Slot;

public interface InstallableItem<T extends Slot> extends VDFMappedObject {
    void customInstall(T slot);
}
