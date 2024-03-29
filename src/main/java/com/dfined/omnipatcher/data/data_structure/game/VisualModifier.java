package com.dfined.omnipatcher.data.data_structure.game;

import com.dfined.omnipatcher.data.data_structure.HashDataMap;

import java.util.Arrays;
import java.util.List;

public class VisualModifier extends GameData {
    private static final List<String> ALLOWED_TYPES = Arrays.asList("particle", "particle_combined", "model", "entity_model");
    String type;
    String asset;
    String modifier;

    public VisualModifier(HashDataMap map, Hero hero) {
        super(map);
        if (asset != null) {
            if (asset.equals(hero.getName())) {
                asset = hero.getModelPath();
            }
        }
    }

    public boolean isAllowed() {
        if (type != null) {
            if (modifier != null && asset != null) {
                return ALLOWED_TYPES.contains(type) && assetIsFile(modifier) && assetIsFile(asset);
            }
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public String getAsset() {
        return asset;
    }

    public String getModifier() {
        return modifier;
    }

    private boolean assetIsFile(String asset) {
        return asset.contains(".");
    }
}
