package com.dfined.omnipatcher.v2.model;

import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class ItemsToInstall {
    private static ItemsToInstall instance;
    private final WearableModInfo wearables = new WearableModInfo();


    public void performInstall(File dotaRootDir) throws IOException {
//        OmniPatcherV2.FSM.clearDir(FileStore.TMP);
//        EntryStream.of(wearables).mapValues(WearableInstallInfo::getItem).invert().forKeyValue(WearableItem::customInstall);
//        try {
//            var wearableItems = wearables.getOverrides();
//            ValveDumpsterFormatWriter.write(OmniPatcherV2.FSM.getInDir(GamePathConstants.ITEMS_GAME_PATH, FileStore.TMP), );
//        } catch (IOException e) {
//            throw new IllegalStateException(new ExceptionInfo().toString(), e);
//        }
//        ValveResourceManager.installDirectoryToGame(OmniPatcherV2.FSM.getDir(FileStore.TMP), new File(dotaRootDir, GamePathConstants.MODS_VPK_PATH));
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
