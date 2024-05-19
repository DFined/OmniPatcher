package com.dfined.omnipatcher.v2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@Data
@AllArgsConstructor
public class WearableItemSlot {

    @ToString.Exclude
    Hero hero;
    String slotName;

    public boolean isValidSlot(){
        return slotName != null && !slotName.contains("taunt");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WearableItemSlot that = (WearableItemSlot) o;
        return Objects.equals(hero.getName(), that.hero.getName()) && Objects.equals(slotName, that.slotName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hero.getName(), slotName);
    }
}
