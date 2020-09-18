package dfined.patcher_parser.filesystem;

import dfined.patcher_parser.application.PatcherParser;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class SimpleFileManager implements FileManager {
    private static final Logger log = LogManager.getLogger(PatcherParser.class.getSimpleName());
    private String localPath;
    private String sourcePath;

    public File getFromSource(String sourcePath, String pathInSource, String repoPath, String pathInRepo) {
        return getFromSource(new File(sourcePath), pathInSource, new File(repoPath), pathInRepo);
    }

    public File getFromSource(File sourcePath, String pathInSource, File repoPath, String pathInRepo) {
        File source = new File(sourcePath, pathInSource);
        File dest = new File(repoPath, pathInRepo);
        return getFromSource(source, dest);
    }

    @Override
    public File getFromLocal(String pathInLocal) {
        File localFile = new File(localPath, pathInLocal);
        if(!localFile.exists()){
            localFile = getFromSource(sourcePath, pathInLocal, localPath, pathInLocal);
        }
        return localFile;
    }

    @Override
    public File getFromRepo(String repo, String pathInLocal) {
        return new File(repo, pathInLocal);
    }

    @Override
    public void putIntoRepo(String repoBase, String pathInRepo) {
        putIntoRepo(pathInRepo, repoBase, pathInRepo);
    }

    @Override
    public void putIntoRepo(String pathInSource, String repoBase, String pathInRepo) {
        try {
            FileUtils.copyFile(new File(sourcePath,pathInSource),new File(repoBase, pathInRepo));
        } catch (IOException e) {
            log.error(String.format("File copy of %s from %s to %s failed", pathInRepo, sourcePath, repoBase), e);
        }
    }

    public File getFromSource(File sourceFile, File destFile) {
        try {
            FileUtils.copyFile(sourceFile, destFile);
        } catch (IOException e) {
            log.error(String.format("File copy from %s to %s failed", sourceFile.getAbsolutePath(), destFile.getAbsolutePath()), e);
        }
        return destFile;
    }

    @Override
    public boolean existsInLocal(String localFile) {
        return new File(localPath,localFile).exists();
    }

    @Override
    public void deleteLocal(String localFile) {
        new File(localPath,localFile).delete();
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
}
