package com.dfined.omnipatcher.v2.filesystem;

import com.dfined.omnipatcher.v2.error.ExceptionInfo;
import javavpk.core.Archive;
import javavpk.core.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class CachedVPKFileManager {
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("vmdl_c", "vpcf_c", "txt", "vtex_c");

    private final File cacheDir;
    private final File tmpDir;
    private final File persistentDir;
    private final Archive vpkArchive;
    HashMap<String, Entry> fileSystem = null;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CachedVPKFileManager(File vpk, File cacheDir, String version) {
        try {
            var versionCache = new File(cacheDir, version);
            if (!versionCache.exists() || !versionCache.isDirectory()) {
                FileUtils.cleanDirectory(cacheDir);
                versionCache.mkdir();
            }
            this.cacheDir = versionCache;
            this.tmpDir = new File(cacheDir, "tmp");
            this.persistentDir = new File(cacheDir, "persistent");
            clearDir(FileStore.TMP);
            vpkArchive = new Archive(vpk);

        } catch (IOException e) {
            throw new IllegalStateException(new ExceptionInfo().toString());
        }
    }

    private void load() throws IOException {
        fileSystem = vpkArchive.load(ALLOWED_EXTENSIONS);
    }

    public File getDir(FileStore store) {
        return switch (store) {
            case TMP -> tmpDir;
            case CACHE -> cacheDir;
            case PERSISTENT -> persistentDir;
        };
    }

    public File getInDir(String pathInTmp, FileStore store) {
        return new File(getDir(store), pathInTmp);
    }

    public void clearDir(FileStore store) throws IOException {
        FileUtils.cleanDirectory(getDir(store));
    }

    @Nullable
    public File getFileForPath(String path, FileStore store) {
        try {
            path = path.startsWith("/") ? path.substring(1) : path;
            File resultFile = new File(getDir(store), path);
            if (!resultFile.exists()) {
                if (fileSystem == null) {
                    load();
                }
                Entry entry = fileSystem.get(path);
                if (entry == null) {
                    throw new FileNotFoundException(String.format("File %s not found in vpk", path));
                }
                FileUtils.touch(resultFile);
                entry.extract(resultFile);
            }
            return resultFile;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load file from vpk: " + path, e);
        }
    }


    public static CachedVPKFileManager create(File vpkArchive, File cacheDir, String version) {
        return new CachedVPKFileManager(vpkArchive, cacheDir, version);
    }
}
