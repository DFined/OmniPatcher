package com.dfined.omnipatcher.v2.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class VisualModifier {
    VisualModifierType type;
    String asset;
    String modifier;
    String gesture;
    String frequency;
    String applyWhenInAbilityEffectsSlot;
    String style;
    String skin;
    String persona;
    String scaleSize;
    String modelScale;
    String versusScale;
    String loadoutScale;
    String portraitActivity;
}
