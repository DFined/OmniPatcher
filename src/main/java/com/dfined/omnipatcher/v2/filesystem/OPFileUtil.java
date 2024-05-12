package com.dfined.omnipatcher.v2.filesystem;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.format.ValveDumpsterFormatReader;

import java.io.File;

public class OPFileUtil {
    public static final SimpleFileManager SFM = SimpleFileManager.INSTANCE;

    public static ALTNode readVDF(File file){
        return new ValveDumpsterFormatReader(file).parse();
    }
}
