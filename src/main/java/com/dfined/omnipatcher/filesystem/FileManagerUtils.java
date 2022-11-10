package com.dfined.omnipatcher.filesystem;

import com.dfined.omnipatcher.application.ApplicationSettings;
import com.dfined.omnipatcher.application.Globals;
import com.dfined.omnipatcher.application.OmniPatcher;
import com.dfined.omnipatcher.application.error.ExceptionInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public abstract class FileManagerUtils {

    // If the requested file exists in the Local repo - retrieve it. Otherwise - copy it from source first
    public static File getFromLocal(String path) throws IOException {
        ApplicationSettings settings = OmniPatcher.getInstance().getSettings();
        return OmniPatcher.getFileManager().getFromLocal(settings.getSourceFile(), settings.localDir(), path);
    }

    // True iff the requested file exists in the Local repo
    public static boolean existsInLocal(String path) {
        ApplicationSettings settings = OmniPatcher.getInstance().getSettings();
        return OmniPatcher.getFileManager().existsInLocal(settings.getLocalDir(), path);
    }

    // If the specified file exists in the Local repo - delete it
    public static void deleteLocal(String path) {
        ApplicationSettings settings = OmniPatcher.getInstance().getSettings();
        OmniPatcher.getFileManager().deleteLocal(settings.getLocalDir(), path);
    }

    //Copy file from the specified path in the Source repo to the specified path in the Local repo
    public static File fromSourceToLocal(String sourcePath, String destPath) throws IOException {
        ApplicationSettings settings = OmniPatcher.getInstance().getSettings();
        return OmniPatcher.getFileManager().getFromSource(settings.getSourceFile(), sourcePath, settings.getLocalDir(), destPath);
    }

    //Copy file from the specified path in the Source repo to the same path in the Local repo
    public static File fromSourceToLocal(String path) throws IOException {
        return fromSourceToLocal(path, path);
    }

    //Copy file from the specified path in the Source repo to the specified path in the specified repo
    public static File fromSourceToRepo(String sourcePath, File repo, String dest) throws IOException {
        ApplicationSettings settings = OmniPatcher.getInstance().getSettings();
        return OmniPatcher.getFileManager().getFromSource(settings.getSourceFile(), sourcePath, repo, dest);
    }

    public static <T> T readFileAsObject(String path, Class<T> objectClass, ExceptionInfo exceptionInfo) {
        try {
            var str = FileUtils.readFileToString(new File(path), "UTF-8");
            return Globals.OBJECT_MAPPER.readValue(str, objectClass);
        } catch (IOException ioException){
            throw new IllegalStateException(exceptionInfo.toString());
        }
    }

    public static <T> HashMap<String,T> readFileAsHashMap(String path, ExceptionInfo exceptionInfo) {
        try {
            var str = FileUtils.readFileToString(new File(path), "UTF-8");
            return (HashMap<String,T>) Globals.OBJECT_MAPPER.readValue(str, HashMap.class);
        } catch (IOException ioException){
            throw new IllegalStateException(exceptionInfo.toString());
        }
    }

    public static String getLocalPath() {
        return OmniPatcher.getInstance().getSettings().getLocalDir().getPath();
    }

    public static File getLocalPathFile() {
        return OmniPatcher.getInstance().getSettings().getLocalDir();
    }

    public static String getSourcePath() {
        return OmniPatcher.getInstance().getSettings().getSourceFile().getAbsolutePath();
    }
}
