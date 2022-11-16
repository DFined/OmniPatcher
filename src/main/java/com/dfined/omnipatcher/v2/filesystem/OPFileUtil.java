package com.dfined.omnipatcher.v2.filesystem;

import com.dfined.omnipatcher.v2.format.ValveDumpsterFormatReader;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;

import java.io.File;

public class OPFileUtil {
    public static final SimpleFileManager SFM = SimpleFileManager.INSTANCE;

    public static VDFObjectNode readVDF(File file){
        return new ValveDumpsterFormatReader(file).parse();
    }
}
