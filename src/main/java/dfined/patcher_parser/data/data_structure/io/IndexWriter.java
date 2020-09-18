package dfined.patcher_parser.data.data_structure.io;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public abstract class IndexWriter {
    private static Logger log = LogManager.getLogger(IndexWriter.class);

    public static void writeObject(LineWriter writer, String name, HashMap<String, Object> object) {
        try {
            writeObject(writer, name, object, "");
            writer.close();
        } catch (IOException e) {
            log.fatal("Unable to write index file", e);
            Platform.exit();
        }
    }

    private static void writeObject(LineWriter writer, String name, HashMap<String, Object> object, String prefix) throws IOException {
        writer.writeLine(String.format("%s\"%s\"\n%s{\n", prefix, name, prefix));
        writeBody(writer, object, prefix + "\t");
        writer.writeLine(String.format("%s}\n", prefix));

    }

    private static void writeBody(LineWriter writer, HashMap<String, Object> object, String prefix) throws IOException {
        for (String key : object.keySet()) {
            Object value = object.get(key);
            if (value instanceof HashMap) {
                writeObject(writer, key, (HashMap<String, Object>) value, prefix);
            } else {
                writer.writeLine(String.format("%s\"%s\"\t\t\t\t\"%s\"\n", prefix, key, value.toString()));
            }

        }
    }
}
