package com.dfined.omnipatcher.data.data_structure.io;

import com.dfined.omnipatcher.application.OmniPatcher;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class BufferedLineReader implements LineReader{
    BufferedReader reader;
    private static final Logger log = LogManager.getLogger(OmniPatcher.class.getSimpleName());
    public BufferedLineReader(File file){
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            log.fatal("Unable to construct reader for file %s. Exiting.", file.getAbsolutePath(),e);
            Platform.exit();
            System.exit(1);
        }
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }
}
