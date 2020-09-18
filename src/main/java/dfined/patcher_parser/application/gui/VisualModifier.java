package dfined.patcher_parser.application.gui;

import dfined.patcher_parser.data.data_structure.game.Hero;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VisualModifier {
    private static final List<String> ALLOWED_TYPES = Arrays.asList("particle", "particle_combined", "model", "entity_model");
    private String type;
    private String asset;
    private String modifier;

    public VisualModifier(String type, String asset, String modifier) {
        this.type = type;
        this.asset = asset;
        this.modifier = modifier;
    }

    public static VisualModifier makeModifier(HashMap<String, Object> map, Hero hero) {
        String type = (String) map.get("type");
        if (type != null) {
            if (ALLOWED_TYPES.contains(type)) {
                String asset = (String) map.get("asset");
                String modifier = (String) map.get("modifier");
                if (modifier != null && asset != null) {
                    if(asset.equals(hero.getName())) {
                        asset = hero.getModelPath();
                    }
                }
                return new VisualModifier(type, asset, modifier);
            }
        }
        return null;
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
}
