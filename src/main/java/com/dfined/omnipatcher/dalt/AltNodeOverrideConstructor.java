package com.dfined.omnipatcher.dalt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AltNodeOverrideConstructor {
    HashMap<List<String>, List<ALTNode>> overrides = new HashMap<>();

    public void overrideChildValue(List<String> basePath, ALTNode sourceNode, String childName) {
        var path = new ArrayList<>(basePath);
        path.add(childName);
        overrides.put(path, sourceNode.getChildren(childName));
    }

    public HashMap<List<String>, List<ALTNode>> get() {
        return overrides;
    }
}
