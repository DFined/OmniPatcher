package com.dfined.omnipatcher.filesystem;

import javavpk.core.Archive;
import javavpk.core.Entry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VPKFileManager implements FileManager {
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(new String[]{"vmdl_c","vpcf_c","txt","vtex_c"});
    HashMap<String, Entry> fileSystem;
    String name;

    private void init(File archive) throws IOException {
        if(name != archive.getPath()) {
            Archive vpkArchive = new Archive(archive);
            fileSystem = vpkArchive.load(ALLOWED_EXTENSIONS);
            name = archive.getPath();
        }
    }

    @Override
    public File getFromSource(String sourcePath, String pathInSource, String repoPath, String pathInRepo) throws IOException {
        return getFromSource(new File(sourcePath),pathInSource,new File(repoPath),pathInRepo);
    }

    @Override
    public File getFromSource(File sourcePath, String pathInSource, File repoPath, String pathInRepo) throws IOException {
        init(sourcePath);
        pathInSource = pathInSource.startsWith("/") ? pathInSource.substring(1) : pathInSource;
        File resultFile = new File(repoPath,pathInRepo);
        Entry entry = fileSystem.get(pathInSource);
        if(resultFile.exists()){
            resultFile.delete();
        }
        if(entry==null){
            throw new FileNotFoundException(String.format("File %s not found in vpk %s",pathInSource,sourcePath));
        }
        FileUtils.touch(resultFile);
        entry.extract(resultFile);
        return resultFile;
    }


    @Override
    public File getFromLocal(File sourcePath, File localPath, String pathInLocal) throws IOException {
        File target = new File(localPath,pathInLocal);
        if(!target.exists()){
            getFromSource(sourcePath,pathInLocal,localPath,pathInLocal);
        }
        return target;
    }

    @Override
    public File createFileInRepo(File repo, String path) throws IOException {
        File res = new File(repo,path);
        FileUtils.touch(res);
        return res;
    }

    @Override
    public boolean existsInLocal(File localPath, String localFile) {
        return new File(localPath,localFile).exists();
    }

    @Override
    public void deleteLocal(File localPath, String localFile) {
        new File(localPath,localFile).delete();
    }

    @Override
    public HashMap<String, File> listFilesInRepoDir(File repo, String dirPath) {
        HashMap<String,File> result = new HashMap<>();
        File dir = new File(repo, dirPath);
        addToMapFromDir(result, dir);
        return result;
    }

    private void addToMapFromDir(HashMap<String,File> map, File dir){
        if(dir.isDirectory()){
            Arrays.stream(dir.listFiles()).forEach(file->addToMapFromDir(map, file));
        }else{
            map.put(dir.getPath(),dir);
        }
    }
}
