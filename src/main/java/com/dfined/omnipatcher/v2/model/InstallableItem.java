package com.dfined.omnipatcher.v2.model;

public interface InstallableItem<Slot> {
    public void customInstall(Slot slot);
}
