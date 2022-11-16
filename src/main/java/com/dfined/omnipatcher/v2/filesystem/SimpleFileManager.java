package com.dfined.omnipatcher.v2.filesystem;

import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SimpleFileManager {
    public static final SimpleFileManager INSTANCE = new SimpleFileManager();
    public String readFileAsString(File dir, String path){
        try {
            return FileUtils.readFileToString(new File(dir,path), "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException(new ExceptionInfo().toString());
        }
    }
}
