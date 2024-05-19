package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.Globals;
import com.dfined.omnipatcher.v2.OmniPatcherV2;
import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import com.dfined.omnipatcher.v2.filesystem.FileStore;
import com.dfined.omnipatcher.v2.filesystem.GamePathConstants;
import com.dfined.omnipatcher.v2.filesystem.ValveResourceManager;
import com.dfined.omnipatcher.v2.format.ValveDumpsterFormatWriter;
import lombok.Getter;
import one.util.streamex.EntryStream;

import java.io.File;
import java.io.IOException;

public class ItemsToInstall {
    private static ItemsToInstall instance;
    private final WearableModInfo wearables = new WearableModInfo();


    public void performInstall(File dotaRootDir) throws IOException {
        OmniPatcherV2.FSM.clearDir(FileStore.TMP);
        wearables.doCustomInstall();
        try {
            var wearableOverrides = wearables.getOverrides();
            ValveDumpsterFormatWriter.write(
                    OmniPatcherV2.FSM.getInDir(GamePathConstants.ITEMS_GAME_PATH, FileStore.TMP),
                    Globals.ROOT_NODE,
                    wearableOverrides
            );
        } catch (IOException e) {
            throw new IllegalStateException(new ExceptionInfo().toString(), e);
        }
        ValveResourceManager.installDirectoryToGame(
                OmniPatcherV2.FSM.getDir(FileStore.TMP), new File(dotaRootDir, GamePathConstants.MODS_VPK_PATH)
        );
    }

    public static ItemsToInstall getInstance() {
        if (instance == null){
            instance = new ItemsToInstall();
        }
        return instance;
    }

    public WearableModInfo getWearables() {
        return wearables;
    }
}
