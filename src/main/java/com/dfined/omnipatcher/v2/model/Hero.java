package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.DFUtil;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class Hero {
    String name;
    String qualifiedName;
    List<String> models;
    @EqualsAndHashCode.Exclude
    Map<String, WearableItemSlot> slots;
    @EqualsAndHashCode.Exclude
    WearableItemSlot unknownSlot;

    public HashMap<String, WearableItemSlot> getValidSlots(){
        return DFUtil.filter(slots, WearableItemSlot::isValidSlot);
    }
}
