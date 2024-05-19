package com.dfined.omnipatcher.v2.io;

import com.dfined.omnipatcher.v2.OmniPatcherV2;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class BufferedLineReader {
    BufferedReader reader;
    private static final Logger log = LogManager.getLogger(OmniPatcherV2.class.getSimpleName());

    public BufferedLineReader(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            log.fatal("Unable to construct reader for file {}. Exiting.", file.getAbsolutePath(), e);
            Platform.exit();
            System.exit(1);
        }
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }
}
