package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.dalt.ALTBackedObject;
import com.dfined.omnipatcher.dalt.ALTMapping;
import com.dfined.omnipatcher.dalt.ALTNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class VisualModifier implements ALTBackedObject {
    ALTNode backingObject;

    @ALTMapping(value = "type")
    VisualModifierType type;
    @ALTMapping(value = "asset")
    String asset;
    @ALTMapping(value = "modifier")
    String modifier;
    @ALTMapping(value = "style")
    String style;
    //Currently unused, will be needed when more custom passes are implemented
    String gesture;
    String frequency;
    String applyWhenInAbilityEffectsSlot;
    String skin;
    String persona;
    String scaleSize;
    String modelScale;
    String versusScale;
    String loadoutScale;
    String portraitActivity;

    public VisualModifier(ALTNode backingObject) {
        this.backingObject = backingObject;
    }

    static VisualModifier fromData(ALTNode node) {
        var modifier = new VisualModifier(node);
        ALTBackedObject.baseDeserialize(modifier, node);
        return modifier;
    }

    Integer getStyle() {
        return style == null ? null : Integer.valueOf(style);
    }
}
