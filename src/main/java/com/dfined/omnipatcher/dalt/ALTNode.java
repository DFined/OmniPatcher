package com.dfined.omnipatcher.dalt;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Basic type for the DLAT (DFined's Labeled Abstract Tree) format
 */
@Getter
@Setter
public class ALTNode {
    private String name;
    private String value;

    private final List<String> pathSegments;

    private final LinkedHashMap<String, List<ALTNode>> childNodes;

    public ALTNode(ALTNode clone){
        this.name = clone.getName();
        this.pathSegments = clone.getPathSegments();
        this.value = clone.value;

        this.childNodes = new LinkedHashMap<>();
        clone.getChildNodes().forEach(this::addChildren);

    }

    public ALTNode(String name, String value, List<String> parentPaths) {
        this.name = name;
        this.value = value;
        this.pathSegments = new ArrayList<>(parentPaths);
        this.pathSegments.add(name);
        this.childNodes = new LinkedHashMap<>();
    }

    public void addChild(ALTNode node) {
        this.childNodes.computeIfAbsent(node.name, (ignored) -> new ArrayList<>());
        this.childNodes.get(node.name).add(node);
    }

    public ALTNode getFirstChild(String name) {
        return childNodes.containsKey(name) ? (childNodes.get(name).isEmpty() ? null : childNodes.get(name).get(0)) : null;
    }

    public String tryGetValue(String name) {
        var subNode = getFirstChild(name);
        return subNode == null ? null : subNode.getValue();
    }

    public ArrayList<String> getChildNames() {
        return new ArrayList<>(childNodes.keySet());
    }

    /**
     * Find all nodes in the tree, that have a path, matching a provided list of segments. A null segment matches to anything
     *
     * @return a list of matching nodes in order of a depth-first search
     */
    public ArrayList<ALTNode> findNodesByPaths(List<String> pathSegments) {
        var res = new ArrayList<ALTNode>();
        findNodesByPaths(pathSegments, res);
        return res;
    }

    public void findNodesByPaths(List<String> pathSegments, List<ALTNode> res) {
        if (pathSegments.isEmpty()) {
            res.add(this);
        } else {
            var nextSegment = pathSegments.get(0);
            if (nextSegment == null) {
                for (var child : childNodes.keySet()) {
                    childNodes.get(child).forEach(node -> node.findNodesByPaths(pathSegments.subList(1, pathSegments.size()), res));
                }
            } else {
                if (childNodes.containsKey(nextSegment)) {
                    childNodes.get(nextSegment).forEach(node -> node.findNodesByPaths(pathSegments.subList(1, pathSegments.size()), res));
                }
            }
        }
    }

    public void addChildren(String name, List<ALTNode> altNodes) {
        this.childNodes.computeIfAbsent(name, (ignored) -> new ArrayList<>());
        this.childNodes.get(name).addAll(altNodes);
    }

    public List<ALTNode> getChildren(String childName) {
        return childNodes.get(childName);
    }

    public void copyChildrenFrom(ALTNode backingObject) {
        childNodes.clear();
        backingObject.childNodes.forEach(this::addChildren);
    }

    public void replaceChild(String name, ALTNode value){
        childNodes.remove(name);
        childNodes.put(name, new ArrayList<>());
        childNodes.get(name).add(value);
    }
}
