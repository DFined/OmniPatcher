package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.format.mapper.annotation.VDFModification;
import com.dfined.omnipatcher.v2.format.raw.InstallableItem;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class Item<S extends Slot> implements InstallableItem<S> {
    public abstract ItemRarity getRarity();

    public HashMap<String, String> getInstallationMappings() {
        HashMap<String, String> mappings = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            for (VDFModification modification : field.getAnnotationsByType(VDFModification.class)) {
                mappings.put(modification.src(), modification.dest());
            }
        }
        return mappings;
    }
}
