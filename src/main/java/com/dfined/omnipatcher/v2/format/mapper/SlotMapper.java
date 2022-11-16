package com.dfined.omnipatcher.v2.format.mapper;

import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import com.dfined.omnipatcher.v2.model.Hero;
import com.dfined.omnipatcher.v2.model.WearableItemSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SlotMapper extends Mapper<WearableItemSlot> {
    private final Hero hero;

    @Override
    public WearableItemSlot map(VDFObjectNode node) {
        return new WearableItemSlot(
                hero,
                node.getValue("SlotName"),
                node.getValueOrDefault("DisplayInLoadout", "1"),
                Integer.parseInt(node.getValue("SlotIndex")),
                null
        );
    }
}
