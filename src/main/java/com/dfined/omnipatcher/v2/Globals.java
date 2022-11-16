package com.dfined.omnipatcher.v2;

import com.dfined.omnipatcher.v2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Globals {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Getter
    private static final HashMap<String, Hero> HEROES = new HashMap<>();
    private static final HashMap<String, String> HERO_NAMES_MAP = new HashMap<>();
    public static final List<WearableItem> ITEMS = new ArrayList<>();

    public static Hero getHero(String name) {
        return HEROES.get(name);
    }

    public static Hero getHeroByQualifiedName(String name) {
        return getHero(HERO_NAMES_MAP.get(name));
    }

    public static void initHeroes(HashMap<String, Hero> heroes) {
        HEROES.putAll(heroes);
        StreamEx.of(HEROES.values())
                .mapToEntry(Hero::getQualifiedName, Hero::getName)
                .forKeyValue(HERO_NAMES_MAP::put);
    }

    public static List<WearableItem> getItems(String heroName, String slotName, String itemName, List<ItemRarity> rarities, boolean allowWorkshopItems) {
        return StreamEx.of(ITEMS)
                .filter(item -> itemMatches(heroName, slotName, itemName,rarities, allowWorkshopItems, item))
                .sortedBy(WearableItem::getQualifiedName)
                .toList();
    }

    private static boolean itemMatches(String heroName, String slotName, String itemName, List<ItemRarity> rarities, boolean allowWorkshopItems, WearableItem item) {
        boolean heroNameMatches = heroName == null || HERO_NAMES_MAP.get(heroName).equals(item.getHero().getName());
        boolean slotNameMatches = slotName == null || item.getSlot().getName().equals(slotName);
        boolean itemNameMatches = itemName == null || item.getQualifiedName().toLowerCase(Locale.ROOT).contains(itemName.toLowerCase(Locale.ROOT));
        boolean raritiesMatch = rarities == null || rarities.isEmpty() || rarities.contains(item.getRarity());
        boolean workshopOk = allowWorkshopItems || item.isWorkshopAccepted();
        return heroNameMatches && slotNameMatches && itemNameMatches && raritiesMatch && workshopOk;
    }
}
