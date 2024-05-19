package com.dfined.omnipatcher.v2;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import one.util.streamex.StreamEx;

import java.util.*;

public class Globals {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Getter
    private static final HashMap<String, Hero> HEROES = new HashMap<>();
    private static final HashMap<String, String> HERO_NAMES_MAP = new HashMap<>();
    public static final List<WearableItem> ITEMS = new ArrayList<>();
    public static final HashMap<WearableItemSlot, WearableItem> DEFAULT_WEARABLE_ITEMS = new HashMap<>();
    public static ALTNode ROOT_NODE;

    public static Hero getHero(String name) {
        return HEROES.get(name);
    }

    public static Hero getHeroByQualifiedName(String name) {
        return getHero(HERO_NAMES_MAP.get(name));
    }

    public static void initHeroes(Map<String, Hero> heroes) {
        HEROES.putAll(heroes);
        StreamEx.of(HEROES.values())
                .mapToEntry(Hero::getQualifiedName, Hero::getName)
                .forKeyValue(HERO_NAMES_MAP::put);
    }

    public static List<WearableItem> getItems(
            String heroName, String slotName, String itemName, List<ItemRarity> rarities
    ) {
        return StreamEx.of(ITEMS)
                .filter(item -> itemMatches(heroName, slotName, itemName,rarities, item))
                .sortedBy(WearableItem::getQualifiedName)
                .toList();
    }

    private static boolean itemMatches(
            String heroName, String slotName, String itemName, List<ItemRarity> rarities, WearableItem item
    ) {
        boolean heroNameMatches = heroName == null || HERO_NAMES_MAP.get(heroName).equals(item.getHero().getName());
        boolean slotNameMatches = slotName == null || item.getSlot() == null
                || item.getSlot().getSlotName() == null || item.getSlot().getSlotName().equals(slotName);
        boolean itemNameMatches = itemName == null || item.getQualifiedName().toLowerCase(Locale.ROOT)
                .contains(itemName.toLowerCase(Locale.ROOT));
        boolean raritiesMatch = rarities == null || rarities.isEmpty() || rarities.contains(item.getRarity());
        return heroNameMatches && slotNameMatches && itemNameMatches && raritiesMatch;
    }
}
