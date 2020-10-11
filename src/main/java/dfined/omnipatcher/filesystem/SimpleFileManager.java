package dfined.omnipatcher.filesystem;

import dfined.omnipatcher.application.OmniPatcher;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class SimpleFileManager implements FileManager {
    private static final Logger log = LogManager.getLogger(OmniPatcher.class.getSimpleName());

    public File getFromSource(String sourcePath, String pathInSource, String repoPath, String pathInRepo) throws IOException {
        return getFromSource(new File(sourcePath), pathInSource, new File(repoPath), pathInRepo);
    }

    public File getFromSource(File sourcePath, String pathInSource, File repoPath, String pathInRepo) throws IOException {
        File source = new File(sourcePath, pathInSource);
        File dest = new File(repoPath, pathInRepo);
        return getFromSource(source, dest);
    }

    public File getFromSource(File sourceFile, File destFile) throws IOException {
        FileUtils.copyFile(sourceFile, destFile);
        return destFile;
    }

    @Override
    public File getFromLocal(File sourcePath, File localPath, String pathInLocal) throws IOException {
        File localFile = new File(localPath, pathInLocal);
        if (!localFile.exists()) {
            localFile = getFromSource(sourcePath, pathInLocal, localPath, pathInLocal);
        }
        return localFile;
    }

    @Override
    public File createFileInRepo(File repo, String path) throws IOException {
        File res = new File(repo, path);
        FileUtils.touch(res);
        return res;
    }

    @Override
    public boolean existsInLocal(File localPath, String localFile) {
        return new File(localPath, localFile).exists();
    }

    @Override
    public void deleteLocal(File localPath, String localFile) {
        new File(localPath, localFile).delete();
    }

    @Override
    public HashMap<String, File> listFilesInRepoDir(File repo, String dirPath) {
        HashMap<String,File> result = new HashMap<>();
        File dir = new File(repo, dirPath);
        addToMapFromDir(result, dir);
        return null;
    }

    private void addToMapFromDir(HashMap<String,File> map, File dir){
        if(dir.isDirectory()){
            Arrays.stream(dir.listFiles()).forEach(file->addToMapFromDir(map, file));
        }else{
            map.put(dir.getPath(),dir);
        }
    }
}
