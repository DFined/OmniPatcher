package com.dfined.omnipatcher.v2.format.raw;

import com.dfined.omnipatcher.v2.DFUtil;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.dfined.omnipatcher.v2.format.mapper.Mapper;
import lombok.Getter;

import java.util.*;

@Getter
public class VDFObjectNode extends LinkedHashMap<String, VDFSingleOrListNode> implements VDFNode {
    private final String path;
    private final String name;

    public VDFObjectNode(String path, String name) {
        super();
        this.path = path;
        this.name = name;
    }

    public void put(String name, VDFNode node) {
        if (!this.containsKey(name)) {
            this.put(name, new VDFSingleOrListNode(name));
        }
        this.get(name).add(node);
    }

    public VDFObjectNode getObject(String name) {
        var node = this.get(name).getSingle();
        if (node instanceof VDFObjectNode) {
            return (VDFObjectNode) node;
        }
        throw new IllegalStateException(new ExceptionInfo().toString());
    }


    public String getValue(String name) {
        if (this.containsKey(name)) {
            var node = this.get(name).getSingle();
            if (node instanceof VDFValueNode) {
                return ((VDFValueNode) node).getValue();
            }
        }
        throw new IllegalStateException(new ExceptionInfo().toString());
    }

    public String getValueOrDefault(String name, String dflt) {
        if (this.containsKey(name)) {
            var node = this.get(name).getSingle();
            if (node instanceof VDFValueNode) {
                return ((VDFValueNode) node).getValue();
            }
        }
        return dflt;
    }

    public void removeAll(Collection<String> blacklist) {
        blacklist.forEach(this::remove);
    }

    public List<VDFObjectNode> getObjectsForPaths(String pathRegex) {
        return getObjectsForPaths(pathRegex.split("/"), VDFObjectNode.class);
    }

    public List<VDFValueNode> getvaluesForPaths(String pathRegex) {
        return getObjectsForPaths(pathRegex.split("/"), VDFValueNode.class);
    }

    public List<String> getNamesForPaths(String pathRegex) {
        return DFUtil.convert(getObjectsForPaths(pathRegex.split("/"), VDFValueNode.class), VDFValueNode::getName);
    }

    public <T> List<T> getObjectsForPaths(String pathRegex, Mapper<T> mapper) {
        return DFUtil.convertNonNull(getObjectsForPaths(pathRegex.split("/"), VDFObjectNode.class), mapper::map);
    }

    private <T extends VDFNode> List<T> getObjectsForPaths(String[] pathRegex, Class<T> type) {
        List<T> nodes = new ArrayList<>();
        String fragment = pathRegex[0];
        for (String name : this.keySet()) {
            if (name.matches(fragment)) {
                for (VDFNode node : this.get(name)) {
                    if (pathRegex.length == 1) {
                        if (type.isInstance(node)) {
                            nodes.add((T) node);
                        }
                    } else {
                        if (node instanceof VDFObjectNode) {
                            nodes.addAll((((VDFObjectNode) node).getObjectsForPaths(Arrays.copyOfRange(pathRegex, 1, pathRegex.length), type)));
                        }
                    }
                }
            }
        }
        return nodes;
    }
}
