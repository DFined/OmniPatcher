package com.dfined.omnipatcher.v2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemRarity {
    COMMON(0.5f, 0.5f, 0.5f),
    UNCOMMON(0.3f, 0.8f, 0.8f),
    RARE(0.1f, 0.1f, 0.8f),
    MYTHICAL(0.4f, 0.3f, 0.8f),
    LEGENDARY(0.8f, 0.3f, 0.4f),
    ANCIENT(0.8f, 0.2f, 0.2f),
    IMMORTAL(0.8f, 0.8f, 0.4f),
    ARCANA(0.2f, 0.8f, 0.2f),
    SEASONAL(0.8f, 0.8f, 0.2f);
    final float r, g, b;
}
