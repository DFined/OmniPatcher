package com.dfined.omnipatcher.v2.format.raw;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class VDFSingleOrListNode extends ArrayList<VDFNode> implements VDFNode {
    private final String name;

    public VDFSingleOrListNode(String name) {
        this.name = name;
    }

    public boolean isSingle() {
        return size() <= 1;
    }

    public String requireValue(String def){
        if(isSingle()){
            if(this.getSingle() instanceof VDFValueNode){
                return ((VDFValueNode) this.getSingle()).getValue();
            }
        }
        return def;
    }

    public VDFNode getSingle(){
        return get(0);
    }
}
