package com.dfined.omnipatcher.data.data_structure.game;

import com.dfined.omnipatcher.data.annotations.IgnoreDataMapping;
import com.dfined.omnipatcher.data.data_structure.HashDataMap;

public class GameData implements HashDataMapBacked {
    @IgnoreDataMapping
    String dataObjectName;
    @IgnoreDataMapping
    String backingPath;

    public GameData(HashDataMap map) {
        this.dataObjectName = map.getDataObjectName();
        this.backingPath = map.backingPath();
        try {
            this.init(map);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(String.format("Unable to init HashDataMapBacked class %s.",this.getClass().getSimpleName()), e);
        }
    }

    @Override
    public String getDataObjectName() {
        return dataObjectName;
    }

    @Override
    public String backingPath() {
        return backingPath;
    }

    public String internalBackingPath() { return backingPath.startsWith("/") ? backingPath.split("/",3)[2] : backingPath.split("/",2)[1];}
}
