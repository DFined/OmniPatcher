package dfined.omnipatcher.filesystem;

import java.io.File;
import java.io.IOException;

public interface FileManager {
    // Get a file from a selected source and copy it into the selected repo
    public File getFromSource(String sourcePath, String pathInSource, String repoPath, String pathInRepo) throws IOException;
    // Get a file from a selected source and copy it into the selected repo
    public File getFromSource(File sourcePath, String pathInSource, File repoPath, String pathInRepo) throws IOException;

    // Get a file from local storage if it exists. Otherwise copy it to local and then get it.
    public File getFromLocal(File sourcePath, File localPath, String pathInLocal) throws IOException;

    public File createFileInRepo(File repo, String path) throws IOException;

    public boolean existsInLocal(File localPath, String localFile);

    public void deleteLocal(File localPath, String s);
}
