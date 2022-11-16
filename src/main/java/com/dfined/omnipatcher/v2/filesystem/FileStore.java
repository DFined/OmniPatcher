package com.dfined.omnipatcher.v2.filesystem;

public enum FileStore {
    /**
     * directory which is used to package files for install. Deleted every launch
     */
    TMP,
    /**
     * directory used to unpack items from a vpk archive. Is deleted every time source files are updated
     */
    CACHE,
    /**
     * directory for files which need to be persisted across updates (i.e. icons)
     */
    PERSISTENT
}
