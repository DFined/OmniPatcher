package com.dfined.omnipatcher.v2.format.mapper;

import com.dfined.omnipatcher.v2.DFUtil;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import com.dfined.omnipatcher.v2.model.Item;
import com.dfined.omnipatcher.v2.model.ItemType;

public class ItemMapper extends Mapper<Item> {
    public static final ItemMapper INSTANCE = new ItemMapper();

    @Override
    public Item map(VDFObjectNode node) {
        var prefabKey = node.getValueOrDefault("prefab", null);
        var prefab = DFUtil.safeGetEnum(prefabKey, ItemType.class);
        if (prefab == null) {
            return null;
        }
        return switch (prefab) {
            case WEARABLE, DEFAULT_ITEM -> WearableItemMapper.INSTANCE.map(node);
            case BUNDLE -> null;
        };
    }
}
