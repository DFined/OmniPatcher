package dfined.patcher_parser.data.data_structure.io;

import dfined.patcher_parser.application.PatcherParser;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class BufferedLineWriter implements LineWriter{
    BufferedWriter writer;
    private static final Logger log = LogManager.getLogger(PatcherParser.class.getSimpleName());
    public BufferedLineWriter(File file){
        try {
            if(!file.exists()){
                FileUtils.touch(file);
            }
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            log.fatal("Unable to construct writer for file %s. Exiting.", file.getAbsolutePath(),e);
            Platform.exit();
        }
    }

    @Override
    public void writeLine(String line) throws IOException {
        writer.write(line);
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}