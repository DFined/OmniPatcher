package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.data.annotations.DefaultValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
public class WearableItemSlot extends Slot {
    public static final String UNKNOWN_SLOT = "slot-not-found";
    @ToString.Exclude
    Hero hero;
    String name;
    String showInLoadout;
    int index;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @DefaultValue("")
    WearableItem defaultItem = null;

    public boolean isValidSlot(){
        return isReal() && showInLoadout.equals("1") && !name.contains("taunt");
    }

    public boolean isReal() {
        return index != -1;
    }
}
