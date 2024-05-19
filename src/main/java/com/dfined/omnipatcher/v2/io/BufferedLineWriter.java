package com.dfined.omnipatcher.v2.io;

import com.dfined.omnipatcher.v2.OmniPatcherV2;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class BufferedLineWriter {
    BufferedWriter writer;
    private static final Logger log = LogManager.getLogger(OmniPatcherV2.class.getSimpleName());
    public BufferedLineWriter(File file){
        try {
            if(file.exists()){
                file.delete();
            }
            FileUtils.touch(file);
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            log.fatal("Unable to construct writer for file {}. Exiting.", file.getAbsolutePath(),e);
            Platform.exit();
            System.exit(1);
        }
    }

    public void writeLine(String line) throws IOException {
        writer.write(line);
    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
