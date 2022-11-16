package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.format.mapper.annotation.MapperField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

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
