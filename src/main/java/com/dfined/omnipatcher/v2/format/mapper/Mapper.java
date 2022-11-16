package com.dfined.omnipatcher.v2.format.mapper;

import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import one.util.streamex.StreamEx;

import java.util.List;


public abstract class Mapper<T> {
    public abstract T map(VDFObjectNode node);

    public List<T> lookup(VDFObjectNode node, String paths, List<String> blacklistNames){
        var nodes = node.getObjectsForPaths(paths);
        return StreamEx.of(nodes)
                .nonNull()
                .remove(nodeObj -> blacklistNames.contains(nodeObj.getName()))
                .map(this::map)
                .nonNull()
                .toList();
    }
}
