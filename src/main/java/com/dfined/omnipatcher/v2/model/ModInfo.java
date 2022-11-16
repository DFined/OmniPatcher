package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.format.raw.InstallableItem;

import java.util.HashMap;

public class ModInfo<S extends Slot, T extends InstallableItem<S>, I extends InstallInfo<S, T>> extends HashMap<Slot, I> {
    public InstallInfo<S,T> install(I info){
        return put(info.getSlot(), info);
    }
}
