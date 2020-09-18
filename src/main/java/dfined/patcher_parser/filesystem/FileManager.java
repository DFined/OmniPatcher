package dfined.patcher_parser.filesystem;

import java.io.File;

public interface FileManager {
    public File getFromSource(String sourcePath, String pathInSource, String repoPath, String pathInRepo);
    public File getFromSource(File sourcePath, String pathInSource, File repoPath, String pathInRepo);
    public File getFromSource(File sourceFile, File destFile);

    public File getFromLocal(String pathInLocal);

    public File getFromRepo(String repo, String pathInLocal);

    public void putIntoRepo(String repoBase, String pathInRepo);
    public void putIntoRepo(String pathInSource, String repoBase, String pathInRepo);

    public boolean existsInLocal(String localFile);

    public void deleteLocal(String s);

    public String getLocalPath();
    public void setLocalPath(String localPath);
    public String getSourcePath();
    public void setSourcePath(String sourcePath);
}
