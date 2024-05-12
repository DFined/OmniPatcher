package com.dfined.omnipatcher.v2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;

@Data
@AllArgsConstructor
public class WearableItemSlot {

    @ToString.Exclude
    Hero hero;
    String slot;

    public boolean isValidSlot(){
        return !slot.contains("taunt");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WearableItemSlot that = (WearableItemSlot) o;
        return Objects.equals(hero.getName(), that.hero.getName()) && Objects.equals(slot, that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hero.getName(), slot);
    }
}
