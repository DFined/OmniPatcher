package com.dfined.omnipatcher.v2;

import java.util.List;

/**
 * Helper class for finding data about specific game objects
 */
public class DataHelper {
    private static final String ABILITY_ICONS_PATH = "panorama/images/spellicons";
    private static final String HERO_ICONS_PATH = "panorama/images/heroes";

    /**
     * Get the base model path for a specified entityId. Will first check for heroes, then other entities
     * @return the list of base paths for the entity model
     */
    public static List<String> getBaseEntityModel(String entityId) {
        if(Globals.getHEROES().containsKey(entityId)){
            return Globals.getHEROES().get(entityId).getModels();
        }
        return null;
    }

    public static String getAbilityIconPath(String iconId){
        return String.format("%s/%s_png.vtex_c", ABILITY_ICONS_PATH, iconId);
    }

    public static String getHeroIconPathBase(String iconId){
        return String.format("%s/%s_png.vtex_c", HERO_ICONS_PATH, iconId);
    }
    public static String getHeroIconPathIcons(String iconId){
        return String.format("%s/icons/%s_png.vtex_c", HERO_ICONS_PATH, iconId);
    }
    public static String getHeroIconPathSelection(String iconId){
        return String.format("%s/selection/%s_png.vtex_c", HERO_ICONS_PATH, iconId);
    }
}
