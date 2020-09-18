package dfined.patcher_parser.filesystem;

import dfined.patcher_parser.application.ApplicationSettings;

import java.util.HashMap;

public abstract class FileSystem {
    private static HashMap<Manager, FileManager> managers = new HashMap<>();
    public enum Manager {SIMPLE_FILE_MANAGER};
    public static FileManager getManager(Manager manager){
        FileManager fileManager = managers.get(manager);
        if(fileManager == null){
            fileManager = newManager(manager);
            managers.put(manager, fileManager);
        }
        return fileManager;
    }

    public static void setup(ApplicationSettings settings) {
        FileManager fileManager = FileSystem.getManager(settings.getFileManager());
        fileManager.setLocalPath(settings.dataDir().getPath());
        fileManager.setSourcePath(settings.getSourceDir().getPath());
    }

    private static FileManager newManager(Manager manager){
        switch (manager){
            case SIMPLE_FILE_MANAGER:
                managers.put(manager, new SimpleFileManager());
                return managers.get(manager);
        }
        return null;
    }
}
